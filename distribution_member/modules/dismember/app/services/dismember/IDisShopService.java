package services.dismember;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import dto.dismember.ShopDto;
import entity.dismember.DisShop;
import entity.dismember.DisShopDpLog;
import entity.dismember.ShopCategory;
import entity.dismember.ShopPlatform;


/**
 * Created by LSL on 2015/12/16.
 */
public interface IDisShopService {
	
	List<DisShopDpLog> getDisShopDpLogs(int shopId, String email);
	
	/**
	 * 设置店铺扣点
	 * @param email
	 * @param shopId2Dp 店铺id对应扣点
	 * @param createUser
	 * @return
	 */
	Map<String,Object> setShopDeductionPoints(String email, Map<Integer, Double> shopId2Dp, final String createUser);

    /**
     * 根据条件获取店铺信息记录数
     * @param params
     * @return
     */
    int getCountByCondition(Map<String, Object> params);

    /**
     * 根据条件分页获取用户店铺信息
     * @param params
     * @return
     */
    List<ShopDto> getShopsByCondition(Map<String, Object> params);

    /**
     * 添加新店铺信息
     * @param params
     * @param distributionType 
     * @return
     */
    DisShop addNewShop(Map<String, String> params, Integer distributionType);

    /**
     * 更新指定店铺信息
     * @param params
     * @param distributionType 
     * @return
     */
    boolean updateShop(Map<String, String> params, Integer distributionType);

    /**
     * 删除指定店铺信息
     * @param params
     * @return
     */
    boolean deleteShop(Map<String, String> params);
    
    /**
     * 获取指定店铺信息
     * @param params
     * @return
     */
    DisShop getShop(Map<String, String> params);

    /**
     * 获取所有店铺平台
     * (淘宝、天猫、京东、亚马逊......)
     * @return
     */
    List<ShopPlatform> getAllShopPlatforms();

    /**
     * 获取所有店铺类型
     * (B2B/B2C/C2C/O2O......)
     * @return
     */
    List<ShopCategory> getAllShopCategorys();

    Map<String, Object> shop(String name, String email);

	Map<String, Object> checkShopName(Map<String, Object> param);
	
	void pushOfflineShop(DisShop shop, Integer type);

	Map<String, Object> query(JsonNode node);

	boolean getAccessToken(String code, String state);

	boolean isRepeatClientid(Map<String, String> params);

	List<DisShop> getAllShop(String email);

}
