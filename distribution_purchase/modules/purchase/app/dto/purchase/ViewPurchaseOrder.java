package dto.purchase;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import entity.purchase.PurchaseAudit;
import entity.purchase.PurchaseOrderAuditLog;
import utils.purchase.DateUtils;
import utils.purchase.StringUtils;

/**
 * Created by luwj on 2015/12/8. TODO 修改该实体类是，需要同步openapi 同名实体类
 */
public class ViewPurchaseOrder implements Serializable {

	private static final long serialVersionUID = -1911260747784749392L;
	private Integer id;
	private String email;// 用户
	private String purchaseOrderNo;// 采购单号
	private Integer status;// 采购单状态：0待付款，1已付款，2已取消
	private String remarks;// 备注信息
	private Date purchaseDate;// 下单时间
	private String sorderDate;// 下单时间
	private String statusMes;// 订单状态：待付款，已付款，已取消
	private String paymentId;// 付款方式
	private Double purchaseTotalAmount;// 订单总金额
	private Date payDate;// 付款时间
	private String spayDate;// 付款时间
	private Double purchaseDiscountAmount;// 订单折后金额
	private Integer sid;// 若当前采购订单是缺货采购，那么本属性应该对应缺货的发货单，否则本属性则为空
	private Double discount;// 订单折扣
	private Double salesAmount;// 销售单缺货采购实付金额
	private Integer purchaseType;// 采购类型（1：常规采购，2：缺货采购）
	private Date cancelDate;// 取消时间
	private Double bbcPostage;// 分销平台运费
	private String logisticsMode;// 物流方式
	private String couponsCode;// 优惠码
	private Double couponsAmount;// 优惠码面额
	private Double deductionAmount;// 促销减免金额
	private Boolean isPro;// 是否存在活动
	private Boolean isChoose;// 是否已选优惠
	private String inputUser;// 录入人
	private Double offlineMoney;// 现金交易支付金额
	private Double financeMoney;// 财务实收金额
	private String remark;// 财务备注
	private String reason;// 审核理由
	private Double totalOfflineMoney;// 总现金支付金额（包含关联订单的现金交易支付金额在内）
	private Double orderProTotal;// 订单商品总金额
	private Double reducePrice;// 减价金额
	private Double tAWPrice; // 总的到仓价
	private String customerService;// 客服账号
	private String oaAuditNo;// oa审批单号，唯一的
	private String busenessRemarks;// 业务备注
	private String nickName;// 分销商昵称
	// 是否由财务驳回，此字段只有在发货单财务审核才有用（只有审核不通过才为true，其余为false）
	private boolean rejectedByFinance = false;
	private List<ViewPurchaseDetail> details;// 采购单详情
	private PurchaseAudit purchaseAudit;// 线下转账的才有
	private List<PurchaseOrderAuditLog> csAuditLogs;// 客服审核记录
	private List<PurchaseOrderAuditLog> financeAuditLogs;// 财务审核记录

	public boolean isRejectedByFinance() {
		return rejectedByFinance;
	}

	public void setRejectedByFinance(boolean rejectedByFinance) {
		this.rejectedByFinance = rejectedByFinance;
	}

	public String getOaAuditNo() {
		return oaAuditNo;
	}

	public void setOaAuditNo(String oaAuditNo) {
		this.oaAuditNo = oaAuditNo;
	}

	public String getBusenessRemarks() {
		return busenessRemarks;
	}

	public void setBusenessRemarks(String busenessRemarks) {
		this.busenessRemarks = busenessRemarks;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Double gettAWPrice() {
		return tAWPrice;
	}

	public void settAWPrice(Double tAWPrice) {
		this.tAWPrice = tAWPrice;
	}

	public List<PurchaseOrderAuditLog> getCsAuditLogs() {
		return csAuditLogs;
	}

	public void setCsAuditLogs(List<PurchaseOrderAuditLog> csAuditLogs) {
		this.csAuditLogs = csAuditLogs;
	}

	public List<PurchaseOrderAuditLog> getFinanceAuditLogs() {
		return financeAuditLogs;
	}

	public void setFinanceAuditLogs(List<PurchaseOrderAuditLog> financeAuditLogs) {
		this.financeAuditLogs = financeAuditLogs;
	}

	public PurchaseAudit getPurchaseAudit() {
		return purchaseAudit;
	}

	public void setPurchaseAudit(PurchaseAudit purchaseAudit) {
		this.purchaseAudit = purchaseAudit;
	}

	public String getCustomerService() {
		return customerService;
	}

	public void setCustomerService(String customerService) {
		this.customerService = customerService;
	}

	public Double getOrderProTotal() {
		return orderProTotal;
	}

	public void setOrderProTotal(Double orderProTotal) {
		this.orderProTotal = orderProTotal;
	}

	public Double getReducePrice() {
		return reducePrice;
	}

	public void setReducePrice(Double reducePrice) {
		this.reducePrice = reducePrice;
	}

	public Double getTotalOfflineMoney() {
		return totalOfflineMoney;
	}

	public void setTotalOfflineMoney(Double totalOfflineMoney) {
		this.totalOfflineMoney = totalOfflineMoney;
	}

	public Double getOfflineMoney() {
		return offlineMoney;
	}

	public void setOfflineMoney(Double offlineMoney) {
		this.offlineMoney = offlineMoney;
	}

	public String getInputUser() {
		return StringUtils.isBlankOrNull(inputUser) ? email : inputUser;
	}

	public void setInputUser(String inputUser) {
		this.inputUser = inputUser;
	}

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

	public Double getBbcPostage() {
		return bbcPostage;
	}

	public void setBbcPostage(Double bbcPostage) {
		this.bbcPostage = bbcPostage;
	}

	public String getLogisticsMode() {
		return logisticsMode;
	}

	public void setLogisticsMode(String logisticsMode) {
		this.logisticsMode = logisticsMode;
	}

	public Date getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}

