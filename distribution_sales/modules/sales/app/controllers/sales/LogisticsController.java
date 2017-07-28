package controllers.sales;

import java.text.ParseException;
import java.util.*;

import entity.sales.*;
import mapper.sales.PlatformConfigMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import controllers.annotation.ApiPermission;
import controllers.annotation.Login;
import dto.sales.LogisticsTracingDto;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.sales.ILogisticsTracingService;
import services.sales.IOrderPackService;
import services.sales.ISaleMainService;
import util.sales.DateUtils;
import util.sales.KdniaoSubscribeAPI;

/**
 * Created by LSL on 2016/1/20.
 */
public class LogisticsController extends Controller {

	@Inject private IOrderPackService orderPackService;
	
	@Inject private ILogisticsTracingService logisticsTracingService;
	
	@Inject private ISaleMainService saleMainService;

	@Inject
	private PlatformConfigMapper platformConfigMapper;

    /**
     * 接收B2C推送过来的订单物流信息
     * @return
     */
    public Result receiveLogisticsInfo() {
		ObjectNode result = new JsonNodeFactory(false).objectNode();
		Map<String, String> data = Form.form().bindFromRequest().data();
		JsonNode params = Json.parse(data.get("data"));
		if (params == null || params.size() == 0) {
			result.put("suc", false);
			return ok(result);
		}
		
		int sign = params.has("sign") ? params.get("sign").asInt() : 0;
		
		OrderPack[] opArray = Json.fromJson(params.get("data"),
				OrderPack[].class);
		
		// sign为新增或更新标识，1表示新增，2表示更新
		if (sign == 1) {
			// 批量新增物流信息
			result.put("suc",
					orderPackService.batchAdd(Arrays.asList(opArray)));
		} else if (sign == 2) {
			// 批量更新物流信息
			result.put("suc", orderPackService.batchUpdate(params));
		} else {
			result.put("suc", false);
		}
		
		
		for(OrderPack op : opArray){
			SaleMain sm = saleMainService.getSaleMainOrderByOrderNo(op.getCordernumber());
			if(sm != null && StringUtils.isNotBlank(sm.getThirdPartLogisticsTypeCode()) ){
				String[] ctrackingnumberArray = op.getCtrackingnumber().split("，");//一个订单关联两个运单号，中间是用“，”分开的
				for(String ctrackingnumber:ctrackingnumberArray){
					//通知第三方数据提供方：快递鸟，订阅该单号的物流跟踪信息
					String notifyResult = "";
					try {
						//一个订单关联的多个运单号暂时认为属于同一个物流公司

						List<PlatformConfig> platformConfigList = platformConfigMapper.findPlatformConfigsByCode(KdniaoSubscribeAPI.PLATFORM_CODE);
						if (CollectionUtils.isEmpty(platformConfigList)) {
							Logger.error("未配置快递鸟系统请求参数，请检查!");
						}
						Optional<PlatformConfig> businessIdAny = platformConfigList.stream().filter(d -> d.getPlatformKey().equals(KdniaoSubscribeAPI.EBUSINESSID)).findAny();
						Optional<PlatformConfig> appKeyAny = platformConfigList.stream().filter(d -> d.getPlatformKey().equals(KdniaoSubscribeAPI.APPKEY)).findAny();
						Optional<PlatformConfig> orderTracesAny = platformConfigList.stream().filter(d -> d.getPlatformKey().equals(KdniaoSubscribeAPI.ORDER_TRACES_SUB)).findAny();

						String businessId = businessIdAny.isPresent() ? businessIdAny.get().getPlatformValue() : "";
						String appKey = appKeyAny.isPresent() ? appKeyAny.get().getPlatformValue() : "";
						String orderTraceUrl = orderTracesAny.isPresent() ? orderTracesAny.get().getPlatformValue() : "";

						Logger.info(">>>>>>>>>>>>>>> receiveLogisticsInfo>>>快递鸟相关配置参数: EBusinessId:【{}】, appKey:【{}】, orderTraceUrl:【{}】",
								businessId, appKey, orderTraceUrl);

						notifyResult = KdniaoSubscribeAPI.orderTracesSubByJson(sm.getThirdPartLogisticsTypeCode(), ctrackingnumber, businessId, appKey, orderTraceUrl);
						JsonNode jn = Json.parse(notifyResult);
						Logger.info("订单：[{}],物流单号[{}]跟踪信息订阅结果[{}]",op.getCordernumber(),op.getCtrackingnumber(),jn.get("Success").asBoolean());
						result.put("orderTracesSubSuc",jn.get("Success").asBoolean());
					} catch (Exception e) {
						Logger.info("订单号{}订阅物流信息发生异常：{}", op.getCordernumber(),e);
					}
				}
			}
			else{
				Logger.info("订单：[{}]不存在，或者订单对应的第三方物流代码不存在，无法跟踪物流信息",op.getCordernumber());
			}
			
		};
		
		return ok(result);
    }

