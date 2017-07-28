package service.timer;

import entity.timer.SaleMain;

public interface ISaleMainService {
	
	boolean updateSaleMainOrder(SaleMain saleMain);

	void updateCouponsState(SaleMain sm);
}
