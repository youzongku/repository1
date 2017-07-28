package dto.purchase;

/**
 * 到期日期dto：sku && warehouseId && 库存
 * 
 * @author huangjc
 * @since 2017年3月1日
 */
public class ExpirationDateDto {
	private String sku;
	private Integer warehouseId;
	private Integer subStock;// 到期日期对应的库存
	private String expirationDate;

	public ExpirationDateDto(String sku, Integer warehouseId, String expirationDate, Integer subStock) {
		super();
		this.sku = sku;
		this.warehouseId = warehouseId;
		this.expirationDate = expirationDate;
		this.subStock = subStock;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expirationDate == null) ? 0 : expirationDate.hashCode());
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
		ExpirationDateDto other = (ExpirationDateDto) obj;
		if (expirationDate == null) {
			if (other.expirationDate != null)
				return false;
		} else if (!expirationDate.equals(other.expirationDate))
			return false;
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

	public Integer getSubStock() {
		return subStock;
	}

	public void setSubStock(Integer subStock) {
		this.subStock = subStock;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	@Override
	public String toString() {
		return "ExpirationDateDto [sku=" + sku + ", warehouseId=" + warehouseId + ", subStock=" + subStock
				+ ", expirationDate=" + expirationDate + "]";
	}

}
