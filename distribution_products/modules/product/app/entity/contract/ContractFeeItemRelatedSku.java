package entity.contract;

public class ContractFeeItemRelatedSku {
	private Integer id;

	private Integer feeItemId;

	private String productName;
	
	private String sku;

	private Integer warehouseId;
	
	private String warehouseName;
	
	private Integer categoryId;
	
	private String categoryName;
	
	private Double contractPrice;
	
	public ContractFeeItemRelatedSku() {

	}

	public ContractFeeItemRelatedSku(String sku, Integer warehouseId) {
		super();
		this.sku = sku;
		this.warehouseId = warehouseId;
	}

	public ContractFeeItemRelatedSku(String productName, String sku, Integer warehouseId, String warehouseName,
			Integer categoryId, String categoryName, Double contractPrice) {
		super();
		this.productName = productName;
		this.sku = sku;
		this.warehouseId = warehouseId;
		this.warehouseName = warehouseName;
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.contractPrice = contractPrice;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sku == null) ? 0 : sku.hashCode());
		result = prime * result + ((warehouseId == null) ? 0 : warehouseId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContractFeeItemRelatedSku other = (ContractFeeItemRelatedSku) obj;
		if (sku == null) {
			if (other.sku != null)
				return false;
		} else if (!sku.equals(other.sku))
			return false;
		if (warehouseId == null) {
			if (other.warehouseId != null)
				return false;
		} else if (!warehouseId.equals(other.warehouseId))
			return false;
		return true;
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

	@Override
	public String toString() {
		return "ContractFeeItemRelatedSku [id=" + id + ", feeItemId=" + feeItemId + ", sku=" + sku + ", warehouseId="
				+ warehouseId + "]";
	}

}