    /**
     * 根据条件获取订单物流信息
     * @return
     */
    @Login
    @BodyParser.Of(BodyParser.Json.class)
    public Result gainOrderLogisticsInfo() {
        Map<String, Object> result = Maps.newHashMap();
        JsonNode params = request().body().asJson();
        if (params == null || !params.has("orderNo")) {
            Logger.info("请求参数不存在或格式错误");
            result.put("suc", false);
            result.put("msg", "请求参数不存在或格式错误");
            return ok(Json.toJson(result));
        }
        
        result.put("suc", true);
        result.put("data", orderPackService.getOrderPacksByCondition(params));
        return ok(Json.toJson(result));
    }

    
    /**
	 * 门店系统获取物流信息
	 * @return
	 */
	@ApiPermission
	@BodyParser.Of(BodyParser.Json.class)
	public Result getOrderLogisticsInfo(){
		Map<String, Object> result = Maps.newHashMap();
        JsonNode params = request().body().asJson();
        if (params == null || !params.has("orderNo")) {
            Logger.info("请求参数不存在或格式错误");
            result.put("suc", false);
            result.put("msg", "请求参数不存在或格式错误");
        } else {
            result.put("suc", true);
            result.put("data", orderPackService.getOrderPacksByCondition(params));
        }
        return ok(Json.toJson(result));
	}
	
	/**
	 * 获取本地存储的物流信息
	 * @return
	 */
	public Result getLogisticsTracingInfo(String orderNo){
		Map<String,Object> result=Maps.newHashMap();
		if(StringUtils.isEmpty(orderNo)){
			result.put("suc", false);
		    result.put("msg", "查询信息不正确！");
		    return ok(Json.toJson(result));
		}
		try {
			List<LogisticsTracingDto> logisticsTracingsList = logisticsTracingService.getLogisticsTracings(orderNo);
			if(logisticsTracingsList.isEmpty()){
				result.put("suc", false);
			    result.put("msg", "暂时无订单相关物流信息");
			}else{
				result.put("suc", true);
			    result.put("data", logisticsTracingsList);
			}
		} catch (Exception e) {
			Logger.info("查询物流发生异常{}", e);
			result.put("suc", false);
		    result.put("msg", "查询物流发生异常");
		}
		return ok(Json.toJson(result));
	}
	
	/**
	 * 获取第三方物流平台（快递鸟，http://www.kdniao.com/）推送的物流跟踪信息
	 * @return
	 */
	public Result receiveLogisticsTracingInfo(){
		Map<String, Object> result = Maps.newHashMap();
		Map<String, String> reqdata = Form.form().bindFromRequest().data();
		JsonNode params = Json.parse(reqdata.get("RequestData"));
        //JsonNode datas = request().body().asJson();
        if (params == null || params.size() == 0) {
            Logger.info("请求参数不存在或格式错误");
            
            result.put("Success", false);
            result.put("Reason", "请求参数不存在或格式错误");
        } else {
        	//请求参数过滤，校验
        	
        	//此次推送的物流信息包含具体内容时，则进行处理
        	if(params.get("Count").asInt() > 0){
        		JsonNode datas = params.get("Data");
        		
        		//每一个data数据都是一个运单的物流跟踪信息
        		for(JsonNode data : datas){
        			
        			String ShipperCode =  data.get("ShipperCode").asText();
        			String LogisticCode = data.get("LogisticCode").asText();
        			
        			// 1： 根据运单号和物流公司代码删除原有的记录
        			logisticsTracingService.deleteByShipperCodeAndLogisticCode(ShipperCode,LogisticCode);
        			
        			LogisticsTracing ltTemp = null;
        			List<LogisticsTracing> lts = new ArrayList<LogisticsTracing>();
        			// 2：批量插入这次的跟踪轨迹信息
        			for(JsonNode trace : data.get("Traces")){
        				ltTemp = new LogisticsTracing();
        				ltTemp.setShipperCode(ShipperCode);
        				ltTemp.setLogisticCode(LogisticCode);

        				try {
							ltTemp.setAcceptTime(DateUtils.string2date(trace.get("AcceptTime").asText(), DateUtils.FORMAT_FULL_DATETIME));
						} catch (ParseException e) {
							Logger.info("物流单号[{}]的跟踪信息中，时间数据解析异常，使用当前时间填充",LogisticCode);
							e.printStackTrace();
							ltTemp.setAcceptTime(new Date());
						}
        				ltTemp.setAcceptStation(trace.get("AcceptStation").asText(""));
        				ltTemp.setRemark(trace.has("Remark")?trace.get("Remark").asText(""):"");
        				
        				lts.add(ltTemp);
        				
        			}
        			// 2：插入此次的运单数据
        			logisticsTracingService.batchInsert(lts);
        		}
        	}
            result.put("Success", true);
            result.put("Reason", "接收并处理运单信息:[" + params.get("Count").asInt() + "]");
        }

		Map<String, Object> map = Maps.newHashMap();
		map.put("platformCode", KdniaoSubscribeAPI.PLATFORM_CODE);
		map.put("platformKey", KdniaoSubscribeAPI.EBUSINESSID);
		PlatformConfig platformConfigByCodeAndKey = platformConfigMapper.findPlatformConfigByCodeAndKey(map);

		result.put("EBusinessID", platformConfigByCodeAndKey != null ? platformConfigByCodeAndKey.getPlatformValue() : "");
        result.put("UpdateTime", DateUtils.date2string(new Date(), DateUtils.FORMAT_FULL_DATETIME));
        
        Logger.info("物流跟踪信息接收结果：[{}]",Json.toJson(result).toString());
        
        return ok(Json.toJson(result));
	}
	
}
