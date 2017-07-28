package services.product_inventory;

import java.util.List;
import java.util.Map;

import component.elasticsearch.CloudInventoryDoc;
import component.elasticsearch.MicroInventoryDoc;
import dto.product_inventory.ProductInventoryEnquiryRequest;
import dto.product_inventory.ProductInventoryEnquiryResult;
import entity.product_inventory.ProductInventoryBatchDetail;
//import entity.product_inventory.ProductInventoryContainerNumber;

public interface IProductInventoryService {
	
	
	/**
	 * 接收ERP的入仓记录详情，同时更新云仓库存
	 * @param ps ERP入仓记录详情集合
	 * @return 更新结果
	 */
	public Map<String,String> receiveStockInAndUpdateCloud(List<ProductInventoryBatchDetail> ps);
	
	/**
	 * 查询所有云仓库存
	 * 
	 * @param param
	 * @return
	 * @author ye_ziran
	 * @since 2017年1月6日 下午5:48:00
	 */
	List<CloudInventoryDoc> cloudInventory();
	
	/**
	 * 查询所有微仓库存
	 * 
	 * @param disAccount		分销商账号
	 * @return
	 * @author ye_ziran
	 * @since 2017年1月6日 下午5:48:33
	 */
	List<MicroInventoryDoc> microInventory(String disAccount);
} 