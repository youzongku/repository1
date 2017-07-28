package forms.warehousing;

import entity.warehousing.InventoryChangeHistory;

@SuppressWarnings("serial")
public class InventoryChangeHistoryForm extends InventoryChangeHistory {

	private Integer swarehouseId;// 仓库id
	private String swarehouseName;// 仓库名称

	public Integer getSwarehouseId() {
		return swarehouseId;
	}

	public void setSwarehouseId(Integer swarehouseId) {
		this.swarehouseId = swarehouseId;
	}

	public String getSwarehouseName() {
		return swarehouseName;
	}

	public void setSwarehouseName(String swarehouseName) {
		this.swarehouseName = swarehouseName;
	}

}
