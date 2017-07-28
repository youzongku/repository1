package events.sales;

public class ClosedOrderEvent {
	
	private String so;//订单号
	
	private String em;//账号

	public ClosedOrderEvent(String so, String em) {
		super();
		this.so = so;
		this.em = em;
	}

	public String getSo() {
		return so;
	}

	public void setSo(String so) {
		this.so = so;
	}

	public String getEm() {
		return em;
	}

	public void setEm(String em) {
		this.em = em;
	}
	
	
}
