package controllers.product;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.collect.Sets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;

import controllers.annotation.ALogin;
import dto.ProdcutInventoryDataExportDto;
import dto.category.CategorySearchParamDto;
import dto.category.VirCategoryDto;
import dto.product.PageResultDto;
import dto.product.ProductDispriceDto;
import dto.product.ProductDispriceSearch;
import dto.product.ProductLite;
import dto.product.ProductSearchParamDto;
import entity.banner.BannerInfo;
import entity.product.ExportSyncResult;
import entity.product.ProductBaseLog;
import forms.category.VirtualCategoryForm;
import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Html;
import services.base.utils.JsonFormatUtils;
import services.product.IBannerService;
import services.product.IProductBaseService;
import services.product.IProductDispriceService;
import services.product.IProductEnquiryService;
import services.product.IProductExpirationDateService;
import services.product.ITypeBaseService;
import services.product.IUserService;
import services.product.IVirtualCategoryService;
import session.ISessionService;
import util.product.BufferUtils;
import util.product.DataUtil;
import util.product.DateUtils;
import util.product.ExportProductInventoryDataOrderUtil;
import util.product.ExportProductsStocksUtil;
import util.product.ExportUtil;

@Api(value="/product",description="商品模块")
public class ProductController extends Controller {
	@Inject
	private IProductBaseService baseService;
	@Inject
	private IProductEnquiryService prodEnquiryService;
	@Inject
	private IVirtualCategoryService vcService;
	@Inject
	private IProductDispriceService productDispriceService;
	@Inject
	private ITypeBaseService typeBaseService;
	@Inject
	private ISessionService sessionService;
	@Inject
	private IBannerService bannerService;
	@Inject
	private IUserService userService;
	
	@Inject
	private IProductExpirationDateService productExpirationDateService;
	/**
	 * 获取商品详情
	 * 
	 * @param sku
	 * @param warehouseId
	 * @return
	 */
	public Result product(String sku, Integer warehouseId,Integer model) {
		ProductSearchParamDto searchDto = new ProductSearchParamDto();
		// 先获取类目信息
		baseService.loadCategory();
		if (StringUtils.isEmpty(sku)) {
			return ok(DataUtil.formatData(false, null));
		}
		List<String> skuList = Lists.newArrayList();
		skuList.add(sku);
		searchDto.setSku(sku);
		searchDto.setSkuList(skuList);
		searchDto.setWarehouseId(warehouseId);
		searchDto.setModel(sessionService.get("model") != null ? Integer.parseInt(sessionService.get("model").toString()) : null);
		searchDto.setEmail(sessionService.get("email") != null ? sessionService.get("email").toString() : null);
		return ok(
				DataUtil.formatData(baseService.getProductDetail(searchDto, BufferUtils.category_sku_mapping, BufferUtils.categoryName), null));
	}


	/**
	 * 查询某个真实类目下的所有sku
	 * 
	 * @return
	 * @author
	 */
	public Result getSkusList(Integer catId) {
		return ok(Json.toJson(vcService.getSkuList(catId))); 
	}

	/**
	 * 缓存类目
	 */
	public Result reloadCategory(boolean cateName, boolean skuMapping, boolean wareMap) {
		baseService.reloadCategory(cateName,skuMapping,wareMap);
		return ok("OK");
	}

	/**
	 * 根据SKU获取商品主图
	 * 
	 * @param sku
	 * @return
	 */
	public Result getImgUrl(String sku) {
		if (StringUtils.isEmpty(sku)) {
			return internalServerError();
		}
		return ok(baseService.getImgUrl(sku));
	}

