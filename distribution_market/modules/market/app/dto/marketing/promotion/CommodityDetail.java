package dto.marketing.promotion;
/**
 * 推过来的商品详情
 * @author ljq
 */
public class CommodityDetail {
    //商品SKU
	private String sku;
    //商品类别
	private Integer commodityCategoryId;
	//商品仓库
	private Integer warehouseId;
	//商品类型(完税，跨境)
	private Integer commodityTypeId;
	//商品数量
	private Integer number;
	//商品单价
	private Double totalPrice;
	
	public Double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public Integer getCommodityCategoryId() {
		return commodityCategoryId;
	}
	public void setCommodityCategoryId(Integer commodityCategoryId) {
		this.commodityCategoryId = commodityCategoryId;
	}
	public Integer getWarehouseId() {
		return warehouseId;
	}
	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}
	public Integer getCommodityTypeId() {
		return commodityTypeId;
	}
	public void setCommodityTypeId(Integer commodityTypeId) {
		this.commodityTypeId = commodityTypeId;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	@Override
	public String toString() {
		return "CommodityDetaile [sku=" + sku + ", commodityCategoryId="
				+ commodityCategoryId + ", warehouseId=" + warehouseId
				+ ", commodityTypeId=" + commodityTypeId + ", number=" + number
				+ "]";
	}
	
}
