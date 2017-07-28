package services.payment;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface IDealInventoryService {
	
	
	
	// TODO -------------仓库接口，需要进行对接--------------
	/**
	 * /purchase/cancel
	 * /purchase/getByNo
	 * /inventory/updIvy
	 * /sales/getAllNeedToPurDetail
	 * /inventory/ivyDe
	 * /sales/updStu
	 * /inventory/stkChgRrd
	 * 
	 * */
	
	/**
	 * 更新采购单
	 * @param purchaseNo
	 * @param total 金额
	 * @param flag
	 * @return
	 */
	public String cancelPurchase(String purchaseNo, Double total, String flag,Map<String,String> payMap);
	
	/**
	 * 获取采购单
	 * @param purchaseOrderNo
	 * @param flag
	 * @return
	 */
	public String getPurchase(String purchaseOrderNo,String flag);
	
	
	/**
	 * 支付成功生成交易记录
	 * @param params
	 * @return
	 */
	public String createBillRecord(Map<String, Object> params);

	/**
	 * 支付成功后处理逻辑
	 * @param orderNo
	 * @param total 
	 */
	public void callback(String orderNo, String total,String desc,Map<String, String> payMap);

	public Map<String,Object> receiveSyncReturn(Map<String, String[]> params, ObjectNode result, String orderno, Double amount,String flag,String sid);
	/**
	 * 支付运费成功后处理逻辑
	 * @param orderNo
	 * @param sid
	 * @param total
	 * @param desc
	 * @param payDate
	 * @param tradeNo
	 * @param payType
	 */
	public void callback2(String orderNo,String sid,String total,String payDate, String tradeNo, String payType);
	
	public String getMemberInfo(String email);

	/**
	 * 获取销售单信息
	 * @param orderNo
	 * @return
	 */
	public String getSaleMain(String orderNo);

	/**
	 * 判断是否是销售单
	 * @param outOrderNo
	 * @return
	 */
	public boolean isSaleOrder(String outOrderNo);

	/**
	 * 在线充值逻辑处理
	 * @author zbc
	 * @since 2017年6月19日 上午11:39:56
	 * @param outTradeNo
	 * @param transactionId
	 * @param string
	 */
	public void onlinePaySuccessCallback(String outTradeNo, String transactionId, String string);


	/**
	 * 完成发货单
	 * @author zbc
	 * @since 2017年6月30日 下午12:28:11
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
	public String finishSaleOrder(Integer sid, Double actualPay, boolean isComplete, String payDate, String payNo,
			String payType) throws JsonProcessingException, IOException;

}
