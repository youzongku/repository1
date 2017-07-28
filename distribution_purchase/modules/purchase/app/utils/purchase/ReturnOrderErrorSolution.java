package utils.purchase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import entity.purchase.returnod.ReturnOrder;
import play.libs.Json;

/**
 * 处理退货单审核通过异常：退货至云仓失败导致没有退款
 * 
 * @author zbc 2017年3月23日 上午11:36:16
 */
public class ReturnOrderErrorSolution {

	private static final String URL = "http://bbc.tomtop.hk";

	public static void main(String[] args) {

		// 构造数据
		List<ReturnOrder> returnOrderList = Lists.newArrayList();
		ReturnOrder newRo = new ReturnOrder();
		newRo.setReturnOrderNo("");// 退货单号
		newRo.setEmail("");// email
		newRo.setActualTotalReturnAmount(new Double(0));// 金额
		returnOrderList.add(newRo);

		// auditPassed(returnOrderList);
	}
	
	/**
	 * 退货单审核通过
	 * @param returnOrderList
	 */
	private static void auditPassed(List<ReturnOrder> returnOrderList){
		try {
			// 审核通过，要把钱退回，微仓的货物退回到云仓
			List<String> returnOrderNoList = Lists.transform(returnOrderList, ro -> ro.getReturnOrderNo());
			JsonNode return2CloudNode = returnToCloudInventory(returnOrderNoList);
			if (return2CloudNode == null || return2CloudNode.get("result").asInt() == 1) {
				Map<String, Object> result = Maps.newHashMap();
				result.put("suc", false);
				result.put("msg", "微仓退货-审核通过：还回云仓失败===" + return2CloudNode.toString());
				System.out.println(result);
				return;
			}
			
			// 循环退款
			for (ReturnOrder returnOrder : returnOrderList) {
				// 退回到云仓成功，把钱退回到用户的余额里
				String email = returnOrder.getEmail();// 退回到哪个账户里
				Double returnMoney = returnOrder.getActualTotalReturnAmount();// 退的钱
				// 检查参数是否足以进行退款操作
				System.out.println("退款参数，email=" + email + "，returnMoney=" + returnMoney + "，returnOrderNo="
						+ returnOrder.getReturnOrderNo());
				if (StringUtils.isNotBlankOrNull(email) && returnMoney != null && returnMoney > 0) {
					JsonNode refundResultNode = refund2Balance4ReturnOrder(email, returnOrder.getReturnOrderNo(),
							returnMoney);
					if (refundResultNode == null || refundResultNode.get("code").asInt() != 4) {// 退款失败
						Map<String, Object> result = Maps.newHashMap();
						result.put("suc", false);
						result.put("msg", "微仓退货-审核通过：退货单" + returnOrder.getReturnOrderNo() + "退款失败");
						System.out.println(result);
						return;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("微仓退货-审核通过，还回到云仓&退款失败");
		}

		Map<String, Object> result = Maps.newHashMap();
		result.put("suc", true);
		result.put("msg", "审核成功");
		System.out.println(result);
	}

	private static JsonNode returnToCloudInventory(List<String> returnOrderNoList)
			throws JsonProcessingException, IOException {
		Map<String, Object> param = Maps.newHashMap();
		param.put("returnOrderNoArray", returnOrderNoList);
		System.out.println("微仓退货-微仓库存退还云仓参数:["+param+"]");
		String resultString = post(Json.toJson(param).toString(),
				URL + "/inventory/micro/returnToCloudInventory");
		System.out.println("微仓退货-微仓库存退还云仓结果:["+resultString+"]");
		return parseString(resultString);
	}

	private static JsonNode refund2Balance4ReturnOrder(String email, String returnOrderNo, Double money)
			throws JsonProcessingException, IOException {
		Map<String, Object> map = Maps.newHashMap();
		map.put("email", email);
		map.put("transferAmount", money);
		map.put("transferNumber", returnOrderNo);
		map.put("isBackStage", true);
		map.put("applyType", 4);
		System.out.println("微仓退货，将款退回到余额里-参数："+map);
		JsonNode refundNode = Json
				.parse(post(Json.toJson(map).toString(), URL + "/member/freightRefund"));
		System.out.println("微仓退货，将款退回到余额里-结果："+refundNode);
		return refundNode;
	}

	// -=-----------------------------------------------------

	private static JsonNode parseString(String str) throws JsonProcessingException, IOException {
		ObjectMapper obj = new ObjectMapper();
		return obj.readTree(str);
	}

	/**
	 * 基于进口项目的公共POST方法
	 * 
	 * @param requestBody
	 *            json格式请求体
	 * @param url
	 *            请求路径,直接从根开始写即可，例如“/sale/demo”
	 * @return
	 */
	public static String post(String requestBody, String url) {
		String responString = "";
		PostMethod post = new PostMethod(url);
		HttpClient client = new HttpClient();
		try {
			post.setRequestHeader("token", "cb44ad0f-f796-454b-8452-9aaddb71d97e");
			post.setRequestEntity(new StringRequestEntity(requestBody, "", "utf-8"));
			post.setRequestHeader("content-type", "application/json;charset=utf-8");
			// 发送http请求
			client.executeMethod(post);
			// 打印返回的信息
			responString = post.getResponseBodyAsString();

		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 释放连接
			post.releaseConnection();
		}

		return responString;
	}

	/**
	 * 基于进口项目的公共GET方法
	 * 
	 * @param params
	 *            查询参数数据字典
	 * @param url
	 *            请求路径,直接从根开始写即可，例如“/sale/demo”
	 * @return
	 */
	public static String get(Map<String, String> params, String url) {
		String responString = "";
		GetMethod get = new GetMethod(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		params.forEach(new BiConsumer<String, String>() {
			@Override
			public void accept(String t, String u) {
				// TODO Auto-generated method stub
				nvps.add(new NameValuePair(t, u));
			}
		});

		NameValuePair[] nvpArr = new NameValuePair[params.size()];
		nvpArr = nvps.toArray(nvpArr);
		HttpClient client = new HttpClient();
		try {
			get.setRequestHeader("token", "cb44ad0f-f796-454b-8452-9aaddb71d97e");
			// 参数拼接
			get.setQueryString(nvpArr);
			get.setRequestHeader("content-type", "application/json;charset=utf-8");
			// 发送http请求
			client.executeMethod(get);
			// 打印返回的信息
			responString = get.getResponseBodyAsString();

		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 释放连接
			get.releaseConnection();
		}

		return responString;
	}
}