	/**
	 * 获取商品价格列表
	 * 
	 * @author zbc
	 * @since 2016年7月28日 下午2:20:52
	 */
	public Result getProDisPriceList() {
		JsonNode node = request().body().asJson();
		Logger.info("获取价格列表参数[{}]", node);
		Map<String, Object> result = Maps.newHashMap();
		// 缓存类目
		baseService.loadCategory();
		// 缓存仓库信息
		baseService.loadwarehouse();
		if (node == null) {
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
		result = productDispriceService.read(node, BufferUtils.warehouse_id_mapping, BufferUtils.category_sku_mapping, BufferUtils.categoryName);
		return ok(Json.toJson(result));
	}
	
	/**
	 * 分页查询价格操作记录
	 * @author zbc
	 * @since 2016年8月1日 下午8:45:38
	 */
	public Result getPriceOperateRecord(){
		JsonNode node = request().body().asJson();
		Logger.info("获取价格操作日志列表参数[{}]", node);
		Map<String, Object> result = Maps.newHashMap();
		// 缓存类目
		baseService.loadCategory();
		if (node == null) {
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
		result = productDispriceService.readRecord(node, BufferUtils.category_sku_mapping);
		return ok(Json.toJson(result));
	}

	/**
	 * 修改默认系数
	 * 
	 * @author zbc
	 * @since 2016年7月29日 下午2:17:48
	 */
	public Result updatePricerule() {
		JsonNode node = request().body().asJson();
		// 缓存类目
		baseService.loadCategory();
		// 缓存仓库信息
		baseService.loadwarehouse();
		Map<String, Object> result = Maps.newHashMap();
		Logger.info("修改默认价格设置参数:[{}]", node);
		if (node == null || !node.has("id") ||!node.has("status") || !node.has("lastOperator") || !node.has("defaultFactor")) {
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
		result = productDispriceService.updaterule(node, BufferUtils.warehouse_id_mapping, BufferUtils.category_sku_mapping, BufferUtils.categoryName);
		return ok(Json.toJson(result));
	}

	/**
	 * 一键设置价格
	 * @author zbc
	 * @since 2016年8月1日 下午4:41:16
	 */
	public Result oneKeySetPrice(String type){
		JsonNode node = request().body().asJson();
		// 缓存类目
		baseService.loadCategory();
		// 缓存仓库信息
		baseService.loadwarehouse();
		Map<String, Object> result = Maps.newHashMap();
		Logger.info("一键设置("+(type != null?type:"TOTAL")+")价格参数:[{}]",node);
		if (node == null) {
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
		result = productDispriceService.batchSetPrice(node, BufferUtils.warehouse_id_mapping, BufferUtils.category_sku_mapping, BufferUtils.categoryName,
				type);
		return ok(Json.toJson(result));
	}
	
	/**
	 * 一键设置基础价格
	 * @author zbc
	 * @since 2016年11月3日 下午3:50:14
	 */
	public Result oneKeySetBasePrice(){
		return oneKeySetPrice("BASE");
	}
	/**
	 * 一键设置 经销商供货价
	 * @author zbc
	 * @since 2016年11月3日 下午3:50:39
	 */
	public Result oneKeySetDistributorPrice(){
		return oneKeySetPrice("DIS");
	}
	/**
	 * 一键设置 自贸区经销价格
	 * @author zbc
	 * @since 2016年11月3日 下午3:51:12
	 */
	public Result oneKeySetFtzPrice(){
		return oneKeySetPrice("FTZ");
	}
	/**
	 * 一按键设置 电商供货价
	 * @author zbc
	 * @since 2016年11月3日 下午3:51:47
	 */
	public Result oneKeySetElePrice(){
		return oneKeySetPrice("ELE");
	}
	/**
	 * 一键设置  KA直营供货价
	 * @author zbc
	 * @since 2016年11月3日 下午3:52:23
	 */
	public Result oneKeySetSupPrice(){
		return oneKeySetPrice("SUP");
	}
	/**
	 * 一键设置vip价格
	 * @author zbc
	 * @since 2016年11月3日 下午3:52:23
	 */
	public Result oneKeySetVipPrice(){
		return oneKeySetPrice("VIP");
	}
	
	/**
	 * b2c更新价格是，更具定价默认参数，更新价格
	 * @author zbc
	 * @since 2016年8月4日 下午8:04:46
	 */
	public Result b2cUpdatePrice(){
		JsonNode node = request().body().asJson();
		Map<String,Object> result = Maps.newHashMap();
		Logger.info("b2c同步更新价格参数:[{}]",node);
		// 缓存类目
		baseService.loadCategory();
		// 缓存仓库信息
		baseService.loadwarehouse();
		if(node == null){
			result.put("suc", false);
			result.put("msg","参数错误");
			return ok(Json.toJson(result));
		}
		return ok(Json.toJson("ok"));
	}
	
	public Result addPriceFactor(){
		JsonNode node = request().body().asJson();
		Map<String,Object> result = Maps.newHashMap();
		Logger.info("设置价格参数:[{}]",node);
		// 缓存类目
		baseService.loadCategory();
		// 缓存仓库信息
		baseService.loadwarehouse();
		if(node == null || !node.has("status") || !node.has("lastOperator") || !node.has("factorMap")){
			result.put("suc", false);
			result.put("msg","参数错误");
			return ok(Json.toJson(result));
		}
		result = productDispriceService.addPriceFactor(node, BufferUtils.warehouse_id_mapping, BufferUtils.category_sku_mapping, BufferUtils.categoryName);
		return ok(Json.toJson(result));
	}
	
	public Result getBrand(){
		return ok(Json.toJson(baseService.getBrand()));
	}
	
	/**
	 * 前台使用的查询商品-因为加了非卖状态<br>
	 * 获取商品 { "data":{ "categoryId":4680, "istatus":1, "title":
	 * "美国Gerber嘉宝 香蕉奶油车轮泡芙 42g", "skuList":[], "minPrice":40.01, "pageSize":10,
	 * "currPage":1, "sku":"IF107", "warehouseId":29} }
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Result fproducts() {
		JsonNode node = request().body().asJson();
		Logger.info("ProductController：调用获取商品接口 : " + node);
		ProductSearchParamDto searchDto = JsonFormatUtils.jsonToBean(node.get("data").toString(),
				ProductSearchParamDto.class);
		// 前台的只能查询可卖的商品
		searchDto.setSalable(ProductSearchParamDto.SALABLE_YES);
		searchDto.setwType("-10");// 表示不等于-10
		PageResultDto pageResultDto = getProducts(searchDto);
		Result result = ok(DataUtil.formatData(pageResultDto, null));
		Logger.info("end:" + new Date());
		return result;
	}

	/**
	 * 针对订单导入的商品查询,不根据是否是非卖品这一查询  (订单包含非卖品时，可以正常生成Bbc订单)
	 *
	 * 2017-06-14
	 *
	 * @return
	 */
	public Result fproductsForImport() {
		JsonNode node = request().body().asJson();
		Logger.info("ProductController>>>>>>>>>>>>>>fproductsForImport：调用获取商品接口 : " + node);
		ProductSearchParamDto searchDto = JsonFormatUtils.jsonToBean(node.get("data").toString(),
				ProductSearchParamDto.class);
		searchDto.setwType("-10");// 表示不等于-10
		PageResultDto pageResultDto = getProducts(searchDto);
		Result result = ok(DataUtil.formatData(pageResultDto, null));
		Logger.info("end:" + new Date());
		return result;
	}
	
	/**
	 * 后台使用的查询商品-因为加了非卖状态<br>
	 * 获取商品 { "data":{ "categoryId":4680, "istatus":1, "title":
	 * "美国Gerber嘉宝 香蕉奶油车轮泡芙 42g", "skuList":[], "minPrice":40.01, "pageSize":10,
	 * "currPage":1, "sku":"IF107", "warehouseId":29} }
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Result products() {
		JsonNode node = request().body().asJson();
		Logger.info("ProductController：调用获取商品接口 : " + node);
		ProductSearchParamDto searchDto = JsonFormatUtils.jsonToBean(node.get("data").toString(),
				ProductSearchParamDto.class);
		PageResultDto pageResultDto = getProducts(searchDto);
		Result result = ok(DataUtil.formatData(pageResultDto, null));
		Logger.info("end:" + new Date());
		return result;
	}
	
	/**
	 * 查询商品信息和库存
	 * @return
	 */
	public Result getProductAndStock(){
		JsonNode node = request().body().asJson();
		String nodeStr = node.toString();
		String resultStr= baseService.getProductAndStock(nodeStr);
		JsonNode resultNode=Json.parse(resultStr);
		return ok(resultNode);
	}
	
	/**
	 * 生成商品库存信息文件
	 * @return
	 */
	public Result createProductAndStockFile(){
		JsonNode reqNode = request().body().asJson();
		String reqNodeStr=reqNode.toString();
		String result= baseService.createProductAndStockFile(reqNodeStr);
		JsonNode resultNode = Json.parse(result);
		return ok(resultNode);
	}
	
	/**
	 * 查询商品库存文件是否已生成
	 */
	public Result getProductAndStockFileResult(){
		Map<String,Object> result=Maps.newHashMap();
		try {
			String operator = request().getQueryString("operator");
			String fileName="商品库存信息表.xls";
			ExportSyncResult syncResult= baseService.getProductAndStockFileResult(operator);
			if(syncResult==null){
				return ok("查询不到导出记录,请重新导出文件");
			}
			Integer exportResult = syncResult.getExportResult();
			if(exportResult.intValue()==1 || exportResult.intValue()==2){
				return ok(syncResult.getMsg());
			}
			File file=new File("/tmp/"+syncResult.getFileName());
			//删除导出结果表中记录
			baseService.deleteExportResultByOperator(operator);
			if(!file.exists()){
				return ok("导出文件已不存在，请重新尝试！");
			}
			try {
				response().setHeader("Content-disposition", "attachment;filename="+new String(fileName.getBytes("utf-8"),"ISO8859_1"));
			} catch (UnsupportedEncodingException e) {
				Logger.info("productAndinventorydownloadError----------->{}",e);
				return ok("导出文件发生异常");
			}
			return ok(file);
		} catch (Exception e) {
			Logger.info("productAndinventorydownloadError----------->{}",e);
			return ok("导出文件发生异常");
		}
	}
	
	/**
	 * 抽取方法作为公用
	 * @param node
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private PageResultDto getProducts(ProductSearchParamDto searchDto){
		Logger.info("start:" + new Date());
		// 缓存类目
		baseService.loadCategory();
		Logger.info("查询商品的参数："+searchDto);
		if(searchDto.getModel()==null){
			searchDto.setModel(sessionService.get("model") != null ? Integer.parseInt(sessionService.get("model").toString()) : null);
		}
		if(StringUtils.isEmpty(searchDto.getEmail())){
			searchDto.setEmail(sessionService.get("email") != null ? sessionService.get("email").toString() : null);
		}
		PageResultDto pageResultDto = prodEnquiryService.products(searchDto);
		return pageResultDto;
	}

	/**
	 * 获取单条商品价格信息
	 * @author zbc
	 * @since 2016年7月30日 上午11:33:41
	 */
	public Result getDisPrice(Integer id){
		// 缓存类目
		baseService.loadCategory();
		return ok(Json.toJson(productDispriceService.getDisprice(id,BufferUtils.category_sku_mapping)));
	}
	
	/**
	 * 获取商品 { "data":{ "categoryId":4680, "istatus":1, "title":
	 * "美国Gerber嘉宝 香蕉奶油车轮泡芙 42g", "skuList":[], "minPrice":40.01, "pageSize":10,
	 * "currPage":1, "sku":"IF107", "warehouseId":29} }
	 * 
	 * @return
	 */
	public Result inventoryGoods() {
		JsonNode json = request().body().asJson();
		Logger.info("InventoryGoods : " + json);
		// 缓存类目
		baseService.loadCategory();
		if(null == json.get("data") || !json.get("data").has("email")) {
			return internalServerError("参数错误。");
		}
		ProductSearchParamDto searchDto = JsonFormatUtils.jsonToBean(json.get("data").toString(),
				ProductSearchParamDto.class);
		if (null != searchDto && searchDto.getCategoryId() != null) {
			Integer catId = searchDto.getCategoryId();
			List<String> skus = null;
			if (BufferUtils.category_sku_mapping.containsKey(catId)) {
				skus = BufferUtils.category_sku_mapping.get(catId);
			}
			searchDto.setSkuList(skus);
		}
		return ok(DataUtil.formatData(baseService.inventoryGoods(searchDto, BufferUtils.category_sku_mapping, BufferUtils.categoryName), null));
	}
	
	/**
	 * 
	 * @author lzl
	 * @since 2016年11月9日下午2:12:30
	 */
	public Result addProductType(){
		ObjectNode result = Json.newObject();
		JsonNode json = request().body().asJson();
		if (null == json || json.size() == 0) {
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result.toString()));
		}
		Logger.info("addProductType---->" + json.toString());
		String param = json.toString();
		return ok(Json.toJson(typeBaseService.addProductType(param)));
	}
	
	/**
	 * 
	 * @author lzl
	 * @since 2016年11月9日下午2:12:30
	 */
	public Result changeProductType(){
		ObjectNode result = Json.newObject();
		JsonNode json = request().body().asJson();
		if (null == json || json.size() == 0 || !json.has("tid")) {
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result.toString()));
		}
		Logger.info("addProductType---->" + json.toString());
		String param = json.toString();
		return ok(Json.toJson(typeBaseService.updateProductType(param)));
	}
	
