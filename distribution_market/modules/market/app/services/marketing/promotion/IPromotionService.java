package services.marketing.promotion;

import java.util.Date;
import java.util.List;
import java.util.Map;

import dto.marketing.promotion.ActInstWithProTypeDto;
import dto.marketing.promotion.FullActInstDto;
import dto.marketing.promotion.FullCondtInstDto;
import dto.marketing.promotion.FullProActDto;
import dto.marketing.promotion.OrderPageReturnsDto;
import dto.marketing.promotion.OrderPromotionActivityDto;
import dto.marketing.promotion.ProActWithActInstsAndProTypesDto;
import dto.marketing.promotion.condt.value.FinalCondtValue;
import dto.marketing.promotion.pvlg.value.FinalPvlgValue;
import entity.marketing.promotion.ActivityInformationLog;
import entity.marketing.promotion.ActivityInstance;
import entity.marketing.promotion.ConditionInstanceExt;
import entity.marketing.promotion.PromotionActivity;
import forms.marketing.promotion.ActivityInstanceSearchForm;
import util.marketing.promotion.PageInfo;

/**
 * 促销活动实例接口
 * 
 * @author huangjc
 * @date 2016年7月25日
 */
public interface IPromotionService {
	
	/**
	 * 启动暂停的活动
	 * @param id
	 * @return
	 */
	public boolean activateProActPaused(int id);
	
	/**
	 * 暂停活动
	 * @param id
	 * @return
	 */
	public boolean pausePromotionActivity(int id);
	
	/**
	 * 根据id获取促销活动的基本信息
	 * 
	 * @param proActId
	 * @return
	 */
	PromotionActivity getPromotionActivityById(int proActId);

	/**
	 * 删除活动实例
	 * 
	 * @param actInstIds
	 * @return
	 */
	public boolean deleteActInstByIds(String actInstIds);

	/**
	 * 将促销活动的从删除状态改为不删除
	 * 
	 * @param proActId
	 * @param name
	 * @return
	 */
	public boolean updateProActNotDelete(int proActId, String name);

	/**
	 * 更新条件实例和优惠实例的值
	 * 
	 * @param finalCondtValueList
	 * @param finalPvlgValueList
	 */
	void updateCondtInstsAndPvlgInstsValue(Integer actInstId,
			Integer matchType, List<FinalCondtValue> finalCondtValueList,
			ConditionInstanceExt condtInstExt,
			List<FinalPvlgValue> finalPvlgValueList);

	/**
	 * 根据活动实例id获取实例的活动所有数据
	 * 
	 * @param actInstId
	 *            活动实例id
	 * @return
	 */
	FullActInstDto getFullActInstDtoByActInstId(int actInstId);
	FullCondtInstDto getFullCondtInstDtoByCondtInstId(int condtInstId);
	
	/**
	 * 添加活动实例
	 * 
	 * @param proActId
	 *            促销活动id
	 * @param proTypeId
	 *            促销类型id
	 * @return
	 */
	public boolean addActivityInstance(int proActId, int proTypeId);

	/**
	 * 添加活动基本信息
	 * 
	 * @param list
	 * @return
	 */
	public boolean addPromotionActivity(PromotionActivity activity);

	/**
	 * 修改活动基本信息
	 * 
	 * @param activity
	 * @return
	 */
	public boolean updatePromotionActivity(PromotionActivity activity);

	/**
	 * 得到活动-分页查询
	 * 
	 * @param form
	 *            条件
	 * @return
	 */
	public PageInfo<PromotionActivity> getAllActivity(
			ActivityInstanceSearchForm form);

	/**
	 * 更该活动结束时间
	 */
	public boolean updateEndTime(Date date, Integer id, String lastCreateUser);

	/**
	 * 修改活动实例
	 * 
	 * @param list
	 *            活动实例列表
	 * @return
	 */
	public boolean updateActivityInstance(List<ActivityInstance> list);

	/**
	 * 获取活动：包含活动实例和模板信息
	 * 
	 * @param proActId
	 *            活动id
	 * @return
	 */
	public ProActWithActInstsAndProTypesDto getProActWithActInstsAndProTypesDto(Integer proActId);
	
	/**
	 * 获取活动实例和模板信息，分组操作
	 * 
	 * @param proActId
	 *            活动id
	 * @return
	 */
	public Map<Integer,List<ActInstWithProTypeDto>> getActInstWithProTypeDtoListByGroup(Integer proActId);
	
	boolean canUseThisName(Integer id, String name);

	/**
	 * 得到所有促销中的活动
	 * @param mode 用户模式
	 * @return
	 */
	List<FullProActDto> getExecutoryActivity(int mode);
	/**
	 * 得到支付时间内可以参加的活动
	 */
    List<FullProActDto> getPaymentTimeParticipateActivaty(int mode,Date paymentTime);
	/**
	 * 存储活动信息日志
	 */
	int saveActivityInformationLog(ActivityInformationLog log);

	public FullProActDto getFullProActDtoByProActId(int proActId);

	/**
	 * 获取活动的活动实例中未设置参数的条件实例的个数
	 * 
	 * @param proActId
	 * @return
	 */
	int getProActUnSetVCondtInstCount(int proActId);

	/**
	 * 获取促销活动的活动实例个数
	 * 
	 * @param proActId
	 * @return
	 */
	int getActInstCountByProActId(int proActId);

	/**
	 * 更新活动实例优先级
	 * @param actInstId 活动实例id
	 * @param priority 优先级
	 * @return
	 */
	boolean updatePriorityByCondtInstId(int condtInstId, int priority);

	OrderPageReturnsDto getActivityByPvlg(List<Integer> idList);

	/**
	 * 计算优惠信息
	 * @author zbc
	 * @since 2016年10月25日 上午9:18:00
	 */
	public Map<String,Object> executePvlgInst(List<FullActInstDto> actInstList, OrderPromotionActivityDto dtoArg);

	/**
	 * 检查冲突
	 * @author zbc
	 * @since 2016年10月20日 下午3:03:43
	 */
//	List<FullActInstDto> checkConflict(List<FullActInstDto> conditList);
}
