package controllers.sales;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import controllers.annotation.ALogin;
import controllers.annotation.DivisionMember;
import controllers.annotation.Login;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.sales.ISaleAfterService;
import services.sales.IUserService;
import util.sales.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author longhuashen
 * @since 2017/4/8
 *
 * 销售单售后
 */
public class SaleAfterController extends Controller {

    @Inject
    private IUserService userService;

    @Inject
    private ISaleAfterService saleAfterService;
    
    /**
	 * 计算指定采购单单号退款详情的实际退款均摊
	 * 参数：{purchaseOrderNo: CG001}
	 * @return {
	 * 				suc:true, 
	 * 				shOrderInfoList:[
	 * 					{
	 * 						shOrder: {}, 
	 * 						shOrderDetailList: [
	 * 							{售后详情}
	 * 						]
	 * 					}
	 * 				]
	 * 			}
	 */
    public Result getReturnAmountCapfee4Sku() {
    	JsonNode main = request().body().asJson();
    	Logger.info("getReturnAmountCapfee4Sku---->参数：{}",main);
        if (main==null || !main.has("purchaseOrderNo") 
        		|| main.get("purchaseOrderNo").asText().length()==0) {
        	Map<String, Object> result = Maps.newHashMap();
        	result.put("suc", false);
        	result.put("msg", "参数为空");
            return ok(Json.toJson(result));
        }
        
        String purchaseOrderNo = main.get("purchaseOrderNo").asText();
        return ok(Json.toJson(saleAfterService.getReturnAmountCapfee4Sku(purchaseOrderNo)));
    }

    /**
     * 销售发货售后订单信息
     *
     * @return
     */
    @Login
    public Result getShSaleOrderList() {
        JsonNode main = request().body().asJson();
        if (main.size() == 0) {
            return internalServerError("Expecting Json data");
        }

        String email = userService.getDisAccount();
        return ok(Json.toJson(saleAfterService.selectShSaleOrderList(main, email)));
    }

    /**
     * 获取售后单信息
     * @return
     */
    @Login
    public Result getAfterSaleOrderDto(){
        JsonNode main = request().body().asJson();
        if (main.size() == 0) {
            return internalServerError("Expecting Json data");
        }

        return ok(Json.toJson(saleAfterService.getAfterSaleOrderDtoById(main.get("orderId").asInt())));
    }

    /**
     * 退款申请
     * @return
     */
    public Result saleOrderRefundsApply() {
        Http.MultipartFormData multipartFormData = request().body().asMultipartFormData();
        Map<String, String[]> params = multipartFormData.asFormUrlEncoded();
        List<Http.MultipartFormData.FilePart> files = multipartFormData.getFiles();
        return ok(Json.toJson(saleAfterService.saleOrderRefundsApply(params, files)));
    }

    /**
     * 取消售后单申请
     *
     * @return
     */
    public Result cancleSaleOrderRefundsApply() {
        JsonNode json = request().body().asJson();

        Logger.info("------------->cancleSaleOrderRefundsApply:{}", json.toString());
        if (json.size() == 0) {
            return internalServerError("Expecting Json data");
        }

        return ok(Json.toJson(saleAfterService.cancleSaleOrderRefundsApply(json)));
    }

    /**
     * 寄回商品
     *
     * @return
     */
    public Result saleOrderRefundsApplyLogistics() {
        JsonNode json = request().body().asJson();

        Logger.info("------------->saleOrderRefundsApplyLogistics:{}", json.toString());
        if (json.size() == 0) {
            return internalServerError("Expecting Json data");
        }

        return ok(Json.toJson(saleAfterService.saleOrderRefundsApplyLogistics(json)));
    }

    public Result selectEffectiveShOrderCount() {
        JsonNode json = request().body().asJson();

        Logger.info("------------->selectEffectiveShOrderCount:{}", json.toString());
        if (json.size() == 0) {
            return internalServerError("Expecting Json data");
        }

        return ok(Json.toJson(saleAfterService.selectEffectiveShOrderCount(json)));
    }

