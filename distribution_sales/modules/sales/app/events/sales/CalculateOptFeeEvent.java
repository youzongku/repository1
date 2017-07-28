package events.sales;

import java.util.List;

import entity.sales.SaleBase;
import entity.sales.SaleDetail;
import entity.sales.SaleMain;
/**
 * 计算操作费
 * @author huangjc
 * @date 2017年1月12日
 */
public class CalculateOptFeeEvent {
	private SaleMain main;

	private SaleBase base;

	private List<SaleDetail> details;
	
	public CalculateOptFeeEvent() {
		
	}

	public CalculateOptFeeEvent(SaleMain main, SaleBase base, List<SaleDetail> details) {
		super();
		this.main = main;
		this.base = base;
		this.details = details;
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

}
