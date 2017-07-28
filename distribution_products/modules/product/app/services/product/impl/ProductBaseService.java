package services.product.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;

import dto.CommonExportDto;
import dto.ProdcutInventoryDataExportDto;
import dto.category.CategorySearchParamDto;
import dto.product.PageResultDto;
import dto.product.ProStockDto;
import dto.product.ProductLite;
import dto.product.ProductSearchParamDto;
import entity.category.CategoryBase;
import entity.contract.ContractQuotations;
import entity.marketing.DisSpriceGoods;
import entity.product.ExportSyncResult;
import entity.product.ProductBase;
import entity.product.ProductBaseLog;
import entity.product.Warehouse;
import event.ClearanceProductEvent;
import event.ExportEvent;
import extensions.InjectorInstance;
import mapper.category.CategoryBaseMapper;
import mapper.contract.ContractQuotationsMapper;
import mapper.marketing.DisSpriceGoodsMapper;
import mapper.product.ExportModelMapper;
import mapper.product.ExportSyncResultMapper;
import mapper.product.ProductBaseLogMapper;
import mapper.product.ProductBaseMapper;
import mapper.product.ProductImageMapper;
import mapper.product.ProductTranslateMapper;
import mapper.product.WarehouseMapper;
import play.Logger;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.libs.Json;
import services.product.IHttpService;
import services.product.IInventoryLockService;
import services.product.IProductBaseService;
import services.product.IProductEnquiryService;
import services.product.IVirtualCategoryService;
import util.product.BufferUtils;
import util.product.CommonExportUtils;
import util.product.ProductBaseLogOptTypes;
import util.product.StockUtil;
import valueobjects.product.ProductImages;

public class ProductBaseService implements IProductBaseService, IProductEnquiryService{

	@Inject
	private ProductBaseMapper productBaseMapper;
	@Inject
	private ProductBaseLogMapper productBaseLogMapper;
	@Inject
	private ProductImageMapper imageMapper;
	@Inject
	private CategoryBaseMapper cateBaseMapper;
	@Inject 
	private WarehouseMapper warehouseMapper;
	@Inject
	private ProductTranslateMapper transMapper;
	@Inject
	private DisSpriceGoodsMapper goodsMapper;
	@Inject
	private IVirtualCategoryService vcService;
	@Inject
	private IHttpService httpService;
	@Inject
	private ContractQuotationsMapper quotationsMapper;
	@Inject
	private ExportModelMapper exportModeMapper;
	@Inject
	private IInventoryLockService inventoryLockService; 
	
	@Inject
	private ExportSyncResultMapper exportSyncResultMapper;
	
	public ProductBase getProductBase(ProductSearchParamDto productSearchDto) {
		return productBaseMapper.queryProductBase(productSearchDto);
	}

	public Map<String, Object> getProductDetail(ProductSearchParamDto productSearchDto,
			Map<Integer, List<String>> category_sku_mapping, Map<Integer, CategoryBase> categoryName) {
		Map<String, Object> map = new HashMap<>();
		List<ProductLite> productList = queryProducts(productSearchDto);
		
		List<Map<String, Object>> stockQuery = Lists.newArrayList();
		Map<String,Object> skuWare = null;
		if(CollectionUtils.isNotEmpty(productList)){
			for (ProductLite pro : productList) {
				skuWare = Maps.newHashMap();
				skuWare.put("sku", pro.getCsku());
				skuWare.put("warehouseId", pro.getWarehouseId());
				stockQuery.add(skuWare);
			}
		}
		
		Map<String, ProStockDto> entities = queryStock(productList,productSearchDto);
		ProStockDto sto = null;
		// 设置类目信息
		for (Integer catId : category_sku_mapping.keySet()) {
			for (ProductLite pro : productList) {
				sto = entities.get(pro.getCsku()+"|"+pro.getWarehouseId());
				if(null != sto) {
					pro.setStock(StockUtil.fixStockNumber(sto.getCloudInventory()));
					pro.setMicroStock(StockUtil.fixStockNumber(sto.getMicroInventory()));
				}
				if (category_sku_mapping.get(catId).contains(pro.getCsku())) {
					pro.setCname(categoryName.get(catId).getCname());
					pro.setCategoryId(catId);
					continue;
				}
			}
		}
		isSpecial(productList,productSearchDto);
		ProductLite product = null;
		if (null != productList && productList.size() > 0) {
			product = productList.get(0);
		}
		// 设置已售值
		if (null != product) {
			map.put("success", true);
			map.put("storage", warehouseMapper.getGoodsInventorys(productSearchDto.getSku()));
			map.put("images", new ProductImages(imageMapper.queryProductImgs(productSearchDto)));
			map.put("base",inventoryLockService.substock(productSearchDto.getEmail(), product));
			map.put("trans", transMapper.queryProductTranslate(productSearchDto));
			getCategoryName(product.getCategoryId(), map);
		} else {
			map.put("success", false);
		}
		return map;
	}