	public String getCancelDateStr() {
		String cancelDateStr = "";
		if (cancelDate != null) {
			cancelDateStr = DateUtils.date2FullDateTimeString(cancelDate);
		}
		return cancelDateStr;
	}

	public Integer getPurchaseType() {
		return purchaseType;
	}

	public void setPurchaseType(Integer purchaseType) {
		this.purchaseType = purchaseType;
	}

	public Double getSalesAmount() {
		return salesAmount;
	}

	public void setSalesAmount(Double salesAmount) {
		this.salesAmount = salesAmount;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Integer getSid() {
		return sid;
	}

	public void setSid(Integer sid) {
		this.sid = sid;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPurchaseOrderNo() {
		return purchaseOrderNo;
	}

	public void setPurchaseOrderNo(String purchaseOrderNo) {
		this.purchaseOrderNo = purchaseOrderNo;
	}

	public Date getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public String getSorderDate() {
		if (purchaseDate != null) {
			return DateUtils.date2FullDateTimeString(purchaseDate);
		}
		return sorderDate;
	}

	public void setSorderDate(String sorderDate) {
		this.sorderDate = sorderDate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getStatusMes() {
		if(null == status) {
			return "";
		}
		switch (status) {
			case 0:
				return "待付款";
			case 1:
				return "已完成";
			case 2:
				return "已取消";
			case 3:
				return "已取消";
			case 4:
				return "待客服确认";
			case 5:
				return "审核不通过";
			case 6:
				return "待财务确认";
			default:
				return "未知状态";
		}
		// return statusMes;
	}

	public void setStatusMes(String statusMes) {
		this.statusMes = statusMes;
	}

	public Double getPurchaseTotalAmount() {
		return purchaseTotalAmount;
	}

	public void setPurchaseTotalAmount(Double purchaseTotalAmount) {
		this.purchaseTotalAmount = purchaseTotalAmount;
	}

	public Date getPayDate() {
		return payDate;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	public String getSpayDate() {
		if (payDate != null) {
			return DateUtils.date2FullDateTimeString(payDate);
		}
		return spayDate;
	}

	public void setSpayDate(String spayDate) {
		this.spayDate = spayDate;
	}

	public Double getPurchaseDiscountAmount() {
		return purchaseDiscountAmount;
	}

	public void setPurchaseDiscountAmount(Double purchaseDiscountAmount) {
		this.purchaseDiscountAmount = purchaseDiscountAmount;
	}

	public List<ViewPurchaseDetail> getDetails() {
		return details;
	}

	public void setDetails(List<ViewPurchaseDetail> details) {
		this.details = details;
	}

	public Double getDeductionAmount() {
		return deductionAmount;
	}

	public void setDeductionAmount(Double deductionAmount) {
		this.deductionAmount = deductionAmount;
	}

	public Boolean getIsPro() {
		return isPro;
	}

	public void setIsPro(Boolean isPro) {
		this.isPro = isPro;
	}

	public Boolean getIsChoose() {
		return isChoose;
	}

	public void setIsChoose(Boolean isChoose) {
		this.isChoose = isChoose;
	}

	public Double getFinanceMoney() {
		return financeMoney;
	}

	public void setFinanceMoney(Double financeMoney) {
		this.financeMoney = financeMoney;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	@Override
	public String toString() {
		return "ViewPurchaseOrder [id=" + id + ", email=" + email + ", purchaseOrderNo=" + purchaseOrderNo + ", status="
				+ status + ", remarks=" + remarks + ", purchaseDate=" + purchaseDate + ", sorderDate=" + sorderDate
				+ ", statusMes=" + statusMes + ", paymentId=" + paymentId + ", purchaseTotalAmount="
				+ purchaseTotalAmount + ", payDate=" + payDate + ", spayDate=" + spayDate + ", purchaseDiscountAmount="
				+ purchaseDiscountAmount + ", sid=" + sid + ", discount=" + discount + ", salesAmount=" + salesAmount
				+ ", purchaseType=" + purchaseType + ", cancelDate=" + cancelDate + ", bbcPostage=" + bbcPostage
				+ ", logisticsMode=" + logisticsMode + ", couponsCode=" + couponsCode + ", couponsAmount="
				+ couponsAmount + ", deductionAmount=" + deductionAmount + ", isPro=" + isPro + ", isChoose=" + isChoose
				+ ", inputUser=" + inputUser + ", offlineMoney=" + offlineMoney + ", financeMoney=" + financeMoney
				+ ", remark=" + remark + ", reason=" + reason + ", totalOfflineMoney=" + totalOfflineMoney
				+ ", orderProTotal=" + orderProTotal + ", reducePrice=" + reducePrice + ", tAWPrice=" + tAWPrice
				+ ", customerService=" + customerService + ", oaAuditNo=" + oaAuditNo + ", busenessRemarks="
				+ busenessRemarks + ", nickName=" + nickName + ", rejectedByFinance=" + rejectedByFinance + ", details="
				+ details + ", purchaseAudit=" + purchaseAudit + ", csAuditLogs=" + csAuditLogs + ", financeAuditLogs="
				+ financeAuditLogs + "]";
	}

}
