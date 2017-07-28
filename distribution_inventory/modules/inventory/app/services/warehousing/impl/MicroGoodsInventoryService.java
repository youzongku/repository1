package services.warehousing.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import dto.warehousing.GoodsInventoryListDto;
import dto.warehousing.GoodsInventorySearchDto;
import dto.warehousing.MicroWarehouseDto;
import entity.warehousing.InventoryChangeHistory;
import entity.warehousing.MicroGoodsInventory;
import enums.warehousing.OrderType;
import forms.warehousing.InventoryChangeDetailForm;
import forms.warehousing.InventoryChangeForm;
import forms.warehousing.MicroGoodsInventoryForm;
import forms.warehousing.MicroWarehouseForm;
import mapper.warehousing.GoodsInventoryMapper;
import mapper.warehousing.MicroGoodsInventoryMapper;
import services.base.utils.JsonFormatUtils;
import services.product_inventory.IProductMicroInventoryService;
import services.warehousing.IInventoryChangeHistoryService;
import services.warehousing.IMicroGoodsInventoryService;
import services.warehousing.IMicroWarehouseService;
import util.warehousing.DataUtil;
import util.warehousing.Page;

/**
 * 
 * 
 * @author ye_ziran
 * @since 2016年3月3日 下午12:01:28
 */
public class MicroGoodsInventoryService implements IMicroGoodsInventoryService {

	@Inject
	IInventoryChangeHistoryService iHistoryService;

	@Inject
	MicroGoodsInventoryMapper mInventoryMapper;

	@Inject
	GoodsInventoryMapper inventoryMapper;
	
	@Inject
	IMicroWarehouseService mwarehouseService;

	@Inject
	IProductMicroInventoryService productMicroInventoryService;
	
	/**
	 * 
	 * 
	 * @param mInventory
	 * @return
	 * @author ye_ziran
	 * @since 2016年3月3日 上午11:54:45
	 */
	@Override
	public int insert(MicroGoodsInventory mInventory) {
		return mInventoryMapper.insert(mInventory);
	}

	/**
	 * 更新
	 * <p>
	 * 公式， 微仓出库：微仓数量-,总仓数量- 微仓入库：微仓数量+
	 * 
	 * @param mInventory
	 * @return
	 * @author ye_ziran
	 * @since 2016年3月3日 下午12:01:37
	 */
	@Override
	public int update(MicroGoodsInventory mInventory) {
		return mInventoryMapper.update(mInventory);
	}

	@Override
	public int delete(MicroGoodsInventory mInventory) {
		return mInventoryMapper.delete(mInventory);
	}

	@Override
	public Page<MicroGoodsInventory> query(MicroGoodsInventoryForm mInventory, Integer pageNum, Integer length) {
		List<MicroGoodsInventory> resList = mInventoryMapper.query(mInventory, pageNum, length);
		List<String> skus = resList.stream().filter(rel -> (rel.getSku() != null)).map(MicroGoodsInventory::getSku)
				.collect(Collectors.toList());
		Map<String, String> imgs = Maps.newHashMap();
		if(!CollectionUtils.isEmpty(skus)) {
			List<Map<String, String>> imgUrls = mInventoryMapper.queryImage(skus);
			Iterator<String> key = null;
			for (Map<String, String> map : imgUrls) {
				key = map.values().iterator();
				imgs.put(key.next(), key.next());
			}
		}
		for (MicroGoodsInventory res : resList) {
			res.setImgUrl(imgs.get(res.getSku()));
		}
		int total = mInventoryMapper.total(mInventory);
		Page<MicroGoodsInventory> page = new Page<MicroGoodsInventory>(resList, total, pageNum != null ? pageNum : 1,
				length != null ? length :10);
		return page;
	}

	@Override
	public List<Map<String, Object>> updateStock(InventoryChangeForm inventoryForm) {
		List<Map<String, Object>> resultList = null;
		if (inventoryForm.getOrderType() >= OrderType.OUT.getValue()) {
			resultList = stockOut(inventoryForm);
		} else {
			resultList = stockIn(inventoryForm);
		}
		return resultList;
	}

