package dto.product.result;

import java.util.List;

import dto.JsonResult;
import dto.product.PageResultDto;
import entity.product.IvyOptDetail;

public class IvyOptDetailPageResult<T> extends JsonResult<IvyOptDetailPage>{
	
	@Override
	public IvyOptDetailPage getData() {
		return super.getData();
	}

}

class IvyOptDetailPage extends PageResultDto<IvyOptDetail>{

	public IvyOptDetailPage(Integer pageSize, Integer rows, Integer currPage, List<IvyOptDetail> result) {
		super(pageSize, rows, currPage, result);
	}
	@Override
	public List<IvyOptDetail> getResult() {
		return super.getResult();
	}
	
}