package service.timer.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import mapper.timer.SaleBaseMapper;
import mapper.timer.SaleDetailMapper;
import mapper.timer.SaleMainMapper;
import play.Logger;
import service.timer.IHttpService;
import service.timer.ISaleOrderTaxesService;
import util.timer.SaleOrderStatus;
import util.timer.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import entity.timer.SaleBase;
import entity.timer.SaleDetail;
import entity.timer.SaleMain;
import events.timer.AutoPayEvent;


public class SaleOrderTaxesService implements ISaleOrderTaxesService {
	@Inject
	private SaleMainMapper saleMainMapper;
	@Inject
	private SaleDetailMapper saleDetailMapper;
	@Inject
	private IHttpService httpService;
	@Inject
	private SaleBaseMapper saleBaseMapper;
	
	@Override
	public SaleMain calculateTaxes(Integer mainId, String salesOrderNo) {
		SaleMain selectSaleMain = saleMainMapper.selectSaleMain(mainId, salesOrderNo);
		if(selectSaleMain == null){
			Logger.info("不存在此销售单，id为【"+mainId+"】，salesOrderNo为【"+salesOrderNo+"】");
			return null;
		}
		
		SaleBase saleBase = saleBaseMapper.selectByOrderId(mainId);
		
		// 查询订单具体的商品
		List<SaleDetail> saleDetailList = saleDetailMapper.selectByOrderId(mainId);
		
		// 计算税金
		SaleMain sm = doCalculateTaxes(saleBase,saleDetailList);
		
		Logger.info(salesOrderNo + "计算出来的关税为：" + sm.getImportTar());
		Logger.info(salesOrderNo + "计算出来的增值税为：" + sm.getVat());
		Logger.info(salesOrderNo + "计算出来的消费税为：" + sm.getGst());
		Logger.info(salesOrderNo + "计算出来的行邮税为：" + sm.getPostalFee());
		Logger.info(salesOrderNo + "计算出来的税金为：" + sm.getTaxFee());
		
		// 更新税金
		sm.setId(mainId);
		int count = saleMainMapper.updateTaxFee(sm);
		
		Logger.info(mainId + "更新税金：" + ((count == 1) ? "成功" : "失败"));
		
		return sm;
	}
	