	/**
	 * 
	 * @author lzl
	 * @since 2016年11月9日下午2:19:47
	 */
	public Result getAllTypes(){
		return ok(Json.toJson(typeBaseService.getAllTypes()));
	}
	
	/**
	 * 
	 * @author lzl
	 * @since 2016年11月10日上午11:10:53
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result chooseProductType(){
		ObjectNode result = Json.newObject();
		JsonNode json = request().body().asJson();
		if (null == json || !json.has("typeId") || !json.has("products") || json.get("products").size() <= 0 
				|| !json.has("typeName")) {
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result.toString()));
		} 
		String param = json.toString();
		return ok(Json.toJson(productDispriceService.setTypeForProducts(param)));
	}
	
	public Result deleteType(Integer tid){
		return ok(Json.toJson(typeBaseService.deleteType(tid)));
	}


	/**
	 * Bbc价格导出
	 *
	 * @return
	 */
	public Result exportBbcPrice() throws UnsupportedEncodingException {
		Map<String, Object> result = Maps.newHashMap();
		Map<String, String[]> map = request().queryString();

		if (null == map) {
			Logger.info("参数错误");
			result.put("suc", false);
			result.put("code", "2");
			return ok(Json.toJson(result));
		}
		String[] header = map.get("header");
		if (null == header || header.length <= 0) {
			Logger.info("列头不能为空。");
			result.put("suc", false);
			result.put("code", "2");
			return ok(Json.toJson(result));
		}

		response().setContentType("application/vnd.ms-excel;charset=utf-8");
		response().setHeader("Content-disposition",
				"attachment;filename=" + new String("BBC价格".getBytes(), "ISO8859-1") + ".xls");

		Map<String, String[]> stringMap = request().queryString();
		Set<Map.Entry<String, String[]>> entries = stringMap.entrySet();

		Map<String, Object> objectMap = Maps.newHashMap();
		for (Map.Entry<String, String[]> mapEntry : entries) {
			String key = mapEntry.getKey();
			String value = mapEntry.getValue()[0];
			if (value != null && !value.equals("undefined") && !value.equals("") && !key.equals("header")) {
				objectMap.put(mapEntry.getKey(), mapEntry.getValue()[0]);
			}
		}

		ObjectMapper mapperObj = new ObjectMapper();
		JsonNode node = null;
		try {
			String jsonResp = mapperObj.writeValueAsString(objectMap);
			node = Json.parse(jsonResp);
		} catch (IOException e) {
			Logger.error("------------>exportBbcPrice error:{}", e);
		}

		Logger.info("======>node:{}", node.toString());

		ProductDispriceSearch searchDto = JsonFormatUtils.jsonToBean(node.toString(), ProductDispriceSearch.class);
		// 只导出c类商品
		searchDto.setTypeId(3);
		List<ProductDispriceDto> exportData = productDispriceService.getExportProductDisPrice(searchDto,
				BufferUtils.warehouse_id_mapping, BufferUtils.category_sku_mapping, BufferUtils.categoryName);
		Logger.info("-----管理员开始导出bbc价格订单，导出数据条数：" + exportData.size());
		Map<String, String> headMap = Maps.newHashMap();
		headMap.put("sku", "商品编号");
		headMap.put("productTitle", "商品名称");
		headMap.put("categoryName", "商品分类");
		headMap.put("typeName", "商品类别");
		headMap.put("brand", "品牌");
		headMap.put("warehoseName", "仓库");
		headMap.put("proposalRetailPrice", "零售价(元)");
		headMap.put("distributorPrice", "Bbc价格(元)");
		headMap.put("cloudStock", "云仓库存");
		return ok(ExportUtil.export("bbcPrice.xls", header, headMap, exportData));
	}

