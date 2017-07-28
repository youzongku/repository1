package services.purchase;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import entity.purchase.PurchaseOrderAuditLog;
import forms.purchase.FinanceAuditParam;

/**
 * 采购单审核service
 * @author huangjc
 * @date 2016年12月1日
 */
public interface IPurchaseOrderAuditService {
	
	/**
	 * 获取一个采购单所有的审核记录
	 * @param purchaseOrderNo
	 * @return
	 */
	public List<PurchaseOrderAuditLog> getAOrderAllAuditLogs(String purchaseOrderNo);
	
	/**
	 * 获取最新的审核记录
	 * @param purchaseOrderNo
	 * @return
	 */
	public PurchaseOrderAuditLog getOrderAuditLastestLog(String purchaseOrderNo);
	
	/**
	 * 客服审核，可能通过（流转到财务审核(auditByFinance方法)），可能关闭
	 * @param param 客服审核参数
	 * @return
	 */
	public Map<String,Object> auditByCustomerService(JsonNode node);
	/**
	 * 财务审核，可能审核通过，可能打回到客服审核(auditByCustomerService方法)
	 * @param param 财务审核参数
	 * @return 
	 */
	public Map<String,Object> auditByFinance(FinanceAuditParam param);
}
