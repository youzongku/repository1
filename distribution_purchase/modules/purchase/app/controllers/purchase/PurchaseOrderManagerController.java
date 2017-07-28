package controllers.purchase;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;

import annotation.ALogin;
import annotation.DivisionMember;
import dto.purchase.ReturnMess;
import dto.purchase.ViewPurchaseIterm;
import entity.purchase.PurchaseOrderInput;
import forms.purchase.DeliverDutyPaidGoodsParam;
import forms.purchase.InputOrderParam;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.Context;
import play.mvc.Http.Cookie;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import services.purchase.IPurchaseOrderManagerService;
import services.purchase.IPurchaseOrderService;
import services.purchase.IUserService;
import utils.purchase.JsonCaseUtil;
import utils.purchase.StringUtils;


/**
 * 描述:采购订单管理控制类
 * @author hanfs
 */
@Api(value="/returnOrderManager",description="后台相关的")
public class PurchaseOrderManagerController extends Controller{

	@Inject private IPurchaseOrderService iPurchaseOrderService;
	@Inject	private IUserService userService;
	@Inject	private IPurchaseOrderManagerService purchaseOrderManagerService; 
    
	/**
	 * 采购单商品退货情况
	 * 参数：{returnOrderNo:WCTH201702220000000031}
	 * @return {suc:true, purchaseOrder:{}, poInfoList:[{}]}
	 */
	@ApiOperation(value = "采购单商品退货情况", notes = "", nickname = "", httpMethod = "POST")
	@ApiImplicitParams({
	@ApiImplicitParam(name = "body", value = "采购单商品退货情况", required = true, paramType = "body" 
		,defaultValue = "{\"returnOrderNo\":\"WCTH201702220000000031\"}"
			) })
	public Result getReturnInfo4PurchaseOrder() {
		// 拿到退货单单号
		JsonNode node = request().body().asJson();
		Logger.info("采购单商品退货情况，参数：{}",node);
		if (node == null || !node.has("returnOrderNo")
				|| StringUtils.isBlankOrNull(node.get("returnOrderNo").asText())) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
		
		String returnOrderNo = node.get("returnOrderNo").asText();
		Map<String, Object> result = purchaseOrderManagerService.getReturnInfo4PurchaseOrder(returnOrderNo);
		return ok(Json.toJson(result));
	}
	
    /**
     * 描述：采购单展示
     * @return
     */
    public Result viewManagerPurchaseOrder(){
        ViewPurchaseIterm iterm = new ViewPurchaseIterm();
        //初始默认为0条记录，页数1
        iterm.setTotal(0);
        iterm.setPages(1);
        Cookie cookie = Context.current().request().cookie("CURRENT_LOGIN_USER");
        JsonNode node = request().body().asJson();
        Logger.debug(">>>viewPurchase>>>>>node>>"+node.toString());
        if(cookie==null){
            iterm.setReturnMess(new ReturnMess("2","尚未登录"));
            Logger.debug(">>viewPurchase>>>>iterm>>"+Json.toJson(iterm).toString());
            return ok(Json.toJson(iterm));
        }
        
        if(!node.has("pageCount") || !node.has("pageSize")){
            iterm.setReturnMess(new ReturnMess("1","input error!参数输入错误"));
            Logger.debug(">>viewPurchase>>>>iterm>>"+Json.toJson(iterm).toString());
            return ok(Json.toJson(iterm));
        }

        String email = cookie.value();
        Logger.debug(">>>viewPurchase>>>>>email>>"+email);
        int pageCount = node.get("pageCount").asInt();
        int pageSize = node.get("pageSize").asInt();
        int pageNow = (pageCount == 0 ?0 :(pageCount - 1) * pageSize);
        Map<String, Object> map = Maps.newHashMap();
        map.put("email", email);
        map.put("pageSize", pageSize);
        map.put("pageNow", pageNow);
        if(node.has("orderDate"))
            map.put("orderDate", node.get("orderDate").asInt());
        if(node.has("status") && node.get("status").asInt() != 9)//9表示所有订单状态
            map.put("status", node.get("status").asInt());
        Logger.debug(">>>viewPurchase>>map>>>"+map.toString());
        iterm = iPurchaseOrderService.viewPurchase(map);
        Logger.debug(">>viewPurchase>>>>iterm>>"+Json.toJson(iterm).toString());
        return ok(Json.toJson(iterm));
    }
    
    /**
     * 导入采购单
     * @return
     */
    @DivisionMember
    public Result importOrder(){
		MultipartFormData formData = request().body().asMultipartFormData();
		if (formData == null) {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("flag", false);
			resultMap.put("msg", "系统错误，上传失败！");
			return ok(Json.toJson(resultMap));
		}
		
		List<FilePart> files = formData.getFiles();
		File file = files.get(0).getFile();
		String fileName =  files.get(0).getFilename();
        Map<String, String[]> params = Maps.newHashMap(formData.asFormUrlEncoded());
		return ok(Json.toJson(this.iPurchaseOrderService.importOrder(file, fileName, params, userService.getAccounts())));
    }
    
    /**
     * 后台查询导入的采购单
     * @return
     */
    @ALogin
    public Result getImportOrder(){
    	JsonNode node = request().body().asJson();
    	if (node == null || !node.has("entryUser")) {
    		Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("suc", false);
			resultMap.put("msg", "参数错误！");
			return ok(Json.toJson(resultMap));
		}
    	
    	return ok(Json.toJson(iPurchaseOrderService.getImportOrder(node, PurchaseOrderInput.INPUT_TYPE_IMPORT)));
    }
    
