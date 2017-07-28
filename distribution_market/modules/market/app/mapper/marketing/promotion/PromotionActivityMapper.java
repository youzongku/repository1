package mapper.marketing.promotion;



import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import dto.marketing.promotion.OrderPageReturnsDto;
import entity.marketing.promotion.PromotionActivity;
import forms.marketing.promotion.ActivityInstanceSearchForm;

/**
 * 促销活动mapper
 * 
 * @author huangjc
 * @date 2016年7月25日
 */
public interface PromotionActivityMapper {
	int deleteSoftlyByPrimaryKey(Integer id);

	int insertPromotion(PromotionActivity record);

	PromotionActivity selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(PromotionActivity record);

	public List<PromotionActivity> getAllActivity(
			ActivityInstanceSearchForm form);

	public Integer updateEndTime(@Param("endTime")Date date,@Param("id")Integer id,@Param("lastCreateUser")String lastCreateUser);

	public Integer getRowSize(ActivityInstanceSearchForm pageInfo);
	
	public List<PromotionActivity> getProActivitiesByName(String name);
	
	public List<PromotionActivity> getExecutoryActivity();
	
	public int updateProActNotDelete(@Param("id")int id,@Param("name")String name);
	
	public OrderPageReturnsDto getActivityByPvlg(Integer id);
	
	public int statusStartTimerTrigger();
	
	public int statusEndTimerTrigger();
	
	public List<PromotionActivity> getPaymentTimeParticipateActivaty(Date paymentTime);
	/**
	 * 暂停活动
	 * @param id
	 * @return
	 */
	public int pauseByPrimaryKey(Integer id);
	/**
	 * 启动暂停的活动
	 * @param id
	 * @param status
	 * @return
	 */
	public int activateProActPaused(@Param("id")Integer id,@Param("status")Integer status);
	
	public int updateStatusByPrimaryKey(@Param("id")Integer id,@Param("status")Integer status);

}