	/**
	 * 导出库存功能
	 * @return
	 */
	@ApiOperation(value = "导出库存", notes = "", nickname = "", httpMethod = "GET")
	@ApiImplicitParams({
		@ApiImplicitParam(name="categoryId",value="商品分类", defaultValue="8675", required=false, dataType="integer",paramType="query"),
		@ApiImplicitParam(name="istatus",value="上下架", defaultValue="1", required=false, dataType="integer",paramType="query"),
		@ApiImplicitParam(name="title",value="搜索框的内容", defaultValue="", required=false, dataType="string",paramType="query"),
		@ApiImplicitParam(name="warehouseId",value="仓库id", defaultValue="2024", required=false, dataType="integer",paramType="query"),
		@ApiImplicitParam(name="typeId",value="商品类别", defaultValue="1", required=false, dataType="integer",paramType="query")
	})
	@SuppressWarnings("unchecked")
	@ALogin
	public Result exportProductsStocks(){
		// 获取商品信息
		String categoryId = request().getQueryString("categoryId");
		String istatus = request().getQueryString("istatus");
		String title = request().getQueryString("title");
		String warehouseId = request().getQueryString("warehouseId");
		String typeId = request().getQueryString("typeId");
		ProductSearchParamDto searchDto = new ProductSearchParamDto();
		if (StringUtils.isNotEmpty(categoryId)) {
			searchDto.setCategoryId(Integer.valueOf(categoryId));
		}
		if (StringUtils.isNotEmpty(istatus)) {
			searchDto.setIstatus(Integer.valueOf(istatus));
		}
		searchDto.setTitle(title);
		if (StringUtils.isNotEmpty(warehouseId)) {
			searchDto.setWarehouseId(Integer.valueOf(warehouseId));	
		}
		if (StringUtils.isNotEmpty(typeId)) {
			searchDto.setTypeId(Integer.valueOf(typeId));
		}
		
		searchDto.setTitle(title);
		searchDto.setCurrPage(null);
		searchDto.setPageSize(null);
		PageResultDto pageResultDto = getProducts(searchDto);
		
		Map<String,Object> result=Maps.newHashMap();
		if(pageResultDto==null || pageResultDto.getResult()==null || pageResultDto.getResult().isEmpty()){
			result.put("result", false);
			result.put("msg", "查询不到相关信息");
			return ok(Json.toJson(result));
		}

		// 商品列表
		List<ProductLite> proList = (List<ProductLite>) pageResultDto.getResult();
		Logger.info("原导出商品库存数据条数为 ： " + proList.size());
		
		Map<String, List<ProductLite>> skuWarehouseId2ProList = productExpirationDateService.setCloudSelectedProductsExpirationDates(proList);
		
		String filename = "商品库存"+DateUtils.nowStr()+".xls";
		try {
			response().setHeader("Content-disposition", "attachment;filename="+new String(filename.getBytes("utf-8"),"ISO8859_1"));
		} catch (UnsupportedEncodingException e) {
			Logger.info("商品库存的excel文件不支持中文名");
			e.printStackTrace();
		}
		response().setContentType("application/vnd.ms-excel;charset=utf-8");
		
		Logger.info("经过到期日期拆分后，导出商品库存数据条数为 ： " + skuWarehouseId2ProList.size());
		
		File excelFile = ExportProductsStocksUtil.export(filename, skuWarehouseId2ProList);
		return ok(excelFile);
	}
	
