package services.sales;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import dto.SkuWarehouse2Qty;

public interface IHttpService {
	
	/**
	 * 获取总到仓价，如果到仓价不存在，使用裸采价计算
	 *
	 * @param skuWarehouse2Qty
	 * @param useCostIfAbsent 是否使用裸采价替换空的到仓价
	 * @return {"suc":"true/false", result:10}
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode getTotalArriveWarehousePrice(Map<String, Integer> skuWarehouse2Qty, boolean useCostIfAbsent)
			throws JsonProcessingException, IOException;
	
	/**
	 * 批量获取商品到仓价和裸采价
	 * 
	 * @param list
	 * @return [{sku:"IF001", warehouseId:2024, arriveWarePrice:0.1, cost:0.2}]
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode batchGetArriveWarePrice(List<SkuWarehouse2Qty> list) throws JsonProcessingException,
	IOException;
	
	/**
	 * 获取微仓商品到期日期
	 * 
	 * @param email
	 * @param skuWarehouseIdNodeList
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode getMicroProductsExpirationDate(String email, List<ObjectNode> skuWarehouseIdNodeList) throws JsonProcessingException,
	IOException;
	
	/**
	 * 获取云仓商品到期日期
	 * 
	 * @param email
	 * @param params 内含sku & warehouseId
	 * @return [{"id":8932,"sku":"IF942-1","stock":93786,"warehouseId":2024,"warehouseName":"深圳仓",
				"updateTime":"2017-02-24","createTime":"2017-01-24","expirationDate":"2017-03-25"},...]
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode getCloudProductsExpirationDate(List<ObjectNode> skuWarehouseIdNodeList) throws JsonProcessingException,
	IOException;
	
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
	 * @return {suc:false/true, result:dismember对象json}
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
			Integer countryId, Integer cityId, Integer model) throws JsonProcessingException, IOException;

	/**
	 * 获取分销商信息
	 * 
	 * @param email
	 * @return
	 */
	public JsonNode getMemberInfo(String email) throws JsonProcessingException, IOException;


	/**
	 * 生成采购单
	 * 
	 * @param pMap
	 *            生成采购单参数
	 * @return
	 */
	public JsonNode postPurchase(Map<String, Object> pMap) throws JsonProcessingException, IOException;

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
	public JsonNode backStagePayment(String email,Double amount,String tranNum,Integer applyType,String remark)throws JsonProcessingException, IOException;

	/**
	 * 完成采购单
	 * @author zbc
	 * @since 2016年12月8日 下午3:02:05
	 */
	public JsonNode finishedOrder(String purchaseNo, Double total)throws JsonProcessingException, IOException;
	
	public JsonNode getPurByNo(String orderNo)throws JsonProcessingException, IOException;

	public JsonNode getByNo(String orderNo) throws JsonProcessingException, IOException;

	/**
	 *  锁库改版专用:微仓锁库
	 * @author zbc
	 * @since 2016年12月27日 下午12:16:24
	 */
	public JsonNode microLock(Map<String,Object> param) throws JsonProcessingException, IOException;
	
	/**
	 *  锁库改版专用:更新库存
	 * @author zbc
	 * @since 2016年12月26日 下午7:32:49
	 */
	public JsonNode updateStock(String orderNo)throws JsonProcessingException, IOException;
	
	/**
	 *  锁库改版专用:还原微仓
	 * @param sod 发货单号
	 * @author zbc
	 * @since 2016年12月28日 上午9:10:46
	 */
	public JsonNode restoreMicro(String sod)throws JsonProcessingException, IOException;
	
	/**
	 *  锁库改版专用:还原云仓
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
	
	/**
	 * 根据shopId查询店铺信息
	 * @param shopId
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode getShopInfo(Integer shopId)throws JsonProcessingException, IOException;

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月28日 下午4:17:49
	 */
	public JsonNode contractChargeMatch(List<Map<String, Object>> contractChargeMapList, String salesOrderNo, String payDate)throws JsonProcessingException, IOException;
	
	
	/**
	 * 根据组织架构节点id，获取关联分销商
	 * @author zbc
	 * @since 2017年3月20日 下午5:24:19
	 */
	public JsonNode getOrgInfo(Integer oid) throws JsonProcessingException, IOException;

	/**
	 * 
	 * @author zbc
	 * @since 2017年4月21日 下午12:18:03
	 */
	public JsonNode getCloudProductsExpirationDate(String email, List<ObjectNode> skuWarehouseIdNodeList) throws JsonProcessingException, IOException;

	/**
<<<<<<< HEAD
	 * 获取省下面的id
	 * @author zbc
	 * @since 2017年5月20日 下午4:29:11
	 * @param proId
	 */
	public JsonNode getCities(Integer proId) throws JsonProcessingException, IOException;

	/**
	 * @author zbc
	 * @since 2017年5月22日 下午12:10:27
	 * @param email
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode getAccount(String email) throws JsonProcessingException, IOException;

	/**
	 * 修改采购单运费
	 * @author zbc
	 * @since 2017年5月24日 上午11:38:20
	 * @param email
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode changePurchaseOrderFreight(List<Map<String,Object>> list) throws JsonProcessingException, IOException;

	/**
	 * 后台退款
	 * @author zbc
	 * @since 2017年5月24日 下午3:29:10
	 * @param disAccount 分销商账号
	 * @param amount     退款金额
	 * @param orderNo    订单号
	 * @author zbc
	 * @since 2017年5月15日 下午3:30:45
	 * @param postList
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode refund(String disAccount, Object amount, String orderNo) throws JsonProcessingException, IOException;

	/**
	 * 采购单云仓锁库
	 * @author zbc
	 * @since 2017年6月2日 上午10:28:48
	 * @param pNo
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode orderLock(String pNo) throws JsonProcessingException, IOException;
	
	public JsonNode getContractFeeItems(Map<String, Object> postList) throws JsonProcessingException, IOException;

}
