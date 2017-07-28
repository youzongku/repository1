package events.sales;

import java.io.Serializable;
import java.util.List;

/**
 * @author zbc
 * 2017年3月29日 上午11:22:30
 */
public class CaculateChargeEvent implements Serializable{

	private static final long serialVersionUID = -1945721059946596772L;
	
	private Integer sid;
	
	
	private List<String> orderNos;

	public Integer getSid() {
		return sid;
	}
	public CaculateChargeEvent(){
		
	}
	/**
	 * @param sid
	 */
	public CaculateChargeEvent(Integer sid) {
		super();
		this.sid = sid;
	}
	public CaculateChargeEvent(List<String> orderNos) {
		super();
		this.orderNos = orderNos;
	}
	public void setSid(Integer sid) {
		this.sid = sid;
	}
	public List<String> getOrderNos() {
		return orderNos;
	}
	public void setOrderNos(List<String> orderNos) {
		this.orderNos = orderNos;
	}
}
