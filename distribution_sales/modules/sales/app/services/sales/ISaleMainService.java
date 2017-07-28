package services.sales;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import entity.sales.OrderPack;
import entity.sales.SaleMain;

public interface ISaleMainService {
	
	/**
	 *   用户取消订单，这里实现的是status为1（代付款）订单的取消，其他状态的暂未实现
	 *  change by zbc 正在处理实现
	 * @param main
	 * @return
	 */
	Map<String,Object> cancelOrder(JsonNode main);
	
	/**
	 * 根据销售发货单单号获取销售发货单主表信息
	 * @param orderNo
	 * @return
	 */
	SaleMain getSaleMainOrderByOrderNo(String orderNo);
	
	
	List<SaleMain> getAllSaleMain(Map<String,Object> paramMap);
	
	/**
	 * 描述：批量设置订单审核通过
	 * @param orderIds
	 * @return
	 */
	boolean batchUpdateVerify(List<String> orderIds);

	/**
	 * 根据销售发货单主表ID获取销售发货单主表信息
	 * @return
	 */
	SaleMain getSaleMainOrderByID(Integer id);

	/**
	 * 构建批量支付参数
	 * @param ids
	 * @return
	 */
	public Map<String, Object> buildBatchPayParam(String ids);

	/**
	 * 批量审核
	 * @param ids  订单id
	 * @param email  审核人
	 * @param comment  审核不通过原因
	 * @param status   审核状态
	 * @return
	 */
	public void batchAudit(List<String> ids,String email,String comment,String status);

	/**
	 * 更新优惠码信息
	 * @param sm
	 */
	void updateCouponsState(SaleMain sm);
	
	/**
	 * 同步销售单状态到门店
	 * @param sm
	 */
	void pushStatusToStory(SaleMain sm);
	

	/**
	 * b2c 同步 状态到 bbc 接口
	 * @param main
	 * @return
	 */
	public Map<String, Object> updateSalesStatus(JsonNode main);

	/**
	 * @param main
	 * @return
	 */
	Map<String, Object> erpCatchResult(JsonNode main);

	/**
	 * 批量同步销售单状态到门店
	 * @param sm
	 */
	void batchPushStatusToStory(List<SaleMain> list);
	/**
	 * 批量同步销售单状态到MSite
	 * @param sm
	 */
	void batchPushStatusToMSite(List<OrderPack> list);

	/**
	 * 根据id和accounts查询订单（accounts是指定的分销商）
	 * @param params
	 * @return
	 */
	SaleMain getSaleMainByIdAndAccounts(Map<String, Object> params);

	String closeSalesFromB2C(String param);

	String undoClose(String orderNo);

	Map<String, Object> check(String node, String optUser);

	boolean updSalesOrderVirtualPayInfo(String  main,String ip);

	String changeNickNameByEmail(String param);


	Map<String, Object> updateErpStatus(String reqStr);
	
	/**
	 * 更新发货单（审核不通过会异步关闭订单）
	 * @param main
	 * @return
	 */
	public boolean updateByPrimaryKeySelective(SaleMain main);

	/**
	 * 批量财务审核
	 *
	 * @param node
	 * @return
	 */
	Map<String, Object> batchAuditByFinance(String node);
}
