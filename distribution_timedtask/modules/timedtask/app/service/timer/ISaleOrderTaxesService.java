package service.timer;

import entity.timer.SaleMain;


public interface ISaleOrderTaxesService {
	/**
	 * 计算税金
	 * @param mainId
	 * @param salesOrderNo
	 * @return
	 */
	SaleMain calculateTaxes(Integer mainId, String salesOrderNo);

}
