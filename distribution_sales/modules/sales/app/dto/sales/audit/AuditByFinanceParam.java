package dto.sales.audit;

/**
 * 财务审核传递的参数
 */
public class AuditByFinanceParam {

	private Integer orderId;
	private String comment;
	private int status;

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "AuditByFinanceParam [orderId=" + orderId + ", comment=" + comment + ", status=" + status + "]";
	}

}
