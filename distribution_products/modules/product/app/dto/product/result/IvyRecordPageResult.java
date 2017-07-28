package dto.product.result;


import java.util.List;

import dto.JsonResult;
import dto.product.PageResultDto;
import entity.product.IvyOprecord;

/**
 * @author zbc
 * 2017年4月20日 上午10:17:40
 */
public class IvyRecordPageResult extends JsonResult<IvyRecordPage>{

	@Override
	public IvyRecordPage getData() {
		return super.getData();
	}
	
}

class IvyRecordPage extends  PageResultDto<IvyOprecord> {

	/**
	 * @param pageSize
	 * @param rows
	 * @param currPage
	 * @param result
	 */
	public IvyRecordPage(Integer pageSize, Integer rows, Integer currPage,
			List<IvyOprecord> result) {
		super(pageSize, rows, currPage, result);
	} 
	
	@Override
	public List<IvyOprecord> getResult() {
		return super.getResult();
	} 
}

