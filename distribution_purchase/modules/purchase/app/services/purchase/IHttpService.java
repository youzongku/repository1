package services.purchase;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import dto.purchase.InventoryCloudLockDto;
import entity.purchase.PurchaseActive;
import forms.purchase.GetProductsParams;
import forms.purchase.returnod.ReturnLockParams;

public interface IHttpService {
	
	/**
	 * 根据采购单号获取微仓入库记录
	 * @param purchaseOrderNo
	 * @return 错误：{"suc": false,"msg": "采购单号CG20170110000001453查询不到相关入库信息！"}
	 * 			成功：{
			  "suc": true,
			  "result": [
			    {
			      "id": 287,"account": "13579985814","sku": "IF1019-3",
			      "imgUrl": "https://static.tomtop.com.cn/images/I/1/IF1005-1/IF1005-1-1-0784-lolr.jpg",
			      "productTitle": "丹麦杰克布森黑巧克力橙味饼干","qty": 1,"purchasePrice": 15.92,"capfee": 2.65,
			      "arriveWarePrice": 5.98,"warehouseId": 2024,"warehouseName": "深圳仓","isGift": 0,
			      "orderDetailId": null,"orderNo": "CG201701100000014531","containerNumber": null,
			      "storageLocation": null,"expirationDate": "2017-09-09","purchaseTime": "2017-01-10",
			      "residueNum": 0,"createTime": "2017-01-10","updateTime": "2017-01-10","contractNo": null,
			      "clearancePrice": null
			    },
			  ]
			}
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode getMicroInRecord(String purchaseOrderNo) throws JsonProcessingException,
	IOException;
	
	/**
	 * 获取发货单售后退款均摊，均摊到每个商品（这里只是拿到了指定采购单单号的退货详情）
	 * @param purchaseOrderNo
	 * @return {
	 * 				suc:true, 
	 * 				shOrderInfoList:[
	 * 					{
	 * 						shOrder: {}, 
	 * 						shOrderDetailList: [
	 * 							{售后详情}
	 * 						]
	 * 					}
	 * 				]
	 * 			}
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode getReturnAmountCapfee4Sku(String purchaseOrderNo) throws JsonProcessingException,
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
	 * 微仓退货-微仓锁库
	 * @param params
	 * @return {"result": 0, "msg": "退货锁定库存成功"}<br>
	{"result": 1, "msg": "XXXXXXXXXXXX"}<br>
	result为0，成功；result为1，失败
	 *
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode returnLock(ReturnLockParams params) throws JsonProcessingException,
			IOException;

	/**
	 * 微仓退货-微仓库存退还云仓
	 * @param params
	 * @return {"result": 0, "msg": "微仓库存退货成功"}<br>
	{"result": 1,"returnFailList": ["RD20170328152101","RD20170328152101"]}<br>
	result为0，成功；result为1，失败
	 *
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode returnToCloudInventory(List<String> returnOrderNoList) throws JsonProcessingException,
			IOException;

	/**
	 * 微仓退货-释放微仓库存锁
	 * @param params
	 * @return {"result": 0, "msg": "释放退货锁库成功"}<br>
	{"result": 1,"releaseFailList": ["RD20170328152101","RD20170328152101"]}<br>
	result为0，成功；result为1，失败
	 *
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode releaseReturnLock(List<String> returnOrderNoList) throws JsonProcessingException,
			IOException;



	/**
	 * 获取运费
	 * @param params
	 * @return
	 */
	public String getFreight(Map<String,Object> params);

	/**
	 * 创建发货单
	 * @param params
	 * @return
	 */
	public String createSaleOrder(String params);

	/**
	 * 余额支付
	 * @param params
	 * @return
	 */
	public String balancePayment(Map<String,Object> params);


	/**
	 * 获取欠款
	 * @param email 用户
	 * @return
	 */
	public String getDebt(String email) throws JsonProcessingException, IOException;
	
	/**
	 * 将线下转账多余的钱充到余额里
	 * @param purchaseOrder
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode offlineTransferExtraMoney2Balance(String email,String purchaseOrderNo,Double money) throws JsonProcessingException,
			IOException;

	/**
	 * 微仓退货，将款退回到余额里
	 * @param email
	 * @param purchaseOrderNo
	 * @param money
	 * @return code等于4，就是成功，其余的都是失败
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode refund2Balance4ReturnOrder(String email,String returnOrderNo,Double money) throws JsonProcessingException,
			IOException;

	/**
	 * 获取总到仓价
	 *
	 * @param skuWarehouse2Qty
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode getTotalArriveWarehousePrice(Map<String, Integer> skuWarehouse2Qty)
			throws JsonProcessingException, IOException;

	/**
	 * 根据email获取分销商
	 *
	 * @param email
	 * @return {"suc":true/false,"result":dismember}
	 */
	public JsonNode getDismemberByEmail(String email)
			throws JsonProcessingException, IOException;

