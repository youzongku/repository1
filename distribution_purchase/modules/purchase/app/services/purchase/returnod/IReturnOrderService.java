package services.purchase.returnod;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import dto.purchase.returnod.CalculateReturnAmountResult;
import dto.purchase.returnod.ReturnOrderDto;
import entity.purchase.returnod.ReturnOrder;
import entity.purchase.returnod.ReturnOrderDetail;
import forms.purchase.Page;
import forms.purchase.returnod.AuditReturnOrderParams;
/**
 * 微仓退货service
 *
 * @author huangjc
 * @since 2017年3月10日
 */
public interface IReturnOrderService {
	
	/**
	 * 计算要退款的金额-根据系数计算
	 * @param detailParams
	 * @return
	 */
	CalculateReturnAmountResult calculateExpectReturnAmount(ReturnOrderDetail detailParams);
	/**
	 * 申请退货
	 * @param email 用户
	 * @param detailParams 要退货的商品信息
	 * @return
	 */
	Map<String,Object> applyReturnOrder(ReturnOrder ro, ReturnOrderDetail detailParams);

	/**
	 * 取消退货审核，谁申请，就由谁取消
	 * @return
	 */
	Map<String,Object> cancelReturnOrderApplication(String email, String returnOrderNo);

	/**
	 * 获取退货单列表
	 * @param email 当前登录用户（后台传的是null）
	 * @param paramsNode 查询参数
	 * @return
	 */
	Page<ReturnOrderDto> getReturnOrdersByPage(String email, JsonNode paramsNode);
	
	/**
	 * 计算符合条件的发货单该要退的金额总数
	 * @param paramsNode
	 * @return
	 */
	Double getTotalUserExpectReturnAmount4MatchedConditions(JsonNode paramsNode);

	/**
	 * 待审核退货单
	 * @param email
	 * @param paramsNode
	 * @return
	 */
	public Page<ReturnOrderDto> get2BeAuditedReturnOrders(String email, JsonNode paramsNode);

	/**
	 * 审核
	 *
	 * @return
	 */
	Map<String, Object> audit(AuditReturnOrderParams params);

	/**
	 * 批量审核
	 * @param params
	 * @return
	 */
	public Map<String, Object> batchAudit(AuditReturnOrderParams params);

	ReturnOrderDto getReturnOrder(String returnOrderNo);
}
