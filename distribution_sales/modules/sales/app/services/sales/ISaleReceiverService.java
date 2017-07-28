package services.sales;

import java.util.List;

import entity.marketing.MarketingOrder;
import entity.sales.Receiver;
import entity.sales.SaleBase;
import entity.sales.SaleMain;

public interface ISaleReceiverService {

	boolean saveReceiver(Receiver receiver);
	
	boolean udpateReceiver(Receiver receiver);
	
	Receiver getReceiverByOrderId(Integer orderId);
	
	Receiver addReceiver(SaleMain main, SaleBase base);
	Receiver addReceiver(MarketingOrder mo);
	
	int deleteReceiver(Integer rid, String account);
	
	List<Receiver> query(String account, String searchText);
	
}