	/**
	 * 计算税金
	 * @param saleDetailList
	 * @return
	 */
	private SaleMain doCalculateTaxes(SaleBase saleBase,List<SaleDetail> saleDetailList) {
		// 按仓库来分组
		Map<Integer, List<SaleDetail>> saleDetailListByWarehouseId = saleDetailList
				.stream().collect(
						Collectors.groupingBy(SaleDetail::getWarehouseId));
		
		// 关税
		BigDecimal ImportTar4AOrder = new BigDecimal(0);
		// 增值税
		BigDecimal vat4AOrder = new BigDecimal(0);
		// 消费税
		BigDecimal gst4AOrder = new BigDecimal(0);
		// 行邮税
		BigDecimal postalFee4AOrder = new BigDecimal(0);
		
		
		Map<String,String> warehouseIdTypeMap = Maps.newHashMap();
		try {
			// 查询仓库信息
			JsonNode warehousesNode = httpService.getWarehouse(null);
			for(Iterator<JsonNode> it = warehousesNode.iterator();it.hasNext();){
				JsonNode warehouseNode = it.next();
				warehouseIdTypeMap.put(warehouseNode.get("warehouseId").asText(),warehouseNode.get("type").asText());
			}
		} catch (IOException e) {
			Logger.info("计算税金时，查询仓库信息和商品信息出错了");
			throw new RuntimeException(e);
		}
		
		// 平摊运费，value为每个商品平摊的运费
		Map<SkuWareHouseIdKey, BigDecimal> shareBbcPostage2AProduct = shareBbcPostage2AProduct(saleBase, saleDetailList, warehouseIdTypeMap);
		Logger.info("平摊的运费为："+shareBbcPostage2AProduct);
		
		Logger.info("开始计算税金");
		// 计算税金
		if(warehouseIdTypeMap.size()>0 && saleDetailListByWarehouseId.size()>0){
			try{
				for(Map.Entry<Integer, List<SaleDetail>> entry : saleDetailListByWarehouseId.entrySet()){
					int warehouseId = entry.getKey();
					List<SaleDetail> sdList = entry.getValue();
					// 仓库类型 10:内部保税仓 20:内部完税仓 30:内部直邮仓 40:外部保税仓
					String warehouseType = warehouseIdTypeMap.get(String.valueOf(warehouseId));
					
					List<String> skus = sdList.stream().map(SaleDetail::getSku).collect(Collectors.toList());
					// 再次查询商品的信息
					JsonNode productsNode = httpService.getProducts(null, skus, Integer.valueOf(warehouseId), null);
					
					for(SaleDetail sd : sdList){
						// 消费税税率，增值税税率，关税税率，行邮税税率，头程运费
						Double gstRate=null,vatRate=null,importTarRate=null,postalFeeRate=null,logisticFee=null,cost=null;
						for(Iterator<JsonNode> it = productsNode.get("data").get("result").iterator();it.hasNext();){
							JsonNode productNode = it.next();
							if(sd.getSku().equals(productNode.get("csku").asText())){
								gstRate = productNode.get("gstRate").asDouble();
								vatRate = productNode.get("vatRate").asDouble();
								importTarRate = productNode.get("importTarRate").asDouble();
								postalFeeRate = productNode.get("postalFeeRate").asDouble();
								logisticFee = productNode.get("logisticFee").asDouble();
								cost = productNode.get("cost").asDouble();
								break;
							}
						}
						
						// 获取运费
						BigDecimal bbcPostage2AProduct = shareBbcPostage2AProduct.get(new SkuWareHouseIdKey(sd.getSku(), Integer.valueOf(warehouseId)));
						// 计算订单中每个商品的税金
						
						sd.normalizeParametersBeforeCalculateTaxFee(
								warehouseType, warehouseId, gstRate, vatRate,
								importTarRate, postalFeeRate, logisticFee, cost);
						
						// 统一计算公式，有些仓库不需要计算的税，会返回0
						// 关税
						ImportTar4AOrder = ImportTar4AOrder.add(sd.calculateImportTar(warehouseType,warehouseId,bbcPostage2AProduct,
								gstRate,vatRate,importTarRate,postalFeeRate,logisticFee,cost));
						// 增值税
						vat4AOrder = vat4AOrder.add(sd.calculateVat(warehouseType,warehouseId,bbcPostage2AProduct,
								gstRate,vatRate,importTarRate,postalFeeRate,logisticFee,cost));
						// 消费税
						gst4AOrder = gst4AOrder.add(sd.calculateGst(warehouseType,warehouseId,bbcPostage2AProduct,
								gstRate,vatRate,importTarRate,postalFeeRate,logisticFee,cost));
						// 行邮税
						postalFee4AOrder = postalFee4AOrder.add(sd.getPostalFee(warehouseType,warehouseId,bbcPostage2AProduct,
								gstRate,vatRate,importTarRate,postalFeeRate,logisticFee,cost));
						
						/*
//						订单商品征税规则如下：
						if(2050==warehouseId || 2029==warehouseId){
							// 内部保税仓，福州仓/南沙保税仓
							// 内部保税仓商品以订单商品零售价（即真实售价）征关税、增值税、消费税，各项税金均打七折；
							// 内部保税仓商品，若订单产生运费（如不包邮），按金额分摊到每个商品零售价上进行税金计算
							ImportTar4AOrder = ImportTar4AOrder.add(sd.calculateImportTar(warehouseType,warehouseId,bbcPostage2AProduct,
									gstRate,vatRate,importTarRate,postalFeeRate,logisticFee,cost));
							vat4AOrder = vat4AOrder.add(sd.calculateVat(warehouseType,warehouseId,bbcPostage2AProduct,
									gstRate,vatRate,importTarRate,postalFeeRate,logisticFee,cost));
							gst4AOrder = gst4AOrder.add(sd.calculateGst(warehouseType,warehouseId,bbcPostage2AProduct,
									gstRate,vatRate,importTarRate,postalFeeRate,logisticFee,cost));
						}else if(2024==warehouseId){
							// 内部完税仓，深圳仓
							// 内部完税仓商品以订单商品CIF价（=裸采价+头程）征关税、增值税、消费税；
							ImportTar4AOrder = ImportTar4AOrder.add(sd.calculateImportTar(warehouseType,warehouseId,bbcPostage2AProduct,
									gstRate,vatRate,importTarRate,postalFeeRate,logisticFee,cost));
							vat4AOrder = vat4AOrder.add(sd.calculateVat(warehouseType,warehouseId,bbcPostage2AProduct,
									gstRate,vatRate,importTarRate,postalFeeRate,logisticFee,cost));
							gst4AOrder = gst4AOrder.add(sd.calculateGst(warehouseType,warehouseId,bbcPostage2AProduct,
									gstRate,vatRate,importTarRate,postalFeeRate,logisticFee,cost));
						}else if(73==warehouseId || 6==warehouseId){
							// 海外直邮仓，美国/英国
							// 内部直邮仓商品以订单商品零售价（即真实售价）征行邮税；
							// 内部直邮仓商品，若订单产生运费（如不包邮），按金额分摊到每个商品零售价上进行税金计算
							postalFee4AOrder = postalFee4AOrder.add(sd.getPostalFee(warehouseType,warehouseId,bbcPostage2AProduct,
									gstRate,vatRate,importTarRate,postalFeeRate,logisticFee,cost));
						}else if(2012==warehouseId || 2062==warehouseId){	
							// 外部保税仓，杭州保税仓/福州1仓
							// 外部保税仓税金为0。
						}
						*/
					}
				}
			}catch(Exception e){
				Logger.info("计算税金时出错了");
				throw new RuntimeException(e);
			}
		}
		
		// 关税 + 增值税 + 消费税 + 行邮税
		BigDecimal taxFee4AOrder = ImportTar4AOrder.add(vat4AOrder).add(gst4AOrder).add(postalFee4AOrder);
		
		SaleMain sm = new SaleMain();
		sm.setTaxFee(keepTwoDecimalDouble(taxFee4AOrder.doubleValue()));
		sm.setImportTar(keepTwoDecimalDouble(ImportTar4AOrder.doubleValue()));
		sm.setVat(keepTwoDecimalDouble(vat4AOrder.doubleValue()));
		sm.setGst(keepTwoDecimalDouble(gst4AOrder.doubleValue()));
		sm.setPostalFee(keepTwoDecimalDouble(postalFee4AOrder.doubleValue()));
		
		return sm;
	}
	