	/**
	 * @param catId
	 * @return
	 */
	private void getCategoryName(Integer catId, Map<String, Object> map) {
		CategoryBase cbase = cateBaseMapper.queryCategory(catId);
		if (null != cbase) {
			if (cbase.getIlevel() != 1) {
				getCategoryName(cbase.getIparentid(), map);
			} else {
				map.put("category", cbase.getCname());
			}
		}
	}

	@Override
	public String getImgUrl(String sku) {
		String url = productBaseMapper.getImgUrl(sku);
		return url == null ? "" : url;
	}

	@Override
	public List<String> getBrand() {
		return productBaseMapper.getBrand();
	}
	
	@Override
	public PageResultDto<ProductLite> products(ProductSearchParamDto productSearchDto) {
		// 获取查询条件下所有数据不带分页
		Integer pageSize = productSearchDto.getPageSize();
		Integer currPage = productSearchDto.getCurrPage();
		
		List<ProductLite> productList = queryProducts(productSearchDto);
		int total = productList.size();
		//分页
		if(null != pageSize && productList.size() >pageSize){
			int startIdx = (currPage - 1) *  pageSize;
			int toIdx = startIdx + pageSize;
			if(toIdx > (productList.size() - 1)){
				toIdx = productList.size();
			}
			productList = productList.subList( startIdx, toIdx);
		}

		/*******************
		 * change by zbc 库存查询修改 start
		 **************************************************/
		Map<String, ProStockDto> entities = queryStock(productList, productSearchDto);
		ProStockDto sto = null;
		for (ProductLite pro : productList) {
			sto = entities.get(pro.getCsku() + "|" + pro.getWarehouseId());
			if (null != sto) {
				pro.setStock(StockUtil.fixStockNumber(sto.getCloudInventory()));
				pro.setMicroStock(StockUtil.fixStockNumber(sto.getMicroInventory()));
			}
		}

		isSpecial(productList,productSearchDto);
		
		/*******************
		 * change by zbc 库存查询修改 end
		 **************************************************/
		PageResultDto<ProductLite> pageResultDto = new PageResultDto<ProductLite>(pageSize, total, currPage,inventoryLockService.substock(productSearchDto.getEmail(),productList));
		return pageResultDto;
	}
	
	/**
	 * 查询商品云仓库存微仓库存
	 * @author zbc
	 * @since 2017年1月4日 下午4:29:48
	 */
	private Map<String, ProStockDto> queryStock(List<ProductLite> productList, ProductSearchParamDto productSearchDto){
		List<String>  skus = Lists.transform(productList, u->u.getCsku());
		Map<String, ProStockDto> entities = Maps.newHashMap();
		if(skus.size()>0){
			List<ProStockDto> stockList = Lists.newArrayList();
			try {
				JsonNode stockNode = httpService.getProStock(productSearchDto.getEmail(), skus);
				ObjectMapper map = new ObjectMapper();
				stockList = map.readValue(stockNode.toString(), new TypeReference<List<ProStockDto>>() {});
			} catch (Exception e) {
				Logger.info("查询商品库存异常",e);
			}
			String key = null;
			for(ProStockDto s:stockList){
				if(s.getSku() != null && s.getWarehouseId() != null){
					key = s.getSku()+"|"+s.getWarehouseId();
					entities.put(key, s);
				}
			}
		}
		return entities;
	}

