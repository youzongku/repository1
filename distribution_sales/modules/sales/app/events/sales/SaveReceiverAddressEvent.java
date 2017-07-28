package events.sales;

import entity.marketing.MarketingOrder;
import entity.sales.SaleBase;
import entity.sales.SaleMain;

/**
 * 保存发货人地址
 */
public class SaveReceiverAddressEvent {
	private SaleMain main;
	private SaleBase base;
	private MarketingOrder mo;

	public SaveReceiverAddressEvent(SaleMain main, SaleBase base) {
		super();
		this.main = main;
		this.base = base;
	}

	public SaveReceiverAddressEvent(MarketingOrder mo) {
		super();
		this.mo = mo;
	}

	public MarketingOrder getMo() {
		return mo;
	}

	public void setMo(MarketingOrder mo) {
		this.mo = mo;
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

	@Override
	public String toString() {
		return "SaveReceiverAddressEvent [main=" + main + ", base=" + base + ", mo=" + mo + "]";
	}

}