	public Result productInventoryDataExport(){
		Map<String,Object> result=Maps.newHashMap();
		String expiration_begin = request().getQueryString("expiration_begin");
		String expiration_end = request().getQueryString("expiration_end");
		int type=Integer.valueOf(request().getQueryString("type"));
		List<ProdcutInventoryDataExportDto> dataList= baseService.productInventoryDataExport(expiration_begin,expiration_end);
		if(dataList.isEmpty()){
			result.put("result", false);
			result.put("msg", "查询不到相关信息");
			return ok(Json.toJson(result));
		}
		if(type==1){
			result.put("result", true);
			result.put("data", dataList);
			return ok(Json.toJson(result));
		}
		if(type==2){
			String filename = "商品库存表.xls";
			try {
				response().setHeader("Content-disposition", "attachment;filename="+new String(filename.getBytes("utf-8"),"ISO8859_1"));
			} catch (UnsupportedEncodingException e) {
				Logger.info("商品库存表导出的excel文件不支持中文名");
				e.printStackTrace();
			}
			response().setContentType("application/vnd.ms-excel;charset=utf-8");
			Logger.info("商品库存表数据条数为 ： " + dataList.size());
			
			File excelFile = ExportProductInventoryDataOrderUtil.export(filename, dataList);
			return ok(excelFile);
		}
		result.put("result", false);
		result.put("msg", "参数有误");
		return ok(Json.toJson(result));
	}	

