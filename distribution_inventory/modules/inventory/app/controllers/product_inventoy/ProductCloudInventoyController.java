package controllers.product_inventoy;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import controllers.annotation.GetRequestIp;
import dto.inventory.SearchSkuProductCloudInventoryDto;
import dto.product_inventory.CloudAndMicroInventoryDto;
import dto.product_inventory.ErpStockInResult;
import dto.product_inventory.SearchCloudInventoryResult;
import entity.product_inventory.ProductInventoryBatchDetail;
import entity.product_inventory.ProductInventoryDetail;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.product_inventory.IProductCloudInventoryService;

/**
 * 商品库，云仓控制器
 * @author duyuntao
 */
@GetRequestIp
public class ProductCloudInventoyController extends Controller {

	@Inject
	private IProductCloudInventoryService productCloudInventoryService;
	

	/**
	 * 获取ERP的入仓记录，修改云仓的数量
	 * @return
	 */
	public Result erpStockInDetail() {
		Logger.info("============>获取ERP的入仓记录，修改云仓数量:{}", request().body().asJson().toString());
		try {
			//每个进口仓库的入仓就
			ProductInventoryBatchDetail[] productInventoryBatchDetailList = Json.fromJson(request().body().asJson(), ProductInventoryBatchDetail[].class);
			//			1.1：保存ERP的入仓历史记录到t_product_inventory_batch_detail
			//			1.2：统计此次入仓记录的总数，添加到云仓总计表
			return ok(Json.toJson(productCloudInventoryService.erpStockInDetail(Lists.newArrayList(productInventoryBatchDetailList))));
		} catch (Exception e) {
			Logger.error(">>>>>>>>>>>>>获取ERP的入仓记录，修改云仓数量异常:{}", e);
			ErpStockInResult erpStockInResult = new ErpStockInResult();
			erpStockInResult.setResult(false);
			erpStockInResult.setMsg("获取ERP的入仓记录异常!");
			return ok(Json.toJson(erpStockInResult));
		}
	}
	
	/**
	 * 查询一组SKU的云仓数量
	 * @param {"warehouseid":1,"skus":["sku1","sku2","sku3"...]}
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result list() {
		Logger.info("===============>查询一组SKU的云仓数量:{}", request().body().asJson().toString());
		try {
			JsonNode skus = request().body().asJson();

			SearchSkuProductCloudInventoryDto searchSkuProductCloudInventoryDto = Json.fromJson(skus, SearchSkuProductCloudInventoryDto.class);

			return ok(Json.toJson(productCloudInventoryService.list(searchSkuProductCloudInventoryDto)));
		} catch (Exception e) {
			Logger.error("查询SKU云仓数量出错!{}", e);
			SearchCloudInventoryResult searchCloudInventoryResult = new SearchCloudInventoryResult();
			searchCloudInventoryResult.setType(2);
			searchCloudInventoryResult.setMsg("系统异常!" + e);
			return ok(Json.toJson(searchCloudInventoryResult));
		}


		//			1.1：云仓 = 总仓 - 有效的即时锁定的库存
	}

	/**
	 * 查询一组sku的云仓和微仓数量
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result searchProductCloudAndMicroInventory() {
		try {
			CloudAndMicroInventoryDto cloudAndMicroInventoryDto = Json.fromJson(request().body().asJson(), CloudAndMicroInventoryDto.class);
			return ok(Json.toJson(productCloudInventoryService.searchProductCloudAndMicroInventory(cloudAndMicroInventoryDto)));
		} catch (Exception e) {
			Logger.error(">>>>>>>>>>>>>>> searchProductCloudAndMicroInventory error:{}", e);
			return ok(Json.toJson(false));
		}
	}

	/**
	 * 查询一组(sku,warehouseId) 的云仓和微仓数量
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result searchProductCloudInventory() {
		try {
			JsonNode main = request().body().asJson();
			Logger.info("searchProductCloudInventoryParam{}", main.toString());
			if (main.size() == 0) {
				return internalServerError("Expecting Json data");
			}
			JsonNode jsonNode = main.get("skuWarehouseIdArray");
			List<ProductInventoryDetail> reqParam=Lists.newArrayList();
			for(JsonNode skuAndWarehouseId:jsonNode){
				ProductInventoryDetail detail=new ProductInventoryDetail();
				String sku = skuAndWarehouseId.get("sku").asText();
				int warehoueId = skuAndWarehouseId.get("warehouseId").asInt();
				detail.setSku(sku);
				detail.setWarehouseId(warehoueId);
				reqParam.add(detail);
			}
			return ok(Json.toJson(productCloudInventoryService.searchProductCloudInventory(reqParam)));
			} catch (Exception e) {
				Logger.error(">>>>>>>>>>>>>>> searchProductCloudAndMicroInventory error:{}", e);
				return ok(Json.toJson(false));
		}
	}
	

	/**
	 * 展示云仓明细
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result getCloudInventoryDetail() {
		JsonNode main = request().body().asJson();

		Logger.info("查询云仓明细:[{}]",main);

		if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}

		String s_main = main.toString();
		//设置日期格式
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(fmt);
		Json.setObjectMapper(mapper);
		return ok(Json.toJson(productCloudInventoryService.getCloudInventoryDetail(s_main)));
	}
	/**
	 * 查询库存分布
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result getInventoryDispersion(){
		String sku = request().getQueryString("sku");
		Integer warehouseId = Integer.valueOf(request().getQueryString("warehouseId"));
		String resut_s=productCloudInventoryService.getInventoryDispersion(sku,warehouseId);
		JsonNode result_node = Json.parse(resut_s);
		return ok(result_node);
	}
	


//创建新方法时可以直接cope 本段代码作为起始代码	
//	public Result tets() {
//		
//		try {
//			pibd = DataUtil.fromJson(List.class, request().body().asJson().toString());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		return ok(pibd.toString());
//	}
	
	/**
	 * ERP 库存查询返回值
	 * {
		  "timestamp": 1484186235134,
		  "code": 200,
		  "message": "success",
		  "version": "1.0",
		  "page_index": 0,
		  "page_size": 0,
		  "total_count": 1,
		  "page_total": 0,
		  "has_next_page": false,
		  "result": [
		    {
		      "_id": "1",
		      "sku": "IF639",
		      "wait_delivery_count": 25298,
		      "expiration": [
		        {
		          "productionDate": "2017-12-07",
		          "leftCount": 143996
		        },
		        {
		          "productionDate": "2017-02-08",
		          "leftCount": 6805
		        },
		        {
		          "productionDate": "2017-04-27",
		          "leftCount": 1350
		        }
		      ]
		    }
		  ]
		}
	 * 
	 * 
	 * 查看erp库存
	 * @author zbc
	 * @since 2017年1月12日 上午10:38:24
	 */
	public Result erpStock(){
		JsonNode node = request().body().asJson();
		Logger.info("获取erp库存参数:[{}]",node);
		Map<String,Object> res = Maps.newHashMap();
		if(node == null||node!=null&&(!node.has("skus")||!node.has("warehouseId"))){
			res.put("suc", false);
			res.put("msg", "参数错误");
		}else{
			res = productCloudInventoryService.erpStock(node.toString());
		}
		return ok(Json.toJson(res));
	}
	
}
