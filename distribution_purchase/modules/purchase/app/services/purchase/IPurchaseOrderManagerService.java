package services.purchase;

import java.util.Map;

public interface IPurchaseOrderManagerService {
	/**
	 * 采购单商品退货情况
	 * @param returnOrderNo
	 * @return {suc:true, purchaseOrder:{}, poInfoList:[{}]}
	 */
	Map<String, Object> getReturnInfo4PurchaseOrder(String returnOrderNo);
}
