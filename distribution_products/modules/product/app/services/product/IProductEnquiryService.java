package services.product;

import dto.product.PageResultDto;
import dto.product.ProductSearchParamDto;

public interface IProductEnquiryService {
	
	PageResultDto products(ProductSearchParamDto searchDto);
}
