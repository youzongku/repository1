package dto.purchase;

import java.util.List;

/**
 * 采购订单支付完毕，更新微仓，物理仓信息 物理库存变更记录操作 Created by luwj on 2015/12/24.
 */
public class InStorageIterm {

	private ReturnMess returnMess;

	private String email;

	private String purchaseNo;

	private String purchaseOrderId;

	private String status;

	private Integer purchaseType;

	private Integer sid;

	private List<InStorageDetail> pros;
	
	private String couponsCode;
	
	private Double couponsAmount;
	
	public String getCouponsCode() {
		return couponsCode;
	}

	public void setCouponsCode(String couponsCode) {
		this.couponsCode = couponsCode;
	}

	public Double getCouponsAmount() {
		return couponsAmount;
	}

	public void setCouponsAmount(Double couponsAmount) {
		this.couponsAmount = couponsAmount;
	}

	public Integer getPurchaseType() {
		return purchaseType;
	}

	public void setPurchaseType(Integer purchaseType) {
		this.purchaseType = purchaseType;
	}

	public ReturnMess getReturnMess() {
		return returnMess;
	}

	public void setReturnMess(ReturnMess returnMess) {
		this.returnMess = returnMess;
	}

	public Integer getSid() {
		return sid;
	}

	public void setSid(Integer sid) {
		this.sid = sid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPurchaseNo() {
		return purchaseNo;
	}

	public void setPurchaseNo(String purchaseNo) {
		this.purchaseNo = purchaseNo;
	}

	public String getPurchaseOrderId() {
		return purchaseOrderId;
	}

	public void setPurchaseOrderId(String purchaseOrderId) {
		this.purchaseOrderId = purchaseOrderId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<InStorageDetail> getPros() {
		return pros;
	}

	public void setPros(List<InStorageDetail> pros) {
		this.pros = pros;
	}
}
