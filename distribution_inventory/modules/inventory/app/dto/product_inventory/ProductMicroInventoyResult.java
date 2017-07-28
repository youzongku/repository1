package dto.product_inventory;

import java.io.Serializable;
import java.util.List;

public class ProductMicroInventoyResult implements Serializable {
	private boolean result;
	private String msg;
	private Object entity;
	private List entityList;
	public ProductMicroInventoyResult(boolean result, String msg,Object entity, List entityList) {
		super();
		this.entity=entity;
		this.result = result;
		this.msg = msg;
		this.entityList = entityList;
	}
	public ProductMicroInventoyResult() {
	}
	public boolean getResult() {
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
	public Object getEntity() {
		return entity;
	}
	public void setEntity(Object entity) {
		this.entity = entity;
	}
	public List getEntityList() {
		return entityList;
	}
	public void setEntityList(List entityList) {
		this.entityList = entityList;
	}
}
