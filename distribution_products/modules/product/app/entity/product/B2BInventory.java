package entity.product;

import java.util.List;

public class B2BInventory {
	
	private List<WarehouseInventory> inventorys;
	
	private Integer count;

	public List<WarehouseInventory> getInventorys() {
		return inventorys;
	}

	public void setInventorys(List<WarehouseInventory> inventorys) {
		this.inventorys = inventorys;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
	
}
