package dto.purchase;

import entity.purchase.PurchaseOrder;

/**
 * 返回参数实体 Created by luwj on 2015/12/7.
 */
public class ReturnMess {

	private String errorCode;//错误代码

	private String errorInfo;//错误信息
	
	private PurchaseOrder order;//采购单主表信息

	public ReturnMess(){}

	public ReturnMess(String errorCode, String errorInfo) {
		this.errorCode = errorCode;
		this.errorInfo = errorInfo;
	}

	public PurchaseOrder getOrder() {
		return order;
	}

	public void setOrder(PurchaseOrder order) {
		this.order = order;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	@Override
	public String toString() {
		return "ReturnMess [errorCode=" + errorCode + ", errorInfo="
				+ errorInfo + ", order=" + order + "]";
	}
	
}
