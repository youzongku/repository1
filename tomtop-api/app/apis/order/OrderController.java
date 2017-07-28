package apis.order;

import annotations.ApiPermission;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import forms.ReturnMessageForm;
import forms.order.*;
import forms.product.ProductReadResponseForm;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Constans;
import utils.HttpUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 *
 * 订单api;包括销售订单和采购订单
 *
 * @author ye_ziran on 2016/3/22.
 */
//@ApiPermission
public class OrderController extends Controller{

    public static final int BUFFER_SIZE = 4096;

    /**
     * 销售订单下单接口
     * @return
     */
    public Result salesOrderConfirm() throws IOException {

        Result res = noContent();//默认状态

        Form<SalesConfirmForm> f = Form.form(SalesConfirmForm.class).bindFromRequest();
        //验证必填项
        if(f.hasErrors()){
            ReturnMessageForm returnForm = new ReturnMessageForm();
            returnForm.setRes(false);
            Logger.warn("销售订单下单接口，请求参数错误: {}", f.errorsAsJson());
            returnForm.setMsg("请求参数错误，请仔细核对");
            res = internalServerError(Json.toJson(returnForm));
            return res;
        }
        SalesConfirmForm confirmForm = f.get();
        Map<String, SalesConfirmDetailForm> detailsMap = new HashMap<>();

        ObjectNode orderConfirmNode = Json.newObject();
        ObjectNode orderConfirmBaseNode = Json.newObject();
        ArrayNode orderDetail = Json.newArray();
        ArrayNode dispriceSysNodes = Json.newArray();

        JsonNode addrNode = null;
        JsonNode prodNode = null;
        double discount = 0;

        {//根据memberEmail拿到当前会员的折扣率
            String memberInfo = HttpUtil.get(Constans.MEMBER_INFO + "?email=" + confirmForm.getMemberEmail());
            Logger.debug("memberInfo = {}", memberInfo);
            JsonNode jNode = Json.parse(memberInfo);
            discount = jNode.get("discount").asDouble();
        }

        {//根据地址id，查询地址信息
            String addrInfo = HttpUtil.get(Constans.SALE_ORDER_RECEIVER + "?addrId=" + confirmForm.getAddrId() + "&email=" + confirmForm.getMemberEmail());
            if (addrInfo == null) {//地址id错误判断
                ReturnMessageForm returnForm = new ReturnMessageForm();
                returnForm.setRes(false);
                String errMsg = "保存订单错误： addrId[" + confirmForm.getAddrId() + "]不属于分销商[" + confirmForm.getMemberEmail();
                returnForm.setMsg(errMsg);
                Logger.warn(errMsg);
                res = internalServerError(Json.toJson(returnForm));
                return res;
            } else {
                Logger.debug("addrInfo = {}", addrInfo);
                addrNode = Json.parse(addrInfo);
                orderConfirmBaseNode.put("address", addrNode.get(0).get("receiverAddr").asText());
                orderConfirmBaseNode.put("receiver", addrNode.get(0).get("receiverName").asText());
                orderConfirmBaseNode.put("tel", addrNode.get(0).get("receiverTel").asText());
                orderConfirmBaseNode.put("idcard", addrNode.get(0).get("receiverIdcard").asText());
                orderConfirmBaseNode.put("postCode", addrNode.get(0).get("postCode").asText());
            }
        }

        {//根据details,找到sku的信息列表
            ObjectNode subNode = Json.newObject();
            ObjectNode mainNode = Json.newObject();
            List<SalesConfirmDetailForm> detailForms = confirmForm.getOrderDetails();

            ArrayNode skuList = Json.newArray();
            for (SalesConfirmDetailForm detailForm : detailForms) {
                skuList.add(detailForm.getSku());
                detailsMap.put(detailForm.getSku(), detailForm);
            }
            subNode.put("pageSize", Integer.MAX_VALUE);
            subNode.put("currPage", 1);
            subNode.put("istatus", 1);
            subNode.set("skuList", skuList);
            mainNode.set("data", subNode);
            String prodStr = HttpUtil.post(Constans.PRODUCT_READ, mainNode);
            prodNode = Json.parse(prodStr).get("data");
        }

        //组装orderConfirmNode
        if(null != prodNode) {
            if(prodNode.get("result") != null ){
                if(prodNode.get("result").isArray()){
                    Iterator<JsonNode> it = prodNode.get("result").iterator();
                    while(it.hasNext()){
                        JsonNode jn = it.next();
                        SalesConfirmDetailForm detailForm = detailsMap.get(jn.get("csku").asText());
                        JsonNode detailNode = trans2SalesOrderDetail(jn, detailForm.getQty(), discount);
                        JsonNode dispriceSys = trans2DispriceSys(jn, detailForm.getActualPrice());
                        orderDetail.add(detailNode);
                        dispriceSysNodes.add(dispriceSys);
                    }
                }
            }
            orderConfirmNode.put("id", "");
            orderConfirmNode.put("isNotified", confirmForm.isNotify() ? 1 : 0);
            orderConfirmNode.put("email", confirmForm.getMemberEmail());
            orderConfirmNode.put("tradeNo", confirmForm.getTpTradeNo());
            orderConfirmBaseNode.put("platformOrderNo", confirmForm.getTpOrderNo());
            orderConfirmBaseNode.put("shopId", confirmForm.getShopId());
            orderConfirmBaseNode.put("addrId", confirmForm.getAddrId());
            orderConfirmBaseNode.put("orderPostage", confirmForm.getTpOrderFreight());
            orderConfirmBaseNode.put("orderingDate", confirmForm.getTpOrderDate());
            orderConfirmBaseNode.put("orderActualAmount", confirmForm.getTpOrderActureAmount());
            orderConfirmBaseNode.put("remark", confirmForm.getRemark());
            orderConfirmNode.set("base", orderConfirmBaseNode);
            orderConfirmNode.set("details", orderDetail);
            orderConfirmNode.set("disPriceSystem", dispriceSysNodes);

            String confirmResponse = HttpUtil.post(Constans.SALES_ORDER_CONFIRM, orderConfirmNode);
            if(confirmResponse != null){
                JsonNode resps = Json.parse(confirmResponse);
                Logger.debug("saveSalesOrder result : {}", confirmResponse);
                ReturnMessageForm returnForm = new ReturnMessageForm();
                if(resps.get("mainId") != null ){
                    returnForm.setRes(true);
                    returnForm.setMsg(resps.get("currentDetail").get(0).get("salesOrderNo").asText());
                    res = ok(Json.toJson(returnForm));

                }else{
                    returnForm.setRes(false);
                    returnForm.setMsg("保存销售单出错！错误原因：网络超时");
                    Logger.warn("保存销售单出错!");
                    res = internalServerError(Json.toJson(returnForm));
                }

            }else{
                ReturnMessageForm returnForm = new ReturnMessageForm();
                returnForm.setRes(false);
                returnForm.setMsg("服务器繁忙，请稍后再请求~");
                res = internalServerError(Json.toJson(returnForm));
            }
        }

        return res;
    }

