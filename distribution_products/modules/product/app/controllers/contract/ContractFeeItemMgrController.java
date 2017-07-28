package controllers.contract;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;

import controllers.annotation.ALogin;
import dto.contract.fee.ContractFeeItemDto;
import dto.contract.fee.ContractFeeItemPageQeuryParam;
import dto.contract.fee.ContractFeeParam;
import dto.contract.fee.GetContractFeeItemsParam;
import dto.contract.fee.GetContractFeeItemsParam.ContractParam;
import dto.contract.fee.GetContractFeeItemsParam.SkuWarehouseId;
import entity.contract.ContractFeeItem;
import entity.contract.ContractFeeItemRelatedSku;
import entity.contract.ContractQuotations;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.product.IContractFeeItemMgrService;
import services.product.IQuotedService;
import services.product.IUserService;
import util.product.DateUtils;
import util.product.JsonCaseUtil;
import util.product.Page;
import util.product.RegExpValidatorUtils;

@Api(value="/合同费用项管理",description="合同费用项管理")
public class ContractFeeItemMgrController extends Controller {
	
	@Inject
	private IContractFeeItemMgrService contractFeeItemMgrService;
	@Inject
	private IUserService userService;
	@Inject
	private IQuotedService quotedService;
	
	@ApiOperation(value="查询费用适用的商品", httpMethod="POST", notes="contractNo:合同号，必传<br/>"
			+ "categoryId:商品分类<br/>"
			+ "warehouseId:仓库id<br/>"
			+ "searchText:商品编号/商品名称（,隔开可多个查询）<br/>"
			+ "currPage:当前页码,必传<br/>"
			+ "pageSize:页记录数,必传<br/>")
	@ApiImplicitParams({
		@ApiImplicitParam(name="body",required=true, value="",
				defaultValue="{"
					+"\"contractNo\":\"HT2017050915114300000256\","
					+"\"categoryId\":4706,"
					+"\"warehouseId\":2024,"
					+"\"searchText\":\"IF942-1,IF942-2\","
					+"\"currPage\":1,"
					+"\"pageSize\":10"
				+"}",
				dataType="string",paramType="body")
		}
	)
	@ALogin
	public Result getContractPros() {
		// {categoryId, warehouseId, searchText:xxxx}
		Map<String, Object> result = Maps.newHashMap();
		JsonNode node = request().body().asJson();
		Logger.info("获取合同商品，参数：{}",node);
		if (null == node || !node.has("currPage")
				|| JsonCaseUtil.jsonToInteger(node.get("currPage"))<1
				|| !node.has("pageSize")
				|| JsonCaseUtil.jsonToInteger(node.get("pageSize"))<1
				|| !node.has("contractNo")) {
			result.put("suc", false);
			result.put("msg", "请检查参数是否正确");
			return ok(Json.toJson(result));
		}
		
		Map<String, Object> param = Maps.newHashMap();
		param.put("page", JsonCaseUtil.jsonToInteger(node.get("currPage")));
		param.put("rows", JsonCaseUtil.jsonToInteger(node.get("pageSize")));
		param.put("contractNo", JsonCaseUtil.jsonToString(node.get("contractNo")));
		if (node.has("warehouseId") && StringUtils.isNotEmpty(node.get("warehouseId").asText())) {
			param.put("warehouseId", node.get("warehouseId").asInt());	
		}
		if (node.has("categoryId") && StringUtils.isNotEmpty(node.get("categoryId").asText())) {
			param.put("categoryId", node.get("categoryId").asInt());	
		}
		if (node.has("searchText") && StringUtils.isNotEmpty(node.get("searchText").asText())) {
			param.put("search", node.get("searchText").asText());	
		}
		param.put("status", ContractQuotations.HAS_BEGUN);
		
		result.put("suc", true);
		String opUser = userService.getAdminAccount();
		result.put("data", quotedService.getQuoted(Json.toJson(param), opUser));
		return ok(Json.toJson(result));
	}
	
