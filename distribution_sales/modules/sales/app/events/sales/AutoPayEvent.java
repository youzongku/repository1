package events.sales;

import java.util.List;

import entity.sales.SaleBase;
import entity.sales.SaleDetail;
import entity.sales.SaleMain;

public class AutoPayEvent {
	
	private String purchaseNo;//采购单号
	
	private SaleMain main;
	
	private SaleBase base;
	
	private List<SaleDetail> details;
	
	private Double freight;//运费

	public String getPurchaseNo() {
		return purchaseNo;
	}

	public void setPurchaseNo(String purchaseNo) {
		this.purchaseNo = purchaseNo;
	}

	public SaleMain getMain() {
		return main;
	}

	public void setMain(SaleMain main) {
		this.main = main;
	}

	public SaleBase getBase() {
		return base;
	}

	public void setBase(SaleBase base) {
		this.base = base;
	}

	public List<SaleDetail> getDetails() {
		return details;
	}

	public void setDetails(List<SaleDetail> details) {
		this.details = details;
	}

	public Double getFreight() {
		return freight;
	}

	public void setFreight(Double freight) {
		this.freight = freight;
	}

}
