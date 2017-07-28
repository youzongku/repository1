package services.product_inventory;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.fasterxml.jackson.databind.JsonNode;

import dto.product_inventory.InitProductInventoryResult;
import dto.product_inventory.InventoryCommonResult;
import dto.product_inventory.ProductMicroInventoyResult;
import entity.product_inventory.InventorySyncRecord;
import entity.product_inventory.OrderMicroInventoryDeductRecord;
import entity.product_inventory.ProductInventoryDetail;
import entity.product_inventory.ProductInventoryTotal;


public interface IinventorySyncRecordService {
	
	public InventorySyncRecord selectBySkuAndWarehouseId(String sku,Integer warehouseId);
	
	public int updateSelective(InventorySyncRecord record);
	
	public int insertSelective(InventorySyncRecord record);

	public List<InventorySyncRecord> selectBySku(String csku);
	
	/**
	 * 校验微仓囤货数量
	 * @param s_skuStock
	 * @param integer 
	 */
	public InitProductInventoryResult checkMicroInventoryStockpile(String s_skuStock, Integer integer);

	/**
	 * 根据条件查询是否有已经微仓出库但未流转至HK的订单
	 * @param sku
	 * @param warehouseId
	 * @return
	 */
	public InitProductInventoryResult checkOrderExistHKBySku(String sku, int warehouseId);

	/**
	 * 校验云仓库
	 * @param reqParamList
	 * @return
	 */
	public String cloudInventoryCheck();

	public List<OrderMicroInventoryDeductRecord> getMicroDeductRecordByOrderNoAndSku(List<String> orderNoLists,
			String sku, Date expirationDate);

	public ProductInventoryDetail getInventoryDetailByParam(ProductInventoryDetail paramDetail);

	public int getMicroStockpileTotalWithExpirdationDate(ProductInventoryDetail paramDetail);

	public ProductMicroInventoyResult getOrderOccupyNum(ProductInventoryDetail paramDetail);

	public ProductMicroInventoyResult updateCloudInventory(List inventoryDetailDataLists);

	public List<ProductInventoryTotal> getCloudInventoryByWarehouseId(Integer warehouseId);

	public int getMicroStockpileTotal(String sku, Integer warehouseId);

	public ProductMicroInventoyResult getOrderOccupyNumBySkuAndWarehouseId(String sku, Integer warehouseId);

	/**
	 * 将云仓库存设置为0
	 * @param reqStr
	 * @return
	 */
	public String setCloudInventory(String reqStr);
	
	/**
	 * 从erp查询商品到期日期库存接口获取商品库存明细数据
	 * @param sku
	 * @param warehouseId
	 * @param type  若到期日期库存接口查询不到商品数据是否去查商品数据接口 0不查， 1 查
	 * @return
	 */
	public InventoryCommonResult<JsonNode> getErpExpirationInventoryData(String sku, int warehouseId,int type);

	/**
	 * 从erp查询商品列表接口获取商品库存数据
	 * @param sku
	 * @param warehouseId
	 * @return
	 */
	public InventoryCommonResult<JsonNode> getErpInventoryData(String sku, int warehouseId);

	/**
	 * 格式化erp返回数据结果
	 * @param tempErpDataStr
	 * @param warehouseId 
	 * @param sku 
	 * @return
	 */
	public InventoryCommonResult<List<ProductInventoryDetail>> formatErpInventoryData(String tempErpDataStr, String sku, Integer warehouseId);

	/**
	 * 获取清点同步结果
	 * @param time 
	 * @return
	 */
	public InventoryCommonResult<String> downLoadPhysicalResult(String time);

	/**
	 * 获取清点excel输出
	 * @param time
	 * @return
	 * @throws IOException 
	 */
	public byte[] downLoadCheckResult(String time) throws IOException;
}
