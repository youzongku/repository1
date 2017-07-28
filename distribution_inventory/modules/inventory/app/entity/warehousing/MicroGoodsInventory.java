package entity.warehousing;

import java.util.Date;

/**
 * 微仓库存信息实体
 * <p>
 * 对应表t_micro_goods_inventory
 * 
 * @author ye_ziran
 * @since 2016年3月2日 下午4:50:54
 */
public class MicroGoodsInventory {

	private Integer id;
	private String sku;
	private String productTitle;// 产品标题
	private Integer productCategory;// 产品类目id，只记录到大类
	private String productCategoryName;// 产品大类名称
	private Integer mwarehouseId;// 微仓id
	private String mwarehouseName;// 微仓名称
	private Integer warehouseId;// 仓库id
	private String warehouseNo;// 仓库编号
	private String warehouseName;// 仓库名
	private Integer totalStock;// 库存总数
	private Integer frozenStock;// 冻结库存数
	private Integer avaliableStock;// 可用库存数
	private Double costprice;// 成本价
	private Date lastUpdated;// 更新时间
	private boolean isGift;// 是否是赠品
	
	private String imgUrl;

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
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

	public String getProductTitle() {
		return productTitle;
	}

	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public Integer getTotalStock() {
		return totalStock;
	}

	public void setTotalStock(Integer totalStock) {
		this.totalStock = totalStock;
	}

	public Integer getFrozenStock() {
		return frozenStock;
	}

	public void setFrozenStock(Integer frozenStock) {
		this.frozenStock = frozenStock;
	}

	public Integer getAvaliableStock() {
		return avaliableStock;
	}

	public void setAvaliableStock(Integer avaliableStock) {
		this.avaliableStock = avaliableStock;
	}

	public Double getCostprice() {
		return costprice;
	}

	public void setCostprice(Double costprice) {
		this.costprice = costprice;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Integer getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(Integer productCategory) {
		this.productCategory = productCategory;
	}

	public Integer getMwarehouseId() {
		return mwarehouseId;
	}

	public void setMwarehouseId(Integer mwarehouseId) {
		this.mwarehouseId = mwarehouseId;
	}

	public String getMwarehouseName() {
		return mwarehouseName;
	}

	public void setMwarehouseName(String mwarehouseName) {
		this.mwarehouseName = mwarehouseName;
	}

	public String getWarehouseNo() {
		return warehouseNo;
	}

	public void setWarehouseNo(String warehouseNo) {
		this.warehouseNo = warehouseNo;
	}

	public String getProductCategoryName() {
		return productCategoryName;
	}

	public void setProductCategoryName(String productCategoryName) {
		this.productCategoryName = productCategoryName;
	}

	public boolean isGift() {
		return isGift;
	}

	public void setGift(boolean isGift) {
		this.isGift = isGift;
	}

	@Override
	public String toString() {
		return "MicroGoodsInventory [id=" + id + ", sku=" + sku
				+ ", productTitle=" + productTitle + ", productCategory="
				+ productCategory + ", productCategoryName="
				+ productCategoryName + ", mwarehouseId=" + mwarehouseId
				+ ", mwarehouseName=" + mwarehouseName + ", warehouseId="
				+ warehouseId + ", warehouseNo=" + warehouseNo
				+ ", warehouseName=" + warehouseName + ", totalStock="
				+ totalStock + ", frozenStock=" + frozenStock
				+ ", avaliableStock=" + avaliableStock + ", costprice="
				+ costprice + ", lastUpdated=" + lastUpdated + ", isGift="
				+ isGift + "]";
	}

}
