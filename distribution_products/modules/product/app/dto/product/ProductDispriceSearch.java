package dto.product;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import entity.product.ProductDisprice;

/**
 * 商品定价列表搜索
 * 
 * @author zbc 2016年7月28日 下午6:21:52
 */
public class ProductDispriceSearch extends  ProductDisprice{
	
private static final Map<String,List<String>> fNameMap = Maps.newHashMap();
	
	static {
		fNameMap.put("TOTAL", Lists.newArrayList(new String[]{
			"cost","arrive_ware_price","floor_price","proposal_retail_price","distributor_price",
			"ftz_price","electricity_prices","supermarket_price","vip_price"
		}));
		fNameMap.put("BASE", Lists.newArrayList(new String[]{
			"cost","arrive_ware_price","floor_price","proposal_retail_price"
		}));
		fNameMap.put("DIS", Lists.newArrayList(new String[]{
				"proposal_retail_price","distributor_price"
		}));
		fNameMap.put("FTZ", Lists.newArrayList(new String[]{
				"proposal_retail_price","ftz_price"
		}));
		fNameMap.put("ELE", Lists.newArrayList(new String[]{
				"proposal_retail_price","electricity_prices"
		}));
		fNameMap.put("SUP", Lists.newArrayList(new String[]{
				"proposal_retail_price","supermarket_price"
		}));
		fNameMap.put("VIP", Lists.newArrayList(new String[]{
				"proposal_retail_price","vip_price"
		}));
	}
	
	private List<String> skuList;
	
	private Integer categoryId;// 类目id

	private String key;// 模糊搜索

	private Integer warehouseId;// 仓库id

	private Integer pageNo;// 页数

	private Integer pageSize;// 分页大小
	
	private Double minCost;// 到岸价 - 最小值

	private Double maxCost;// 到岸价- 最大值

	private Double minFloorPrice;// 最低价 - 最小值

	private Double maxFloorPrice;// 最低价- 最大值

	private Double minProposalRetailPrice;// 建议零售价 - 最小值

	private Double maxProposalRetailPrice;// 建议零售价 - 最大值

	private Double minDistributorPrice;// 经销商价格 - 最小值

	private Double maxDistributorPrice;// 经销商价格- 最大值

	private Double minElectricityPrices;// 电商价格- 最小值

	private Double maxElectricityPrices;// 电商价格- 最大值

	private Double minSupermarketPrice;// 商超价格- 最小值

	private Double maxSupermarketPrice;// 商超价格 - 最大值
	
	private Double minDisTotalCost;//分销总成本 - 最小值
	
	private Double maxDisTotalCost;//分销总成本 -最大值
	
	private Double minArriveWarePrice;//出仓价 - 最小值
	
	private Double maxArriveWarePrice;//出仓价 - 最大值
	
	private Double minDisCompanyCost; //公司成本价 - 最小值
	
	private Double maxDisConpanyCost;//公司成本价- 最大值
	
	private Double minMarketInterventionPrice;//市场干预价 - 最小值
	
	private Double maxMarketInterventionPrice;//市场干预价 - 最大值
	
	private Double minFtzPrice;//自贸区价格 - 最小值
	
	private Double maxFtzPrice;//自贸区价格 - 最小值
	
	private Double minVipPrice;//vip价格 - 最小值
	
	private Double maxVipPrice;//vip价格 - 最大值
	
	private String sort;
	
	private String filter;

	private String remark;
	
	public ProductDispriceSearch() {

	}
	
	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public Double getMinVipPrice() {
		return minVipPrice;
	}
	public void setMinVipPrice(Double minVipPrice) {
		this.minVipPrice = minVipPrice;
	}
	public Double getMaxVipPrice() {
		return maxVipPrice;
	}
	public void setMaxVipPrice(Double maxVipPrice) {
		this.maxVipPrice = maxVipPrice;
	}

	private boolean hasNull;//是否空值搜索
	
	/**
	 * TOTAL 所有价格                             栏目
	 * BASE  基础价格                             栏目
	 * DIS   经销商供货价                      栏目
	 * FTZ   自贸区经销价格           栏目
	 * ELE   电商供货价设置                  栏目
	 * SUP	 KA直营供货价设置             栏目
	 * VIP	 VIP价格             栏目
	 */
	private String type;// 价格 BASE
	
