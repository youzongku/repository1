package mapper.sales;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import dto.sales.CommonExportDto;
import dto.sales.ExportSaleLogistic;
import dto.sales.ProductSalesVolume;
import dto.sales.ProductSalesVolumeSearchDto;
import dto.sales.SaleOrderListDto;
import dto.sales.SalesOrderInfo4FinanceDto;
import dto.sales.SalesToB2cIterm;
import entity.sales.SaleMain;

public interface SaleMainMapper{
	
	SaleMain selectByPrimaryKey(Integer id);

    int insert(SaleMain record);

    int insertSelective(SaleMain record);

    int updateByPrimaryKey(SaleMain record);
	
	int deleteByPrimaryKey(Integer id);

	public int updateTaxFee(SaleMain sm);
	
	public SaleMain selectSaleMain(@Param("id")Integer id,@Param("salesOrderNo")String salesOrderNo);
	
	public List<SaleMain> selectAllSaleMain(Map<String, Object> paramMap);
	public int selectAllSaleMainCount(Map<String,Object> paramMap);
	
	public List<SalesOrderInfo4FinanceDto> selectAllSaleMain4finance(Map<String, Object> paramMap);
	public int selectAllSaleMainCount4finance(Map<String,Object> paramMap);
	
	public int batchUpdateVerify(@Param("idList")List<Integer> idList);
	
	public SaleMain selectByOrderNo(@Param("orderNo") String orderNo);
	public List<SaleMain> selectByOrderNoList(@Param("orderNoList") List<String> orderNoList);
	
	int selectSalesOrderCount(@Param("from") String from,
							  @Param("to")String to,
							  @Param("email")String email,
							  @Param("status")Integer status,
							  @Param("accounts")List<String> accounts);
	
	/**
	 * 销售列表
	 * @param status
	 * @param desc
	 * @param email
	 * @param pageSize
	 * @param currPage
	 * @return
	 */
	public List<SaleOrderListDto> selectSaleOrderListDto(@Param("statusList")List<Integer> statusList,
														 @Param("desc")String desc,
														 @Param("email")String email,
														 @Param("pageSize")int pageSize,
														 @Param("currPage")int currPage);
	
	/**
	 * 获取查询条件对应的总记录数
	 * @param status
	 * @param desc
	 * @param email
	 * @return
	 */
	public int selectSaleOrderListDtoCount(@Param("statusList")List<Integer> statusList,
										   @Param("desc")String desc,
										   @Param("email")String email);

	/**
	 * 查询销售订单信息
	 * @param map
	 */
	public List<SalesToB2cIterm> getSalesInfo(Map<String,Object> map);

	/**
	 * 更新
	 * @param saleMain
	 * @return
	 */
	public int updateByPrimaryKeySelective(SaleMain saleMain);

	/**
	 * 批量更新销售单主表状态(以订单号为条件)
	 * @param saleMains
	 * @return
	 */
	public int batchUpdateStatus(List<SaleMain> saleMains);

	/**
	 * 通过Id查询销售订单信息
	 * @param id
	 * @return
	 */
	public Map<String,String> getSalesById(@Param("id") Integer id);
	
	/**
	 * 获取已发货订单
	 * @return
	 */
	public List<SaleMain> getShippedSales();  
	
	/**
	 * 根据email查询出此分销商下所有已发货状态下的订单的物流信息
	 * @param params
	 * @return
	 */
	public List<ExportSaleLogistic> selectSaleOrederLogistics(Map<String, Object> params);

	public int countTradeNo(String tradeNo);
	
	public SaleMain selectByIdAndAccounts(Map<String, Object>  params);

	/**
	 * 根据email更改订单昵称
	 * @author lzl
	 * @since 2016年12月22日下午5:46:16
	 */
	public int updateNickNameByEmail(SaleMain sale);

	/**
	 * 设置发货单操作费
	 * @param salesOrderNo
	 * @param optFee
	 * @return
	 */
	public int updateOptFeeByOrderNo(@Param("salesOrderNo")String salesOrderNo, @Param("optFee")Double optFee);
	
	/**
	 * 根据支付时间查询订单
	 * @param paystr
	 * @return
	 */
	public List<SaleMain> getAutoConfirmOrders(@Param("paystr")String paystr);

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月29日 上午11:38:56
	 */
	public dto.sales.SalesPriceDto getCharges(Integer sid);

	/**
	 * 查询导出功能函数信息
	 * 跨库查询导出函数的相关信息
	 * @param functionId
	 * @return
	 */
	public CommonExportDto getExprotDtoByFunctionId(@Param("functionId")String functionId);

	/**
	 * 获取导出数据信息
	 * @param sql
	 * @return
	 */
	public List<Map> getExportDataBySqlFunction(@Param("param")String sql);


	/**
	 * 定时任务统计每天商品的销量
	 * @param begin
	 * @param end
	 * @return
	 */
	public Integer executSalesVolumeCountFunction(@Param("begin")String begin, @Param("end")String end);

	public List<ProductSalesVolume> getProductInfo(ProductSalesVolumeSearchDto volumeSearchDto);

	public Integer productInfoTotal(ProductSalesVolumeSearchDto volumeSearchDto);

	List<dto.sales.SalesPriceDto> getListCharges(@Param("idList")List<Integer> idList);
}