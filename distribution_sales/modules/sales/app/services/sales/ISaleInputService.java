package services.sales;

/**
 * 录入发货单
 * @author huangjc
 * @date 2017年3月24日
 */
public interface ISaleInputService {

	String getCheckedProducts(String email);

	String addProducts(String param);

	String refreshProducts(String email);

	String updateInfo(String param);

	String batchDelete(String param);

}