    /**
     *
     *
     * 销售订单查询
     * @return json格式的字符串
     */
    public Result salesOrderRead() throws IOException{
        Result res = noContent();
        String url = Constans.SALES_ORDER_READ;

        Form<SalesSearchParamForm> f = Form.form(SalesSearchParamForm.class).bindFromRequest();
        Map<String,String> params = f.data();

        String respStr = HttpUtil.post(url, params);
        if(respStr != null){
            res = ok(Json.parse(respStr));
        }else{
            ReturnMessageForm returnForm = new ReturnMessageForm();
            returnForm.setRes(false);
            returnForm.setMsg("服务器繁忙，请稍后再请求~");
            res = internalServerError(Json.toJson(returnForm));
        }

        return res;
    }

    /**
     * 生成采购单
     *
     *
     *
     * @return  json格式的字符串
     */
    public Result purchaseOrderConfirm() {

        Result res = noContent();
        Form<PurchaseConfirmForm> f = Form.form(PurchaseConfirmForm.class).bindFromRequest();
        PurchaseConfirmForm form = f.get();
        if(f.hasErrors()){
            ReturnMessageForm returnForm = new ReturnMessageForm();
            returnForm.setRes(false);
            Logger.warn("采购订单下单接口，请求参数错误: {}", f.errorsAsJson());
            returnForm.setMsg("请求参数错误，请仔细核对");
            res = internalServerError(Json.toJson(returnForm));
            return res;
        }

        //通过memberEmail拿到折扣
        String memberInfo = HttpUtil.get(Constans.MEMBER_INFO + "?email=" + form.getMemberEmail());
        Logger.info("memberInfo = {}", memberInfo);
        JsonNode jNode =Json.parse(memberInfo);
        double discount = jNode.get("discount").asDouble();

        ObjectNode subNode = Json.newObject();
        ObjectNode mainNode = Json.newObject();
        StringBuilder skuList = new StringBuilder(2<<8);
        List<PurchaseConfirmDetailForm> orderDetails = form.getOrderDetails();
        Map<String, PurchaseConfirmDetailForm> detailsMap = new HashMap<>();
        for(PurchaseConfirmDetailForm dForm : orderDetails){
            skuList.append(dForm.getSku()).append(",");
            detailsMap.put(dForm.getSku(), dForm);
        }
        subNode.put("pageSize", Integer.MAX_VALUE);
        subNode.put("currPage", 1);
        subNode.put("istatus", 1);
        subNode.put("sku", skuList.toString());
        mainNode.set("data", subNode);

        //查找商品信息
        String resStr = HttpUtil.post(Constans.PRODUCT_READ, mainNode);
        Logger.debug("product == {}", resStr);

        JsonNode prodData = Json.parse(resStr);

        //组装post数据
        JsonNode data = prodData.get("data");
        ObjectNode orderConfirmNode = Json.newObject();
        ArrayNode orderDetail = Json.newArray();
        double totalprice = 0d;
        if(data.get("result") != null ){
            if(data.get("result").isArray()){
                Iterator<JsonNode> it = data.get("result").iterator();
                while(it.hasNext()){
                    JsonNode jn = it.next();
                    JsonNode detailNode = trans2OrderDetail(jn, detailsMap.get(jn.get("csku").asText()).getQty(), discount);
                    if(detailNode.get("sumPrice") != null){
                        totalprice += detailNode.get("sumPrice").asDouble();
                    }
                    orderDetail.add(detailNode);
                }
            }
        }
        orderConfirmNode.put("email", form.getMemberEmail());
        orderConfirmNode.put("totalPrice", totalprice);
        orderConfirmNode.set("orderDetail", orderDetail);

        //生成订单
        String reponseStr = HttpUtil.post(Constans.PURCHASE_ORDER_CONFIRM, orderConfirmNode);
        Logger.debug("savePurchaseOrder result : {}", reponseStr);

        JsonNode resps = Json.parse(reponseStr);
        if(reponseStr != null){
            ReturnMessageForm returnForm = new ReturnMessageForm();
            if(resps.get("errorCode") != null ){
                if(resps.get("errorCode").asText().equals("0")){
                    returnForm.setRes(true);
                    returnForm.setMsg(resps.get("errorInfo").asText());
                    res = ok(Json.toJson(returnForm));
                }else {
                    returnForm.setRes(true);
                    returnForm.setMsg("保存采购单出错! 错误原因：" + resps.get("errorInfo").asText());
                    Logger.warn("保存采购单出错! errorMsg : ", resps.get("errorInfo").asText());
                    res = internalServerError(Json.toJson(returnForm));
                }
            }else{
                returnForm.setRes(false);
                returnForm.setMsg("保存采购单出错！错误原因：未知");
                Logger.warn("保存采购单出错! errorMsg : resps = null");
                res = internalServerError(Json.toJson(returnForm));
            }
        }else{
            ReturnMessageForm returnForm = new ReturnMessageForm();
            returnForm.setRes(false);
            returnForm.setMsg("服务器繁忙，请稍后再请求~");
            res = internalServerError(Json.toJson(returnForm));
        }

        return res;
    }

