package util.sales;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

import play.Logger;

/**
 * 地址转换工具类
 * add by zbc 
 * @author 
 *
 */
public class AddressUtils {
	
	private static JsonNode PJSONS;
	
	private static JsonNode CJSONS;
	
	private static JsonNode AJSONS;
	
	private static ObjectMapper MAP;
	
	static{
		try {
			Map<String,String> proParams = Maps.newHashMap();
			MAP = new ObjectMapper();
			String provincesRes = HttpUtil.get(proParams, HttpUtil.B2BBASEURL+"/member/getprovs");
			PJSONS = MAP.readTree(provincesRes);
			String citiesRes = HttpUtil.get(proParams, HttpUtil.B2BBASEURL+"/member/getAllCities");
			CJSONS = MAP.readTree(citiesRes);
			String areasRes = HttpUtil.get(proParams, HttpUtil.B2BBASEURL+"/member/getAllAreas");
			AJSONS = MAP.readTree(areasRes);
		} catch (Exception e) {
			Logger.info("缓存省市地区异常",e);
		}
	}
	/**
	 * 
	 * 省市地区转换失败
	 * /member/getprovs 
	 * /member/getcities
	 * /member/getareas
	 * @param address
	 * @return
	 */
	public static  String dealAddress(String address){
		try {
			Map<String,String> addressMap = Maps.newHashMap();
			for(JsonNode pJson : PJSONS){
				String  provinces = pJson.get("provinceName").asText();
				String regPro = provinces.substring(0, provinces.length() -1);
				if(address.contains(regPro) ){
					addressMap.put("provinceName",provinces);
					//满足条件跳出循环
					break; 
				}
			}
			for(JsonNode cJson : CJSONS){
				String  city = cJson.get("cityName").asText();
				String regCity = city.substring(0, city.length() -1);
				if(address.contains(regCity)){
					addressMap.put("cityName",city);
					//满足条件跳出循环
					break; 
				}
			}
			for(JsonNode aJson : AJSONS){
				String  area = aJson.get("areaName").asText();
				String regArea = area.substring(0, area.length());
				if(address.contains(regArea) ){
					addressMap.put("areaName",area);
					//满足条件跳出循环
					break; 
				}
			}
			if(!addressMap.containsKey("provinceName") 
					||	!addressMap.containsKey("cityName")
						||	!addressMap.containsKey("areaName")){
				throw new RuntimeException();
			}
			return  addressMap.get("provinceName") + " "
						+ addressMap.get("cityName") + " "
							+ addressMap.get("areaName") + " " 
								+ address;
		} catch (Exception e) {
			Logger.error("省市地区转换失败");
		} 
		return address;
	}

	/**
	 * 判断省、市、区是否匹配
	 *
	 * @param address
	 * @return
	 */
	public static boolean isAdjustAddress(String address){
		if(org.apache.commons.lang3.StringUtils.isBlank(address)) {
			return false;
		}
		String[] arr = address.split(" ");
		if (arr.length < 4) {
			return false;
		}
		address = arr[0] + arr[1] + arr[2];
		try {
			Map<String,String> addressMap = Maps.newHashMap();

			Integer provinceId = null;
			for(JsonNode pJson : PJSONS){
				String  provinces = pJson.get("provinceName").asText();
				String regPro = provinces.substring(0, provinces.length() -1);
				if(address.contains(regPro) ){
					provinceId = pJson.get("id").asInt();
					//满足条件跳出循环
					break;
				}
			}

			Integer cityId = null;
			if (provinceId == null) {
				return false;
			} else {
				for(JsonNode cJson : CJSONS){
					String  city = cJson.get("cityName").asText();
					String regCity = city.substring(0, city.length() -1);
					if(address.contains(regCity)){
						addressMap.put("cityName",city);
						//满足条件跳出循环
						Integer city_provinceId = cJson.get("provinceId").asInt();
						if (city_provinceId.intValue() == provinceId.intValue()) {
							cityId = cJson.get("id").asInt();
						}
						break;
					}
				}
			}

			Integer areaId = null;
			if (cityId == null) {
				return false;
			} else {
				for(JsonNode aJson : AJSONS){
					String  area = aJson.get("areaName").asText();
					String regArea = area.substring(0, area.length());
					if(address.contains(regArea) ){
						//满足条件跳出循环
						Integer area_cityId = aJson.get("cityId").asInt();
						if (area_cityId.intValue() == area_cityId.intValue()) {
							areaId = aJson.get("id").asInt();
						}
						break;
					}
				}
			}


			if (areaId != null) {
				return true;
			}
			return false;
		} catch (Exception e) {
			Logger.error("省市地区转换失败");
		}
		return false;
	}
	
}
