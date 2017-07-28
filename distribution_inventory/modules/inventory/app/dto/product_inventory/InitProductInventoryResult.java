package dto.product_inventory;

import java.util.Map;

public class InitProductInventoryResult {
	private boolean result;
	private String msg;
	private Map<String,Integer> expirationWithNum;
	public InitProductInventoryResult(boolean result, String msg, Map<String, Integer> expirationWithNum) {
		super();
		this.result = result;
		this.msg = msg;
		this.expirationWithNum = expirationWithNum;
	}
	public InitProductInventoryResult(boolean result, String msg) {
		super();
		this.result = result;
		this.msg = msg;
	}
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
	
	public Map<String,Integer> getExpirationWithNum() {
		return expirationWithNum;
	}
	public void setExpirationWithNum(Map<String,Integer> expirationWithNum) {
		this.expirationWithNum = expirationWithNum;
	}
	
}
