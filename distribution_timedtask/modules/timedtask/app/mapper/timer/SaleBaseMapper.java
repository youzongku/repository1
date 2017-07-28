package mapper.timer;

import entity.timer.SaleBase;

public interface SaleBaseMapper extends BaseMapper<SaleBase> {
	
	SaleBase selectByOrderId(Integer orderId);

}