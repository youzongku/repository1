package services.marketing.promotion;

import java.util.List;

import util.marketing.promotion.PageInfo;
import dto.marketing.promotion.FullProTypeDto;
import entity.marketing.promotion.PromotionCondition;
import entity.marketing.promotion.PromotionPrivilege;
import entity.marketing.promotion.PromotionType;
import forms.marketing.promotion.PromotionTypeForm;
import forms.marketing.promotion.PromotionTypeSearchForm;

/**
 * 促销活动service接口
 * 
 * @author huangjc
 * @date 2016年7月25日
 */
public interface IPromotionTypeService {
	
	public void updateUsed(List<Integer> proTypeIdList);
	
	public boolean canUseThisName(PromotionTypeForm proTypeForm);
	
	public boolean insertPromotionTypeDto(PromotionTypeForm proTypeForm);
	
	/**
	 * 获取一个促销类型
	 * 
	 * @param id
	 * @return
	 */
	public FullProTypeDto getFullProTypeDto(Integer id);

	/**
	 * 促销类型分页查询
	 * 
	 * @param form
	 *            封装了查询参数
	 * @return
	 */
	public PageInfo<PromotionType> getPromotionTypePage(
			PromotionTypeSearchForm form);

	/**
	 * 更新一个促销类型
	 * 
	 * @param proTypeForm
	 * @return
	 */
	public boolean updatePromotionTypeDto(PromotionTypeForm proTypeForm);

	/**
	 * 删除一个促销类型
	 * 
	 * @param id
	 * @return
	 */
	public boolean deleteProTypeById(Integer id);

	/**
	 * 获取指定条件属性的条件
	 * @param attr 添加属性
	 * @return
	 */
	public List<PromotionCondition> getProCondtListByAttr(short attr);

	/**
	 * 根据属性获取所有有效的优惠
	 * 
	 * @return
	 */
	public List<PromotionPrivilege> getProPvlgListByAttr(short attr);
	
	/**
	 * 拷贝促销类型
	 * @param typeDtoExists
	 * @param newProTypeName
	 */
	public void copy(FullProTypeDto typeDtoExists, String newProTypeName);

}