    public Result selectEffectiveShOrderByDetailOrderId() {
        JsonNode json = request().body().asJson();
        if (json.size() == 0) {
            return internalServerError("Expecting Json data");
        }
        return ok(Json.toJson(saleAfterService.selectEffectiveShOrderByDetailOrderId(json)));
    }

    /**
     * 展示发货单售后列表
     *
     * @return
     */
    @ALogin
    @DivisionMember
    public Result showSalesOrderRefunds() {
        JsonNode json = request().body().asJson();
        if (json == null) {
            Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put("result", false);
            resultMap.put("msg", "数据格式错误");
            return ok(Json.toJson(resultMap));
        }

        String account = userService.getRelateAccounts();
        List<String> accounts = null;
        if (!StringUtils.isBlankOrNull(account)) {
            accounts = Arrays.asList(account.split(","));
        }
        return ok(Json.toJson(saleAfterService.selectSaleOrderRefundsListOfBackstage(
                json, accounts)));
    }

    /**
     * 获取售后单信息
     *
     * @return
     */
    public Result getSalesOrderRefundsById() {
        JsonNode json = request().body().asJson();
        if (json == null) {
            Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put("result", false);
            resultMap.put("msg", "数据格式错误");
            return ok(Json.toJson(resultMap));
        }

        return ok(Json.toJson(saleAfterService.getSalesOrderRefundsById(json)));
    }

    /**
     * 获取售后单附件信息
     *
     * @return
     */
    public Result getShAttachmentListByShOrderId() {
        JsonNode json = request().body().asJson();
        if (json == null) {
            Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put("result", false);
            resultMap.put("msg", "数据格式错误");
            return ok(Json.toJson(resultMap));
        }

        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("result", true);
        resultMap.put("data", saleAfterService.getShAttachmentListByShOrderId(json));
        return ok(Json.toJson(resultMap));
    }

    /**
     * 获取售后单对应的采购信息
     *
     * @return
     */
    public Result getShOrderDetails() {
        JsonNode json = request().body().asJson();
        if (json == null) {
            Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put("result", false);
            resultMap.put("msg", "数据格式错误");
            return ok(Json.toJson(resultMap));
        }

        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("result", true);
        resultMap.put("data", saleAfterService.getShOrderDetailListByShOrderId(json));
        return ok(Json.toJson(resultMap));
    }

    /**
     * 获取售后单附件图片
     *
     * @return
     */
    public Result getShAttachmentImgById() {
        Map<String, String> param = Form.form().bindFromRequest().data();
        String idStr = param.get("id");
        Map<String, Object> map = new HashMap<String, Object>();
        if ("".equals(idStr) || idStr == null) {
            return ok(Json.toJson(map.put("msg", "id为空")));
        }
        File img = saleAfterService.getShAttachmentImg(Integer.valueOf(idStr));
        if(!img.exists()){
            return ok(Json.toJson(""));
        }
        return ok(img);
    }

    /**
     * 售后单审核
     *
     * @return
     */
    @ALogin
    public Result shAudit() {
        JsonNode json = request().body().asJson();
        Logger.info("售后单审核参数----{}", json);

        if (json == null) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("result", false);
            resultMap.put("msg", "数据格式错误");
            return ok(Json.toJson(resultMap));
        }

        String loginAccount = userService.getAdminAccount();
        Map<String, Object> result = saleAfterService.shAudit(json, loginAccount);

        return ok(Json.toJson(result));
    }

    /**
     * 获取售后单操作日志
     *
     * @return
     */
    public Result getShLogListByShOrderId() {
        JsonNode json = request().body().asJson();
        if (json == null) {
            Map<String, Object> resultMap = Maps.newHashMap();
            resultMap.put("result", false);
            resultMap.put("msg", "数据格式错误");
            return ok(Json.toJson(resultMap));
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", true);
        resultMap.put("data", saleAfterService.getShLogListByShOrderId(json));
        return ok(Json.toJson(resultMap));
    }
}