	/**
	 * 入库
	 * 
	 * @param inventoryForm
	 * @return
	 * @author ye_ziran
	 * @since 2016年3月9日 下午2:25:59
	 */
	private List<Map<String, Object>> stockIn(InventoryChangeForm inventoryForm) {
		List<Map<String, Object>> resultList = new ArrayList<>();
		List<InventoryChangeDetailForm> detailList = inventoryForm.getDetailList();
		for (InventoryChangeDetailForm detailForm : detailList) {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			int num = detailForm.getNum();

			// 判断是否存在库存
			MicroGoodsInventoryForm mInventoryForm = new MicroGoodsInventoryForm();
			// sku+微仓id+仓库id，有且只有一条记录
			mInventoryForm.setSku(detailForm.getSku());
			mInventoryForm.setMwarehouseId(inventoryForm.getMwarehouseId());
			mInventoryForm.setWarehouseId(detailForm.getWarehouseId());
			Page<MicroGoodsInventory> resPage = this.query(mInventoryForm, 1, 10);
			if (resPage.getList() != null && resPage.getList().size() > 0) {
				// 只会有1条记录，如果大于1，即程序bug
				MicroGoodsInventory mInventory = resPage.getList().get(0);
				int totalStock = mInventory.getTotalStock();
				int avaliableStock = mInventory.getAvaliableStock();
				//
				GoodsInventorySearchDto paramDto = new GoodsInventorySearchDto();
				paramDto.setSku(detailForm.getSku());
				paramDto.setWarehouseId(detailForm.getWarehouseId());
				List<GoodsInventoryListDto> list = inventoryMapper.getCloudGoodsInvens(paramDto);
				GoodsInventoryListDto inventory = list.get(0);
				if (num > inventory.getAvailableStock()) {
					resultMap.put("result", false);
					resultMap.put("msg", "入库失败，商品[sku=" + detailForm.getSku() + "]在云仓["
							+ inventoryForm.getMwarehouseId() + "]中库存不足");
					resultList.add(resultMap);
					continue;
				}

				mInventory.setTotalStock(totalStock + num);
				mInventory.setAvaliableStock(avaliableStock + num);
				mInventory.setCostprice(detailForm.getCostprice());
				update(mInventory);
			} else {
				MicroGoodsInventory mInventory = new MicroGoodsInventory();
				mInventory.setSku(detailForm.getSku());
				mInventory.setMwarehouseId(inventoryForm.getMwarehouseId());
				mInventory.setWarehouseId(detailForm.getWarehouseId());
				mInventory.setMwarehouseName(inventoryForm.getDistributorName());
				mInventory.setWarehouseName(detailForm.getWarehouseName());
				mInventory.setProductTitle(detailForm.getProductTitle());
				mInventory.setProductCategory(detailForm.getProductCategoryId());
				mInventory.setProductCategoryName(detailForm.getProductCategoryName());
				mInventory.setTotalStock(num);
				mInventory.setAvaliableStock(num);
				mInventory.setCostprice(detailForm.getCostprice());
				insert(mInventory);
			}

			// 记录库存变更
			InventoryChangeHistory history = new InventoryChangeHistory();
			history.setChangeTime(new Date());
			history.setDisMemberEmail(inventoryForm.getDistributorEmail());
			history.setEffective(true);
			history.setNum(num);
			history.setOperator(inventoryForm.getDistributorEmail());
			history.setOrderNo(inventoryForm.getOrderNo());
			history.setProductName(detailForm.getProductTitle());
			history.setSku(detailForm.getSku());
			history.setOrderType(inventoryForm.getOrderType());
			history.setType(1);
			history.setMwarehouseId(inventoryForm.getMwarehouseId());
			history.setMwarehouseName(inventoryForm.getDistributorEmail());
			
			//微仓记录添加真实仓id
			history.setWarehouseId(detailForm.getWarehouseId());
			history.setWarehouseName(detailForm.getWarehouseName());
			
			//设置分销价格体系
			history.setDisProfitRate(detailForm.getDisProfitRate());
			history.setDisProfit(detailForm.getDisProfit());
			history.setDisVat(detailForm.getDisVat());
			history.setDisStockFee(detailForm.getDisStockFee());
			history.setDisShippingType(detailForm.getDisShippingType());
			history.setDisOtherCost(detailForm.getDisOtherCost());
			history.setDisTotalCost(detailForm.getDisTotalCost());
			history.setDisTransferFee(detailForm.getDisTransferFee());
			history.setDisListFee(detailForm.getDisListFee());
			history.setDisTradeFee(detailForm.getDisTradeFee());
			history.setDisPayFee(detailForm.getDisPayFee());
			history.setDisPostalFee(detailForm.getDisPostalFee());
			history.setDisImportTar(detailForm.getDisImportTar());
			history.setDisGst(detailForm.getDisGst());
			history.setDisInsurance(detailForm.getDisInsurance());
			history.setDisTotalVat(detailForm.getDisTotalVat());
			history.setCost(detailForm.getCost());
			history.setDisFreight(detailForm.getDisFreight());
			history.setDisPrice(detailForm.getDisPrice());
			history.setDisCifPrice(detailForm.getDisCifPrice());
			history.setPurchasePrice(detailForm.getPurchasePrice());
			
			iHistoryService.save(history);

			resultMap.put("result", true);
			resultList.add(resultMap);
		}
		return resultList;
	}

