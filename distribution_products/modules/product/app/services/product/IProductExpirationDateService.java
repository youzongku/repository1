package services.product;

import java.util.List;
import java.util.Map;

import dto.product.ProductLite;

public interface IProductExpirationDateService {
	/**
	 * 云仓发货选择商品到期日期
	 * @param proList
	 * @return
	 */
	Map<String, List<ProductLite>> setCloudSelectedProductsExpirationDates(List<ProductLite> proList);
}
