package dto.product_inventory;

import java.util.List;
import java.util.Map;

import entity.product_inventory.ProductInventoryDetail;

public class InventoryDetailDistributeDto {
	private List<ProductInventoryDetail> cloudInventoryDetailList; 
	private String microInventoryStockpile;
	private String orderLockNum;
	private String orderFlowNum;
	private String kaDistributorLockNum;
	public List<ProductInventoryDetail> getCloudInventoryDetailList() {
		return cloudInventoryDetailList;
	}
	public void setCloudInventoryDetailList(List<ProductInventoryDetail> cloudInventoryDetailList) {
		this.cloudInventoryDetailList = cloudInventoryDetailList;
	}
	public String getMicroInventoryStockpile() {
		return microInventoryStockpile;
	}
	public void setMicroInventoryStockpile(String microInventoryStockpile) {
		this.microInventoryStockpile = microInventoryStockpile;
	}
	public String getOrderLockNum() {
		return orderLockNum;
	}
	public void setOrderLockNum(String orderLockNum) {
		this.orderLockNum = orderLockNum;
	}
	public String getOrderFlowNum() {
		return orderFlowNum;
	}
	public void setOrderFlowNum(String orderFlowNum) {
		this.orderFlowNum = orderFlowNum;
	}
	public String getKaDistributorLockNum() {
		return kaDistributorLockNum;
	}
	public void setKaDistributorLockNum(String kaDistributorLockNum) {
		this.kaDistributorLockNum = kaDistributorLockNum;
	}

}
