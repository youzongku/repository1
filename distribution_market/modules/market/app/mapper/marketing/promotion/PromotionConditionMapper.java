package mapper.marketing.promotion;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.marketing.promotion.PromotionCondition;
/**
 * 促销条件mapper
 * @author huangjc
 * @date 2016年7月25日
 */
public interface PromotionConditionMapper {
	int deleteSoftlyByPrimaryKey(Integer id);
	
	/**
	 * 根据条件属性获取条件
	 * @param attr 条件属性
	 * @param isDelete
	 * @return
	 */
	List<PromotionCondition> getProCondtListByAttr(@Param("attr")short attr, @Param("isDelete")boolean isDelete);
	
	List<PromotionCondition> selectByIdList(@Param("idList")List<Integer> idList);
	
    int insert(PromotionCondition record);

    PromotionCondition selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PromotionCondition record);

}