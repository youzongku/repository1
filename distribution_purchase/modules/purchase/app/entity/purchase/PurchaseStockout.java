package entity.purchase;

import java.util.Date;

/**
 * 整批出库的数据
 * 
 * @author huangjc
 * @date 2016年12月16日
 */
public class PurchaseStockout implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private int status = 0;// 状态：0未执行；1执行成功；2执行失败
	private String purchaseOrderNo;
	private String jsonStr;
	private Date createDate;
	private Date lastUpdateDate;

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPurchaseOrderNo() {
		return purchaseOrderNo;
	}

	public void setPurchaseOrderNo(String purchaseOrderNo) {
		this.purchaseOrderNo = purchaseOrderNo;
	}

	public String getJsonStr() {
		return jsonStr;
	}

	public void setJsonStr(String jsonStr) {
		this.jsonStr = jsonStr;
	}

	@Override
	public String toString() {
		return "PurchaseStockout [id=" + id + ", status=" + status
				+ ", purchaseOrderNo=" + purchaseOrderNo + ", jsonStr="
				+ jsonStr + ", createDate=" + createDate + "]";
	}

}
