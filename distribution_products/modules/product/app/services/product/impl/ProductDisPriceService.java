package services.product.impl;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import dto.product.B2cSyncPriceDto;
import dto.product.ClearancePriceDto;
import dto.product.OperatePriceLogDto;
import dto.product.OperateRuleDto;
import dto.product.ProStockDto;
import dto.product.ProductDispriceDto;
import dto.product.ProductDispriceSearch;
import dto.product.ProductPriceFactorDto;
import dxo.category.SkuWarehouse2Qty;
import entity.category.CategoryBase;
import entity.product.OperateProductPrice;
import entity.product.OperateProductPriceRule;
import entity.product.ProductDisprice;
import entity.product.ProductPriceCategoryBrand;
import entity.product.ProductPriceFactor;
import entity.product.ProductPriceRule;
import entity.product.Warehouse;
import mapper.category.CategoryBaseMapper;
import mapper.product.OperateProductPriceMapper;
import mapper.product.OperateProductPriceRuleMapper;
import mapper.product.ProductDispriceMapper;
import mapper.product.ProductPriceCategoryBrandMapper;
import mapper.product.ProductPriceFactorMapper;
import mapper.product.ProductPriceRuleMapper;
import play.Logger;
import play.libs.Json;
import services.base.utils.DoubleCalculateUtils;
import services.base.utils.JsonFormatUtils;
import services.product.IProductDispriceService;
import services.product.IVirtualCategoryService;
import util.product.Constant;
import util.product.HttpUtil;
import util.product.Page;

/**
 * @author zbc 2016年7月28日 下午2:27:36
 */
public class ProductDisPriceService implements IProductDispriceService {

	@Inject
	private IVirtualCategoryService vcService;
	@Inject
	private CategoryBaseMapper categoryBaseMapper;
	@Inject
	private ProductDispriceMapper disPriceMapper;
	@Inject
	private ProductPriceRuleMapper productPriceRuleMapper;
	@Inject
	private OperateProductPriceRuleMapper operateProductPriceRuleMapper;
	@Inject
	private OperateProductPriceMapper operateProductPriceMapper;
	@Inject
	private ProductPriceCategoryBrandMapper cateBrandMapper;

	/**
	 * 定义批量插入，更新 最大长度常量 
	 */
	private static final int CU_MAX_LENGTH = 150;
	
	private static DecimalFormat f = new DecimalFormat("###0.00");
	static {
		f.setRoundingMode(RoundingMode.HALF_UP);
	}
	@Inject
	private ProductPriceFactorMapper factorMapper;
	// 根据设置价格类型，过滤掉 修改价格字段
	private static final Map<String, List<String>> FILTERTMAP = Maps
			.newHashMap();

	static {
		FILTERTMAP.put("BASE", Lists.newArrayList(new String[] { "floorPrice", "proposalRetailPrice" }));
		FILTERTMAP.put("DIS", Lists.newArrayList(new String[] { "distributorPrice" }));
		FILTERTMAP.put("FTZ", Lists.newArrayList(new String[] { "ftzPrice" }));
		FILTERTMAP.put("ELE", Lists.newArrayList(new String[] { "electricityPrices" }));
		FILTERTMAP.put("SUP", Lists.newArrayList(new String[] { "supermarketPrice" }));
		FILTERTMAP.put("VIP", Lists.newArrayList(new String[] { "vipPrice" }));
	}

	@Override
	public List<Map<String,Object>> batchGetArriveWarePrice(List<SkuWarehouse2Qty> skuWarehouse2QtyList){
		List<Map<String,Object>> list = Lists.newArrayList();
		// 按照仓库id来区分sku
		Map<Integer, List<SkuWarehouse2Qty>> skusByWarehouseId = skuWarehouse2QtyList.stream()
				.collect(Collectors.groupingBy(SkuWarehouse2Qty::getWarehouseId));

		// 分仓计算
		Map<String,Object> map;
		for (Map.Entry<Integer, List<SkuWarehouse2Qty>> entrySet : skusByWarehouseId.entrySet()) {
			// sku集合
			List<String> skuList = entrySet.getValue().stream().map(SkuWarehouse2Qty::getSku)
					.collect(Collectors.toList());
			ProductDispriceSearch param = new ProductDispriceSearch();
			param.setSkuList(skuList);
			param.setWarehouseId(entrySet.getKey());
			// 查询到仓价
			List<ProductDispriceDto> disPriceList = disPriceMapper.getEditPriceList(param);
			// 防止空指针
			if (disPriceList != null && disPriceList.size() > 0) {
				Logger.info("查询到仓价参数：{}，结果数量：{}", param, disPriceList.size());
				for(ProductDispriceDto pdd : disPriceList){
					// {sku:"IF001", warehouseId:2024, arriveWarePrice:0.1, cost:0.2}
					map = Maps.newHashMap();
					map.put("sku", pdd.getSku());
					map.put("warehouseId", entrySet.getKey());
					map.put("arriveWarePrice", pdd.getArriveWarePrice());// 到仓价
					map.put("cost", pdd.getCost());
					list.add(map);
				}
			}
		}
		return list;
	}
	
	@Override
	public Double calculateTotalArriveWarePrice(List<SkuWarehouse2Qty> skuWarehouse2QtyList, boolean useCostIfAbsent) {
		// 按照仓库id来区分sku
		Map<Integer, List<SkuWarehouse2Qty>> skuWarehouse2QtyByWarehouseId = skuWarehouse2QtyList.stream().collect(Collectors.groupingBy(SkuWarehouse2Qty::getWarehouseId));
		
		// 总的到仓价
		BigDecimal tawPrice = new BigDecimal(0);
		
		// 分仓计算
		for (Map.Entry<Integer, List<SkuWarehouse2Qty>> entrySet : skuWarehouse2QtyByWarehouseId.entrySet()) {
			// sku集合
			List<String> skuList = entrySet.getValue().stream().map(SkuWarehouse2Qty::getSku).collect(Collectors.toList());
			ProductDispriceSearch param = new ProductDispriceSearch();
			param.setSkuList(skuList);
			param.setWarehouseId(entrySet.getKey());
			// 查询到仓价
			List<ProductDispriceDto> disPriceList = disPriceMapper.getEditPriceList(param);
			// 防止空指针
			if(disPriceList!=null && disPriceList.size()>0){
				Logger.info("查询到仓价参数：{}，结果数量：{}",param,disPriceList.size());
				// 将结果进行按照sku来区分
				Map<String, ProductDispriceDto> sku2Dto = disPriceList.stream()
						.collect(Collectors.toMap(ProductDispriceDto::getSku, Function.identity()));
				
				// 计算一个仓库下sku集合的总到仓价，并累加到一个不区分仓库的总到仓价
				tawPrice = tawPrice.add( entrySet.getValue().stream()
						.map(skuWarehouse2Qty -> {
							// 计算一个sku的总到仓价
							return sku2Dto.get(skuWarehouse2Qty.getSku())
									.calculateArriveWarePrice(skuWarehouse2Qty.getQty(), useCostIfAbsent);
						}).reduce(new BigDecimal(0), (x, y) -> x.add(y)) );
			}
		}
		return tawPrice.doubleValue();
	}