	@Override
	public PageResultDto<ProductLite> inventoryGoods(ProductSearchParamDto productSearchDto,
			Map<Integer, List<String>> category_sku_mapping, Map<Integer, CategoryBase> categoryName) {
		// 获取查询条件下所有数据不带分页
		Integer pageSize = productSearchDto.getPageSize();
		Integer currPage = productSearchDto.getCurrPage();
		if (null != productSearchDto.getCategoryId()) {
			List<String> skus = productSearchDto.getSkuList();
			List<String> cateSkus = category_sku_mapping.get(productSearchDto.getCategoryId());
			if (CollectionUtils.isNotEmpty(skus)) {
				Map<Boolean, List<String>> exsit = skus.stream()
						.collect(Collectors.partitioningBy(e -> cateSkus.contains(e)));
				productSearchDto.setSkuList(exsit.get(true));
			} else {
				productSearchDto.setSkuList(cateSkus);
			}
			if (!CollectionUtils.isNotEmpty(productSearchDto.getSkuList())) {
				return new PageResultDto<ProductLite>(null, 0, null, new ArrayList<>());
			}
		}
		
		//获取微仓skus
		List<String> micSkus = Lists.newArrayList();
		try {
			JsonNode stoNode = httpService.getMriStock(productSearchDto.getEmail(), productSearchDto.getSkuList(), productSearchDto.getWarehouseId());
			for(JsonNode n:stoNode){
				if(n.get("stock").asDouble()>0){
					micSkus.add(n.get("sku").asText());
				}
			}
			productSearchDto.setSkuList(micSkus);
		} catch (Exception e) {
			Logger.info("查询微仓商品异常",e);
		}
		if (!CollectionUtils.isNotEmpty(micSkus)) {
			return new PageResultDto<ProductLite>(null, 0, null, new ArrayList<>());
		}
		
		List<ProductLite> productList = productBaseMapper.products(productSearchDto);

		/*******************change by zbc  库存查询修改 start **************************************************/
		Map<String, ProStockDto> entities = queryStock(productList,productSearchDto);
		ProStockDto sto = null;
		// 设置类目信息
		for (Integer catId : category_sku_mapping.keySet()) {
			for (ProductLite pro : productList) {
				sto = entities.get(pro.getCsku()+"|"+pro.getWarehouseId());
				if(null != sto) {
					pro.setStock(StockUtil.fixStockNumber(sto.getCloudInventory()));
					pro.setMicroStock(StockUtil.fixStockNumber(sto.getMicroInventory()));
				}
				if (category_sku_mapping.get(catId).contains(pro.getCsku())) {
					pro.setCname(categoryName.get(catId).getCname());
					pro.setCategoryId(catId);
					continue;
				}
			}
		}
		
		isSpecial(productList, productSearchDto);

		/*******************change by zbc  库存查询修改 end **************************************************/
		return new PageResultDto<ProductLite>(pageSize, productBaseMapper.productCount(productSearchDto), currPage, inventoryLockService.substock(productSearchDto.getEmail(),productList));
	}

	@Override
	public List<Warehouse> selectAllWare() {
		return  warehouseMapper.selectAll();
	}
	
	@Override
	public List<ProductLite> getProducts(ProductSearchParamDto productSearchDto, int siteId, int langId) {
		List<ProductLite> list = productBaseMapper.products(productSearchDto);
		if (null != list && list.size() > 0) {
			List<String> skus = Lists.transform(list, i -> i.getCsku());
			if (skus.size() > 0) {
				try {
					List<DisSpriceGoods> goods = goodsMapper.findActivitySkus(skus);
					Map<String, DisSpriceGoods> map = Maps.uniqueIndex(goods, g -> g.getSku());
					DisSpriceGoods good = null;
					for (ProductLite product : list) {
						good = map.get(product.getCsku());
						if (null != good) {
							product.setIsSpecial(true);
							product.setSpecialSale(good.getSpecialPrice());
						}
					}
				} catch (Exception e) {
					Logger.error("获取特价信息失败",e);
				}
				
			}
		}
		return list;
	}
	
	/**
	 * 查询商品
	 * @param productSearchDto
	 * @return
	 */
	private List<ProductLite> queryProducts(ProductSearchParamDto productSearchDto) {
		long start = System.currentTimeMillis();
		List<ProductLite> list = productBaseMapper.products(productSearchDto);
		Logger.info("查询商品end：{}", System.currentTimeMillis() - start);
		return list;
	}
	
	/**
	 * 是否是特价商品
	 * @param list
	 */
	private void isSpecial(List<ProductLite> list, ProductSearchParamDto productSearchDto) {
		if (null != list && list.size() > 0) {
			List<String> skus = Lists.transform(list, i -> i.getCsku());
			if (skus.size() > 0) {
				try {
					List<DisSpriceGoods> goods = goodsMapper.findActivitySkus(skus);
					Map<String, DisSpriceGoods> map = Maps.uniqueIndex(goods, g -> g.getSku());
					DisSpriceGoods good = null;
					ContractQuotations quoted = null;
					Map<String, ContractQuotations> quotedMap = Maps.newHashMap();
					// add by xuse
					if (StringUtils.isNotEmpty(productSearchDto.getEmail())) {
						Map<String, Object> param = Maps.newHashMap();
						param.put("skus", skus);
						param.put("warehouseId", productSearchDto.getWarehouseId());
						param.put("account", productSearchDto.getEmail());
						List<ContractQuotations> quoteds = quotationsMapper.productSearch(param);
						quotedMap = Maps.uniqueIndex(quoteds, g -> g.getSku() + "_" + g.getWarehouseId());
					}
					for (ProductLite product : list) {
						quoted = quotedMap.get(product.getCsku() + "_" + product.getWarehouseId());
						if (null != quoted) {
							product.setDisPrice(quoted.getContractPrice());
							product.setContractNo(quoted.getContractNo());
						} else {
							good = map.get(product.getCsku());
							if(good != null) {
								product.setDisPrice(good.getSpecialPrice());
								product.setIsSpecial(true);
								product.setSpecialSale(good.getSpecialPrice());								
							}
						}
					}
				} catch (Exception e) {
					Logger.error("获取特价信息失败", e);
				}

			}
		}
	}

