package dto.product_inventory;

import entity.product_inventory.ProductMicroInventoryTotal;

public class MicroInventoryQueryResult extends ProductMicroInventoryTotal {
	private Integer lockNum;
	
	public MicroInventoryQueryResult(ProductMicroInventoryTotal microInventoryTotal,Integer lockNum){
		this.setAccount(microInventoryTotal.getAccount());
		this.setCreateTime(microInventoryTotal.getCreateTime());
		this.setId(microInventoryTotal.getId());
		this.setProductTitle(microInventoryTotal.getProductTitle());
		this.setSku(microInventoryTotal.getSku());
		this.setStock(microInventoryTotal.getStock());
		this.setWarehouseId(microInventoryTotal.getWarehouseId());
		this.setWarehouseName(microInventoryTotal.getWarehouseName());
		this.setLockNum(lockNum);
	}
	public MicroInventoryQueryResult(){
		
	}
	public Integer getLockNum() {
		return lockNum;
	}

	public void setLockNum(Integer lockNum) {
		this.lockNum = lockNum;
	}
	@Override
	public String toString() {
		return "MicroInventoryQueryResult [lockNum=" + lockNum + ", getLockNum()=" + getLockNum() + ", getId()="
				+ getId() + ", getSku()=" + getSku() + ", getWarehouseId()=" + getWarehouseId()
				+ ", getWarehouseName()=" + getWarehouseName() + ", getStock()=" + getStock() + ", getProductTitle()="
				+ getProductTitle() + ", getUpdateTime()=" + getUpdateTime() + ", getCreateTime()=" + getCreateTime()
				+ ", getAccount()=" + getAccount() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}
}
