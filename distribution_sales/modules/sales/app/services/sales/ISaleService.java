package services.sales;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import dto.sales.ExportSaleLogistic;
import dto.sales.SaleOrderListDto;
import dto.sales.SalesPriceDto;
import dto.sales.audit.AuditByCustomerServiceParam;
import entity.sales.SaleBase;
import entity.sales.SaleDetail;
import entity.sales.SaleMain;
import pager.sales.Pager;

public interface ISaleService {
	
	/**
	 * 查询一定时间范围内的订单数量,时间参数二选一
	 * {
	 *     "days":"xx",
	 *     "months":"xx"
	 * }
	 * @return
	 */
	int  getSalesOrderCount(String param, List<String> accounts); ;
	
	/**
	 * 查询销售订单列表
	 * @param email 
	 * @param status
	 * @param desc
	 * @return
	 */
	public Pager<SaleOrderListDto> selectSaleOrderListDto(JsonNode main, String email);
	
	/**
	 * 查询销售订单详情列表
	 * @param main
	 * @return
	 */
	public List<SaleDetail> selectSaleOrderDetail(JsonNode main);

	/**
	 * 描述：获取后台销售订单
	 * 2016年5月5日
	 * @param main 传入的json数据
	 * @return
	 */
	public Map<String,Object> selectSaleOrderListOfBackstage(JsonNode main,String relateAccount); 
	public Map<String,Object> selectSaleOrderListOfBackstage4finance(JsonNode json, String relateAccounts);
	
	/**
	 * 描述：获取后台导出订单数据
	 * 2016年5月5日
	 * @return
	 */
	public List<String> getExportOrderListOfBackstage(Map<String, String[]> paramMap);

	JsonNode checkCoupons(String couponsCode, Double amount);
	
	/**
	 * 获取最便宜的物流
	 * @param skuObj
	 * @param proId
	 * @param warehouseId
	 * @return
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 */
	public ArrayList<Entry<String, Double>> postShoppingMethod(JsonNode skuObj, Integer proId, Integer cityId, Integer warehouseId,Map<String, String> freightName,JsonNode methodNode) throws JsonProcessingException, IOException;

	/**
	 * 系统自动更新已货订单 状态
	 */
	public void autoConfirmReceipt();
	
	 /**
     * 查询从B2c推送过来的物流信息
     * @param params
     * @return
     */
	List<ExportSaleLogistic> selectSaleOrederLogistics(Map<String, Object> params);

	boolean checkTradeNo(String tradeNo);
	
	
	/**
	 * 开放接口获取销售单信息
	 * @author zbc
	 * @since 2016年9月6日 上午10:42:04
	 */
	public Map<String,Object> openQuery(JsonNode main);

	Map<String, Object> selectSaleOrdersOfBackstageForTel(String param, List<String> accounts);

	/**
	 * 保存缓存信息
	 * @param params
	 * @return
	 */
	String saveBufferMemory(String params);

	/**
	 * 根据当前登录的账号查询缓存信息
	 * @return
	 */
	String getBufferMemory();

	/**
	 * 清空当前登录账号的缓存
	 * @return
	 */
	String clearBufferMemory();

	/**
	 * 获取销售单商品采购信息
	 * @param orderId
	 * @return
	 */
	Map<String,Object> getPurchaseInfo(int orderId);

	/**
	 * 同步更新 发货单 优惠码码信息
	 * @author zbc
	 * @since 2016年11月24日 上午10:33:58
	 */
	String syncCoupons(Integer mainId);

	/**
	 * 修改订单价格
	 * @author zbc
	 * @since 2016年12月2日 下午5:06:31
	 */
	Map<String, Object> changeOrderPrice(String node,String admin);

	/**
	 * 客服审核
	 * @author zbc
	 * @since 2016年12月21日 下午3:36:25
	 */
	Map<String, Object> auditByCustomerService(AuditByCustomerServiceParam param,boolean isauto);

	/**
	 * 财务审核
	 * @author zbc
	 * @since 2016年12月21日 下午3:37:20
	 */
	Map<String, Object> auditByFinance(String string, String ip);
	
	
	/**
	 * 构造订单详情
	 * @author zbc
	 * @since 2016年12月27日 上午10:57:32
	 */
	List<SaleDetail> parseSaleDetail(SaleMain sm, JsonNode productStrNode, Map<String, Integer> sukNumMap,
			JsonNode memberNode, Map<String, Double> finalPriceMap);

	/**
	 * 构造订单信息
	 * @author zbc
	 * @since 2016年12月27日 上午10:57:45
	 */
	SaleMain parseSaleMain(JsonNode main);

	/**
	 * 构造订单信息
	 * @author zbc
	 * @since 2016年12月27日 上午10:59:00
	 */
	SaleBase parseSaleBase(SaleMain sm, JsonNode main, JsonNode shopStrNode, JsonNode custStrNode, JsonNode skuObj)
			throws JsonProcessingException, IOException;

	BigDecimal getAmount(String pNo, SaleBase base) throws JsonProcessingException, IOException;

	SaleBase parseSaleBase4OpenApi(SaleMain sm, JsonNode main, JsonNode shopStrNode, JsonNode custStrNode,
			JsonNode skuObj) throws JsonProcessingException, IOException;

	/**
	 * 计算操作费
	 * @param sm
	 * @param sb
	 * @param details
	 */
	void calculateOptFee(SaleMain sm, SaleBase sb, List<SaleDetail> details);
	
	/**
	 * 获取财务审核数据
	 * @author zbc
	 * @since 2017年1月19日 下午2:36:45
	 */
	public SalesPriceDto getAmount(Integer sid);
	

	/**
	 * 订单自动客服确认，支付后1小时
	 */
	public void autoCsConfirm();

	File exportSaleOrder(String fileName, String[] header, Map<String, String> headerMapping, List<String> exportKeys);

	List<SalesPriceDto> listAmounts(List<Integer> idList);

	/**
	 * 判断订单是否亏本
	 * 不盈利返回true，盈利订单返回false
	 * @author zbc
	 * @since 2017年6月21日 下午5:18:53
	 * @param id
	 * @return
	 */
	boolean noProfit(Integer id);

	
}