	@ApiOperation(value = "获取合同费用项操作日志", notes = "feeItemId:合同费用项id，必传<br/>"
			+ "返回值optType说明：<br/>"
			+ "1添加预估费用<br/>2添加实际费用<br/>3修改预估费用<br/>4修改实际费用<br/>"
			+ "5添加费用率<br/>6修改费用率", 
			nickname = "", httpMethod = "GET", produces = "text/plain")
	@ApiImplicitParams({
			@ApiImplicitParam(name="feeItemId",value="合同费用项id",required=true,paramType="path",dataType="Integer")
	})
	public Result getLogs(Integer feeItemId){
		Logger.info("获取合同费用项操作日志，参数：feeItemId = {}",feeItemId);
		Map<String, Object> resultMap = Maps.newHashMap();
		if (feeItemId==null || feeItemId<1) {
			resultMap.put("suc", false);
			resultMap.put("msg", "参数无效");
			return ok(Json.toJson(resultMap));
		}
		return ok(Json.toJson(contractFeeItemMgrService.getLogs(feeItemId)));
	}
	
	/**
	 * 检查录入实际费用的参数是否缺少，true就是有问题
	 */
	private boolean checkInputRealFeeParams(JsonNode node){
		return node==null 
				|| !node.has("feeItemId") 
				|| StringUtils.isBlank(JsonCaseUtil.jsonToString(node.get("feeItemId")))
				|| !node.has("realTotalCost")  
				|| StringUtils.isBlank(JsonCaseUtil.jsonToString(node.get("realTotalCost")))
				|| !node.has("realTotalPerformance") 
				|| StringUtils.isBlank(JsonCaseUtil.jsonToString(node.get("realTotalPerformance")));
	}
	
	@ApiOperation(value="录入实际费用", httpMethod="POST", notes="feeItemId:合同费用项id，必传<br/>"
				+ "realTotalCost:实际费用,必传<br/>realTotalPerformance:实际业绩，必传")
	@ApiImplicitParams({
		@ApiImplicitParam(name="body",required=true, value="",
				defaultValue="{"
					+"\"feeItemId\":6,"
					+"\"realTotalCost\":1,"
					+"\"realTotalPerformance\":1"
				+"}",
				dataType="string",paramType="body")
		}
	)
	@ALogin
	public Result inputRealFee(){
		Map<String, Object> resultMap = Maps.newHashMap();
		JsonNode node = request().body().asJson();
		Logger.info("合同费用项录入实际费用，参数：{}",node);
		if (checkInputRealFeeParams(node)) {
			resultMap.put("suc", false);
			resultMap.put("msg", "参数错误");
			return ok(Json.toJson(resultMap));
		}
		
		ContractFeeParam param = new ContractFeeParam();
		param.setFeeItemId(JsonCaseUtil.jsonToInteger(node.get("feeItemId")));
		param.setRealTotalCost(JsonCaseUtil.jsonToDouble(node.get("realTotalCost")));
		param.setRealTotalPerformance(JsonCaseUtil.jsonToDouble(node.get("realTotalPerformance")));
		param.setOptUser(userService.getAdminAccount());
		return ok(Json.toJson(contractFeeItemMgrService.inputRealFee(param)));
	}
	
	@ApiOperation(value = "删除合同费用项", notes = "feeItemId:合同费用项id，必传", nickname = "", httpMethod = "GET", produces = "text/plain")
	@ApiImplicitParams({
			@ApiImplicitParam(name="feeItemId",value="合同费用项id",required=true,paramType="path",dataType="Integer")
	})
	public Result deleteContractFeeItem(Integer feeItemId){
		Logger.info("删除合同费用项，参数：feeItemId = {}",feeItemId);
		Map<String, Object> resultMap = Maps.newHashMap();
		if (feeItemId==null || feeItemId<1) {
			resultMap.put("suc", false);
			resultMap.put("msg", "参数无效");
			return ok(Json.toJson(resultMap));
		}
		return ok(Json.toJson(contractFeeItemMgrService.deleteContractFeeItem(feeItemId)));
	}
	
