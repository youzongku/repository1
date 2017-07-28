package dto.product.result;


import java.util.List;

import dto.JsonResult;
import dto.product.PageResultDto;
import entity.product.InventoryLock;

/**
 * @author zbc
 * 2017年4月20日 上午10:17:40
 */
public class InventoryLockPageResult extends JsonResult<InventoryLockPage>{

	@Override
	public InventoryLockPage getData() {
		return super.getData();
	}
}

class InventoryLockPage extends  PageResultDto<InventoryLock> {

	/**
	 * @param pageSize
	 * @param rows
	 * @param currPage
	 * @param result
	 */
	public InventoryLockPage(Integer pageSize, Integer rows, Integer currPage,
			List<InventoryLock> result) {
		super(pageSize, rows, currPage, result);
	} 
	
	@Override
	public List<InventoryLock> getResult() {
		return super.getResult();
	} 
}