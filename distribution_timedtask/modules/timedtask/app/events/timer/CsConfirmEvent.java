package events.timer;

import java.util.Map;

import com.google.common.collect.Maps;

public class CsConfirmEvent {
	private String orderNo;//订单号
	private Boolean flag = true;
	
	private Map<String, Object> csparam = null;
	
	public CsConfirmEvent() {
	}
	
	public CsConfirmEvent(String orderNo,Boolean flag) {
		this.orderNo = orderNo;
		this.flag = flag;
		csparam = Maps.newHashMap();
		csparam.put("sno", this.orderNo);
		csparam.put("csAudit", this.flag);
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Boolean getFlag() {
		return flag;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
	}

	public Map<String, Object> getCsparam() {
		return csparam;
	}

}