	/**
	 * 平摊运费
	 * @param saleBase
	 * @param saleDetailList
	 * @return 返回每个商品平摊的运费
	 */
	private Map<SkuWareHouseIdKey,BigDecimal> shareBbcPostage2AProduct(SaleBase saleBase,List<SaleDetail> saleDetailList, Map<String,String> warehouseIdTypeMap){
		Map<SkuWareHouseIdKey,BigDecimal> map = new HashMap<SkuWareHouseIdKey,BigDecimal>();
		Double bbcPostage = saleBase.getBbcPostage();// 运费
		if(bbcPostage != null && bbcPostage.doubleValue()>0){
			BigDecimal bbcPostageBD = new BigDecimal(bbcPostage);
			BigDecimal totalSellingPrice = getTotalSellingPrice(saleDetailList);// 销售单的总最终售价
			for(SaleDetail sa : saleDetailList){
				String warehouseType = warehouseIdTypeMap.get(String.valueOf(sa.getWarehouseId().intValue()));// 仓库类型
				// 内部保税仓商品和内部直邮仓商品，若订单产生运费（如不包邮），按金额分摊到每个商品零售价上进行税金计算
				boolean shareBbcPostage = "10".equals(warehouseType) || "30".equals(warehouseType);
				
				if(shareBbcPostage){
					BigDecimal percentage = sa.calculateTotalFinalSellingPrice().divide(totalSellingPrice);// 获得此商品总零售价的占比
					// 计算此类商品的运费
					BigDecimal bbcPostage4ASku = percentage.multiply(bbcPostageBD);
					// 查看此商品的数量是否大于1，如果大于1，要把平摊到此类商品的运费再平摊到1个商品上面
					if(sa.getQty() > 1){
						BigDecimal bbcPostage4AProduct = bbcPostage4ASku.divide(new BigDecimal(sa.getQty()));
						map.put(new SkuWareHouseIdKey(sa.getSku(),sa.getWarehouseId()), bbcPostage4AProduct);
					}else{
						map.put(new SkuWareHouseIdKey(sa.getSku(),sa.getWarehouseId()), bbcPostage4ASku);
					}
				}
			}
		}
		return map;
	}
	
	/**
	 * 获取商品的总售价
	 * @param saleDetailList
	 * @return
	 */
	public BigDecimal getTotalSellingPrice(List<SaleDetail> saleDetailList){
		BigDecimal total = new BigDecimal(0);
		for(SaleDetail sa : saleDetailList){
			total = total.add( sa.calculateTotalFinalSellingPrice() );
		}
		return total;
	}
	
	/**
	 * 以sku和warehouseId为key
	 * @author huangjc
	 * @date 2016年11月2日
	 */
	private class SkuWareHouseIdKey{
		private String sku;
		private Integer warehouseId;
		
		public SkuWareHouseIdKey(String sku, Integer warehouseId) {
			this.sku=sku;
			this.warehouseId=warehouseId;
		}
		public String getSku() {
			return sku;
		}
		public void setSku(String sku) {
			this.sku = sku;
		}
		public Integer getWarehouseId() {
			return warehouseId;
		}
		public void setWarehouseId(Integer warehouseId) {
			this.warehouseId = warehouseId;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((sku == null) ? 0 : sku.hashCode());
			result = prime * result
					+ ((warehouseId == null) ? 0 : warehouseId.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SkuWareHouseIdKey other = (SkuWareHouseIdKey) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (sku == null) {
				if (other.sku != null)
					return false;
			} else if (!sku.equals(other.sku))
				return false;
			if (warehouseId == null) {
				if (other.warehouseId != null)
					return false;
			} else if (!warehouseId.equals(other.warehouseId))
				return false;
			return true;
		}
		private SaleOrderTaxesService getOuterType() {
			return SaleOrderTaxesService.this;
		}
		@Override
		public String toString() {
			return "SkuWareHouseIdKey [sku=" + sku + ", warehouseId="
					+ warehouseId + "]";
		}
	}
	
	private Double keepTwoDecimalDouble(Double f) {
        DecimalFormat decimalFormat=new DecimalFormat(".00");
        return Double.parseDouble(decimalFormat.format(f));
    }
	public Double jsonToDouble(JsonNode node){
		return node!=null?("null".equals(node.asText())?null:node.asDouble()):null;
	}

}
	