	/**
	 * 过滤掉 不在权限集合中的字段
	 * 
	 * @author zbc
	 * @since 2016年11月3日 下午12:05:39
	 */
	private Map<String, Double> filterFiled(Map<String, Double> chageMap,
			String type) {
		if(type==null){
			return chageMap;
		}

		List<String> filterList = FILTERTMAP.get(type);
		Map<String, Double> newMap = Maps.newHashMap();
		String key = null;
		for (Map.Entry<String, Double> entry : chageMap.entrySet()) {
			key = entry.getKey();
			if (filterList.contains(key)) {
				newMap.put(key, chageMap.get(key));
			}
		}
		return newMap;
	}

	@Override
	public Map<String, Object> read(JsonNode node,
			Map<String, Warehouse> warehouse_id_mapping,
			Map<Integer, List<String>> category_sku_mapping,
			Map<Integer, CategoryBase> categoryName) {
		Map<String, Object> result = Maps.newHashMap();
		try {
			ProductDispriceSearch searchDto = JsonFormatUtils.jsonToBean(
					node.toString(), ProductDispriceSearch.class);
			int page = searchDto.getPageNo() != null ? searchDto.getPageNo()
					: 1;
			int pageSize = searchDto.getPageSize() != null ? searchDto
					.getPageSize() : 10;
			List<ProductDispriceDto> list = Lists.newArrayList();
			Page<ProductDispriceDto> pages = null;
			int total = 0;
			if (null != searchDto.getCategoryId()) {
				List<String> skuList = getSkuList(searchDto.getCategoryId());
				if (skuList.size() < 1) {
					pages = new Page<ProductDispriceDto>(list, total, page,pageSize);
					result.put("suc", true);
					result.put("pages", pages);
					return result;
				}
				searchDto.setSkuList(skuList);
			}
			list = disPriceMapper.getProductDisPrice(searchDto);
			total = disPriceMapper.getProductDisPriceCount(searchDto);
			Warehouse ware = null;
			// 设置类目信息
			for (Integer catId : category_sku_mapping.keySet()) {
				for (ProductDispriceDto pri : list) {
					// 设置仓库名称
					ware = warehouse_id_mapping.get(pri.getDisStockId()
							.toString());
					if (ware != null) {
						pri.setWarehoseName(ware.getWarehouseName());
					}
					if (category_sku_mapping.get(catId).contains(pri.getSku())) {
						pri.setCategoryName(categoryName.get(catId).getCname());
						pri.setCategoryId(catId);
						continue;
					}
				}
			}
			pages = new Page<ProductDispriceDto>(list, total, page,pageSize);
			result.put("suc", true);
			result.put("pages", pages);
		} catch (Exception e) {
			result.put("suc", false);
			result.put("msg", "获取定价列表数据异常");
			Logger.error("获取定价列表失败", e);
		}
		return result;
	}

	@Override
	public List<String> getSkuList(Integer cateId) {
		Set<Integer> all = new HashSet<>();
		List<Integer> list = new ArrayList<>();
		list.add(cateId);
		vcService.queryChild(list, all);
		return vcService.getSkuLists(new ArrayList<>(all));
	}

	@Override
	public List<ProductPriceRule> readrule() {
		return productPriceRuleMapper.selectAll();
	}

	@Override
	public Map<String, Object> updaterule(JsonNode node,
			Map<String, Warehouse> warehouse_id_mapping,
			Map<Integer, List<String>> category_sku_mapping,
			Map<Integer, CategoryBase> categoryName) {
		// 更新内容
		Map<String, Object> result = Maps.newHashMap();
		try {
			ProductPriceRule rule = JsonFormatUtils.jsonToBean(node.toString(),
					ProductPriceRule.class);
			ProductPriceRule oldRule = productPriceRuleMapper
					.selectByPrimaryKey(rule.getId());
			if (rule.getStatus().equals(oldRule.getStatus())
					&& rule.getDefaultFactor().equals(
							oldRule.getDefaultFactor())) {
				result.put("suc", true);
				result.put("msg", "没有任何修改");
				result.put("rule", oldRule);
				return result;
			}
			rule.setLastOperatorTime(new Date());
			boolean flag = productPriceRuleMapper
					.updateByPrimaryKeySelective(rule) > 0;
			// 更新价格
			rule = productPriceRuleMapper.selectByPrimaryKey(rule.getId());
			// 更新成功
			if (flag) {
				// 生成作记录
				OperateProductPriceRule ruleRecord = new OperateProductPriceRule();
				ruleRecord.setFactor(rule.getDefaultFactor());
				ruleRecord.setOperate(rule.getLastOperator());
				ruleRecord.setOperateTime(rule.getLastOperatorTime());
				ruleRecord.setPriceClassification(rule
						.getPriceClassificationDesc());
				ruleRecord.setStatus(rule.getStatus());
				ruleRecord.setStatusDesc(rule.getStatus() ? "应用中" : "未应用");
				ruleRecord.setPriceClassificationId(rule.getId());
				operateProductPriceRuleMapper.insertSelective(ruleRecord);
			}
			int count = 0;
			// 如果为应用中 更新价格
			if (rule.getStatus()) {
				Map<String, String> subs = null;
				String fieldName = rule.getFieldName();
				ProductDispriceSearch dto = new ProductDispriceSearch();
				dto.setOperateType(rule.getPriceClassification());
				List<ProductDispriceDto> dtoList = disPriceMapper
						.getEditPriceList(dto);
				List<ProductDisprice> list = Lists.newArrayList();
				List<OperateProductPrice> priceRecords = Lists.newArrayList();
				OperateProductPrice priceRecord = null;
				ProductDisprice price = null;
				Warehouse ware = null;
				for (ProductDispriceDto priceDto : dtoList) {
					priceRecord = new OperateProductPrice();
					price = new ProductDisprice();
					subs = Maps.newHashMap();
					subs.put("\\$p", invokeGet(priceDto, fixStr(fieldName))
							.toString());
					subs.put("\\$f", rule.getDefaultFactor().toString());
					Double changePrice = runJS(subs, rule.getcRule());
					price.setId(priceDto.getId());
					price.setOperateDate(new Date());
					// 设置值
					invokeSet(price, fixStr(rule.getPriceClassification()),
							changePrice);
					list.add(price);
					priceRecord.setPriceIid(priceDto.getId());
					priceRecord.setProductTitle(priceDto.getProductTitle());
					ware = warehouse_id_mapping.get(priceDto.getDisStockId()
							.toString());
					priceRecord.setWarehouseId(priceDto.getDisStockId());
					priceRecord.setWarehouseName(ware.getWarehouseName());
					priceRecord.setSku(priceDto.getSku());
					for (Integer catId : category_sku_mapping.keySet()) {
						if (category_sku_mapping.get(catId).contains(
								priceRecord.getSku())) {
							priceRecord.setCategoryName(categoryName.get(catId)
									.getCname());
							priceRecord.setCategoryId(catId);
							break;
						}
					}
					priceRecord.setFieldName(rule.getPriceClassification());
					priceRecord.setOperatorTime(rule.getLastOperatorTime());
					priceRecord.setOperator(rule.getLastOperator());
					priceRecord.setChangePrice(changePrice);
					priceRecord.setOperateDesc(rule
							.getPriceClassificationDesc());
					priceRecords.add(priceRecord);
					count++;
				}
				// 批量更新价格
				boolean priceFlag = disPriceMapper.batchUpdate(list) > 0;
				if (priceFlag) {
					// 批量插入价格更新记录
					operateProductPriceMapper.batchInsert(priceRecords);
				}
			}
			result.put("suc", true);
			if (count > 0) {
				result.put("msg", "设置价格系数成功,更新[" + count + "]个商品价格");
			} else {
				result.put("msg", "设置价格系数成功");
			}
			result.put("rule", rule);
		} catch (Exception e) {
			result.put("suc", false);
			result.put("msg", "设置价格系数异常");
			Logger.info("设置价格系数异常", e);
		}
		return result;
	}