    /**
     * 采购单查询
     *
     *
     * @return
     */
    public Result purchaseOrderRead() {
        Result res = noContent();
        String url = Constans.PURCHASE_ORDER_READ;

        Form<PurchaseSearchParamForm> f = Form.form(PurchaseSearchParamForm.class).bindFromRequest();
        Map<String,String> params = f.data();

        String respStr = HttpUtil.post(url, params);
        if(respStr != null){
            JsonNode resDataJn = Json.parse(respStr);
            JsonNode orderJn = resDataJn.get("orders");
            ObjectMapper om = new ObjectMapper();
            try {//用form过滤输出内容

                List<PurchaseReadResponseForm> orderFormList = new ArrayList<>();
                for(int i=0,len=orderJn.size(); i<len; i++){
                    PurchaseReadResponseForm orderForm = Json.fromJson(orderJn.get(i), PurchaseReadResponseForm.class);
                    orderFormList.add(orderForm);
                }
                ObjectNode returnNode = Json.newObject();
                returnNode.put("total", resDataJn.get("total").asText());
                returnNode.put("page", resDataJn.get("page").asText());
                returnNode.put("recordPerPage", resDataJn.get("recordPerPage").asText());
                returnNode.set("orders", Json.toJson(orderFormList));
                res = ok(Json.toJson(returnNode));
            } catch (Exception e) {
                Logger.error("GET orders/purchase error, cause by ObjectMapper.readValue, msg={}", e.getMessage());
                ReturnMessageForm returnForm = new ReturnMessageForm();
                returnForm.setRes(false);
                returnForm.setMsg("GET\"orders/purchase\"响应数据解析错误！");
                res = internalServerError(Json.toJson(returnForm));
            }

        }else{
            ReturnMessageForm returnForm = new ReturnMessageForm();
            returnForm.setRes(false);
            returnForm.setMsg("服务器繁忙，请稍后再请求~");
            res = internalServerError(Json.toJson(returnForm));
        }

        return res;
    }

