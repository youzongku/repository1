package services.product;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import dto.JsonResult;
import dto.product.CloudExpirationFormatResult;
import dto.product.ProductLite;
import dto.product.inventory.CreateSaleOrderResult;
import dto.product.inventory.InventoryLockStock;
import dto.product.inventory.ProductCloudInventoryResult;
import dto.product.inventory.ProductInventoryDetail;

/**
 * @author zbc
 * 2017年4月18日 下午3:28:37
 */
public interface IInventoryLockService {

	/**
	 * 
	 * @author zbc
	 * @since 2017年4月18日 下午7:59:10
	 */
	JsonResult<?> create(String string,String admin);

	/**
	 * 
	 * @author zbc
	 * @since 2017年4月19日 下午3:44:15
	 */
	JsonResult<?> get(Integer id);

	/**
	 * 
	 * @author zbc
	 * @since 2017年4月19日 下午4:50:48
	 */
	JsonResult<?> release(Integer id);

	/**
	 * 
	 * @author zbc
	 * @since 2017年4月19日 下午6:31:37
	 */
	JsonResult<?> detailPages(String string);

	/**
	 * 
	 * @author zbc
	 * @since 2017年4月20日 下午12:04:41
	 */
	JsonResult<?> page(String string);

	/**
	 * KA锁库重新计算库存
	 * @author zbc
	 * @since 2017年4月20日 下午5:05:07
	 */
	List<ProductLite> substock(String email, List<ProductLite> list);
	
	/**
	 * KA锁库重新计算库存
	 * @author zbc
	 * @since 2017年4月20日 下午5:05:07
	 */
	ProductLite substock(String email, ProductLite product);

	/**
	 * 
	 * @author zbc
	 * @since 2017年4月21日 上午11:50:08
	 */
	List<CloudExpirationFormatResult> dealCloud(String string);

	/**
	 * 
	 * @author zbc
	 * @since 2017年4月21日 下午4:01:11
	 */
	ProductCloudInventoryResult<ProductInventoryDetail> cloudlock(String string);

	/**
	 * 
	 * @author zbc
	 * @since 2017年4月22日 下午5:07:48
	 */
	CreateSaleOrderResult microlock(String string);

	/**
	 * 
	 * @author zbc
	 * @since 2017年4月25日 上午11:41:56
	 */
	InventoryLockStock stock(String sku, Integer wareId);

	JsonResult<?> cloudSelectedExpirationDates(String string);

	JsonResult<?> reSetInventoryLock(String string, String adminAccount);

	JsonResult<?> getRecords(String string);

	JsonResult<?> getResetDetails(String string);
}
