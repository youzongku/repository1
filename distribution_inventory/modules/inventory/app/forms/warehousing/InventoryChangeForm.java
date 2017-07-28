package forms.warehousing;

import java.io.Serializable;
import java.util.List;

import org.elasticsearch.common.collect.Lists;

import play.data.validation.Constraints.Required;

/**
 * 库存dto
 * 
 * @author ye_ziran
 * @since 2016年3月3日 下午3:13:17
 */
public class InventoryChangeForm implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Required
	private int orderType;// 订单类型,1入0出
	@Required
	private String orderTitle;// 订单标题,销售出库、采购入库...
	@Required
	private String orderNo;// 订单编号
	@Required
	private String orderOrigin;// 订单来源

	private Integer mwarehouseId;//微仓id
	private String distributorId;//分销商ID
	private String distributorName;//分销商名称
	private String distributorEmail;// 分销商Email

	private List<InventoryChangeDetailForm> detailList;

	public int getOrderType() {
		return orderType;
	}
	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}
	public String getOrderTitle() {
		return orderTitle;
	}
	public void setOrderTitle(String orderTitle) {
		this.orderTitle = orderTitle;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getOrderOrigin() {
		return orderOrigin;
	}
	public void setOrderOrigin(String orderOrigin) {
		this.orderOrigin = orderOrigin;
	}
	public List<InventoryChangeDetailForm> getDetailList() {
		return detailList;
	}
	public void setDetailList(List<InventoryChangeDetailForm> detailList) {
		this.detailList = detailList;
	}
	public Integer getMwarehouseId() {
		return mwarehouseId;
	}
	public void setMwarehouseId(Integer mwarehouseId) {
		this.mwarehouseId = mwarehouseId;
	}
	public String getDistributorId() {
		return distributorId;
	}
	public void setDistributorId(String distributorId) {
		this.distributorId = distributorId;
	}
	public String getDistributorName() {
		return distributorName;
	}
	public void setDistributorName(String distributorName) {
		this.distributorName = distributorName;
	}
	public String getDistributorEmail() {
		return distributorEmail;
	}
	public void setDistributorEmail(String distributorEmail) {
		this.distributorEmail = distributorEmail;
	}

	@Override
	public String toString() {
		return "InventoryChangeForm [orderType=" + orderType + ", orderTitle=" + orderTitle + ", orderNo=" + orderNo
				+ ", orderOrigin=" + orderOrigin + ", distributorEmail=" + distributorEmail + ", detailList="
				+ detailList + "]";
	}

}