	/**
	 * 出库
	 * 
	 * @param inventoryForm
	 * @return
	 * @author ye_ziran
	 * @since 2016年3月9日 下午2:25:52
	 */
	private List<Map<String, Object>> stockOut(InventoryChangeForm inventoryForm) {
		List<Map<String, Object>> resultList = new ArrayList<>();
		List<InventoryChangeDetailForm> detailList = inventoryForm.getDetailList();
		for (InventoryChangeDetailForm detailForm : detailList) {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			int num = detailForm.getNum();

			// 判断是否存在库存
			MicroGoodsInventoryForm mInventoryForm = new MicroGoodsInventoryForm();
			// sku+微仓id+仓库id，有且只有一条记录
			mInventoryForm.setSku(detailForm.getSku());
			mInventoryForm.setMwarehouseId(inventoryForm.getMwarehouseId());
			mInventoryForm.setWarehouseId(detailForm.getWarehouseId());
			Page<MicroGoodsInventory> resPage = query(mInventoryForm, 1, 10);
			if (resPage.getList() != null && resPage.getList().size() > 0) {
				// 只会有1条记录，如果大于1，即程序bug
				MicroGoodsInventory mInventory = resPage.getList().get(0);
				int totalStock = mInventory.getTotalStock();
				int avaliableStock = mInventory.getAvaliableStock();
				if (avaliableStock - num >= 0) {

					mInventory.setTotalStock(totalStock - num);
					mInventory.setAvaliableStock(avaliableStock - num);
					mInventory.setCostprice(detailForm.getCostprice());
					update(mInventory);

					// 记录库存变更
					InventoryChangeHistory history = new InventoryChangeHistory();
					history.setChangeTime(new Date());
					history.setDisMemberEmail(inventoryForm.getDistributorEmail());
					history.setEffective(true);
					history.setNum(num);
					history.setOperator(inventoryForm.getDistributorEmail());
					history.setOrderNo(inventoryForm.getOrderNo());
					history.setProductName(detailForm.getProductTitle());
					history.setSku(detailForm.getSku());
					history.setOrderType(inventoryForm.getOrderType());
					history.setType(0);
					history.setMwarehouseId(inventoryForm.getMwarehouseId());
					history.setMwarehouseName(inventoryForm.getDistributorEmail());
					history.setWarehouseId(detailForm.getWarehouseId());
					history.setWarehouseName(detailForm.getWarehouseName());
					iHistoryService.save(history);

					resultMap.put("result", true);
				} else {
					resultMap.put("result", false);
					resultMap.put("msg",
							"出库失败，商品[sku=" + detailForm.getSku() + "]在微仓[" + inventoryForm.getMwarehouseId() + "]库存不足");
				}
			} else {
				resultMap.put("result", false);
				resultMap.put("msg",
						"出库失败，微仓[" + inventoryForm.getMwarehouseId() + "]不存在[sku=" + detailForm.getSku() + "]的库存");
			}
			resultList.add(resultMap);
		}
		return resultList;
	}

	/**
	 * add by zbc （替换b2c）微仓查询接口
	 */
	@Override
	public JsonNode b2bQuery(JsonNode node) {
		MicroGoodsInventoryForm mInventoryForm = JsonFormatUtils.jsonToBean(node.toString(), MicroGoodsInventoryForm.class);
		//通过分销商Email得到分销商的微仓id
		MicroWarehouseDto mwarehouseDto = null;
		if(null != mInventoryForm.getDistributorEmail()){
			MicroWarehouseForm mwarehouseForm = new MicroWarehouseForm();
			mwarehouseForm.setDistributorEmail(mInventoryForm.getDistributorEmail());
			Page<MicroWarehouseDto> mList = mwarehouseService.query(mwarehouseForm);
			if(mList.getList() != null && mList.getList().size() > 0){
				mwarehouseDto = mList.getList().get(0);
				mInventoryForm.setMwarehouseId(mwarehouseDto.getId());
			}else{
				mInventoryForm.setMwarehouseId(0);
			}
		}
		
		//Page<MicroGoodsInventory> resPage = query(mInventoryForm, mInventoryForm.getPageNo(), mInventoryForm.getPageSize());
		
		List<MicroGoodsInventory> resList=productMicroInventoryService.queryByParams(mInventoryForm);
				
		List<String> skus = resList.stream().filter(rel -> (rel.getSku() != null)).map(MicroGoodsInventory::getSku)
				.collect(Collectors.toList());
		Map<String, String> imgs = Maps.newHashMap();
		if(!CollectionUtils.isEmpty(skus)) {
			List<Map<String, String>> imgUrls = mInventoryMapper.queryImage(skus);
			Iterator<String> key = null;
			for (Map<String, String> map : imgUrls) {
				key = map.values().iterator();
				imgs.put(key.next(), key.next());
			}
		}
		for (MicroGoodsInventory res : resList) {
			res.setImgUrl(imgs.get(res.getSku()));
		}
		int total = productMicroInventoryService.total(mInventoryForm);
		Page<MicroGoodsInventory> page = new Page<MicroGoodsInventory>(resList, total, mInventoryForm.getPageNo() != null ? mInventoryForm.getPageNo() : 1,
				mInventoryForm.getPageSize() != null ? mInventoryForm.getPageSize() :10);
		
		return DataUtil.formatData(page,null);
	}

}
