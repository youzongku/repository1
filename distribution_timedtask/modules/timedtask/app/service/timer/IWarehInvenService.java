package service.timer;

import java.util.List;

import entity.timer.Warehouse;


/**
 *
 * 存储b2c推送仓库信息、商品库存信息
 */
public interface IWarehInvenService {

	/**
	 * @return
	 */
	public List<Warehouse> queryWarehouse(Integer wid);

}