	@ApiOperation(value = "提前结束合同费用项", notes = "feeItemId:合同费用项id，必传", nickname = "", httpMethod = "GET", produces = "text/plain")
	@ApiImplicitParams({
			@ApiImplicitParam(name="feeItemId",value="合同费用项id",required=true,paramType="path",dataType="Integer")
	})
	@ALogin
	public Result finishAheadOfTime(Integer feeItemId){
		Logger.info("提前结束合同费用项，参数：feeItemId = {}",feeItemId);
		Map<String, Object> resultMap = Maps.newHashMap();
		if (feeItemId==null || feeItemId<1) {
			resultMap.put("suc", false);
			resultMap.put("msg", "参数无效");
			return ok(Json.toJson(resultMap));
		}
		return ok(Json.toJson(contractFeeItemMgrService.finishAheadOfTime(feeItemId, userService.getAdminAccount())));
	}
	
	/**
	 * 检查新增/更新费用项的值，true就是有问题
	 */
	private boolean checkAddOrUpdateCommonParams(JsonNode node){
		boolean matchedCdts = node.get("matchedCdts").asBoolean();
		if (matchedCdts) {
			if (!node.has("conditions")) {
				return true;
			}
		} else {
			if (!node.has("relatedSkus")) {
				return true;
			}
		}
		
		if (node.has("feeRate")) {
			// 费用率
			boolean contentEmpty = StringUtils.isBlank(JsonCaseUtil.jsonToString(node.get("feeRate")));
			if (!contentEmpty) {
				Double value = JsonCaseUtil.jsonToDouble(node.get("feeRate"));
				// 取值范围确定
				if (value>0 && value<=1) {
					return false;
				} else {
					return true;// 参数取值范围不对
				}
			}
			return contentEmpty;
		} else {
			boolean badParams = !node.has("estimatedTotalCost") 
					|| StringUtils.isBlank(JsonCaseUtil.jsonToString(node.get("estimatedTotalCost")))
					|| !node.has("estimatedTotalPerformance")
					|| StringUtils.isBlank(JsonCaseUtil.jsonToString(node.get("estimatedTotalPerformance")));
			if (!badParams) {
				// 参数没问题
				Double estimatedTotalCost = JsonCaseUtil.jsonToDouble(node.get("estimatedTotalCost"));
				Double estimatedTotalPerformance = JsonCaseUtil.jsonToDouble(node.get("estimatedTotalPerformance"));
				if (estimatedTotalCost<=0 || estimatedTotalPerformance<=0) {
					return true;// 参数取值范围不对
				} else {
					return false;
				}
			}
			// 费用值
			return badParams;
		}
	}
	
