package dto.sales;

import java.util.List;

import entity.sales.ShOrder;
import entity.sales.ShOrderDetail;
/**
 * 售后单，包含了详情
 */
public class ShOrderInfo {

	private ShOrder shOrder;
	private List<ShOrderDetail> shOrderDetailList;

	public ShOrderInfo(ShOrder shOrder, List<ShOrderDetail> shOrderDetailList) {
		super();
		this.shOrder = shOrder;
		this.shOrderDetailList = shOrderDetailList;
	}

	public ShOrder getShOrder() {
		return shOrder;
	}

	public void setShOrder(ShOrder shOrder) {
		this.shOrder = shOrder;
	}

	public List<ShOrderDetail> getShOrderDetailList() {
		return shOrderDetailList;
	}

	public void setShOrderDetailList(List<ShOrderDetail> shOrderDetailList) {
		this.shOrderDetailList = shOrderDetailList;
	}

	@Override
	public String toString() {
		return "ShOrderInfo [shOrder=" + shOrder + ", shOrderDetailList=" + shOrderDetailList + "]";
	}

}