	/**
	 * 首页预加载
	 * 
	 * @return
	 * @author ye_ziran
	 * @since 2017年3月24日 上午11:12:52
	 */
	public Result indexPreRender() throws IOException{
		List<BannerInfo> banners = null;
		List<BannerInfo> rightSideBanners = null;
		Integer bbcCategoryId = null;
		List<VirCategoryDto> virCategorys = null;
		List<VirCategoryDto> firstLevelCates = null;
		
		Configuration config = Play.application().configuration().getConfig("preRender");
		String filePath = config.getString("path");
		{
			banners = bannerService.selectAllBanner();
			/**
			 * 初始化虚拟类目
			 * <br>
			 * 显示在bbc首页的虚拟类目都在name为'bbc'的
			 */
			{
				CategorySearchParamDto catSearchParam = new CategorySearchParamDto();
				catSearchParam.setName("bbc");
				List<VirtualCategoryForm> bbcSubVcList = vcService.query(catSearchParam);
				if(null != bbcSubVcList && !bbcSubVcList.isEmpty()){
					bbcCategoryId = bbcSubVcList.get(0).getVcId();
					virCategorys = vcService.getAllSubsByParentId(bbcCategoryId);
				}
				if(null != virCategorys) {
					Map<Integer, VirCategoryDto> maps = virCategorys.stream().collect(Collectors.toMap(VirCategoryDto :: getId, Function.identity()));
					for (BannerInfo banner : banners) {//添加banner信息
						if(banner.getType() == 2){//一级类目banner
							VirCategoryDto vir = maps.get(banner.getCategoryId());
							vir.setBannerId(banner.getId());
							vir.setBannerBgcolor(banner.getBgColor());
							vir.setBannerLink(banner.getRelatedInterfaceUrl());
						}
						if(banner.getType() == 3){//二级类目banner
							VirCategoryDto vir = maps.get(banner.getCategoryId());
							vir.setBannerId(banner.getId());
							vir.setBannerBgcolor(banner.getBgColor());
							vir.setBannerLink(banner.getRelatedInterfaceUrl());
						}
					}
					virCategorys = maps.values().stream().collect(Collectors.toList());
					buildCategorys(virCategorys);
					firstLevelCates = new ArrayList<>();
					for (int i=virCategorys.size()-1; i>=0; i--) {
						VirCategoryDto vir = virCategorys.get(i);
						if(vir.getLevel() == 1 && !vir.getSubList().isEmpty()){
							firstLevelCates.add(vir);
						}
					}
					
					int count = 1;
					for (int i=virCategorys.size()-1; i>=0; i--) {
						VirCategoryDto vir = virCategorys.get(i);
						if(vir.getLevel() == 1 && !vir.getSubList().isEmpty()){//有二级类目都要去渲染一个html，然后存起来
							Html html = views.html.base.vcategory.render(vir, count);
							File file = new File(filePath+count+".html");
							if(!file.exists()){
								file.createNewFile();
							}
							OutputStreamWriter oStreamWriter = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
							oStreamWriter.append(html.toString());
							oStreamWriter.close();
							count++;
						}
					}
				}
			}
		}
		Map<String,Object> result=Maps.newHashMap();
		
		//type为0的是轮播图
		rightSideBanners = banners.stream().filter(v -> v.getType() == 1).collect(Collectors.toList());
		banners = banners.stream().filter(v -> v.getType() == 0).collect(Collectors.toList());
		
		Html html = views.html.base.index.render(banners, rightSideBanners, firstLevelCates);
		
		File file = new File(filePath+"index-buffer.html");
		if(!file.exists()){
			file.createNewFile();
		}
		OutputStreamWriter oStreamWriter = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
		oStreamWriter.append(html.toString());
		oStreamWriter.close();
		
		result.put("result", true);
		result.put("msg", "预加载完成");
		return ok(Json.toJson(result));
	}
	