	/**
	 * 根据变量名获取 值
	 * 
	 * @author zbc
	 * @since 2016年7月29日 下午5:16:43
	 */
	@SuppressWarnings("rawtypes")
	public static Object invokeGet(Object obj, String fieldName) {
		Object value = null;
		try {
			Class cla = (Class) obj.getClass();
			Method[] methods = cla.getMethods();
			for (Method method : methods) {
				if (method.getName().startsWith("get")
						&& method.getName().contains(fieldName)) {
					value = method.invoke(obj);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 根据变量名设置值
	 * 
	 * @author zbc
	 * @since 2016年7月29日 下午5:16:43
	 */
	@SuppressWarnings("rawtypes")
	public void invokeSet(Object obj, String fieldName, Double changePrice) {
		try {
			Class cla = (Class) obj.getClass();
			Method[] methods = cla.getMethods();
			for (Method method : methods) {
				if (method.getName().startsWith("set")
						&& method.getName().contains(fieldName)) {
					method.invoke(obj, changePrice);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 首字母大写
	 * 
	 * @author zbc
	 * @since 2016年7月29日 下午9:06:21
	 */
	public String fixStr(String str) {
		StringBuilder sb = new StringBuilder(str);
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		return str = sb.toString();
	}

	/**
	 * 计算公式
	 * 
	 * @author zbc
	 * @since 2016年7月29日 下午5:10:07
	 */
	@Override
	public Double runJS(Map<String, String> subs, String ruleValue) {
		ScriptEngineManager manager = new ScriptEngineManager(
				ClassLoader.getSystemClassLoader());
		ScriptEngine engine = manager.getEngineByName("js");
		try {
			subs.put("ceil", "Math.ceil");
			subs.put("floor", "Math.floor");
			for (String subKey : subs.keySet()) {
				ruleValue = ruleValue.replaceAll(subKey, subs.get(subKey));
			}
			Double cost = new Double(String.valueOf(engine.eval(ruleValue)));
			DoubleCalculateUtils duti = new DoubleCalculateUtils(cost);
			// 四舍五入 保留两位
			return Double.valueOf(f.format(duti.doubleValue()));
		} catch (Exception e) {
			Logger.error("****************************************************************");
			Logger.error("* Run Js Error !");
			Logger.error("* Rule: " + ruleValue);
			Logger.error("****************************************************************");
			Logger.error("runJS Exception Details", e);
			return null;
		}
	}

	@Override
	public Map<String, Object> getDisprice(Integer id,
			Map<Integer, List<String>> category_sku_mapping) {
		Map<String, Object> res = Maps.newHashMap();
		try {
			ProductDispriceDto pri = disPriceMapper.selectByPrimaryKey(id);
			for (Integer catId : category_sku_mapping.keySet()) {
				if (category_sku_mapping.get(catId).contains(pri.getSku())) {
					pri.setCategoryId(catId);
					break;
				}
			}
			
			// 系数map
			Map<String, Double> factorMap = Maps.newHashMap();
			// 利润值map
			Map<String, Double> profitMap = Maps.newHashMap();
			// 根据系数计算的 价格map
			Map<String, Double> priceMap = Maps.newHashMap();
			// 根据利润计算的 价格map
			Map<String, Double> fitPriceMap = Maps.newHashMap();
			Map<String, String> subs = null;
			List<ProductPriceRule> rules = productPriceRuleMapper.selectAll();
			Map<String, ProductPriceRule> ruleMap = Maps.uniqueIndex(rules,
					vc -> vc.getPriceClassification());
			ProductPriceRule rule = null;
			String fieldName = null;
			Double factor, profit;
			List<ProductPriceFactor> flist = null;
			
			List<Integer> ids = Lists.newArrayList();
			ids.add(pri.getId());
			flist = factorMapper.getByPriceIds(ids);
			if (flist.size() > 0) {
				for (ProductPriceFactor fc : flist) {
					factorMap.put(fc.getKind(), fc.getFactor());
					profitMap.put(fc.getKind(), fc.getProfit());
				}
				// 根据最后一次设置的系数 计算价格
				for (String key : factorMap.keySet()) {
					factor = factorMap.get(key);
					rule = ruleMap.get(key);
					fieldName = rule.getFieldName();
					if (factor == null
							|| invokeGet(pri, fixStr(fieldName)) == null) {
						continue;
					}
					subs = Maps.newHashMap();
					subs.put("\\$p", invokeGet(pri, fixStr(fieldName))
							.toString());
					subs.put("\\$f", factor.toString());
					priceMap.put(key, runJS(subs, rule.getcRule()));
				}
				// change by zbc 2016-10-24 根据最后一次设置的利润值计算价格
				for (String key : profitMap.keySet()) {
					profit = profitMap.get(key);
					rule = ruleMap.get(key);
					fieldName = rule.getFieldName();
					if (profit == null
							|| invokeGet(pri, fixStr(fieldName)) == null) {
						continue;
					}
					subs = Maps.newHashMap();
					subs.put("\\$p", invokeGet(pri, fixStr(fieldName))
							.toString());
					subs.put("\\$f", profit.toString());
					fitPriceMap.put(key, runJS(subs, rule.getProfitRule()));
				}

			}
			res.put("suc", true);
			res.put("pri", pri);
			res.put("factorMap", factorMap);
			res.put("priceMap", priceMap);
			res.put("fitPriceMap", fitPriceMap);
			res.put("fitMap", profitMap);
		} catch (Exception e) {
			res.put("suc", false);
			res.put("msg", "获取价格异常");
			Logger.info("获取价格异常", e);
		}

		return res;
	}

	@Override
	public Map<String, Object> setDisprice(JsonNode node, String type) {
		Map<String, Object> result = Maps.newHashMap();
		try {
			OperateProductPrice operate = JsonFormatUtils.jsonToBean(
					node.toString(), OperateProductPrice.class);
			ProductDisprice price = disPriceMapper.selectByPrimaryKey(operate
					.getPriceIid());
			price.setOperateDate(new Date());
			Map<String, Double> changeMap = filterFiled(operate.getChangeMap(),
					type);
			Double oldPrice = null;
			Double changePrice = null;
			int count = 0;
			List<String> changeField = Lists.newArrayList();
			for (String key : changeMap.keySet()) {
				oldPrice = (Double) invokeGet(price, fixStr(key));
				changePrice = changeMap.get(key);
				if (oldPrice != null && oldPrice.equals(changePrice)) {
					continue;
				} else if (oldPrice == null && changePrice == null) {
					continue;
				}
				changeField.add(key);
				count++;
				invokeSet(price, fixStr(key), changeMap.get(key));
			}
			if (count <= 0) {
				result.put("suc", true);
				result.put("msg", "没有任何修改");
				return result;
			}
			boolean flag = disPriceMapper.updateByPrimaryKey(price) > 0;
			if (flag) {
				List<OperateProductPrice> operateList = Lists.newArrayList();
				operate.setOperatorTime(new Date());
				OperateProductPrice record = null;
				for (String key : changeField) {
					record = new OperateProductPrice();
					operate.setOperateDesc(Constant.PRICE_TYPE_MAP.get(key));
					operate.setChangePrice(changeMap.get(key));
					BeanUtils.copyProperties(operate, record);
					record.setFieldName(key);
					record.setRemark(operate.getRemark());
					operateList.add(record);
				}
				// 批量插入价格更新记录
				operateProductPriceMapper.batchInsert(operateList);
				// change by zbc 同步价格到购物车
				try {
					Map<String, Object> req = Maps.newHashMap();
					req.put("reqStr", node);
					Logger.debug("购物车价格修改请求参数[{}]:", req.get("reqStr"));
					String url = HttpUtil.B2BBASEURL
							+ "/cart/updatecartprice";
					String retStr = HttpUtil.doPost(url, req, "UTF-8", false);
					Logger.info("购物车商品价格同步返回值[{}]:", retStr);
				} catch (Exception e) {
					Logger.info("同步购物车价格异常", e);
					;
				}

//				{//es维护
//					ProductLite prodLite = new ProductLite();
//					prodLite.setCsku(price.getSku());
//					prodLite.setWarehouseId(price.getDisStockId());
//					
//					ProductLiteDoc doc = esService.getProdDocFromEs(prodLite);
//					//更新es中的数据
//					doc.setFtzPrice(price.getFtzPrice());
//					doc.setVipPrice(price.getVipPrice());
//					doc.setDistributorPrice(price.getDistributorPrice());
//					doc.setSupermarketPrice(price.getSupermarketPrice());
//					doc.setElectricityPrices(price.getElectricityPrices());
//					doc.setProposalRetailPrice(price.getProposalRetailPrice());
//					esService.update(doc);
//				}
			}
			result.put("suc", true);
			result.put("msg", "设置成功");
		} catch (Exception e) {
			result.put("suc", false);
			result.put("msg", "设置失败");
			Logger.info("设置价格异常", e);
		}
		return result;
	}

	@Override
	public List<OperateProductPrice> readPriceLog(Integer priceIid, String type) {
		return operateProductPriceMapper.getRecordList(priceIid,
				FILTERTMAP.get(type));
	}

	@Override
	public Map<String, Object> batchSetPrice(JsonNode node,
			Map<String, Warehouse> warehouse_id_mapping,
			Map<Integer, List<String>> category_sku_mapping,
			Map<Integer, CategoryBase> categoryName, String type) {
		Map<String, Object> result = Maps.newHashMap();
		try {
			ProductDispriceSearch dto = null;
			OperateProductPrice operate = null;
			List<ProductDispriceDto> dtoList = Lists.newArrayList();
			// 用于查询价格系数
			List<Integer> idList = Lists.newArrayList();

			// 搜索条件下的所有商品
			if (node.has("idAll") && node.get("idAll").asBoolean()) {
				dto = JsonFormatUtils.jsonToBean(node.toString(),
						ProductDispriceSearch.class);
				operate = new OperateProductPrice();
				operate.setChangeFactorMap(dto.getChangeFactorMap());
				operate.setOperator(dto.getOperator());
				operate.setSetType(dto.getSetType());
				if (null != dto.getCategoryId()) {
					List<String> skuList = getSkuList(dto.getCategoryId());
					if (skuList.size() < 1) {
						result.put("suc", true);
						result.put("msg", "一键设置价格成功,更新[0]个商品价格");
						return result;
					}
					dto.setSkuList(skuList);
				}
				dtoList = disPriceMapper.getProductDisPrice(dto);
				for (ProductDispriceDto d : dtoList) {
					idList.add(d.getId());
				}
			} else {
				operate = JsonFormatUtils.jsonToBean(node.toString(),
						OperateProductPrice.class);
				dto = new ProductDispriceSearch();
				dto.setIdList(operate.getPriceIidList());
				dto.setRemark(operate.getRemark());
				dtoList = disPriceMapper.getEditPriceList(dto);
				idList.addAll(dto.getIdList());
			}
			List<ProductPriceFactor> flist = factorMapper.getByPriceIds(idList);
			Map<String, ProductPriceFactor> fmap = Maps.newHashMap();
			for (ProductPriceFactor f : flist) {
				fmap.put(f.getPriceId() + "||" + f.getKind(), f);
			}
			List<ProductPriceFactor> aflist = Lists.newArrayList();
			List<ProductPriceFactor> uflist = Lists.newArrayList();
			ProductPriceFactor prifactor = null;
			List<ProductDisprice> list = Lists.newArrayList();
			ProductDisprice price = null;
			// change by zbc 过滤掉不改设置的 字段
			Map<String, Double> factorMap = filterFiled(
					operate.getChangeFactorMap(), type);
			List<ProductPriceRule> ruleList = productPriceRuleMapper
					.selectAll();
			Map<String, ProductPriceRule> ruleMap = Maps.uniqueIndex(ruleList,
					vc -> vc.getPriceClassification());
			ProductPriceRule rule = null;
			// 更新参数计数，判断是否符合更新条件，更新参数小于1不做更新处理
			int updateCount;
			String fieldName;
			Double factor;
			Double changePrice;
			Map<String, String> subs = null;
			OperateProductPrice priceRecord = null;
			Warehouse ware = null;
			operate.setOperatorTime(new Date());
			List<OperateProductPrice> priceRecords = Lists.newArrayList();
			int count = 0;
			for (ProductDispriceDto priceDto : dtoList) {
				updateCount = 0;
				price = new ProductDisprice();
				for (String key : factorMap.keySet()) {
					rule = ruleMap.get(key);
					fieldName = rule.getFieldName();
					factor = factorMap.get(key);
					// 更新变量不为空
					if (invokeGet(priceDto, fixStr(fieldName)) != null) {
						subs = Maps.newHashMap();
						subs.put("\\$p", invokeGet(priceDto, fixStr(fieldName))
								.toString());
						subs.put("\\$f", factor.toString());
						// change by zbc 2016-11-08 需求：利润值设置
						// 设置标识 setType:FR PF
						if (operate.getSetType() != null
								&& operate.getSetType().equals("PF")) {
							// 如果设置 利润值
							changePrice = runJS(subs, String.valueOf(invokeGet(
									rule, "ProfitRule")));
						} else {// 如果设置 系数 ;
							changePrice = runJS(subs,
									String.valueOf(invokeGet(rule, "cRule")));
						}
						invokeSet(price, fixStr(key), changePrice);
						updateCount++;
						priceRecord = new OperateProductPrice();
						priceRecord.setPriceIid(priceDto.getId());
						priceRecord.setProductTitle(priceDto.getProductTitle());
						ware = warehouse_id_mapping.get(priceDto
								.getDisStockId().toString());
						priceRecord.setWarehouseId(priceDto.getDisStockId());
						priceRecord.setWarehouseName(ware.getWarehouseName());
						priceRecord.setSku(priceDto.getSku());
						for (Integer catId : category_sku_mapping.keySet()) {
							if (category_sku_mapping.get(catId).contains(
									priceRecord.getSku())) {
								priceRecord.setCategoryName(categoryName.get(
										catId).getCname());
								priceRecord.setCategoryId(catId);
								break;
							}
						}
						priceRecord.setFieldName(key);
						priceRecord.setOperatorTime(operate.getOperatorTime());
						priceRecord.setOperator(operate.getOperator());
						priceRecord.setChangePrice(changePrice);
						priceRecord.setOperateDesc(rule
								.getPriceClassificationDesc());
						priceRecord.setRemark(dto.getRemark());
						priceRecords.add(priceRecord);
						prifactor = fmap.get(priceDto.getId() + "||" + key);
						if (prifactor != null) {
							if (operate.getSetType() != null
									&& operate.getSetType().equals("PF")) {
								// 如果设置利润值
								invokeSet(prifactor, "Profit", factor);
							} else {
								// 如果设置系数
								invokeSet(prifactor, "Factor", factor);
							}
							prifactor.setUpdateDate(new Date());
							uflist.add(prifactor);
						} else {
							prifactor = new ProductPriceFactor();
							if (operate.getSetType() != null
									&& operate.getSetType().equals("PF")) {
								// 如果设置利润值
								invokeSet(prifactor, "Profit", factor);
							} else {
								// 如果设置系数
								invokeSet(prifactor, "Factor", factor);
							}
							prifactor.setKind(key);
							prifactor.setPriceId(priceDto.getId());
							aflist.add(prifactor);
						}
					}
				}
				if (updateCount > 0) {
					count++;
					price.setId(priceDto.getId());
					price.setOperateDate(operate.getOperatorTime());
					list.add(price);

//					{//es ---add by ye_ziran
//						ProductLite prodLite = new ProductLite();
//						prodLite.setCsku(priceDto.getSku());
//						prodLite.setWarehouseId(priceDto.getDisStockId());
//
//						ProductLiteDoc doc = esService.getProdDocFromEs(prodLite);
//						//更新es中的数据
//						if(null != price.getFtzPrice()){
//							doc.setFtzPrice(price.getFtzPrice());
//						}
//						if(null != price.getVipPrice()){
//							doc.setVipPrice(price.getVipPrice());
//						}
//						if(null != price.getDistributorPrice()){
//							doc.setDistributorPrice(price.getDistributorPrice());
//						}
//						if(null != price.getSupermarketPrice()){
//							doc.setSupermarketPrice(price.getSupermarketPrice());
//						}
//						if(null != price.getElectricityPrices()){
//							doc.setElectricityPrices(price.getElectricityPrices());
//						}
//						if(null != price.getProposalRetailPrice()){
//							doc.setProposalRetailPrice(price.getProposalRetailPrice());
//						}
//						docList.add(doc);
//					}

				}
			}
			// change by zbc 由于批量插入数量过多时会出错，所以拆分为多个集合
			// 批量更新价格
			Boolean priceFlag = true;
			List<List<ProductDisprice>> subPriceList = createList(list,CU_MAX_LENGTH);
			for(List<ProductDisprice> sub :subPriceList){
				if(disPriceMapper.batchUpdate(sub) <= 0){
					priceFlag = false;
				}
			}
			if (priceFlag) {
				// change by zbc 由于批量插入数量过多时会出错，所以拆分为多个集合
				// 批量插入价格更新记录
				List<List<OperateProductPrice>> subPriceRecords = createList(priceRecords,CU_MAX_LENGTH);
				subPriceRecords.forEach(e->{
					operateProductPriceMapper.batchInsert(e);
				});
				if(uflist.size() > 0) {
					// 批量更新
					List<List<ProductPriceFactor>> subUflist = createList(uflist, CU_MAX_LENGTH);
					subUflist.forEach(e->{
						factorMapper.batchUpdate(e);
					});
				}
				if (aflist.size() > 0) {
					// 批量插入
					List<List<ProductPriceFactor>> subAflist = createList(aflist, CU_MAX_LENGTH);
					subAflist.forEach(e->{
						factorMapper.batchInsert(e);
					});
				}
				//es维护
//				esService.update(docList);
			}
			result.put("suc", true);
			result.put("msg", "一键设置价格成功,更新[" + count + "]个商品价格");
			Logger.info("一键设置价格成功,更新[" + count + "]个商品价格");
		} catch (Exception e) {
			result.put("suc", false);
			result.put("msg", "一键设置价格失败");
			Logger.info("一键设置价格异常", e);
		}
		return result;
	}

	
	/**
	 * @param targe 集合
	 * @param size  子集合大小
	 * 拆分集合
	 * @author zbc
	 * @since 2017年3月9日 下午5:14:58
	 */
	private static <T> List<List<T>>  createList(List<T> targe,int size) {  
        List<List<T>> listArr = Lists.newArrayList();  
        //获取被拆分的数组个数  
        int arrSize = targe.size()%size==0?targe.size()/size:targe.size()/size+1;  
        for(int i=0;i<arrSize;i++) {  
            List<T>  sub = Lists.newArrayList();  
            //把指定索引数据放入到list中  
            for(int j=i*size;j<=size*(i+1)-1;j++) {  
                if(j<=targe.size()-1) {  
                    sub.add(targe.get(j));  
                }  
            }  
            listArr.add(sub);  
        }  
        return listArr;  
    }  
	
	@Override
	public Map<String, Object> readRecord(JsonNode node,
			Map<Integer, List<String>> category_sku_mapping) {
		Map<String, Object> result = Maps.newHashMap();
		try {
			OperatePriceLogDto search = JsonFormatUtils.jsonToBean(
					node.toString(), OperatePriceLogDto.class);
			List<OperateProductPrice> list = Lists.newArrayList();
			int total = 0;
			int pageNo = search.getPageNo() != null ? search.getPageNo() : 1;
			int pageSize = search.getPageSize() != null ? search.getPageSize()
					: 10;
			Page<OperateProductPrice> pages = null;
			if (search.getCategoryId() != null) {
				List<String> skuList = category_sku_mapping.get(search
						.getCategoryId());
				if (skuList.size() < 1) {
					pages = new Page<OperateProductPrice>(list, total, pageNo,
							pageSize);
					result.put("suc", true);
					result.put("pages", pages);
					return result;
				}
				search.setSkuList(skuList);
			}
			search.setfNameList(FILTERTMAP.get(search.getType()));
			list = operateProductPriceMapper.getRecordPages(search);
			total = operateProductPriceMapper.getRecordCount(search);
			pages = new Page<OperateProductPrice>(list, total, pageNo, pageSize);
			result.put("suc", true);
			result.put("pages", pages);
		} catch (Exception e) {
			result.put("suc", false);
			result.put("msg", "获取价格操作日志失败");
			Logger.info("获取价格操作日志异常", e);
		}
		return result;
	}

	@Override
	public Map<String, Object> readRuleLog(JsonNode node) {
		Map<String, Object> res = Maps.newHashMap();
		try {
			OperateRuleDto search = JsonFormatUtils.jsonToBean(node.toString(),
					OperateRuleDto.class);
			List<OperateProductPriceRule> list = operateProductPriceRuleMapper
					.getLogPages(search);
			int total = 0;
			int pageNo = search.getPageNo() != null ? search.getPageNo() : 1;
			int pageSize = search.getPageSize() != null ? search.getPageSize()
					: 10;
			total = operateProductPriceRuleMapper.getLogCount(search);
			Page<OperateProductPriceRule> pages = new Page<OperateProductPriceRule>(
					list, total, pageNo, pageSize);
			res.put("suc", true);
			res.put("pages", pages);
		} catch (Exception e) {
			res.put("suc", false);
			res.put("msg", "分页操作日志失败");
			Logger.info("分页查询异常", e);
		}
		return res;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> b2cUpdatePrice(JsonNode node,
			Map<String, Warehouse> warehouse_id_mapping,
			Map<Integer, List<String>> category_sku_mapping,
			Map<Integer, CategoryBase> categoryName) {
		Map<String, Object> res = Maps.newHashMap();
		try {
			List<ProductPriceRule> rules = productPriceRuleMapper.selectAll();
			Map<String, ProductPriceRule> ruleMap = Maps.newHashMap();
			for (ProductPriceRule rule : rules) {
				if (rule.getDefaultFactor() != null) {
					ruleMap.put(rule.getPriceClassification(), rule);
				}
			}
			if (ruleMap.size() > 0) {
				Map<String, String> subs = null;
				Double changePrice = null;
				List<B2cSyncPriceDto> list = JsonFormatUtils.jsonToBean(
						node.toString(), List.class);
				List<ProductDispriceDto> edList = disPriceMapper
						.getB2cEditList(list);
				if (edList.size() < 1) {
					res.put("suc", false);
					res.put("msg", "b2c同步价格时,设置商品价格异常，找不到对应商品");
					return res;
				}
				// 计算设置字段数量
				int addCount;
				ProductPriceRule rule = null;
				ProductDisprice price = null;
				List<ProductDisprice> priceList = Lists.newArrayList();
				List<OperateProductPrice> priceRecords = Lists.newArrayList();
				Warehouse ware = null;
				OperateProductPrice priceRecord = null;
				int count = 0;
				for (ProductDispriceDto dto : edList) {
					addCount = 0;
					price = new ProductDisprice();
					price.setId(dto.getId());
					for (String key : ruleMap.keySet()) {
						rule = ruleMap.get(key);
						// 设置字段为空 变量不能为空
						if (invokeGet(dto, fixStr(key)) == null
								&& invokeGet(dto, fixStr(rule.getFieldName())) != null) {
							subs = Maps.newHashMap();
							invokeSet(
									price,
									fixStr(rule.getFieldName()),
									(Double) invokeGet(dto,
											fixStr(rule.getFieldName())));
							subs.put("\\$p",
									invokeGet(dto, fixStr(rule.getFieldName()))
											.toString());
							subs.put("\\$f", rule.getDefaultFactor().toString());
							changePrice = runJS(subs, rule.getcRule());
							invokeSet(price, fixStr(key), changePrice);
						}

					}
					for (String key : ruleMap.keySet()) {
						rule = ruleMap.get(key);
						if (invokeGet(dto, fixStr(key)) == null
								&& invokeGet(price, fixStr(rule.getFieldName())) != null) {
							subs = Maps.newHashMap();
							subs.put(
									"\\$p",
									invokeGet(price,
											fixStr(rule.getFieldName()))
											.toString());
							subs.put("\\$f", rule.getDefaultFactor().toString());
							changePrice = runJS(subs, rule.getcRule());
							invokeSet(price, fixStr(key), changePrice);
							addCount++;
							priceRecord = new OperateProductPrice();
							priceRecord.setPriceIid(dto.getId());
							priceRecord.setProductTitle(dto.getProductTitle());
							ware = warehouse_id_mapping.get(dto.getDisStockId()
									.toString());
							priceRecord.setWarehouseId(dto.getDisStockId());
							priceRecord.setWarehouseName(ware
									.getWarehouseName());
							priceRecord.setSku(dto.getSku());
							for (Integer catId : category_sku_mapping.keySet()) {
								if (category_sku_mapping.get(catId).contains(
										priceRecord.getSku())) {
									priceRecord.setCategoryName(categoryName
											.get(catId).getCname());
									priceRecord.setCategoryId(catId);
									break;
								}
							}
							priceRecord.setFieldName(key);
							priceRecord.setOperatorTime(new Date());
							priceRecord.setOperator("system");
							priceRecord.setChangePrice(changePrice);
							priceRecord.setOperateDesc(rule
									.getPriceClassificationDesc());
							priceRecords.add(priceRecord);
						}
					}
					if (addCount > 0) {
						count++;
						price.setId(dto.getId());
						price.setOperateDate(new Date());
						priceList.add(price);
					}
				}
				// 批量更新价格
				boolean priceFlag = disPriceMapper.batchUpdate(priceList) > 0;
				if (priceFlag) {
					// 批量插入价格更新记录
					operateProductPriceMapper.batchInsert(priceRecords);
				}
				res.put("suc", true);
				res.put("msg", "b2c同步价格时,根据默认系数更新[" + count + "]个商品价格");
				Logger.info("b2c同步价格时,根据默认系数更新[" + count + "]个商品价格");
			}
		} catch (Exception e) {
			res.put("suc", false);
			res.put("msg", "b2c同步价格时,根据默认系数更新商品价格异常");
			Logger.info("b2c同步价格时,根据默认系数更新商品价格异常", e);
		}
		return res;
	}

	@Override
	public Map<String, Object> addPriceFactor(JsonNode node,
			Map<String, Warehouse> warehouse_id_mapping,
			Map<Integer, List<String>> category_sku_mapping,
			Map<Integer, CategoryBase> categoryName) {
		Map<String, Object> res = Maps.newHashMap();
		try {
			// 计算设置字段数量
			int count = 0;
			ProductPriceFactorDto factorDto = JsonFormatUtils.jsonToBean(
					node.toString(), ProductPriceFactorDto.class);
			ProductPriceCategoryBrand catebrand = null;
			boolean flag = false;
			boolean updateFlag = factorDto.getId() != null;// 判断是更新还是新增
			if (updateFlag) {
				catebrand = cateBrandMapper.selectByPrimaryKey(factorDto
						.getId());
				catebrand.setStatus(factorDto.getStatus());
				catebrand.setLastOperator(factorDto.getLastOperator());
				catebrand.setLastOperatorTime(new Date());
				flag = cateBrandMapper.updateByPrimaryKeySelective(catebrand) > 0;
			} else {
				catebrand = new ProductPriceCategoryBrand();
				catebrand.setBrand(factorDto.getBrand());
				catebrand.setCategoryId(factorDto.getCategoryId());
				ProductPriceCategoryBrand repeatCatebrand = cateBrandMapper
						.select(catebrand);
				if (repeatCatebrand != null) {
					res.put("msg", false);
					res.put("msg", "该数据已经存在，无需重复添加");
					return res;
				}
				catebrand.setCategoryName(categoryName.get(
						factorDto.getCategoryId()).getCname());
				catebrand.setStatus(factorDto.getStatus());
				catebrand.setLastOperator(factorDto.getLastOperator());
				catebrand.setLastOperatorTime(new Date());
				flag = cateBrandMapper.insertSelective(catebrand) > 0;
			}
			if (flag) {
				ProductPriceFactor pricefactor = null;
				List<ProductPriceFactor> ulist = Lists.newArrayList();
				List<ProductPriceFactor> alist = Lists.newArrayList();
				Map<String, Double> factorMap = factorDto.getFactorMap();

				List<OperateProductPriceRule> recordList = Lists.newArrayList();
				OperateProductPriceRule ruleRecord = new OperateProductPriceRule();
				ruleRecord.setBrand(catebrand.getBrand());
				// 生成作记录
				ruleRecord.setOperate(catebrand.getLastOperator());
				ruleRecord.setOperateTime(catebrand.getLastOperatorTime());
				ruleRecord.setStatus(catebrand.getStatus());
				ruleRecord.setStatusDesc(catebrand.getStatus() ? "应用中" : "未应用");
				ruleRecord.setCategoryBrandId(catebrand.getId());
				ruleRecord.setCategoryId(catebrand.getCategoryId());
				ruleRecord.setCategoryName(catebrand.getCategoryName());
				ruleRecord.setBrand(catebrand.getBrand());
				List<ProductPriceFactor> fList = factorMapper
						.getBybBrandId(catebrand.getId());
				Map<String, ProductPriceFactor> exitMap = Maps.uniqueIndex(
						fList, v -> v.getKind());
				ProductPriceFactor oldFactor = null;
				OperateProductPriceRule record = null;
				Double factor;
				for (String key : factorMap.keySet()) {
					factor = factorMap.get(key);
					oldFactor = exitMap.get(key);
					if (oldFactor == null && factor == null) {
						continue;
					}
					factor = factor != null ? Double.valueOf(factor) : null;
					if (oldFactor != null) {
						if (oldFactor.getFactor() == null) {
							if (factor == null) {
								continue;
							}
						} else {
							if (factor != null
									&& factor.equals(oldFactor.getFactor())) {
								continue;
							}
						}
						oldFactor.setFactor(factor);
						ulist.add(oldFactor);
					} else {
						pricefactor = new ProductPriceFactor();
						pricefactor.setCategoryBrandId(catebrand.getId());
						pricefactor.setKind(key);
						pricefactor.setFactor(factor);
						alist.add(pricefactor);
					}
					record = new OperateProductPriceRule();
					ruleRecord.setFactor(factor);
					ruleRecord.setPriceClassification(key);
					ruleRecord.setPriceClassification(Constant.PRICE_TYPE_MAP
							.get(key));
					BeanUtils.copyProperties(ruleRecord, record);
					recordList.add(record);
				}
				if (ulist.size() > 0) {
					flag = factorMapper.batchUpdate(ulist) > 0;
				}
				if (alist.size() > 0) {
					flag = factorMapper.batchInsert(alist) > 0;
				}

				if (flag) {
					if (recordList.size() > 0) {
						operateProductPriceRuleMapper.batchInsert(recordList);
					}
					List<String> skuList = category_sku_mapping.get(catebrand
							.getCategoryId());
					if (skuList.size() > 0 && catebrand.getStatus()) {
						// 根据类目和品牌 获取所有商品 获取所有商品
						ProductDispriceSearch search = new ProductDispriceSearch();
						search.setSkuList(skuList);
						search.setBrand(catebrand.getBrand());
						List<ProductDispriceDto> priceDtos = disPriceMapper
								.getEditPriceList(search);
						List<ProductPriceRule> rules = productPriceRuleMapper
								.selectAll();
						String operate = catebrand.getLastOperator();
						Map<String, ProductPriceRule> ruleMap = Maps
								.uniqueIndex(rules,
										vc -> vc.getPriceClassification());
						;
						ProductPriceRule rule;
						ProductDisprice price;
						List<ProductDisprice> priceList = Lists.newArrayList();
						List<OperateProductPrice> priceRecords = Lists
								.newArrayList();
						Warehouse ware;
						int updateCount;
						String fieldName;
						Double changePrice;
						Map<String, String> subs = null;
						OperateProductPrice priceRecord = null;
						for (ProductDispriceDto priceDto : priceDtos) {
							price = new ProductDisprice();
							for (String key : ruleMap.keySet()) {
								rule = ruleMap.get(key);
								factor = factorMap.get(key);
								if (factor == null) {
									continue;
								}
								fieldName = rule.getFieldName();
								// 设置字段为空 变量不能为空
								if (invokeGet(priceDto, fixStr(key)) == null
										&& invokeGet(priceDto,
												fixStr(rule.getFieldName())) != null) {
									subs = Maps.newHashMap();
									fieldName = rule.getFieldName();
									invokeSet(
											price,
											fixStr(fieldName),
											(Double) invokeGet(priceDto,
													fixStr(fieldName)));
									subs.put(
											"\\$p",
											invokeGet(priceDto,
													fixStr(rule.getFieldName()))
													.toString());
									subs.put("\\$f", factor.toString());
									changePrice = runJS(subs, rule.getcRule());
									invokeSet(price, fixStr(key), changePrice);
								}
							}
							updateCount = 0;
							for (String key : factorMap.keySet()) {
								rule = ruleMap.get(key);
								fieldName = rule.getFieldName();
								factor = factorMap.get(key);
								if (factor == null) {
									continue;
								}
								// 更新变量不为空
								if (invokeGet(priceDto, fixStr(key)) == null
										&& invokeGet(price, fixStr(fieldName)) != null) {
									subs = Maps.newHashMap();
									subs.put("\\$p",
											invokeGet(price, fixStr(fieldName))
													.toString());
									subs.put("\\$f", factor.toString());
									changePrice = runJS(subs, rule.getcRule());
									invokeSet(price, fixStr(key), changePrice);
									updateCount++;
									priceRecord = new OperateProductPrice();
									priceRecord.setPriceIid(priceDto.getId());
									priceRecord.setProductTitle(priceDto
											.getProductTitle());
									ware = warehouse_id_mapping.get(priceDto
											.getDisStockId().toString());
									priceRecord.setWarehouseId(priceDto
											.getDisStockId());
									priceRecord.setWarehouseName(ware
											.getWarehouseName());
									priceRecord.setSku(priceDto.getSku());
									for (Integer catId : category_sku_mapping
											.keySet()) {
										if (category_sku_mapping.get(catId)
												.contains(priceRecord.getSku())) {
											priceRecord
													.setCategoryName(categoryName
															.get(catId)
															.getCname());
											priceRecord.setCategoryId(catId);
											break;
										}
									}
									priceRecord.setFieldName(key);
									priceRecord.setOperatorTime(new Date());
									priceRecord.setOperator(operate);
									priceRecord.setChangePrice(changePrice);
									priceRecord.setOperateDesc(rule
											.getPriceClassificationDesc());
									priceRecords.add(priceRecord);
								}
							}
							if (updateCount > 0) {
								count++;
								price.setId(priceDto.getId());
								price.setOperateDate(new Date());
								priceList.add(price);
							}
						}
						// 批量更新价格
						boolean priceFlag = disPriceMapper
								.batchUpdate(priceList) > 0;
						if (priceFlag) {
							// 批量插入价格更新记录
							operateProductPriceMapper.batchInsert(priceRecords);
						}
					}
				}
			}
			res.put("suc", flag);
			res.put("msg", "设置价格系数"
					+ (flag ? "成功更新[" + count + "]个商品价格" : "失败"));
		} catch (Exception e) {
			res.put("suc", false);
			res.put("msg", "设置价格系数异常");
			Logger.info("设置价格系数异常", e);
		}
		return res;
	}

	@Override
	public Map<String, Object> getPriceFactorList(JsonNode node) {
		Map<String, Object> res = Maps.newHashMap();
		try {
			ProductPriceFactorDto search = JsonFormatUtils.jsonToBean(
					node.toString(), ProductPriceFactorDto.class);
			List<ProductPriceCategoryBrand> list = cateBrandMapper
					.getPageList(search);
			int total = 0;
			int pageNo = search.getCurrPage() != null ? search.getCurrPage()
					: 1;
			int pageSize = search.getPageSize() != null ? search.getPageSize()
					: 10;
			total = cateBrandMapper.getPageCount(search);
			if (total > 0) {
				List<ProductPriceFactor> factorList = null;
				Map<String, Double> factorMap = null;
				for (ProductPriceCategoryBrand brand : list) {
					factorList = factorMapper.getBybBrandId(brand.getId());
					factorMap = Maps.newHashMap();
					for (ProductPriceFactor ft : factorList) {
						factorMap.put(ft.getKind(), ft.getFactor());
					}
					brand.setFactorMap(factorMap);
				}
			}
			Page<ProductPriceCategoryBrand> pages = new Page<ProductPriceCategoryBrand>(
					list, total, pageNo, pageSize);
			res.put("suc", true);
			res.put("pages", pages);
		} catch (Exception e) {
			res.put("suc", false);
			res.put("msg", "分页操作日志失败");
			Logger.info("分页查询异常", e);
		}
		return res;
	}

	@Override
	public Map<String, Object> initCateData() {
		Map<String, Object> res = Maps.newHashMap();
		try {
			List<CategoryBase> vcList = categoryBaseMapper.getInitCate();
			ProductPriceCategoryBrand cateBrand = null;
			List<ProductPriceCategoryBrand> list = Lists.newArrayList();
			int count = 0;
			if (vcList.size() > 0) {
				for (CategoryBase cb : vcList) {
					cateBrand = new ProductPriceCategoryBrand();
					cateBrand.setCategoryId(cb.getIid());
					cateBrand.setCategoryName(cb.getCname());
					list.add(cateBrand);
					count++;
				}
				cateBrandMapper.batchInsert(list);
			}
			res.put("suc", true);
			res.put("msg", "更新成功，获取<em>" + count + "</em>条数据！");
		} catch (Exception e) {
			res.put("suc", false);
			res.put("msg", "更新异常");
			Logger.info("初始化数据异常", e);
		}
		return res;
	}

	@Override
	public String setTypeForProducts(String param) {
		ObjectNode result = Json.newObject();
		JsonNode node = Json.parse(param);
		List<ProductDisprice> disprices = new ArrayList<ProductDisprice>();
		ProductDisprice disprice = null;
		String typeName = node.get("typeName").asText();
		Integer typeId = node.get("typeId").asInt();
		for (JsonNode json : node.get("products")) {
			disprice = new ProductDisprice();
			disprice.setTypeName(typeName);
			disprice.setTypeId(typeId);
			disprice.setSku(json.get("sku").asText());
			disprice.setDisStockId(json.get("warehouseId").asInt());
			disprices.add(disprice);
		}
		
		int flag = disPriceMapper.batchUpdateType(disprices);
		if (flag == 0) {
			result.put("suc", false);
			result.put("msg", "更新商品类型失败");
			return result.toString();
		}
		
		result.put("suc", true);
		return result.toString();
	}

	@Override
	public List<ProductDispriceDto> getExportProductDisPrice(ProductDispriceSearch searchDto,Map<String, Warehouse> warehouse_id_mapping,
															 Map<Integer, List<String>> category_sku_mapping,
															 Map<Integer, CategoryBase> categoryName) {


		List<ProductDispriceDto> list = Lists.newArrayList();
		if (null != searchDto.getCategoryId()) {
			List<String> skuList = getSkuList(searchDto.getCategoryId());
			if (skuList.size() < 1) {
				return list;
			}
			searchDto.setSkuList(skuList);
		}

		list = disPriceMapper.getProductDisPrice(searchDto);

		Warehouse ware = null;
		// 设置类目信息
		for (Integer catId : category_sku_mapping.keySet()) {
			for (ProductDispriceDto pri : list) {
				// 设置仓库名称
				ware = warehouse_id_mapping.get(pri.getDisStockId()
						.toString());
				if (ware != null) {
					pri.setWarehoseName(ware.getWarehouseName());
				}
				if (category_sku_mapping.get(catId).contains(pri.getSku())) {
					pri.setCategoryName(categoryName.get(catId).getCname());
					pri.setCategoryId(catId);
					continue;
				}
			}
		}

		List<String> skus = list.stream().filter(d -> d.getSku() != null && !d.getSku().equals("")).map(d -> d.getSku()).collect(Collectors.toList());

		Map<String,Object> param = Maps.newHashMap();
		param.put("account", "");
		if(skus!= null){
			param.put("skus", skus);
		}

		ObjectMapper map = new ObjectMapper();
		String searchCloudResultStr = HttpUtil.post(Json.toJson(param).toString(), HttpUtil.B2BBASEURL + "/inventory/cloud/searchProductCloudAndMicroInventory");

		//设置库存
		List<ProStockDto> stockList = Lists.newArrayList();
		try {
			stockList = map.readValue(searchCloudResultStr, new TypeReference<List<ProStockDto>>() {});
		} catch (IOException e) {
			Logger.error("------------>getExportProductDisPrice getCloudInventory error:{}", e);
		}

		for (ProductDispriceDto productDispriceDto : list) {
			Optional<ProStockDto> any = stockList.stream().filter(d -> d.getWarehouseId() != null
					&& d.getWarehouseId().intValue() == productDispriceDto.getDisStockId().intValue()
					&& d.getSku().equals(productDispriceDto.getSku())).findFirst();
			if (any.isPresent()) {
				productDispriceDto.setCloudStock(any.get().getCloudInventory());
			} else {
				Logger.info("---------------------------库存数据不存在：------>{}", productDispriceDto.getSku());
				productDispriceDto.setCloudStock(0);
			}
		}
		return list;
	}

	@Override
	public Map<String, Object> pageSearchClearancePrice(String string) {
		Map<String, Object> result = Maps.newHashMap();
		try {
			ProductDispriceSearch searchDto = JsonFormatUtils.jsonToBean(
					string, ProductDispriceSearch.class);
			int page = searchDto.getPageNo() != null ? searchDto.getPageNo()
					: 1;
			int pageSize = searchDto.getPageSize() != null ? searchDto
					.getPageSize() : 10;
			List<ClearancePriceDto> list = disPriceMapper.pageSearch(searchDto);
			int total = disPriceMapper.pageCount(searchDto);
			result.put("suc", true);
			result.put("pages", new Page<ClearancePriceDto>(list, total, page,pageSize));
			return result;
		} catch (Exception e) {
			Logger.error("获取清货价列表异常"+ e);
			result.put("suc", false);
			result.put("msg", "获取清货价列表异常");
			return result;
		}
		
	}

	@Override
	public List<ProductDisprice> getProductDispriceBySkuAndStockId(String sku, int stockId) {
		
		return disPriceMapper.getProductDispriceBySkuAndStockId(sku,stockId);
	}

	@Override
	public int updateClearancePrice(List<ProductDisprice> priceWaitUpdateLists) {
		
		return disPriceMapper.batchUpdateClearancePrice(priceWaitUpdateLists);
	}
}
