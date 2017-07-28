package entity.purchase;

public class PurchaseActive {
	
	private Integer id;
	
	private Integer activeid;//活动ID
	
	private String priviledgeid;//优惠Id 由逗号分隔开
	
	private String orderno;//订单号

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getActiveid() {
		return activeid;
	}

	public void setActiveid(Integer activeid) {
		this.activeid = activeid;
	}

	public String getPriviledgeid() {
		return priviledgeid;
	}

	public void setPriviledgeid(String priviledgeid) {
		this.priviledgeid = priviledgeid;
	}

	public String getOrderno() {
		return orderno;
	}

	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}
	
}
