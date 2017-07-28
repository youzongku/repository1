package mapper.purchase;

import entity.purchase.PurchaseOrderDetail;

import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * @author luwj
 *
 */
public interface PurchaseOrderDetailMapper extends BaseMapper<PurchaseOrderDetail> {

    public List<PurchaseOrderDetail> getDetails(PurchaseOrderDetail detail);
    
    public List<PurchaseOrderDetail> getDetailsByPurchaseIds(@Param("purchaseIdList") List<Integer> purchaseIdList);
    
    public List<PurchaseOrderDetail> getAlldetailsByPurNo(String purchaseNo);
    
    public int batchSaveDetails(List<PurchaseOrderDetail> details);

	public List<PurchaseOrderDetail> getGift(Integer id);
	
	public int batchUpdateDetails(@Param("orderId")Integer orderId, @Param("skus")List<PurchaseOrderDetail> skus);

	/**
	 * 批量更新均摊价格
	 * @author zbc
	 * @since 2016年11月14日 下午7:13:01
	 */
	public int  batchUpdateCapFee(@Param("list")List<PurchaseOrderDetail> list);
}