package dto.product_inventory;

import java.io.Serializable;
import java.util.List;

public class PurchaseOrderResult implements Serializable {

	private boolean result;
	private String msg;
	private List<ProductMicroDeductDetail> microOutList;
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public List<ProductMicroDeductDetail> getMicroOutList() {
		return microOutList;
	}
	public void setMicroOutList(List<ProductMicroDeductDetail> microOutList) {
		this.microOutList = microOutList;
	}
	
	@Override
	public String toString() {
		return "PurchaseOrderResult [result=" + result + ", msg=" + msg + ", microOutList=" + microOutList + "]";
	}
	
}