	@ApiOperation(value="新增合同费用项", httpMethod="POST", notes="feeTypeId：费用项id，必传<br/>"
			+ "contractNo：合同号，必传<br/>"
			+ "matchedCdts：是否符合查询条件，true/false<br/><br/>"
			+ "以下为2选1（必传）：<br/>"
			+ "1、当matchedCdts为true时，表明勾选了符合条件的合同商品，此时，需要传递参数如下：<br/>"
			+ "1.1、conditions：{\"categoryId\":\"4757\", \"warehouseId\":2024, \"search\":\"食品\"}<br/>"
			+ "2、matchedCdts为false时，表明不勾选了符合条件的合同商品，此时，需要传递参数如下：<br/>"
			+ "2.1、relatedSkus：[{\"sku\":\"IM148\",\"warehouseId\":2024}]<br/>"
			+ "<br/>以下为2选1（必传）：<br/>"
			+ "1、当是固定值时，传的参数是：estimatedTotalCost（预估费用）和estimatedTotalPerformance（预估业绩）<br/>"
			+ "2、当是固定费用率时，传的参数是：feeRate（费用率，取值范围：(0,1]）")
	@ApiImplicitParams({
		@ApiImplicitParam(name="body",required=true, value="",
				defaultValue="{"
					+"\"feeTypeId\":1,"
					+"\"contractNo\":\"HT2017050915114300000256\","
					+"\"estimatedTotalCost\":1,"
					+"\"estimatedTotalPerformance\":1,"
					+"\"remarks\":\"这里是备注\","
					+"\"matchedCdts\":false,"
					+"\"relatedSkus\":[{"
					+"	\"sku\":\"IF942-1\","
					+"	\"warehouseId\":2024"
					+"}]"
				+"}",
				dataType="string",paramType="body")
		}
	)
	@ALogin
	public Result addContractFeeItem(){
		Map<String, Object> resultMap = Maps.newHashMap();
		JsonNode node = request().body().asJson();
		Logger.info("添加合同费用项，参数：{}",node);
		if (node==null 
				|| !node.has("feeTypeId")
				|| StringUtils.isBlank(JsonCaseUtil.jsonToString(node.get("feeTypeId")))
				|| !node.has("contractNo") 
				|| StringUtils.isBlank(JsonCaseUtil.jsonToString(node.get("contractNo")))
				|| !node.has("matchedCdts")
				|| checkAddOrUpdateCommonParams(node)) {
			resultMap.put("suc", false);
			resultMap.put("msg", "参数错误");
			return ok(Json.toJson(resultMap));
		}
		
		ContractFeeParam param = null;
		try {
			param = parseAddParams(node);
		} catch (Exception e) {
			Logger.info("新增合同费用项，解析参数异常：{}",e);
			resultMap.put("suc", false);
			resultMap.put("msg", "参数错误");
			return ok(Json.toJson(resultMap));
		}
		
		// 检查参数是否合理
		if (param.getMatchedCdts()==null) {
			Logger.info("新增合同费用项，缺少matchedCdts");
			resultMap.put("suc", false);
			resultMap.put("msg", "参数错误");
			return ok(Json.toJson(resultMap));
		}
		if (!param.getMatchedCdts()) {
			if (CollectionUtils.isEmpty(param.getRelatedSkus())) {
				Logger.info("新增合同费用项，请选择关联的合同商品");
				resultMap.put("suc", false);
				resultMap.put("msg", "请选择关联的合同商品");
				return ok(Json.toJson(resultMap));
			}
		}
		
		param.setOptUser(userService.getAdminAccount());
		resultMap = contractFeeItemMgrService.addContractFeeItem(param);
		return ok(Json.toJson(resultMap));
	}
	
