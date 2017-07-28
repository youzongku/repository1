package services.sales;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import entity.sales.OrderPack;
import entity.sales.SaleBase;


public interface IYZService {

	/**
	 * 拉取订单
	 * @param email 
	 * @return
	 */
	public Map<String,Object> pullOrder(JsonNode main, String email);

	/**
	 * 同步物流信息到有赞平台
	 * @author zbc
	 * @since 2017年5月10日 下午6:22:29
	 * @param secret
	 * @param clientid
	 * @param orderNo
	 * @param base
	 * @param pack
	 */
	public void pushLogistic(String secret, String clientid, String orderNo, SaleBase base, OrderPack pack);
	
	
}
