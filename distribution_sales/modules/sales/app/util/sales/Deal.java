package util.sales;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.google.common.collect.Maps;

import play.Logger;
import play.libs.Json;

/**
 * 处理发货单 锁库异常脚本
 * @author zbc
 * 2017年3月23日 上午11:36:16
 */
public class Deal {

	private static final String URL = "http://bbc.tomtop.hk";
	public static void main(String[] args) {
		String[] orders ={"CG201703230000060339"};
		deal(orders);
	}
	
	/**
	 * 处理锁库异常订单
	 * @author zbc
	 * @since 2017年3月23日 下午12:30:51
	 */
	public static void deal(String[] orders){
		try {
			Integer sid;
			for(String orderNo :orders){
				System.out.println(lock(orderNo));
				System.out.println(finishedOrder(orderNo,null));
				sid = getPurByNo(orderNo).get("orders").get(0).get("sid").asInt();
				System.out.println(sid);
				JsonNode stockOut = updateStock(orderNo);
				System.out.println(stockOut);
				if(stockOut.get("result").asBoolean()){
					saveMicroOut(sid,stockOut.get("microOutList"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 
	
	/**
	 * 
	 * @author zbc
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 * @since 2017年3月23日 上午11:38:42
	 */
	private static JsonNode lock(String orderNo) throws JsonProcessingException, IOException {
		Map<String,String> params = Maps.newHashMap();
		params.put("od", orderNo);
		return  parseString(get(params, URL+"/purchase/orderLock"));
	}
	
	public static JsonNode getPurByNo(String orderNo) throws JsonProcessingException, IOException {
		HashMap<String,Object> param = Maps.newHashMap();
		param.put("pNo", orderNo);
		String resultString = post(Json.toJson(param).toString(), 
				URL + "/purchase/getOrderById");
		return parseString(resultString);
	}
	
	public static JsonNode finishedOrder(String purchaseNo, Double total) throws JsonProcessingException, IOException{
		Map<String, Object> param = Maps.newHashMap();
		param.put("purchaseNo", purchaseNo);
		param.put("flag", "PAY_SUCCESS");
		param.put("actualAmount", total);
		param.put("payType", "system");
		param.put("payDate", DateUtils.date2string(new Date(),DateUtils.FORMAT_FULL_DATETIME));
		Logger.info("完成采购单参数:[{}]",Json.toJson(param));
		String response_string = post(Json.toJson(param).toString(),
				URL+ "/purchase/cancel");
		Logger.info("完成采购单结果:[{}]",response_string);
		return parseString(response_string);
	}
	

	
	private static JsonNode updateStock(String orderNo) throws JsonProcessingException, IOException {
		Map<String, String> param = Maps.newHashMap();
		param.put("orderNo", orderNo);
		Logger.info("释放锁库，更新库存参数:[{}]",param);
		String resultString = get(param, 
				URL+"/inventory/inventoryorder/updatestock");
		Logger.info("释放锁库，更新库存结果:[{}]",resultString);
		return parseString(resultString);
	}
	
	public  static  JsonNode saveMicroOut(Integer sid, JsonNode mircoOut)throws JsonProcessingException, IOException{
		Map<String, Object> param = Maps.newHashMap();
		param.put("sid", sid);
		param.put("mircoOut", mircoOut);
		Logger.info("保存微仓出库历史数据参数:[{}]",param);
		String resultString = post(Json.toJson(param).toString(), URL + "/sales/saveMicroOut");
		System.out.println(resultString);
		return parseString(resultString);
	}
	
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
			post.setRequestEntity(new StringRequestEntity(requestBody, "",
					"utf-8"));
			post.setRequestHeader("content-type",
					"application/json;charset=utf-8");
			// 发送http请求
			client.executeMethod(post);
			// 打印返回的信息
			responString = post.getResponseBodyAsString();

		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
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
			//参数拼接
			get.setQueryString(nvpArr);
			get.setRequestHeader("content-type",
					"application/json;charset=utf-8");
			// 发送http请求
			client.executeMethod(get);
			// 打印返回的信息
			responString = get.getResponseBodyAsString();

		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			// 释放连接
			get.releaseConnection();
		}

		return responString;
	}
}
