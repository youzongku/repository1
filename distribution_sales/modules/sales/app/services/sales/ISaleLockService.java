package services.sales;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import dto.JsonResult;
import entity.sales.SaleDetail;
import entity.sales.SaleMain;
/**
 * 销售发货锁库 service
 * @author zbc
 * 2016年12月27日 上午10:47:47
 */
public interface ISaleLockService {

	/**
	 * TODO 锁库逻辑： 生成订单
	 * @author zbc
	 * @since 2016年12月27日 上午10:52:03
	 */
	public Map<String, Object> order(JsonNode main);
	
	
	/**
	 * @param main 发货单信息
	 * @param list 微仓出库
	 * @param lockCloud false：普通的发货；true：只发云仓
	 * @param marketNo  营销单，用于指定出库数据
	 * TODO 锁库逻辑：发货锁定微仓：
	 * @author zbc
	 * @since 2016年12月27日 上午11:52:11
	 */
	public JsonNode saleOut(SaleMain main,List<SaleDetail> list,String marketNo,Boolean lockCloud);


	/**
	 * TODO 锁库逻辑： 保存历史数据
	 * @author zbc
	 * @since 2016年12月27日 下午5:43:14
	 */
	public Map<String, Object> saveMicroOutHistory(Integer id, String str);


	/**
	 * TODO 锁库逻辑：关闭订单
	 * @author zbc
	 * @since 2016年12月28日 上午10:05:59
	 */
	public Map<String, Object> cancelOrder(JsonNode main,String account);


	/**
	 * 订单导入生成订单
	 * @author zbc
	 * @since 2017年1月4日 上午10:13:16
	 */
	public Map<String,Object> saveSaleOrder(JsonNode main);


	/**
	 * 保存历史数据
	 * @author zbc
	 * @since 2017年1月5日 下午3:14:39
	 */
	public void saveHistory(SaleMain sm, JsonNode json);


	/**
	 * openapi 下单
	 * @author zbc
	 * @param store 
	 * @since 2017年1月5日 下午4:20:50
	 */
	public  Map<String, Object> order4OpenApi(JsonNode main, boolean store);


	public Map<String, Object> remicsto(String node);


	/**
	 * erp关闭发货单
	 * @author zbc
	 * @since 2017年1月6日 上午10:25:51
	 */
	public String closeSalesFromB2C(String param);

	public  Map<String, Object> orderCreatedFromPurchaseOrder(JsonNode main);


	/**
	 * 云仓发货下单
	 * @author zbc
	 * @since 2017年1月10日 下午2:50:06
	 */
	public Map<String, Object> cOrder(JsonNode main);


	/**
	 * 
	 * 发货支付完成逻辑
	 * @author zbc
	 * @since 2017年6月21日 下午4:37:18
	 * @param string
	 * @return
	 */
	public JsonResult<?> finishSaleOrder(String string);

	/**
	 * 异常处理 删除异常发货单
	 * @author zbc
	 * @since 2017年6月27日 下午12:14:18
	 * @param sm
	 */
	public void deleteSaleOrder(SaleMain sm);


	/**
	 * 保存操作日志
	 * @author zbc
	 * @since 2017年6月29日 上午9:37:16
	 * @param id
	 * @param operateType
	 * @param result
	 * @param comment
	 * @param operator
	 */
	boolean saveRecord(Integer id, Integer operateType, Integer result, String comment, String operator);

}