	@Override
	public void loadCategory() {
		if (BufferUtils.categoryName.size() <= 0) {
			CategorySearchParamDto dto = new CategorySearchParamDto();
			dto.setLevel(1);
			List<CategoryBase> vcList = cateBaseMapper.realCateQuery(dto);
			BufferUtils.categoryName = Maps.uniqueIndex(vcList, vc -> vc.getIid());
			for (CategoryBase category : vcList) {
				Set<Integer> all = new HashSet<>();
				List<Integer> list = new ArrayList<>();
				list.add(category.getIid());
				vcService.queryChild(list, all);
				List<String> skus = vcService.getSkuLists(new ArrayList<>(all));
				BufferUtils.category_sku_mapping.put(category.getIid(), skus);
			}
		}
		
	}

	@Override
	public void loadwarehouse() {
		if (BufferUtils.warehouse_id_mapping.size() <= 0) {
			List<Warehouse> list = selectAllWare();
			list.forEach(w->{
				BufferUtils.warehouse_id_mapping.put(w.getWarehouseNo(), w);
			});
		}
		
	}

	@Override
	public void reloadCategory(boolean cateName, boolean skuMapping, boolean wareMap) {
		if (cateName) {
			BufferUtils.clearCategoryName();
		}
		if (skuMapping) {
			BufferUtils.clearCategorySkuMapping();
		}
		if (wareMap) {
			BufferUtils.clearWarehouseIdMapping();
		}
		vcService.emptyAll();
	}

	@Override
	public List<ProductLite> getProductInfo(ProductSearchParamDto productSearchDto) {
		return null;
	}

	@Override
	public List<ProdcutInventoryDataExportDto> productInventoryDataExport(String expiration_begin,
			String expiration_end) {
		return productBaseMapper.productInventoryDataExport(expiration_begin,expiration_end);
	}

	@Override
	public CommonExportDto getExportModelByFunctionId(String functionId) {

		return exportModeMapper.getExprotByFunctionId(functionId);

	}

	@Override
	public List<Map> productInventoryDataExportTest(String sql) {
		return productBaseMapper.export(sql);
	}

	@Override
	public List<Warehouse> getAvailableWarehouse() {
		return warehouseMapper.getAvailableWarehouse();
	}

	@Override
	public Map<String, Object> setSalable(Set<String> skuSet, int salable, String optUser) {
		// 进行过滤操作
		if(CollectionUtils.isEmpty(skuSet)){
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "没有选择商品");
			return result;
		}
		
		List<String> skus = Lists.newArrayList(skuSet);
		if (skus.size()<=50) {
			addSalableSetLogs(skus, salable, optUser);
			int count = productBaseMapper.updateSalable(skus, salable);
			boolean suc = count > 0;
			Logger.info("设置非卖品：结果：{}，要设置的sku个数为：{}，成功个数：{}", suc, skus.size(), count);
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", suc);
			result.put("msg", suc ? "设置成功" : "设置失败");
			return result;
		}
		
