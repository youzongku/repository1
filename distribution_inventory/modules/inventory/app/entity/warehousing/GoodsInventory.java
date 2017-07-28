package entity.warehousing;

import java.util.Date;

/**
 * 产品库存信息实体
 * 
 * @author luwj
 */
public class GoodsInventory implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;

	private String sku;

	private Integer warehouseId;// 仓库id

	private String warehouseName;// 仓库名

	private int totalStock;// 库存总数

	private int frozenStock;// 冻结库存数

	private int availableStock;// 可用库存数

	private Double costprice;// 成本价

	private Date lastUpdated;// 更新时间

	private String productTitle;

	private Integer categoryI;
	private Integer categoryII;
	private Integer categoryIII;
	private String categoryIName;
	private String categoryIIName;
	private String categoryIIIName;

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

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public int getTotalStock() {
		return totalStock;
	}

	public void setTotalStock(int totalStock) {
		this.totalStock = totalStock;
	}

	public int getFrozenStock() {
		return frozenStock;
	}

	public void setFrozenStock(int frozenStock) {
		this.frozenStock = frozenStock;
	}

	public int getAvailableStock() {
		return availableStock;
	}

	public void setAvailableStock(int availableStock) {
		this.availableStock = availableStock;
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

	public String getProductTitle() {
		return productTitle;
	}

	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}

	public Integer getCategoryI() {
		return categoryI;
	}

	public void setCategoryI(Integer categoryI) {
		this.categoryI = categoryI;
	}

	public Integer getCategoryII() {
		return categoryII;
	}

	public void setCategoryII(Integer categoryII) {
		this.categoryII = categoryII;
	}

	public Integer getCategoryIII() {
		return categoryIII;
	}

	public void setCategoryIII(Integer categoryIII) {
		this.categoryIII = categoryIII;
	}

	public String getCategoryIName() {
		return categoryIName;
	}

	public void setCategoryIName(String categoryIName) {
		this.categoryIName = categoryIName;
	}

	public String getCategoryIIName() {
		return categoryIIName;
	}

	public void setCategoryIIName(String categoryIIName) {
		this.categoryIIName = categoryIIName;
	}

	public String getCategoryIIIName() {
		return categoryIIIName;
	}

	public void setCategoryIIIName(String categoryIIIName) {
		this.categoryIIIName = categoryIIIName;
	}

	@Override
	public String toString() {
		return "GoodsInventory [id=" + id + ", sku=" + sku + ", warehouseId=" + warehouseId + ", warehouseName="
				+ warehouseName + ", totalStock=" + totalStock + ", frozenStock=" + frozenStock + ", availableStock="
				+ availableStock + ", costprice=" + costprice + ", lastUpdated=" + lastUpdated + "]";
	}

}