	/**
	 * 获取商品信息
	 *
	 * @param skus
	 * @param warehouseId
	 * @return
	 */
	public JsonNode getProducts(GetProductsParams params) throws JsonProcessingException,
			IOException;

	/**
	 * 获取分销商信息
	 *
	 * @param email
	 * @return
	 */
	public JsonNode getMemberInfo(String email) throws JsonProcessingException,
			IOException;


	public JsonNode checkActive(Map<String, Object> param)
			throws JsonProcessingException, IOException;

	/**
	 * @param code 优惠码
	 * @param orderStatus 订单状态
	 * @param actuallyPay 实际金额
	 * @param orderAmount 订单金额
	 * @param user 使用人
	 * @param istatus  优惠码状态
	 * 更新优惠码信息
	 * @author zbc
	 * @since 2016年12月14日 上午9:59:27
	 */
	public JsonNode updateCoupons(String user,String code, Integer orderStatus, Integer istatus,
								  Double actuallyPay, Double orderAmount,Date usageTime,String orderNo)
			throws JsonProcessingException, IOException;

	/**
	 * 采购单支付，统计促销活动相关信息
	 * @author zbc
	 * @since 2016年12月14日 上午11:00:27
	 */
	public JsonNode syncActiveLog(PurchaseActive act, String email, Double total, String orderno)
			throws JsonProcessingException, IOException;

	public JsonNode actExcute(Integer actId, Map<String, Object> param)throws JsonProcessingException, IOException;


	/**
	 * 云仓锁库
	 * @author zbc
	 * @since 2016年12月26日 下午5:54:54
	 */
	public JsonNode cloudLock(InventoryCloudLockDto lockDto)throws JsonProcessingException, IOException ;

	/**
	 * 整单优惠后重新更新锁库时的到仓价
	 * @param postMap
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode changeInventoryCafee(Map<String, Object> postMap) throws JsonProcessingException, IOException;

	/**
	 * 支付成功永久锁定库存
	 * @author zbc
	 * @since 2016年12月26日 下午7:16:10
	 */
	public JsonNode permanentLock(String orderNo)throws JsonProcessingException, IOException;

	/**
	 * 取消订单 释放锁库
	 * @author zbc
	 * @since 2016年12月26日 下午7:25:56
	 */
	public JsonNode unLock(String orderNo)throws JsonProcessingException, IOException;

	/**
	 * 更新库存
	 * @author zbc
	 * @since 2016年12月26日 下午7:32:49
	 */
	public JsonNode updateStock(String orderNo)throws JsonProcessingException, IOException;

	/**
	 * 获取客户订单信息
	 * @author zbc
	 * @since 2016年12月27日 下午3:52:04
	 */
	public JsonNode getSaleMain(Integer sid)throws JsonProcessingException, IOException;


	public JsonNode getPriceList(List<String> skus)throws JsonProcessingException, IOException;

	/**
	 * 保存微仓出库明细
	 * @author zbc
	 * @since 2016年12月27日 下午5:54:44
	 */
	public JsonNode saveMicroOut(Integer sid, JsonNode mircoOut)throws JsonProcessingException, IOException;

	/**
	 * TODO 锁库改版专用:还原微仓
	 * @param sid 发货单id
	 * @param operater 操作人
	 * @author zbc
	 * @since 2016年12月28日 上午9:10:46
	 */
	public JsonNode restoreMicro(Integer sid,String operater)throws JsonProcessingException, IOException;

	/**
	 * 校验付款流水号
	 * @param transferNumber
	 * @author zbc
	 * @since 2017年1月16日 上午10:33:37
	 */
	public JsonNode checktransferNumber(String transferNumber)throws JsonProcessingException, IOException;

	/**
	 * 获取分销用户客服账号
	 *
	 * @param email
	 *            分销账号
	 * @return
	 */
	public JsonNode custaccount(String email) throws JsonProcessingException, IOException;

	/**
	 * 获取订单详情
	 * @author zbc
	 * @since 2017年4月26日 下午2:42:20
	 */
	public JsonNode getOrderDetails(String orderNo) throws JsonProcessingException, IOException;

	/**
	 * 获取云仓商品到期日期
	 * @author zbc
	 * @since 2017年5月17日 上午9:21:07
	 * @param email
	 * @param skuWarehouseIdNodeList
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode getCloudProductsExpirationDate(String email, List<ObjectNode> skuWarehouseIdNodeList)
			throws JsonProcessingException, IOException;

	/**
	 * 采购单失效 关闭发货单
	 * @author zbc
	 * @since 2017年6月14日 上午9:44:04
	 * @param so
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode cancelSalesOrder(String so)throws JsonProcessingException, IOException;

	/**
	 * 完成发货单
	 * @author zbc
	 * @since 2017年6月21日 下午5:37:44
	 * @param sid
	 * @param actualPay
	 * @param isComplete
	 * @param payDate
	 * @param payNo
	 * @param payType
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode finishSaleOrder(Integer sid, Double actualPay, boolean isComplete,String payDate, String payNo, String payType)
			throws JsonProcessingException, IOException;
}
