package dto.product.result;

import java.util.List;

import dto.JsonResult;
import dto.product.ProductLite;

/**
 * @author zbc
 * 2017年5月8日 下午4:23:15
 */
public class ProductLiteResult<T> extends JsonResult<List<ProductLite>> {
	
	@Override
	public List<ProductLite> getData() {
		return super.getData();
	}

}
