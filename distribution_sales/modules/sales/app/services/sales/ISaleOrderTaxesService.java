package services.sales;

import entity.sales.SaleMain;
import events.sales.AutoPayEvent;

public interface ISaleOrderTaxesService {
	/**
	 * 计算税金
	 * @param mainId
	 * @param salesOrderNo
	 * @return
	 */
	SaleMain calculateTaxes(Integer mainId, String salesOrderNo);

	public boolean autoPay(AutoPayEvent event);
}
