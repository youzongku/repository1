package forms.warehousing;

import java.io.Serializable;
import java.util.Date;

public class MicroGoodsInventoryForm implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String sku;
	private String productTitle;// 产品标题
	private Integer productCategoryId;// 产品类目id，只记录到大类
	private String productCategoryName;// 产品大类ID
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
	private String key;// 搜索关键词（商品标题或sku）
	private String distributorEmail;// 分销商Email
	private Boolean isGift;// 是否是赠品
	private Integer pageNo;
	private Integer pageSize;

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

	public Integer getProductCategoryId() {
		return productCategoryId;
	}

	public void setProductCategoryId(Integer productCategory) {
		this.productCategoryId = productCategory;
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

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouseNo() {
		return warehouseNo;
	}

	public void setWarehouseNo(String warehouseNo) {
		this.warehouseNo = warehouseNo;
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

	public String getDistributorEmail() {
		return distributorEmail;
	}

	public void setDistributorEmail(String distributorEmail) {
		this.distributorEmail = distributorEmail;
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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getProductCategoryName() {
		return productCategoryName;
	}

	public void setProductCategoryName(String productCategoryName) {
		this.productCategoryName = productCategoryName;
	}

	public Boolean getIsGift() {
		return isGift;
	}

	public void setGift(Boolean isGift) {
		this.isGift = isGift;
	}

	@Override
	public String toString() {
		return "MicroGoodsInventoryForm [id=" + id + ", sku=" + sku
				+ ", productTitle=" + productTitle + ", productCategoryId="
				+ productCategoryId + ", productCategoryName="
				+ productCategoryName + ", mwarehouseId=" + mwarehouseId
				+ ", mwarehouseName=" + mwarehouseName + ", warehouseId="
				+ warehouseId + ", warehouseNo=" + warehouseNo
				+ ", warehouseName=" + warehouseName + ", totalStock="
				+ totalStock + ", frozenStock=" + frozenStock
				+ ", avaliableStock=" + avaliableStock + ", costprice="
				+ costprice + ", lastUpdated=" + lastUpdated + ", key=" + key
				+ ", distributorEmail=" + distributorEmail + ", isGift="
				+ isGift + ", pageNo=" + pageNo + ", pageSize=" + pageSize
				+ "]";
	}

}
