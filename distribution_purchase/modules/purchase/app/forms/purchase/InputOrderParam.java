package forms.purchase;

import utils.purchase.PurchaseTypes;

/**
 * 微仓订单-录入下单的参数封装
 * 
 * @author huangjc
 * @date 2016年9月26日
 */
public class InputOrderParam {
	private int inputId;
	// private boolean isPaied;
	private String payType;
	private double money;
	private int purchaseType = PurchaseTypes.PURCHASE_ORDER_NORMAL;// 1正常采购单，2缺货采购单
	private String businessRemarks;// 业务备注
	private String oaAuditNo;// oa审批单号，是唯一的
	private Double bbcPostage;

	public InputOrderParam(int inputId, String payType, double money) {
		super();
		this.inputId = inputId;
		this.payType = payType;
		this.money = money;
	}
	
	public InputOrderParam(int inputId, String payType, double money, String businessRemarks, String oaAuditNo) {
		super();
		this.inputId = inputId;
		this.payType = payType;
		this.money = money;
		this.businessRemarks = businessRemarks;
		this.oaAuditNo = oaAuditNo;
	}
	
	public Double getBbcPostage() {
		return bbcPostage;
	}

	public void setBbcPostage(Double bbcPostage) {
		this.bbcPostage = bbcPostage;
	}

	public String getOaAuditNo() {
		return oaAuditNo;
	}

	public void setOaAuditNo(String oaAuditNo) {
		this.oaAuditNo = oaAuditNo;
	}

	public String getBusinessRemarks() {
		return businessRemarks;
	}

	public void setBusinessRemarks(String businessRemarks) {
		this.businessRemarks = businessRemarks;
	}

	public int getPurchaseType() {
		return purchaseType;
	}

	public void setPurchaseType(int purchaseType) {
		this.purchaseType = purchaseType;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public int getInputId() {
		return inputId;
	}

	public void setInputId(int inputId) {
		this.inputId = inputId;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	@Override
	public String toString() {
		return "InputOrderParam [inputId=" + inputId + ", payType=" + payType
				+ ", money=" + money + ", purchaseType=" + purchaseType
				+ ", businessRemarks=" + businessRemarks + ", oaAuditNo="
				+ oaAuditNo + ", bbcPostage=" + bbcPostage + "]";
	}

}
