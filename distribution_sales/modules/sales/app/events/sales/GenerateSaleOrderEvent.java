package events.sales;

import java.util.List;

import entity.marketing.MarketingOrder;
import entity.marketing.MarketingOrderDetail;

public class GenerateSaleOrderEvent {

	private String operater;// 操作人

	private MarketingOrder marketOrder;// 营销单

	private List<MarketingOrderDetail> details;// 营销单详情

	public GenerateSaleOrderEvent(String operater, MarketingOrder marketOrder,
			List<MarketingOrderDetail> details) {
		super();
		this.operater = operater;
		this.marketOrder = marketOrder;
		this.details = details;
	}

	public String getOperater() {
		return operater;
	}

	public void setOperater(String operater) {
		this.operater = operater;
	}

	public MarketingOrder getMarketOrder() {
		return marketOrder;
	}

	public void setMarketOrder(MarketingOrder marketOrder) {
		this.marketOrder = marketOrder;
	}

	public List<MarketingOrderDetail> getDetails() {
		return details;
	}

	public void setDetails(List<MarketingOrderDetail> details) {
		this.details = details;
	}

}
