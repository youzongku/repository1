package mapper.sales;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import dto.sales.SalesToB2cDetail;
import entity.sales.SaleDetail;

public interface SaleDetailMapper extends BaseMapper<SaleDetail> {
	
	public int deleteByOrderId(@Param("orderId")Integer orderId);

	public int batchInsert(List<SaleDetail> saleDetails);
	
	public int batchInsertSelective(List<SaleDetail> saleDetails);
	
	public int batchUpdate(List<SaleDetail> saleDetails);
	
	public List<SaleDetail> selectHistoryByOrderId(Integer orderId);
	
	public List<SaleDetail> getHistoryByOrderId(Integer orderId);

	public List<SaleDetail> selectEffectiveDetails(@Param("list")List<Integer> mainIds);

	/**
	 * 根据订单id查询详情
	 * @param orderId
	 * @return
	 */
	public List<SaleDetail> selectByOrderId(Integer orderId);
	public List<SaleDetail> selectByOrderNo(@Param("orderNo")String orderNo);
	/**
	 * 根据订单id集合批量查询详情
	 * @param orderIdList
	 * @return
	 */
	public List<SaleDetail> selectByOrderIdList(List<Integer> orderIdList);
	public List<SaleDetail> selectByOrderNoList(List<String> orderNoList);
	
	
	/**
	 * 根据销售发货单主表id删除发货单详情
	 * @param id
	 * @return
	 */
	public int deleteByMainOrderId(Integer id);
	
	/**
	 * 查询订单详情
	 * @param id
	 * @return
	 */
	public List<SalesToB2cDetail> getDetails(Integer id);
	
	/**
	 * 将客户订单详情表是否处于售后的标识修改为1（售后进行中）
	 * @return
	 */
	public int updateIsAfterSaleTo1(String saleNo,String sku);

	public List<SaleDetail> selectByOrderIdAndWarehouseIdAndSku(@Param("orderNo")String orderNo, @Param("warehouseId")Integer warehouseId, @Param("list")List<String> skus);

	public Map<String, Object> getAmount(Integer salesorderid);

	public List<SaleDetail> selectDetails(@Param("list")List<Integer> mainIds);

	public SaleDetail getDetailBySkuAndOrderNo(SaleDetail saleDetail);
	
	/**
	 * 
	 * @param sid
	 * @return 返回值结构：[{"contractNo":"HT2017032410345200000023", "sum":69.6}, ...]
	 */
	List<Map<String,Object>> getContractPrice(@Param("sid")Integer sid);
}