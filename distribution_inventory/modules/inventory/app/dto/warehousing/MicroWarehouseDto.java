package dto.warehousing;

import entity.warehousing.MicroWarehouse;

public class MicroWarehouseDto extends MicroWarehouse {
	private Integer totalStock;

	public Integer getTotalStock() {
		return totalStock;
	}

	public void setTotalStock(Integer totalStock) {
		this.totalStock = totalStock;
	}
	
}
