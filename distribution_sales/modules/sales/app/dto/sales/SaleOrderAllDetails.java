package dto.sales;

import java.util.ArrayList;
import java.util.List;

import entity.sales.SaleDetail;

public class SaleOrderAllDetails {

	private int mainId;

	private List<SaleDetail> currentDetail = new ArrayList<SaleDetail>();

	private List<SaleDetail> historyDetail = new ArrayList<SaleDetail>();
	
	private String salesOrderNo;
	
	public String getSalesOrderNo() {
		return salesOrderNo;
	}

	public void setSalesOrderNo(String salesOrderNo) {
		this.salesOrderNo = salesOrderNo;
	}

	public int getMainId() {
		return mainId;
	}

	public void setMainId(int mainId) {
		this.mainId = mainId;
	}

	public List<SaleDetail> getCurrentDetail() {
		return currentDetail;
	}

	public void setCurrentDetail(List<SaleDetail> currentDetail) {
		this.currentDetail = currentDetail;
	}

	public List<SaleDetail> getHistoryDetail() {
		return historyDetail;
	}

	public void setHistoryDetail(List<SaleDetail> historyDetail) {
		this.historyDetail = historyDetail;
	}

}
