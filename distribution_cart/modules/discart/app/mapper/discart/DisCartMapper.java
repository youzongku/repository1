package mapper.discart;

import entity.discart.DisCart;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface DisCartMapper extends BaseMapper<DisCart> {

    /**
     * 获取当前用户可用购物车
     * @param email
     * @return
     */
    DisCart getUsableDisCart(@Param("email")String email);

    /**
     * 参加活动
     * @param map
     * @return
     */
	int insertActiveInfo(@Param("info")Map<String, Object> map);

	/**
	 * 获取活动信息
	 * @param id
	 * @return
	 */
	Map<String, Object> getActiveInfo(Integer id);

	/**
	 * 更新活动信息
	 * @param map
	 * @return
	 */
	int updateActiveInfo(@Param("info")Map<String, Object> map);

	/**
	 * 删除活动信息（物理删除）
	 * @param email
	 * @return
	 */
	int deleteActiveInfo(String email);

}