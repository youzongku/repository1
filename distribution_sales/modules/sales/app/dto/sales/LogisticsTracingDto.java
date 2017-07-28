package dto.sales;

import entity.sales.LogisticsTracing;

public class LogisticsTracingDto extends LogisticsTracing {
	private String cordernumber;
	private String ctrackingnumber;
	private String logisticName;
	public String getCordernumber() {
		return cordernumber;
	}
	public void setCordernumber(String cordernumber) {
		this.cordernumber = cordernumber;
	}
	public String getCtrackingnumber() {
		return ctrackingnumber;
	}
	public void setCtrackingnumber(String ctrackingnumber) {
		this.ctrackingnumber = ctrackingnumber;
	}
	public String getLogisticName() {
		return logisticName;
	}
	public void setLogisticName(String logisticName) {
		this.logisticName = logisticName;
	}
}
