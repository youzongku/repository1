package dto.sales;

import java.io.Serializable;

public class RelatedSku implements Serializable{
	
	private static final long serialVersionUID = -6901595216795299740L;
	
	private Integer id;
	private Integer feeItemId;
	private String sku;
	private Integer warehouseId;
	private String productName;
	private String warehouseName;
	private Integer categoryId;
	private String categoryName;
	private Double contractPrice;

	
	public String getWarehouseName() {
		return warehouseName;
	}
	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}
	public Integer getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public Double getContractPrice() {
		return contractPrice;
	}
	public void setContractPrice(Double contractPrice) {
		this.contractPrice = contractPrice;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getFeeItemId() {
		return feeItemId;
	}
	public void setFeeItemId(Integer feeItemId) {
		this.feeItemId = feeItemId;
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
}