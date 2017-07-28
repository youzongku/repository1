package services.dismember;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import dto.dismember.PageResultDto;
import entity.dismember.DisActive;
import entity.dismember.DisCoupons;



public interface IActiveService {
	
	/**
	 * 新增优惠活动
	 * @param active
	 * @return
	 */
	public Map<String,Object> saveActive(DisActive active);

	/**
	 * 分页查询优惠活动
	 * @param active
	 * @return
	 */
	public PageResultDto queryPageActive(JsonNode node);

	/**
	 * 分页查询优惠码
	 * @param active
	 * @return
	 */
	public PageResultDto queryPageCoupons(JsonNode node);
	
	/**
	 * 更新优惠码状态
	 * @param coupons
	 * @return
	 */
	public Map<String, Object> updateCoupons(DisCoupons coupons);

	List<DisCoupons> queryCoupons(Map<String, Object> param);
	/**
	 * 获取优惠码信息并验证
	 * @param coupons
	 * @return
	 */
	Map<String, Object> getCouponsInfo(String couponsNo, Double orderAmount);

	/**
	 * 执行每天12点更新活动状态
	 */
	public void execute();

	public DisActive getActive(Integer activeId);
	
	
}
