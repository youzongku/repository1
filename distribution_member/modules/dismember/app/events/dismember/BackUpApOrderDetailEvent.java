package events.dismember;

/**
 * 备份账期支付的订单事件
 */
public class BackUpApOrderDetailEvent {
	public BackUpApOrderDetailEvent(Integer apOrderId, String orderNo) {
		super();
		this.orderNo = orderNo;
		this.apOrderId = apOrderId;
	}

	private String orderNo;
	private Integer apOrderId;

	public Integer getApOrderId() {
		return apOrderId;
	}

	public void setApOrderId(Integer apOrderId) {
		this.apOrderId = apOrderId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

}
