package controllers.payment;

import java.math.BigDecimal;
import java.util.Iterator;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import entity.payment.shengpay.ShengConfig;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.payment.IShengPayService;

/**
 * Created by LSL on 2016/5/3.
 */
public class ShengPay extends Controller {

    @Inject
    private IShengPayService shengPayService;

    /**
     * 盛付通单个或批量支付
     * [
     *     {
     *         "orderNo": "",//订单号
     *         "amount": "",//交易金额
     *         "IDCard": "",//订购人证件号
     *         "realName": "",//订购人真实姓名
     *         "cellphone": "",//订购人联系方式，可空
     *         "currency": "",//交易币种，可空
     *         "sku": "",//SKU，可空
     *         "remark": ""//商品描述，可空
     *     },......
     * ]
     */
    public Result applyPay() {
        ObjectNode result = Json.newObject();
        JsonNode params = request().body().asJson();
        if (params.isNull() || !params.isArray()) {
            result.put("suc", false);
            result.put("msg", "请求参数不存在或格式错误");
        } else {
            BigDecimal totalAmount = new BigDecimal("0.00");
            Iterator<JsonNode> nodes = params.iterator();
            //计算所有订单总金额
            while (nodes.hasNext()) {
                totalAmount = totalAmount.add(new BigDecimal(
                        nodes.next().get("amount").asDouble()));
            }
            //查询要扣款的账户的余额
            ShengConfig config = shengPayService.getShengConfig();
            JsonNode balanceNode = shengPayService.remoteQueryBalance(config.getPayer());
            if (balanceNode.get("suc").asBoolean()) {
                BigDecimal balance = new BigDecimal(
                        balanceNode.get("msg").get("availableBalance").asDouble());
                if (balance.compareTo(totalAmount) == 1 ||
                        balance.compareTo(totalAmount) == 0) {
                    //余额大于等于总金额
                    ArrayNode array = JsonNodeFactory.instance.arrayNode();
                    nodes = params.iterator();
                    //循环单笔支付
                    while (nodes.hasNext()) {
                        JsonNode res = shengPayService.applyPay(nodes.next());
                        array.add(res);
                    }
                    result.put("suc", true);
                    result.set("info", array);
                } else {
                    result.put("suc", false);
                    result.put("msg", "支付账户余额不足");
                }
            } else {
                result.put("suc", false);
                result.put("msg", balanceNode.get("msg").asText());
            }
        }
        return ok(result);
    }

    /**
     * 盛付通转账
     * [
     *     {
     *         "orderNo": "",//订单号
     *         "amount": "",//交易金额
     *         "IDCard": "",//订购人证件号
     *         "realName": "",//订购人真实姓名
     *         "cellphone": "",//订购人联系方式，可空
     *         "currency": "",//交易币种，可空
     *         "sku": "",//SKU，可空
     *         "remark": ""//商品描述，可空
     *     },......
     * ]
     */
    public Result applyTransfer() {
        ObjectNode result = Json.newObject();
        JsonNode params = request().body().asJson();
        if (params.isNull() || !params.isArray()) {
            result.put("suc", false);
            result.put("msg", "请求参数不存在或格式错误");
        } else {
            BigDecimal totalAmount = new BigDecimal("0.00");
            Iterator<JsonNode> nodes = params.iterator();
            //计算转账总金额
            while (nodes.hasNext()) {
                totalAmount = totalAmount.add(new BigDecimal(
                        nodes.next().get("amount").asDouble()));
            }
            //查询要扣款的账户的余额
            ShengConfig config = shengPayService.getShengConfig();
            JsonNode balanceNode = shengPayService.remoteQueryBalance(config.getReceipt());
            if (balanceNode.get("suc").asBoolean()) {
                BigDecimal balance = new BigDecimal(
                        balanceNode.get("msg").get("availableBalance").asDouble());
                if (balance.compareTo(totalAmount) == 1 ||
                        balance.compareTo(totalAmount) == 0) {
                    //余额大于等于总金额
                    ArrayNode array = JsonNodeFactory.instance.arrayNode();
                    nodes = params.iterator();
                    //开始转账
                    while (nodes.hasNext()) {
                        JsonNode res = shengPayService.applyTransfer(nodes.next());
                        array.add(res);
                    }
                    result.put("suc", true);
                    result.set("info", array);
                } else {
                    result.put("suc", false);
                    result.put("msg", "支付账户余额不足");
                }
            } else {
                result.put("suc", false);
                result.put("msg", balanceNode.get("msg").asText());
            }
        }
        return ok(result);
    }

    /**
     * 查询指定商户号账户余额
     * @return
     */
    public Result queryBalance() {
        String merchantNo = request().getQueryString("account");
        return ok(shengPayService.queryBalanceRecord(merchantNo));
    }

    /**
     * 测试
     */
    public Result test() {
        ObjectNode node = Json.newObject();
        node.put("orderNo", "20160505II104500");
        node.put("IDCard", "431281198607140015");
        node.put("realName", "张小军");
        node.put("cellphone", "13510573442");
        shengPayService.applyPay(node);
        shengPayService.applyTransfer(node);
        shengPayService.remoteQueryBalance("438806");
        return ok("true");
    }

}
