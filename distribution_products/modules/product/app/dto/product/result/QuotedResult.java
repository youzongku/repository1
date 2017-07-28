package dto.product.result;

import dto.JsonResult;
import dto.product.ContractQuotationsDto;

public class QuotedResult<T> extends JsonResult<ContractQuotationsDto> {
	
	@Override
	public ContractQuotationsDto getData() {
		return super.getData();
	}
}
