package services.sales;

import com.fasterxml.jackson.databind.JsonNode;

import dto.JsonResult;
import entity.sales.SaleInvoice;
import entity.sales.SaleMain;
import entity.sales.hb.SalesHBDelivery;

/**
 * @author zbc
 * 2017年6月22日 下午3:17:01
 */
public interface ISaleInvoiceService {
	
	/**
	 * 校验发票信息是否有效
	 * @author zbc
	 * @since 2017年6月22日 下午3:15:24
	 * @param json
	 * @param createUser
	 * @return
	 */
	public JsonResult<SaleInvoice> checkVaildInvoice(JsonNode json);


	/**
	 * 保存发货单发票信息
	 * @author zbc
	 * @since 2017年6月22日 下午4:24:13
	 * @param sm
	 * @param createUser
	 * @param data
	 */
	public void save(SaleMain sm, String createUser, SaleInvoice data);


	/**
	 * 保存合并发货单发票信息
	 * @author zbc
	 * @since 2017年6月22日 下午4:24:30
	 * @param delivery
	 * @param createUser
	 * @param data
	 */
	public void save(SalesHBDelivery delivery, String createUser, SaleInvoice data);


	public JsonResult<?> getInvoice(String so);

}