	@ApiOperation(value="更新合同费用项", httpMethod="POST", notes="feeItemId:合同费用项id，必传<br/>"
			+ "contractNo:合同号，必传<br/>"
			+ "matchedCdts：是否符合查询条件，true/false<br/><br/>"
			+ "以下为2选1（必传）：<br/>"
			+ "1、当matchedCdts为true时，表明勾选了符合条件的合同商品，此时，需要传递参数如下：<br/>"
			+ "1.1、conditions：{\"categoryId\":\"4757\", \"warehouseId\":2024, \"search\":\"食品\"}<br/>"
			+ "2、matchedCdts为false时，表明不勾选了符合条件的合同商品，此时，需要传递参数如下：<br/>"
			+ "2.1、relatedSkus：[{\"sku\":\"IM148\",\"warehouseId\":2024}]<br/>"
			+ "3、当不传递matchedCdts时，表明不修改合同商品<br/>"
			+ "<br/>以下为2选1（必传）：<br/>"
			+ "1、当是固定值时，传的参数是：estimatedTotalCost（预估费用）和estimatedTotalPerformance（预估业绩）<br/>"
			+ "2、当是固定费用率时，传的参数是：feeRate（费用率，取值范围：(0,1]）")
	@ApiImplicitParams({
		@ApiImplicitParam(name="body",required=true, value="",
				defaultValue="{"
					+"\"feeItemId\":6,"
					+"\"estimatedTotalCost\":2,"
					+"\"estimatedTotalPerformance\":3,"
					+"\"remarks\":\"这里是备注\","
					+"\"matchedCdts\":false,"
					+"\"relatedSkus\":[{"
					+"	\"sku\":\"IF942-1\","
					+"	\"warehouseId\":2024"
					+"}]"
				+"}",
				dataType="string",paramType="body")
		}
	)
	@ALogin
	public Result updateContractFeeItem(){
		Map<String, Object> resultMap = Maps.newHashMap();
		JsonNode node = request().body().asJson();
		Logger.info("更新合同费用项，参数：{}",node);
		if (node==null 
				|| !node.has("feeItemId") 
				|| StringUtils.isBlank(JsonCaseUtil.jsonToString(node.get("feeItemId")))
				|| checkAddOrUpdateCommonParams(node)) {
			resultMap.put("suc", false);
			resultMap.put("msg", "参数错误");
			return ok(Json.toJson(resultMap));
		}
		
		ContractFeeParam param = null;
		try {
			param = parseUpdateParams(node);
		} catch (Exception e) {
			Logger.info("更新合同费用项，解析参数异常：{}",e);
			resultMap.put("suc", false);
			resultMap.put("msg", "参数错误");
			return ok(Json.toJson(resultMap));
		}
		
		param.setOptUser(userService.getAdminAccount());
		resultMap = contractFeeItemMgrService.updateContractFeeItem(param);
		return ok(Json.toJson(resultMap));
	}
	
	/**
	 * 解析新增费用项的参数
	 */
	private ContractFeeParam parseAddParams(JsonNode node){
		ContractFeeParam param = parseContractFeeParam(node);
		param.setFeeTypeId(JsonCaseUtil.jsonToInteger(node.get("feeTypeId")));
		param.setContractNo(JsonCaseUtil.jsonToString(node.get("contractNo")));
		return param;
	}
	
	/**
	 * 解析更新费用项的参数
	 */
	private ContractFeeParam parseUpdateParams(JsonNode node){
		ContractFeeParam param = parseContractFeeParam(node);
		param.setFeeItemId(JsonCaseUtil.jsonToInteger(node.get("feeItemId")));
		param.setContractNo(JsonCaseUtil.jsonToString(node.get("contractNo")));
		return param;
	}
	
	private ContractFeeParam parseContractFeeParam(JsonNode node){
		ContractFeeParam param = new ContractFeeParam();

		if (node.has("feeRate")) {
			Double feeRate = JsonCaseUtil.jsonToDouble(node.get("feeRate"));
			param.setFeeRate(feeRate);
		} else {
			Double estimatedTotalCost = JsonCaseUtil.jsonToDouble(node.get("estimatedTotalCost"));
			Double estimatedTotalPerformance = JsonCaseUtil.jsonToDouble(node.get("estimatedTotalPerformance"));
			param.setEstimatedTotalCost(estimatedTotalCost);
			param.setEstimatedTotalPerformance(estimatedTotalPerformance);
		}
		
		if (node.has("matchedCdts")) {
			param.setMatchedCdts(node.get("matchedCdts").asBoolean());
			if (param.getMatchedCdts()) {// 符合条件的
				JsonNode conditionsNode = node.get("conditions");
				Map<String,Object> map = Maps.newHashMap();
				if(conditionsNode.has("categoryId")){
					map.put("categoryId", conditionsNode.get("categoryId").asInt());	
				}
				if(conditionsNode.has("warehouseId")){
					map.put("warehouseId", conditionsNode.get("warehouseId").asInt());	
				}
				if(conditionsNode.has("search") && StringUtils.isNotBlank(conditionsNode.get("search").asText())){
					map.put("search", conditionsNode.get("search").asText());	
				}
				map.put("contractNo", JsonCaseUtil.jsonToString(node.get("contractNo")));
				param.setCdtsMap(map);
			} else {// 直接选择sku的
				List<ContractFeeItemRelatedSku> relatedSkus = Lists.newArrayList();
				for(Iterator<JsonNode> it = node.get("relatedSkus").iterator(); it.hasNext(); ) {
					JsonNode proNode = it.next();
					ContractFeeItemRelatedSku relatedSku = new ContractFeeItemRelatedSku(
							JsonCaseUtil.jsonToString(proNode.get("sku")),
							JsonCaseUtil.jsonToInteger(proNode.get("warehouseId")) );
					relatedSkus.add(relatedSku);
				}
				param.setRelatedSkus(relatedSkus);
			}
		}
		
		param.setRemarks(node.has("remarks")?JsonCaseUtil.jsonToString(node.get("remarks")):null);
		return param;
	}
	
