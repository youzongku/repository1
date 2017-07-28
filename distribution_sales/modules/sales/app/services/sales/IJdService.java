package services.sales;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.jd.open.api.sdk.domain.order.OrderSearchInfo;
import com.jd.open.api.sdk.domain.ware.Sku;
import com.jd.open.api.sdk.domain.ware.Ware;
import events.sales.JdLogisticsEvent;


public interface IJdService {

	/**
	 * 拉取订单
	 * @param email 
	 * @return
	 */
	public Map<String,Object> pullOrder(JsonNode main, String email);

	public List<Ware> pullProduct(JsonNode main);

	Map<String, Object> getOrderInfo(JsonNode main,JsonNode shopNode,String optionalFields);

	List<Sku> getSkus(JsonNode main, List<OrderSearchInfo> orderInfos);

	/**
	 * 取消同步状态
	 * @param shopId
	 */
	void cancelSync(Integer shopId);

	/**
	 * 判断是否店铺点单是否同步拉取中
	 * @param res
	 * @param shopId
	 * @param email
	 * @return
	 */
	boolean syncOrder(Map<String, Object> res, Integer shopId, String email);

	/**
	 * 获取店铺信息
	 * @param shopId
	 * @return
	 */
	public JsonNode checkShop(Integer shopId);

	/**
	 * 校验必填字段
	 * @param shopNode
	 * @param field
	 * @return
	 */
	public boolean isRightful(JsonNode shopNode, String ...field);


	/**
	 * 同步物流信息
	 *
	 * @param jdLogisticsEvent
	 */
	void pushLogistic(JdLogisticsEvent jdLogisticsEvent);
}
