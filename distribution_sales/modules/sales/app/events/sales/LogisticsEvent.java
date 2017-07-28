package events.sales;

import java.util.List;

import entity.sales.OrderPack;
import entity.sales.SaleBase;
import entity.sales.SaleMain;

public class LogisticsEvent {
	
	private List<SaleBase> sbs;
	private List<SaleMain> sms;
	private List<OrderPack> ops;
	
	public LogisticsEvent(List<SaleBase> sbs, List<SaleMain> sms, List<OrderPack> ops) {
		super();
		this.sbs = sbs;
		this.sms = sms;
		this.ops = ops;
	}
	public List<SaleBase> getSbs() {
		return sbs;
	}
	public void setSbs(List<SaleBase> sbs) {
		this.sbs = sbs;
	}
	public List<SaleMain> getSms() {
		return sms;
	}
	public void setSms(List<SaleMain> sms) {
		this.sms = sms;
	}
	public List<OrderPack> getOps() {
		return ops;
	}
	public void setOps(List<OrderPack> ops) {
		this.ops = ops;
	}
	
}