    @ALogin
    public Result proUpdate(){
    	JsonNode  node = request().body().asJson();
    	if(node == null||!node.has("id")||!node.has("warehouseId")){
    		Map<String, Object> resultMap = new HashMap<String, Object>();
    		resultMap.put("suc", false);
			resultMap.put("msg", "参数错误！");
			return ok(Json.toJson(resultMap));
    	}
    	
    	return ok(Json.toJson(iPurchaseOrderService.proUpdate(node)));
    }
    
    public Result giftUpdate(){
    	JsonNode  node = request().body().asJson();
    	if(node == null||!node.has("id")||!node.has("warehouseId")){
    		Map<String, Object> resultMap = new HashMap<String, Object>();
    		resultMap.put("suc", false);
			resultMap.put("msg", "参数错误！");
			return ok(Json.toJson(resultMap));
    	}
    	
    	return ok(Json.toJson(iPurchaseOrderService.giftUpdate(node)));
    }
    
    /**
     * 录入下单
     * @author zbc
     * @since 2016年9月1日 下午4:36:12
     */
    @ALogin
    public Result inputOrder(){
    	// {inputId:1, isPaied:true, money:1.1}
    	JsonNode  node = request().body().asJson();
    	int inputId = node.get("inputId").asInt();
    	String payType = node.has("payType") ? node.get("payType").asText() : null;
    	double money = "cash".equals(payType) ? node.get("money").asDouble() : 0;
    	String businessRemarks = node.has("remarks") ? node.get("remarks").asText() : null;
    	String oaAuditNo = node.has("oaAuditNo") ? node.get("oaAuditNo").asText() : null;
    	InputOrderParam param = new InputOrderParam(inputId,payType,money,businessRemarks,oaAuditNo);
    	return ok(Json.toJson(iPurchaseOrderService.generInputOder(param)));
    }
    
    /**
     * 完税仓商品出库
     * @return
     */
    public Result deliverDutyPaidGoods(){
    	// 完税仓商品发货
    	JsonNode  node = request().body().asJson();
    	int inputId = node.get("inputId").asInt();
    	String payType = node.has("payType") ? node.get("payType").asText() : null;
    	double money = "cash".equals(payType) ? node.get("money").asDouble() : 0;
    	InputOrderParam param = new InputOrderParam(inputId,payType,money);
    	param.setBusinessRemarks(node.get("remarks").asText());// 业务备注
    	param.setOaAuditNo(node.get("oaAuditNo").asText());// oa审批号
    	param.setBbcPostage(node.get("bbcPostage").asDouble());// 运费
    	
    	DeliverDutyPaidGoodsParam deliverParam = new DeliverDutyPaidGoodsParam();
    	deliverParam.setReceiver(node.get("receiver").asText());
    	deliverParam.setTelephone(node.get("telephone").asText());
    	deliverParam.setProvinceId(node.get("provinceId").asInt());
    	deliverParam.setAddress(node.get("address").asText());
    	deliverParam.setPostCode(node.get("postCode").asText());
    	deliverParam.setShippingCode(node.get("shippingCode").asText());// 运送方式
    	deliverParam.setShippingName(node.get("shippingName").asText());// 运送名称
    	deliverParam.setBbcPostage(node.get("bbcPostage").asDouble());// 运费
//    	deliverParam.setMoney2Paid(node.get("money2Paid").asDouble());// 包含运费的
    	return ok(Json.toJson(iPurchaseOrderService.deliverDutyPaidGoods(param, deliverParam)));
    }
    
    /**
     * 后台采购单现金支付-不需要支付密码
     * @return
     */
    public Result balancePaymentBackStage(){
    	JsonNode  node = request().body().asJson();
    	String purchaseOrderNo = node.get("purchaseOrderNo").asText();
    	Map<String, Object> result = iPurchaseOrderService.balancePaymentBackStage(purchaseOrderNo);
    	return ok(Json.toJson(result));
    }
    
	/**
	 * TODO 临时后台用
	 * @author zbc
	 * @since 2016年12月3日 下午8:04:57
	 */
	@DivisionMember
	public Result viewPurchase() {
		JsonNode node = request().body().asJson();
		if (null == node) {
			return internalServerError();
		}
		
		String account = userService.getAccounts();
		List<String> accounts = null;
		if (!StringUtils.isBlankOrNull(account)) {
			accounts = Arrays.asList(account.split(","));
		}
		Logger.debug(">>>/manager/viewPurchase>>>>>{}", node);
		((ObjectNode)node).set("isBack",Json.toJson(true));
		return ok(Json.toJson(iPurchaseOrderService.queryPurchases(node, accounts)));
	}
    
	/**
	 * 根据订单数据获取促销活动信息
	 * @author zbc
	 * @since 2016年12月14日 下午4:13:33
	 */
	@ALogin
	public Result getMaketAct(String orderNo){
		return ok(Json.toJson(iPurchaseOrderService.getMaketAct(orderNo)));
	}
	
	
	/**
	 * 保存采购单发货信息
	 * @author zbc
	 * @since 2017年5月25日 下午4:54:29
	 * @return
	 */
	public Result savePurchaseSaleOut(){
		JsonNode json = request().body().asJson();
		if(JsonCaseUtil.checkParam(json, new String[]{"pno","jsonStr"})){
			return ok(iPurchaseOrderService.savePurchaseStockout(json.toString()));
		}
		return ok(Json.newObject().put("suc",true).put("msg", "参数错误"));
	}
}
