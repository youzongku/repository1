package service.timer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import entity.timer.HistoryOrderData;
import entity.timer.SaleDetail;


public interface IHttpService {
	/**
	 * 获取操作费
	 * @param warehouseId
	 * @param qty
	 * @return 返回值：
	 * <br/>{"msg":"计算完毕","result":true,"warehouseId":2026,"qty":10,"optfee":100.0}<br/>
	 * {"msg":"仓库不存在","result":false,"warehouseId":202,"qty":10,"optfee":0}
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode getOptFee(int warehouseId,int qty)
			throws JsonProcessingException, IOException;
	
	/**
	 * 根据email获取分销商
	 * @param email
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode getDismemberByEmail(String email)
			throws JsonProcessingException, IOException;
	
	/**
	 * 获取采购单
	 * @param purchaseOrderNo
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode getPurchaseOrder(String purchaseOrderNo) throws JsonProcessingException, IOException;
	
	/**
	 * 获取采购单详情
	 * @param purchaseOrderNo
	 * @return
	 */
	public JsonNode getPurchaseDetailList(String purchaseOrderNo) throws JsonProcessingException, IOException;

	/**
	 * 获取店铺信息
	 * 
	 * @param email
	 *            分销账号
	 * @param shopName
	 *            店铺名称
	 * @param platformId
	 *            平台ID
	 * @return
	 */
	public JsonNode getShopInfo(String email, String shopName, Integer platformId) throws JsonProcessingException, IOException;

	/**
	 * 获取分销用户客服账号
	 * 
	 * @param email
	 *            分销账号
	 * @return
	 */
	public JsonNode custaccount(String email) throws JsonProcessingException, IOException;

	/**
	 * 获取商品信息
	 * 
	 * @param skus
	 * @param warehouseId
	 * @param distributionMode 
	 * @return
	 */
	public JsonNode getProducts(String email, List<String> skus, Integer warehouseId, Integer distributionMode)throws JsonProcessingException, IOException;
	
	public JsonNode getWarehouse(Integer wid) throws JsonProcessingException, IOException;
	
	/**
	 * 获取省ID
	 * 
	 * @param province
	 * @return
	 */
	public JsonNode getProvinces(String province) throws JsonProcessingException, IOException;

	/**
	 * 根据仓库获取物流方式
	 * 
	 * @param warehouseId
	 * @return
	 */
	public JsonNode getShoppingMethod(Integer warehouseId) throws JsonProcessingException, IOException;

	/**
	 * @param warehouseId
	 * @param shippingCode
	 * @param skuList
	 * @param provinceId
	 * @param countryId
	 * @return
	 */
	public JsonNode getFreight(Integer warehouseId, String shippingCode, JsonNode skuList, Integer provinceId,
			Integer countryId) throws JsonProcessingException, IOException;

	/**
	 * 获取分销商信息
	 * 
	 * @param email
	 * @return
	 */
	public JsonNode getMemberInfo(String email) throws JsonProcessingException, IOException;

	/**
	 * 检查库存信息
	 * 
	 * @param email
	 * @param salesOrderNo
	 * @param sd
	 * @param totalCheck 
	 * @return
	 */
	// TODO -------------仓库接口，需要进行对接--------------
	public JsonNode checkInventory(String email, String salesOrderNo, List<SaleDetail> sd, Boolean totalCheck) throws JsonProcessingException, IOException;
	/**
	 * 扣除库存
	 * 
	 * @param email
	 *            分销账号
	 * @param sd
	 *            扣减详情
	 * @return
	 */
	// TODO -------------仓库接口，需要进行对接--------------
	public String deductionStock(String email, List<SaleDetail> sd) throws JsonProcessingException, IOException;

	/**
	 * 生成采购单
	 * 
	 * @param pMap
	 *            生成采购单参数
	 * @return
	 */
	public JsonNode postPurchase(Map<String, Object> pMap) throws JsonProcessingException, IOException;

	/**
	 * 采购入库
	 * 
	 * @param email
	 *            分销账号
	 * @param updIvyParam
	 *            入库详情
	 * @param purchaseNo
	 *            采购单号
	 * @return
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 */
	// TODO -------------仓库接口，需要进行对接--------------
	public JsonNode updateInventory(String email, List<SaleDetail> updIvyParam, String purchaseNo) throws JsonProcessingException, IOException;

	/**
	 * 还原微仓
	 * @param detailList
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	// TODO -------------仓库接口，需要进行对接--------------
	public JsonNode restoreMicroStock(String email,List<SaleDetail> detailList) throws JsonProcessingException, IOException;
	/**
	 * 取消采购单
	 * @param purchaseOrderNo 采购单号
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode cancelPurchaseOrder(String purchaseOrderNo) throws JsonProcessingException, IOException;

	/**
	 * 修改采购单价格
	 * @author zbc
	 * @since 2016年12月2日 下午6:05:18
	 */
	public JsonNode changeOrderPrice(JsonNode node)throws JsonProcessingException, IOException;

	/**
	 * 后台系统支付(自动扣除余额)
	 * @author zbc
	 * @since 2016年12月8日 下午2:43:37
	 */
	public JsonNode backStagePayment(String email,Double amount,String tranNum,Integer applyType)throws JsonProcessingException, IOException;

	/**
	 * 完成采购单
	 * @author zbc
	 * @since 2016年12月8日 下午3:02:05
	 */
	public JsonNode finishedOrder(String purchaseNo, Double total)throws JsonProcessingException, IOException;
	
	public JsonNode getPurByNo(String orderNo)throws JsonProcessingException, IOException;

	public JsonNode getByNo(String orderNo) throws JsonProcessingException, IOException;

	/**
	 * TODO 锁库改版专用:微仓锁库
	 * @author zbc
	 * @since 2016年12月27日 下午12:16:24
	 */
	public JsonNode microLock(Map<String,Object> param) throws JsonProcessingException, IOException;
	
	/**
	 * TODO 锁库改版专用:更新库存
	 * @author zbc
	 * @since 2016年12月26日 下午7:32:49
	 */
	public JsonNode updateStock(String orderNo)throws JsonProcessingException, IOException;
	
	/**
	 * TODO 锁库改版专用:还原微仓
	 * @param sod 发货单号
	 * @author zbc
	 * @since 2016年12月28日 上午9:10:46
	 */
	public JsonNode restoreMicro(String sod)throws JsonProcessingException, IOException;
	
	/**
	 * TODO 锁库改版专用:还原云仓
	 * @param pod 采购单号
	 * @author zbc
	 * @since 2016年12月28日 上午9:11:52
	 */
	public JsonNode restoreCloud(String pod)throws JsonProcessingException, IOException;

	/**
	 * 获取商品价格
	 * @author zbc
	 * @since 2017年1月4日 下午6:49:37
	 */
	public JsonNode getPriceList(List<String> skus)throws JsonProcessingException, IOException ;

	/**
	 * 云仓锁库
	 * @author zbc
	 * @since 2017年1月4日 下午6:53:20
	 */
	public JsonNode cloudLock(Map<String, Object> postMap) throws JsonProcessingException, IOException;

	/**
	 * 释放锁库
	 * @author zbc
	 * @since 2017年1月9日 上午10:13:33
	 */
	public JsonNode unLock(String orderNo) throws JsonProcessingException, IOException;

	public JsonNode historyOrderClosed(HistoryOrderData order)throws JsonProcessingException, IOException;
}
