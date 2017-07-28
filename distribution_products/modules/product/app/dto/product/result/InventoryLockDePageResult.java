package dto.product.result;


import java.util.List;

import dto.JsonResult;
import dto.product.PageResultDto;
import entity.product.InventoryLockDetail;

/**
 * @author zbc
 * 2017年4月20日 上午10:17:40
 */
public class InventoryLockDePageResult extends JsonResult<InventoryLockDePage>{

	@Override
	public InventoryLockDePage getData() {
		return super.getData();
	}
	
}

class InventoryLockDePage extends  PageResultDto<InventoryLockDetail> {

	/**
	 * @param pageSize
	 * @param rows
	 * @param currPage
	 * @param result
	 */
	public InventoryLockDePage(Integer pageSize, Integer rows, Integer currPage,
			List<InventoryLockDetail> result) {
		super(pageSize, rows, currPage, result);
	} 
	
	@Override
	public List<InventoryLockDetail> getResult() {
		return super.getResult();
	} 
}