	/**
	 * 
	 * 
	 * @author ye_ziran
	 * @since 2017年3月31日 上午11:35:00
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void buildCategorys(List<VirCategoryDto> virCategorys){
		ProductSearchParamDto searchDto = new ProductSearchParamDto();
		searchDto.setIstatus(1);
		searchDto.setPageSize(8);
		searchDto.setCurrPage(1);
		
		for (VirCategoryDto vir : virCategorys) {
			if(vir.getLevel() != 3){//一级、二级添加subList
				List<VirCategoryDto> subList = virCategorys.stream().filter(v -> v.getParentid().equals(vir.getId())).collect(Collectors.toList());
				vir.setSubList(subList);
			}
			if(vir.getLevel() == 2){//二级添加商品list
				searchDto.setvCategoryId(Lists.newArrayList(vir.getId()));
				List res =baseService.products(searchDto).getResult();
				if(null != res){
					vir.setProdList((List<ProductLite>) res);
				}
			}
		}
	}

	/**
	 * 设置商品非卖状态
	 * @return
	 */
	@ApiOperation(value = "设置商品非卖状态", notes = "", nickname = "", httpMethod = "POST")
	@ApiImplicitParams({
	@ApiImplicitParam(name = "body", value = "", required = true, paramType = "body" 
		,defaultValue = 
		"{"+
			   "\n \"skus\": [\"sku1\",\"sku2\",...], "+ 
			   " \n\"salable\": 1, "+ 
			"\n}"
			) })
	@BodyParser.Of(BodyParser.Json.class)
	@ALogin
	public Result setSalable(){
		Map<String, Object> result = Maps.newHashMap();
		JsonNode node = request().body().asJson();
		Logger.info("设置非卖状态的参数：{}", node);
		if (node == null || !node.has("allMatched")
				|| !node.has("salable")
				|| (node.get("salable").asInt()!=0 && node.get("salable").asInt()!=1)) {
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
		
		boolean allMatched = node.get("allMatched").asBoolean();
		
		boolean parametersOk = allMatched ? node.has("termsMatched") : node.has("skus");
		if (!parametersOk) {
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
		
		// 非卖品状态
		int salable = node.get("salable").asInt();
		
		// 指定sku的
		Set<String> skuSet = Sets.newHashSet();
		if (allMatched) {
			// 符合条件的
			JsonNode termsMatchedNode = node.get("termsMatched");
			ProductSearchParamDto searchDto = JsonFormatUtils.jsonToBean(termsMatchedNode.get("data").toString(),ProductSearchParamDto.class);
			// 不需要分页
			searchDto.setPageSize(null);
			searchDto.setCurrPage(null);
			// 缓存类目
			baseService.loadCategory();
			Logger.info("查询商品的参数："+searchDto);
			if(searchDto.getModel()==null){
				searchDto.setModel(sessionService.get("model") != null ? Integer.parseInt(sessionService.get("model").toString()) : null);
			}
			if(StringUtils.isEmpty(searchDto.getEmail())){
				searchDto.setEmail(sessionService.get("email") != null ? sessionService.get("email").toString() : null);
			}
			PageResultDto<ProductLite> pageResultDto = prodEnquiryService.products(searchDto);
			List<ProductLite> plList = pageResultDto.getResult();
			
			if (CollectionUtils.isEmpty(plList)) {
				result.put("suc", false);
				result.put("msg", "没有符合条件的商品");
				return ok(Json.toJson(result));
			}
			
			List<String> skus = Lists.transform(plList, pl->pl.getCsku());
			skuSet.addAll(skus);
		} else {
			for (Iterator<JsonNode> it = node.get("skus").iterator(); it.hasNext();) {
				JsonNode skuNode = it.next();
				if (StringUtils.isNotEmpty(skuNode.asText())) {
					skuSet.add(skuNode.asText());
				}
			}
		}
		
		String optUser = userService.getAdminAccount();
		return ok(Json.toJson(baseService.setSalable(skuSet, salable, optUser)));
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public Result getSalableSetLogs(){
		Map<String, Object> result = Maps.newHashMap();
		JsonNode node = request().body().asJson();
		if (node == null || !node.has("sku")) {
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
		
		String sku = node.get("sku").asText();
		List<ProductBaseLog> logs = baseService.getSalableSetLogs(sku);
		result.put("suc", true);
		result.put("salableLogs", logs);
		return ok(Json.toJson(result));
	}
}