    /**
     * 计算分销价
     *
     * note :分销价 = 成本 / (1 - 折扣*利润率)
     * @param jn
     * @param discount          折扣
     * @return
     */
    private double calPrice(JsonNode jn, double discount){
        discount = discount / 100;
        BigDecimal costPrice = BigDecimal.valueOf(jn.get("disTotalCost").asDouble());//分销分销总成本
        BigDecimal disProfitRate = BigDecimal.valueOf(jn.get("disProfitRate").asDouble());//分销利润率

        BigDecimal disPrice = costPrice.divide(new BigDecimal(1.0).subtract(new BigDecimal(discount).multiply(disProfitRate)), 2, RoundingMode.HALF_UP);

        return disPrice.doubleValue();
    }

    /**
     *
     * 从返回的产品信息转换成订单的详情
     *
     * @param jn
     * @return
     */
    private JsonNode trans2SalesOrderDetail(JsonNode jn, int qty,double discount){
        ObjectNode orderDetail = Json.newObject();
        orderDetail.set("productName", jn.get("ctitle"));
        orderDetail.set("marketPrice", jn.get("salePrice"));
        orderDetail.set("warehouseId", jn.get("warehouseId"));
        orderDetail.set("warehouseName", jn.get("warehouseName"));
        orderDetail.set("productImg", jn.get("imageUrl"));
        orderDetail.set("sku", jn.get("csku"));
        orderDetail.set("marketPrice", jn.get("originalPrice"));
        double realPrice = calPrice(jn, discount);
        orderDetail.put("purchasePrice", realPrice);
        orderDetail.put("qty", qty);

        return orderDetail;
    }

    /**
     * 将产品的详细数据转换成分销价格系统格式的数据
     * @param jn
     * @param finalSellingPrice
     * @return
     */
    private JsonNode trans2DispriceSys(JsonNode jn, double finalSellingPrice){
        ObjectNode disprice = (ObjectNode) jn;
        disprice.put("finalSellingPrice", finalSellingPrice);
        return disprice;
    }

    /**
     *
     * 从返回的产品信息转换成订单的详情
     *
     * @param jn
     * @return
     */
    private JsonNode trans2OrderDetail(JsonNode jn, int qty,double discount){
        ObjectNode orderDetail = Json.newObject();
        orderDetail.set("title", jn.get("ctitle"));
        orderDetail.set("marketPrice", jn.get("salePrice"));
        orderDetail.set("purchaseCostPrice", jn.get("disTotalCost"));
        orderDetail.set("warehouseId", jn.get("warehouseId"));
        orderDetail.set("publicImg", jn.get("imageUrl"));
        orderDetail.set("sku", jn.get("csku"));
        orderDetail.set("disProfit", jn.get("disProfit"));
        orderDetail.set("disStockFee", jn.get("disStockFee"));
        orderDetail.set("dislistFee", jn.get("disListFee"));
        orderDetail.set("disShippingType", jn.get("disShippingType"));
        orderDetail.set("disOtherCost", jn.get("disOtherCost"));
        orderDetail.set("disTransferFee", jn.get("disTransferFee"));
        orderDetail.set("distradeFee", jn.get("disTradeFee"));
        orderDetail.set("dispayFee", jn.get("dispayFee"));
        orderDetail.set("dispostalFee", jn.get("disPostalFee"));
        orderDetail.set("disgst", jn.get("disGst"));
        orderDetail.set("disinsurance", jn.get("disInsurance"));
        orderDetail.set("distotalvat", jn.get("disTotalVat"));
        orderDetail.set("diCifPrice", jn.get("disCifPrice"));
        orderDetail.set("cost", jn.get("cost"));
        orderDetail.set("disFreight", jn.get("disFreight"));
        orderDetail.set("disStockId", jn.get("disStockId"));
        orderDetail.set("disPrice", jn.get("disPrice"));
        orderDetail.set("disProfitMargin", jn.get("disProfitRate"));
        double realPrice = calPrice(jn, discount);
        orderDetail.put("price", realPrice);
        orderDetail.put("qty", qty);
        orderDetail.put("sumPrice", new BigDecimal(realPrice).multiply(new BigDecimal(qty)).doubleValue() );

        return orderDetail;
    }

}