	/**
	 [
		{
		  "contractNo": "HT001", 
		  "pros": [
		    {
		      "sku": "IF639", 
		      "warehouseId": 2024
		    }
		  ]
		},...
	]
	 * @return
	 */
	@ApiOperation(value="获取用于计算的合同费用项", httpMethod="POST", notes="contractNo:合同号，必传<br/>pros:类型id,必传")
	@ApiImplicitParams({
		@ApiImplicitParam(name="body",required=true, value="",
				defaultValue="[{\"contractNo\": \"HT2017050915114300000256\", \"pros\": [{\"sku\": \"IF942-1\", \"warehouseId\": 2024}]}]",
				dataType="string",paramType="body")
		}
	)
	public Result getContractFeeItems4Calculation() {
		Map<String, Object> resultMap = Maps.newHashMap();
		JsonNode node = request().body().asJson();
		Logger.info("获取用于计算的合同费用项，参数：{}",node);
		if (node==null) {
			resultMap.put("suc", false);
			resultMap.put("msg", "参数错误");
			return ok(Json.toJson(resultMap));
		}
		
		GetContractFeeItemsParam param = null;
		try {
			param = parseGetContractFeeItemsParams(node);
		} catch (Exception e) {
			Logger.info("合同费用项管理，获取用于计算的合同费用项异常：{}",e);
			resultMap.put("suc", false);
			resultMap.put("msg", "参数错误");
			return ok(Json.toJson(resultMap));
		}
		if (param==null) {
			resultMap.put("suc", false);
			resultMap.put("msg", "参数错误");
			return ok(Json.toJson(resultMap));
		}
		
		resultMap = contractFeeItemMgrService.getContractFeeItems4Calculation(param);
		return ok(Json.toJson(resultMap));
	}

	private GetContractFeeItemsParam parseGetContractFeeItemsParams(JsonNode node) throws ParseException {
		/*
		 {
		 	"contracts":[
		 		{
		 			"pros":[{"sku":"IF942-1","warehouseId":2024}],
					"contractNo":"HT2017050915114300000256"
				}
			],
			"payDate":"2017-05-16 10:20:46"
		}
		 */
		LocalDateTime payDate = DateUtils.toLocalDateTime(DateUtils.string2date(JsonCaseUtil.jsonToString(node.get("payDate")), DateUtils.FORMAT_FULL_DATETIME));
		
		List<ContractParam> contracts = Lists.newArrayList();
		for (Iterator<JsonNode> it = node.get("contracts").iterator(); it.hasNext(); ) {
			JsonNode nextNode = it.next();
			// sku集合
			List<SkuWarehouseId> pros = Lists.newArrayList();
			for (Iterator<JsonNode> prosIt = nextNode.get("pros").iterator(); prosIt.hasNext(); ) {
				JsonNode proNode = prosIt.next();
				pros.add(new GetContractFeeItemsParam.SkuWarehouseId(JsonCaseUtil.jsonToString(proNode.get("sku")),
								JsonCaseUtil.jsonToInteger(proNode.get("warehouseId"))));
			}
			ContractParam contractParam = new ContractParam(
					JsonCaseUtil.jsonToString(nextNode.get("contractNo")),
					pros);
			contracts.add(contractParam);
		}
		GetContractFeeItemsParam param = new GetContractFeeItemsParam(contracts, payDate);
		return param;
	}
	
