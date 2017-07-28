package mapper.purchase;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.purchase.OrderOperateRecord;
import entity.purchase.PurchaseOrderAuditLog;

public interface PurchaseOrderAuditLogMapper extends
		BaseMapper<PurchaseOrderAuditLog> {

	/**
	 * 保存采购单审核记录
	 * 
	 * @param log
	 * @return
	 */
	public int insertOrderAuditLog(PurchaseOrderAuditLog log);

	/**
	 * 获取一个单的所有审核记录
	 * 
	 * @param purchaseNo
	 * @return
	 */
	public List<PurchaseOrderAuditLog> selectAOrderAllLogsByAuditType(
			@Param("purchaseNo") String purchaseNo,
			@Param("auditType") Integer auditType);
	
	/**
	 * 获取一个单的所有审核记录
	 * 
	 * @param purchaseNo
	 * @return
	 */
	public List<PurchaseOrderAuditLog> selectAllLogsByAuditType(
			@Param("purchaseOrderNoList") List<String> purchaseOrderNoList,
			@Param("auditType") Integer auditType);

	/**
	 * 获取一个单的最新那条审核记录
	 * 
	 * @param purchaseNo
	 * @return
	 */
	public PurchaseOrderAuditLog selectAOrderLastestLog(
			@Param("purchaseNo") String purchaseNo);

	public List<OrderOperateRecord> getOperateRecord(@Param("purchaseNo")String purchaseNo);
}