		// 分批处理
		int size = skus.size();
		Logger.info("设置非卖品：要设置的商品过多，进行分批处理，要设置的sku数量为：{}", size);
		int start = 0;// 初始start为0
		int end = 50;// 初始end为50
		int step = 50;// 步长50
		int totalCount = 0;// 总共成功个数
		int batchNum = 0;// 批次
		while (start < end) {
			batchNum++;
			List<String> subSkus = skus.subList(start, end);
			Logger.info("设置非卖品：第{}批次，设置{}~{}的sku的非卖品状态，共{}个", batchNum, start, end, end-start);
			addSalableSetLogs(subSkus, salable, optUser);
			int count = productBaseMapper.updateSalable(subSkus, salable);
			Logger.info("设置非卖品：当前成功设置{}个", count);
			totalCount += count;
			Logger.info("设置非卖品：累计成功设置{}个", totalCount);
			
			start = end;
			end = start + step;
			if (end>size) {
				end = size;
			}
		}
		Logger.info("设置非卖品：总共成功设置{}个", totalCount);
		boolean suc = totalCount == size;
		Map<String, Object> result = Maps.newHashMap();
		result.put("suc", suc);
		result.put("msg", suc ? "设置成功" : "设置失败");
		return result;
	}
	
	/**
	 * 添加设置非卖品的日志
	 * @param skus
	 * @param salable
	 * @param optUser
	 */
	private void addSalableSetLogs(List<String> skus, int salable, String optUser) {
		if (CollectionUtils.isEmpty(skus)) {
			return;
		}
		
		List<ProductBaseLog> logList = Lists.newArrayListWithCapacity(skus.size());
		for (String sku : skus) {
			logList.add(
					new ProductBaseLog(sku, salable, ProductBaseLogOptTypes.OPT_SALABLE, optUser)
			);
		}
		
		productBaseLogMapper.batchInsert(logList);
	}
	
	@Override
	public List<ProductBaseLog> getSalableSetLogs(String sku) {
		if (StringUtils.isEmpty(sku)) {
			return Lists.newArrayList();
		}
		return productBaseLogMapper.selectBySku(sku);
	}

	@Override
	public String getProductAndStock(String nodeStr) {
		Map<String,Object> result=Maps.newHashMap();
		Map<String,Object> data =Maps.newHashMap();
		try {
			JsonNode node= Json.parse(nodeStr);
			String status=null;
			if(node.has("istatus")){//商品状态，在售、下架...
				status=node.get("istatus").asText();
			}
			String categoryId=null;;
			if(node.has("categoryId")){//类目
				categoryId=node.get("categoryId").asText();
			}
			String typeId=null;;
			if(node.has("typeId")){//等级，1(A)、2(B)、3(C)
				typeId=node.get("typeId").asText();
			}
			String title=null;;
			if(node.has("title")){//商品名称
				title=node.get("title").asText();
			}
			String warehouseId=null;;
			if(node.has("warehouseId")){//仓库id
				warehouseId=node.get("warehouseId").asText();
			}
			int currPage=node.get("currPage").asInt();
			int pageSize=node.get("pageSize").asInt();
			
			List<Map> productLists= productBaseMapper.getProductAndStock(status,categoryId,typeId,title,warehouseId,currPage,pageSize);
			int rows=0;
			if(productLists != null && productLists.size()>0){
				rows = (int) productLists.get(0).get("totalcount");
			}
			data.put("rows", rows);
			int totalPage=rows / pageSize + ((rows % pageSize > 0) ? 1 : 0);
			data.put("totalPage", totalPage);
			data.put("pageSize", pageSize);
			data.put("currPage", currPage);
			data.put("result", productLists);
			
			
			result.put("data", data);
			result.put("errCode", 0);
		} catch (Exception e) {
			Logger.info("查询商品信息和库存发生异常：{}", e);
			result.put("data", data);
			result.put("errCode", 1);
			result.put("errMsg", "查询商品信息和库存发生异常");
		}
		return Json.toJson(result).toString();
	}

	@Override
	public String createProductAndStockFile(String reqNodeStr) {
		Map<String,Object> result=Maps.newHashMap();
		JsonNode reqNode = Json.parse(reqNodeStr);
		try {
			String operator = reqNode.get("operator").asText();
			ExportSyncResult syncResult = exportSyncResultMapper.selectByOperator(operator);
			if(syncResult!=null){
				result.put("result", 1);
				result.put("msg", syncResult.getMsg());
				return Json.toJson(result).toString();
			}else{
				ExportSyncResult exportResult=new ExportSyncResult();
				exportResult.setOperator(operator);
				exportResult.setExportResult(1);
				exportResult.setMsg("文件正在生成，请稍后下载");
				exportResult.setCreateTime(new Date());
				exportSyncResultMapper.insertSelective(exportResult);
			}
			//异步任务
			InjectorInstance.getInstance(EventBus.class).post(new ExportEvent(reqNodeStr));
			result.put("result", 1);
			result.put("msg", "文件正在生成,请稍后下载");
		} catch (Exception e) {
			result.put("result", 2);
			result.put("msg", "文件生成异常！");
		}
		return Json.toJson(result).toString();
	}

	@Override
	public ExportSyncResult getProductAndStockFileResult(String operator) {
		return exportSyncResultMapper.selectByOperator(operator);
	}

	@Override
	public void deleteExportResultByOperator(String operator) {
		exportSyncResultMapper.deleteExportResultByOperator(operator);
	}
}