	private List<String> fNameList;

	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public List<String> getfNameList() {
		return fNameMap.get(type);
	}
	public void setfNameList(List<String> fNameList) {
		this.fNameList = fNameList;
	}
	public boolean isHasNull() {
		return hasNull;
	}

	public void setHasNull(boolean hasNull) {
		this.hasNull = hasNull;
	}

	private String brand;//商品品牌
	
	
	public Double getMinMarketInterventionPrice() {
		return minMarketInterventionPrice;
	}

	public void setMinMarketInterventionPrice(Double minMarketInterventionPrice) {
		this.minMarketInterventionPrice = minMarketInterventionPrice;
	}

	public Double getMaxMarketInterventionPrice() {
		return maxMarketInterventionPrice;
	}

	public void setMaxMarketInterventionPrice(Double maxMarketInterventionPrice) {
		this.maxMarketInterventionPrice = maxMarketInterventionPrice;
	}

	public Double getMinFtzPrice() {
		return minFtzPrice;
	}

	public void setMinFtzPrice(Double minFtzPrice) {
		this.minFtzPrice = minFtzPrice;
	}

	public Double getMaxFtzPrice() {
		return maxFtzPrice;
	}

	public void setMaxFtzPrice(Double maxFtzPrice) {
		this.maxFtzPrice = maxFtzPrice;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public Double getMinDisTotalCost() {
		return minDisTotalCost;
	}

	public void setMinDisTotalCost(Double minDisTotalCost) {
		this.minDisTotalCost = minDisTotalCost;
	}

	public Double getMaxDisTotalCost() {
		return maxDisTotalCost;
	}

	public void setMaxDisTotalCost(Double maxDisTotalCost) {
		this.maxDisTotalCost = maxDisTotalCost;
	}

	public Double getMinArriveWarePrice() {
		return minArriveWarePrice;
	}

	public void setMinArriveWarePrice(Double minArriveWarePrice) {
		this.minArriveWarePrice = minArriveWarePrice;
	}

	public Double getMaxArriveWarePrice() {
		return maxArriveWarePrice;
	}

	public void setMaxArriveWarePrice(Double maxArriveWarePrice) {
		this.maxArriveWarePrice = maxArriveWarePrice;
	}

	public Double getMinDisCompanyCost() {
		return minDisCompanyCost;
	}

	public void setMinDisCompanyCost(Double minDisCompanyCost) {
		this.minDisCompanyCost = minDisCompanyCost;
	}

	public Double getMaxDisConpanyCost() {
		return maxDisConpanyCost;
	}

	public void setMaxDisConpanyCost(Double maxDisConpanyCost) {
		this.maxDisConpanyCost = maxDisConpanyCost;
	}

	private String operateType;//操作方式
	
	private List<Integer> idList;//批量设置价格id 参数
	
	private boolean idAll; 
	
	private Map<String,Double> changeFactorMap;//系数设置map
	
	private String operator;//操作人
	
	private String setType;//设置类型: 系数设置FR,利润设置PF

	public String getSetType() {
		return setType;
	}
	public void setSetType(String setType) {
		this.setType = setType;
	}
	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Map<String, Double> getChangeFactorMap() {
		return changeFactorMap;
	}

	public void setChangeFactorMap(Map<String, Double> changeFactorMap) {
		this.changeFactorMap = changeFactorMap;
	}

	public boolean isIdAll() {
		return idAll;
	}

	public void setIdAll(boolean idAll) {
		this.idAll = idAll;
	}

	public List<Integer> getIdList() {
		return idList;
	}

	public void setIdList(List<Integer> idList) {
		this.idList = idList;
	}

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

	public List<String> getSkuList() {
		return skuList;
	}

	public void setSkuList(List<String> skuList) {
		this.skuList = skuList;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Double getMinFloorPrice() {
		return minFloorPrice;
	}

	public void setMinFloorPrice(Double minFloorPrice) {
		this.minFloorPrice = minFloorPrice;
	}

	public Double getMaxFloorPrice() {
		return maxFloorPrice;
	}

	public void setMaxFloorPrice(Double maxFloorPrice) {
		this.maxFloorPrice = maxFloorPrice;
	}

	public Double getMinProposalRetailPrice() {
		return minProposalRetailPrice;
	}

	public void setMinProposalRetailPrice(Double minProposalRetailPrice) {
		this.minProposalRetailPrice = minProposalRetailPrice;
	}

	public Double getMaxProposalRetailPrice() {
		return maxProposalRetailPrice;
	}

	public void setMaxProposalRetailPrice(Double maxProposalRetailPrice) {
		this.maxProposalRetailPrice = maxProposalRetailPrice;
	}

	public Double getMinDistributorPrice() {
		return minDistributorPrice;
	}

	public void setMinDistributorPrice(Double minDistributorPrice) {
		this.minDistributorPrice = minDistributorPrice;
	}

	public Double getMaxDistributorPrice() {
		return maxDistributorPrice;
	}

	public void setMaxDistributorPrice(Double maxDistributorPrice) {
		this.maxDistributorPrice = maxDistributorPrice;
	}

	public Double getMinElectricityPrices() {
		return minElectricityPrices;
	}

	public void setMinElectricityPrices(Double minElectricityPrices) {
		this.minElectricityPrices = minElectricityPrices;
	}

	public Double getMaxElectricityPrices() {
		return maxElectricityPrices;
	}

	public void setMaxElectricityPrices(Double maxElectricityPrices) {
		this.maxElectricityPrices = maxElectricityPrices;
	}

	public Double getMinSupermarketPrice() {
		return minSupermarketPrice;
	}

	public void setMinSupermarketPrice(Double minSupermarketPrice) {
		this.minSupermarketPrice = minSupermarketPrice;
	}

	public Double getMaxSupermarketPrice() {
		return maxSupermarketPrice;
	}

	public void setMaxSupermarketPrice(Double maxSupermarketPrice) {
		this.maxSupermarketPrice = maxSupermarketPrice;
	}
	public Double getMinCost() {
		return minCost;
	}
	public void setMinCost(Double minCost) {
		this.minCost = minCost;
	}
	public Double getMaxCost() {
		return maxCost;
	}
	public void setMaxCost(Double maxCost) {
		this.maxCost = maxCost;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "ProductDispriceSearch [skuList=" + skuList + ", categoryId="
				+ categoryId + ", key=" + key + ", warehouseId=" + warehouseId
				+ ", pageNo=" + pageNo + ", pageSize=" + pageSize
				+ ", minCost=" + minCost + ", maxCost=" + maxCost
				+ ", minFloorPrice=" + minFloorPrice + ", maxFloorPrice="
				+ maxFloorPrice + ", minProposalRetailPrice="
				+ minProposalRetailPrice + ", maxProposalRetailPrice="
				+ maxProposalRetailPrice + ", minDistributorPrice="
				+ minDistributorPrice + ", maxDistributorPrice="
				+ maxDistributorPrice + ", minElectricityPrices="
				+ minElectricityPrices + ", maxElectricityPrices="
				+ maxElectricityPrices + ", minSupermarketPrice="
				+ minSupermarketPrice + ", maxSupermarketPrice="
				+ maxSupermarketPrice + ", minDisTotalCost=" + minDisTotalCost
				+ ", maxDisTotalCost=" + maxDisTotalCost
				+ ", minArriveWarePrice=" + minArriveWarePrice
				+ ", maxArriveWarePrice=" + maxArriveWarePrice
				+ ", minDisCompanyCost=" + minDisCompanyCost
				+ ", maxDisConpanyCost=" + maxDisConpanyCost
				+ ", minMarketInterventionPrice=" + minMarketInterventionPrice
				+ ", maxMarketInterventionPrice=" + maxMarketInterventionPrice
				+ ", minFtzPrice=" + minFtzPrice + ", maxFtzPrice="
				+ maxFtzPrice + ", minVipPrice=" + minVipPrice
				+ ", maxVipPrice=" + maxVipPrice + ", hasNull=" + hasNull
				+ ", type=" + type + ", fNameList=" + fNameList + ", brand="
				+ brand + ", operateType=" + operateType + ", idList=" + idList
				+ ", idAll=" + idAll + ", changeFactorMap=" + changeFactorMap
				+ ", operator=" + operator + ", setType=" + setType + "]";
	}
	
	public static Integer parseInt(String[] strs){
		return getValue(strs) != null?Integer.valueOf(getValue(strs)):null;
	}
	public static Double parseDouble(String[] strs){
		return getValue(strs) != null?Double.valueOf(getValue(strs)):null;
	}
	public static Boolean parseBoolean(String[] strs){
		return getValue(strs) != null?"true".equals(getValue(strs)):false;
	}
	public static String getValue(String[] strs){
		return strs == null?null:"".equals(strs[0])?null:strs[0];
	}
	
}
