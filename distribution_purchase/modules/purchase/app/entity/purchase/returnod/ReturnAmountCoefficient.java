package entity.purchase.returnod;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;

import play.libs.Json;
import utils.purchase.StringUtils;

/**
 * 商品退款比例
 * 
 * @author huangjc
 * @date 2017年2月14日
 */
public class ReturnAmountCoefficient {
	private Integer id;
	private String sku;
	private Integer warehouseId;
	private String coefficientValue;
	private JsonNode coefficientJsonValue;

	private Date createTime;
	private String createUser;
	private Date lastUpdateTime;
	private String lastUpdateUser;

	public JsonNode getCoefficientJsonValue() {
		if(coefficientValue!=null && coefficientValue.length()>0){
			coefficientJsonValue = Json.parse(coefficientValue);
		}
		return coefficientJsonValue;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getCoefficientValue() {
		return coefficientValue;
	}

	public void setCoefficientValue(String coefficientValue) {
		this.coefficientValue = coefficientValue;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getLastUpdateUser() {
		return lastUpdateUser;
	}

	public void setLastUpdateUser(String lastUpdateUser) {
		this.lastUpdateUser = lastUpdateUser;
	}
	
	/**
	 * 获取到退货系数，有可能为null
	 * @param dateSpace
	 * @return
	 */
	public Double getCoefficient(Integer dateSpace){
		Map<Integer, Double> coefficientMap = coefficientValue2Map();
		return doGetCoefficient(coefficientMap, dateSpace);
	}
	
	private Double doGetCoefficient(Map<Integer, Double> coefficientMap,Integer dateSpace){
		Set<Integer> keySet = coefficientMap.keySet();
		Optional<Integer> min = keySet.stream().filter(key->key>dateSpace).min((key1,key2)->{
			return key1.compareTo(key2);
		});
		// 取不到，说明大于1080
		Integer keySelected = min.orElse(1081);
		return coefficientMap.get(keySelected);
	}
	
	private Map<Integer,Double> coefficientValue2Map(){
		JsonNode node = Json.parse(coefficientValue);
		HashMap<Integer,Double> map = Maps.newHashMap();
		map.put(30, getDoubleValue(node,"30",null));
		map.put(60, getDoubleValue(node,"60",null));
		map.put(90, getDoubleValue(node,"90",null));
		map.put(180, getDoubleValue(node,"180",null));
		map.put(360, getDoubleValue(node,"360",null));
		map.put(540, getDoubleValue(node,"540",null));
		map.put(720, getDoubleValue(node,"720",null));
		map.put(1080, getDoubleValue(node,"1080",null));
		map.put(1081, getDoubleValue(node,"1081",null));
		return map;
	}
	
	private Double getDoubleValue(JsonNode node, String key, Double defaultValue){
		if(node!=null&&node.has(key)&&node.get(key)!=null&&StringUtils.isNotBlankOrNull(node.get(key).asText())){
			return node.get(key).asDouble();
		}
		return defaultValue;
	}

	@Override
	public String toString() {
		return "ReturnAmountCoefficient [id=" + id + ", sku=" + sku
				+ ", warehouseId=" + warehouseId + ", coefficientValue="
				+ coefficientValue + ", createTime=" + createTime
				+ ", createUser=" + createUser + ", lastUpdateTime="
				+ lastUpdateTime + ", lastUpdateUser=" + lastUpdateUser + "]";
	}

}
