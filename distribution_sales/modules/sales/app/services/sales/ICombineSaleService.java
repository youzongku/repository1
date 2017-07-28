package services.sales;

import java.util.List;
import java.util.Map;

import dto.JsonResult;
import dto.sales.hb.CombinedSalesPageQueryParam;
import dto.sales.hb.HBDeliveryAuditParam;
import dto.sales.hb.SalesHBDeliveryDto;
import entity.sales.SaleDetail;
import entity.sales.hb.SalesHBDelivery;
import entity.sales.hb.SalesHBDeliveryLog;
import util.sales.Page;

/**
 * @author zbc
 * 2017年5月19日 下午5:48:04
 */
public interface ICombineSaleService {
	
	/**
	 * 获取合并发货单下的所有商品详情
	 * @param hbNo 合并单单号
	 * @return
	 */
	JsonResult<List<SaleDetail>> getConbinedProDetails(String hbNo);
	
	/**
	 * 合并发货单
	 * @param string
	 * @param adminAccount
	 * @return
	 */
	JsonResult<?> combineSaleOrder(String string, String adminAccount);

	/**
	 * 要合并的发货单的信息
	 * @param string
	 * @param adminAccount
	 * @return
	 */
	JsonResult<?> getToCombineInfo(String string, String adminAccount,String relateAccounts);

	/**
	 * 合并发货列表页
	 * @param query
	 * @return
	 */
	Page<SalesHBDelivery> getSalesHBDeliveryPage(CombinedSalesPageQueryParam query);
	
	/**
	 * 客服审核
	 * @param string
	 * @param adminAccount
	 * @return
	 */
	JsonResult<?> auditByCustomerService(HBDeliveryAuditParam auditParam);
	
	/**
	 * 财务审核
	 * @param string
	 * @param adminAccount
	 * @return
	 */
	JsonResult<?> auditByFinance(HBDeliveryAuditParam auditParam);
	
	/**
	 * 获取一个合并的单
	 * @param hbId 合并单单号
	 * @return
	 */
	JsonResult<SalesHBDeliveryDto> getACombination(int hbId);

	/**
	 * 获取操作日志
	 * @param hbId
	 * @return
	 */
	JsonResult<List<SalesHBDeliveryLog>> getCombinedLogs(Integer hbId);

	JsonResult<?> calculation(String od);

	JsonResult<?> batchOrder(String admin, String jsonStr);

	JsonResult<Map<String, Object>> info2auditByFinance(int hbId);

}
