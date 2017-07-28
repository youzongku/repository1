package services.sales;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;


/**
 * 后台下单service
 * 
 * @author huangjc
 * @since 2017年3月7日
 */
public interface IManagerOrderService {
	/**
	 * 后台录入发货单
	 * 
	 * @param main
	 * @return
	 */
	public Map<String, Object> order(JsonNode main);

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月29日 上午11:13:08
	 */
	public String match(Integer sid);

	/**
	 * 
	 * @author zbc
	 * @since 2017年4月13日 下午2:56:32
	 */
	public String caculateProfit(String orderNo);

	/**
	 * 销量导出
	 * @param reqStr
	 * @return
	 */
	public String commonExport(String reqStr);

	/**
	 * 销量导出前的一个查看商品信息
	 * @param reqParam
	 * @return
	 */
	public String productSalesVolumeSearch(String reqParam);
}
