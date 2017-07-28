package dto.product_inventory;

import java.io.Serializable;

public class InventoryCommonResult<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2387793341969614450L;
	
	private Integer resultCode;
	private String msg;
	private T data;
	
	
	
	public InventoryCommonResult() {
		super();
	}
	public InventoryCommonResult(Integer resultCode, String msg) {
		super();
		this.resultCode = resultCode;
		this.msg = msg;
	}
	public InventoryCommonResult(Integer resultCode, T data) {
		super();
		this.resultCode = resultCode;
		this.data = data;
	}
	
	public InventoryCommonResult(Integer resultCode, String msg, T data) {
		super();
		this.resultCode = resultCode;
		this.msg = msg;
		this.data = data;
	}
	public Integer getResultCode() {
		return resultCode;
	}
	public void setResultCode(Integer resultCode) {
		this.resultCode = resultCode;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	
}
