package services.sales;


import java.util.Map;

import entity.sales.SaleBase;

public interface ISaleBaseService {
	
	SaleBase saveSaleBaseOrder(SaleBase saleBase);
	
	boolean updateSaleBaseOrder(SaleBase saleBase);
	
	SaleBase getSaleBaseByOrderId(Integer orderId);
	
	SaleBase getSaleBaseByOrderNo(String salesOrderNo);

	Map getOrderer(String salesOrderNo);

}
