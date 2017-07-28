package mapper.purchase;

import entity.purchase.PurchaseActive;
import entity.purchase.PurchaseOrder;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * @author luwj
 *
 */
public interface PurchaseOrderMapper extends BaseMapper<PurchaseOrder> {
	
	/**
	 * 根据审批号来查询
	 * @param oaAuditNo
	 * @return
	 */
	public Integer selectCountByOAAuditNo(@Param("oaAuditNo")String oaAuditNo);
	
    /**
     * 多条件查询分页列表记录
     * @param map
     * @return
     */
    public List<PurchaseOrder> getPurchaseList(Map<String, Object> map);
    
    public List<PurchaseOrder> selectOrdersByIdList(List<Integer> idList);

    /**
     * 多条件查询总记录数
     * @param map
     * @return
     */
    public int getPurchaseCount(Map<String, Object> map);
    
    /**
     *通过id\采购单号查询
     * @param purchaseOrder
     * @return
     */
    public PurchaseOrder selectOrder(PurchaseOrder purchaseOrder);

    /**
     * 通过邮箱查询列表
     * @param purchaseOrder
     * @return
     */
    public List<PurchaseOrder> selectOrders(PurchaseOrder purchaseOrder);

    /**
     * 统计30天内的采购订单
     * @param map
     * @return
     */
    public Integer getAmountByDate(Map<String, String> map);

	public Integer insertActivities(PurchaseActive pa);

	public List<PurchaseActive> getActive(String orderno);

	public Integer updateSelect(Integer id);
	
	
	/**
	 * 获取失效的采购单
	 * @author zbc
	 * @since 2016年8月23日 下午3:48:21
	 */
	public List<PurchaseOrder> getInvalidOrders();
	
	
	/**
	 * 批量更新微仓订单状态
	 * @author zbc
	 * @since 2016年8月24日 上午11:09:57
	 */
	public int batchUpdate(List<PurchaseOrder> list);
	
	public int updateSelective(PurchaseOrder po);
	
	/**
	 * 根据单号查询
	 * @author zbc
	 * @since 2016年12月14日 下午4:29:09
	 */
	public PurchaseOrder getOrderByNo(String orderNo);
	
	/**
	 * 根据email更改订单昵称
	 * @author lzl
	 * @since 2016年12月22日下午4:41:32
	 */
	public int updateNickNameByEmail(PurchaseOrder po);
}