package service.discart;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import dto.discart.JsonResult;
import entity.discart.DisCart;

/**
 * Created by LSL on 2015/12/7.
 */
public interface IDisCartService {

    /**
     * 获取当前用户可用购物车
     * (存在则直接返回，不存在则新建再返回)
     * @param email
     * @return
     */
    DisCart getUsableDisCart(String email);

    /**
     * 新建当前用户可用购物车
     * @param email
     * @return
     */
    DisCart createUsableDisCart(String email);

    /**
     * 参加活动
     * @param node
     * @param email
     * @return
     */
	Map<String, Object> saveActiveInfo(JsonNode node,String email);

	/**
	 * 获取活动信息
	 * @param id
	 * @return
	 */
	Map<String, Object> getActive(Integer id);

	/**
	 * 物理删除活动信息
	 * @param email
	 * @return
	 */
	int deleteActiveInfo(String email);

	/**
	 * 添加商品至购物车<br>
	 * proArrayNode内容如下：<br>
	 {
	 	proArray:[
	 		{sku:xxx, warehouseId:xxx, pQty:1}
	 	]
	 }
	 */
	Map<String, Object> pushCart(String email, Integer distributionMode, JsonNode proArrayNode);

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月23日 上午10:09:53
	 */
	Map<String, Object> order(String data, String dismember);

	/**
	 * @author zbc
	 * @since 2017年5月26日 下午2:59:49
	 * @param string 
	 * @param dismember
	 * @return
	 */
	JsonResult<?> delivery(String string, String dismember);

	/**
	 * @author zbc
	 * @since 2017年5月26日 下午2:59:52
	 * @param account
	 * @return
	 */
	JsonResult<?> getDeliveryInfo(String dismember);

	/**
	 * @author zbc
	 * @since 2017年5月26日 下午2:59:54
	 * @param string
	 * @param account
	 * @return
	 */
	JsonResult<?>  getDeliveryFee(String string);

	/**
	 * @author zbc
	 * @since 2017年6月3日 下午4:30:22
	 * @param dismember
	 * @return
	 */
	JsonResult<?> delSelected(String dismember);

}