	@ApiOperation(value = "查看合同费用项", notes = "feeItemId:合同费用项id，必传<br/>", 
			nickname = "", httpMethod = "GET", produces = "text/plain")
	@ApiImplicitParams({
			@ApiImplicitParam(name="feeItemId",value="合同费用项id",required=true,paramType="path",dataType="Integer")
	})
	public Result getContractFeeItemDto(Integer feeItemId){
		Logger.info("查看合同费用项，参数：feeItemId = {}",feeItemId);
		Map<String, Object> resultMap = Maps.newHashMap();
		if (feeItemId==null || feeItemId<1) {
			resultMap.put("suc", false);
			resultMap.put("msg", "参数无效");
			return ok(Json.toJson(resultMap));
		}
		
		ContractFeeItemDto dto = contractFeeItemMgrService.getContractFeeItemDto(feeItemId);
		if (dto==null) {
			resultMap.put("suc", false);
			resultMap.put("msg", "参数无效");
			return ok(Json.toJson(resultMap));
		}
		
		resultMap.put("suc", true);
		resultMap.put("result", dto);
		return ok(Json.toJson(resultMap));
	}
	
	@ApiOperation(value="新增合同费用项", httpMethod="POST", notes="contractNo:合同号，必传<br/>"
			+ "currPage:当前页码,必传<br/>"
			+ "pageSize:页记录数,必传<br/>")
	@ApiImplicitParams({
		@ApiImplicitParam(name="body",required=true, value="",
				defaultValue="{"
					+"\"contractNo\":\"HT2017050915114300000256\","
					+"\"currPage\":1,"
					+"\"pageSize\":10"
				+"}",
				dataType="string",paramType="body")
		}
	)
	public Result getContractFeeItemsPage(){
		Map<String, Object> resultMap = Maps.newHashMap();
		JsonNode node = request().body().asJson();
		Logger.info("合同费用项分页查询，参数：{}",node);
		if (checkPageQueryParams(node)) {
			resultMap.put("suc", false);
			resultMap.put("msg", "参数错误");
			return ok(Json.toJson(resultMap));
		}
		ContractFeeItemPageQeuryParam param = new ContractFeeItemPageQeuryParam(
				JsonCaseUtil.jsonToString(node.get("contractNo")),
				JsonCaseUtil.jsonToInteger(node.get("currPage")),
				JsonCaseUtil.jsonToInteger(node.get("pageSize"))
				);
		Page<ContractFeeItem> page = contractFeeItemMgrService.getContractFeeItemsPage(param);
		resultMap.put("suc", true);
		resultMap.put("result", page);
		return ok(Json.toJson(resultMap));
	}
	/**
	 * 检查参数，true就是有问题
	 */
	private boolean checkPageQueryParams(JsonNode node) {
		return node==null 
				|| !node.has("contractNo") 
				|| StringUtils.isBlank(JsonCaseUtil.jsonToString(node.get("contractNo")))
				|| !node.has("currPage")
				|| StringUtils.isBlank(JsonCaseUtil.jsonToString(node.get("currPage")))
				|| JsonCaseUtil.jsonToInteger(node.get("currPage")) < 1
				|| !node.has("pageSize")
				|| JsonCaseUtil.jsonToInteger(node.get("pageSize")) < 1
				|| StringUtils.isBlank(JsonCaseUtil.jsonToString(node.get("pageSize")));
	}
}
