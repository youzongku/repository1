package services.product_inventory;

import java.util.List;
import java.util.Map;

import dto.product_inventory.CancleSaleOrderResult;
import dto.product_inventory.CreateSaleOrderResult;
import dto.product_inventory.MicroInventoryQueryResult;
import dto.product_inventory.MsiteStockOutResult;
import dto.product_inventory.ProductMicroInventoryDetailDto;
import dto.product_inventory.ProductMicroInventoryDetailSearchDto;
import dto.product_inventory.ProductMicroInventoyResult;
import dto.product_inventory.PurchaseOrderResult;
import dto.product_inventory.ReturnProductData;
import dto.warehousing.MicroRealWarehouseDto;
import entity.product_inventory.ProductMicroInventoryDetail;
import entity.product_inventory.ProductMicroInventoryInRecord;
import entity.product_inventory.ProductMicroInventoryTotal;
import entity.warehousing.MicroGoodsInventory;
import forms.warehousing.MicroGoodsInventoryForm;

public interface IProductMicroInventoryService {

	/**
	 * 根据账户、仓库id、多个sku查询微仓商品
	 * @param parms
	 * @return
	 */
	List<MicroInventoryQueryResult> selectByParams(ProductMicroInventoryDetailSearchDto parms);

	/**
	 * 查询单个sku的详情 
	 * @param microInventoryDetail
	 * @return
	 */
	ProductMicroInventoryDetail  selectByMicroInventoryDetail(ProductMicroInventoryDetail microInventoryDetail);
	
	/**
	 * 单个商品微仓入库
	 * @param microInventory
	 * @return
	 */
	ProductMicroInventoyResult microInventoryInSingle(ProductMicroInventoryDetail microInventory);
	
	/**
	 * 微仓商品库存总表+
	 * 先查询是否有该商品 有-->数量+ 更新数据 返回true
	 * 			          无-->新建一个 返回true
	 * @return
	 */
	ProductMicroInventoyResult microTotalInventoryAdd(ProductMicroInventoryTotal microInventoryTotal);


	CreateSaleOrderResult createSaleOrderWithInventory(String jsonDataStr);


	CancleSaleOrderResult cancleSaleOrder(String jsonStr);

	/**
	 * 根据采购单号将微仓中的商品还原到云仓
	 * 微仓详情-采购数量   微仓总仓-采购数量，微仓入库记录剩余量0
	 * 云仓明细+采购数量   云仓总仓+采购数量， 云仓入库记录
	 * @param orderNo
	 */
	ProductMicroInventoyResult restoreCloudFormMicroInventory(String orderNo);

	/**
	 * M站出库逻辑
	 *
	 * @param jsonDataStr
	 */
	MsiteStockOutResult msiteStockOut(String jsonDataStr);

	/**
	 * 查询指定微仓中商品
	 * @param mInventoryForm
	 * @return
	 */
	List<MicroGoodsInventory> queryByParams(MicroGoodsInventoryForm mInventoryForm);

	int total(MicroGoodsInventoryForm mInventoryForm);

	/**
	 * 查询某sku，account的采购入仓记录
	 * @param s_main
	 * @param email
	 * @return
	 */
	List<ProductMicroInventoryInRecord> getProductMicroInventoryDetail(String s_main, String email);

	/**
	 * M站取消订单
	 *
	 * @param jsonData
	 * @return
	 */
	CancleSaleOrderResult cancleMsiteSaleOrderWithInventory(String jsonData);

	/**
	 * 根据条件查询微仓明细
	 * @param reqParam
	 * @return
	 */
	vo.inventory.Page<ProductMicroInventoryDetail> selectMicroDetailByParam(
			ProductMicroInventoryDetailSearchDto reqParam);

	/**
	 * 获取微仓采购明细
	 * @param id
	 * @return
	 */
	List<ProductMicroInventoryInRecord> getPurchaseDetail(Integer id);


	List<MicroRealWarehouseDto> queryMicroWarehouse(String email);

	/**
	 * 根据入仓记录信息释放微仓库存到云仓
	 * @param microInRecordParam
	 * @param account 
	 * @return
	 */
	ProductMicroInventoyResult releaseMicroStockToCloud(ProductMicroInventoryInRecord microInRecordParam, String account);

	/**
	 * 同步旧系统微仓库存数据
	 */
	void asyncMicroInventory();

	/**
	 * 退货锁定微仓库存
	 * @param returnParam
	 * @return
	 */
	PurchaseOrderResult returnProductLockMicroInventory(ReturnProductData returnParam);

	/**
	 * 退货成功释放微仓库存至云仓
	 * @param returnOrderNo
	 * @return
	 */
	PurchaseOrderResult returnProductSuccess(String returnOrderNo);

	/**
	 *  使退货微仓锁库记录失效
	 * @param returnOrderNo
	 * @return
	 */
	PurchaseOrderResult updateReturnLockRecordEffective(String returnOrderNo);

	/**
	 * 查询微仓明细
	 * @param reqParam
	 * @return
	 */
	List<ProductMicroInventoryDetail> selectMicroDetailByParams(ProductMicroInventoryDetailSearchDto reqParam);

	/**
	 * 根据sku和warid、account获取微仓详情
	 * @param reqParam
	 * @return
	 */
	List<ProductMicroInventoryDetail> selectMicroDetailBySkuAndWareId(ProductMicroInventoryDetailSearchDto reqParam);

	/**
	 * 根据条件查询微仓入仓记录
	 * @param microInRecordParam
	 * @return
	 */
	List<ProductMicroInventoryInRecord> getMicroInventoryInRecordByParam(ProductMicroInventoryInRecord microInRecordParam);
	
}
