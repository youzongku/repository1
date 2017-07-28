package mapper.marketing.promotion;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.marketing.promotion.PromotionType;
import forms.marketing.promotion.PromotionTypeSearchForm;
/**
 * 促销类型mapper
 * @author huangjc
 * @date 2016年7月25日
 */
public interface PromotionTypeMapper {
	
	/**
	 * 更新促销类型的使用状态
	 * @param id
	 * @param used true已使用，false未使用
	 * @return
	 */
	int updateUsed(@Param("idList")List<Integer> idList,@Param("used")boolean used);
	
	PromotionType selectProTypeByName(@Param("proTypeName")String proTypeName);
	
    int insert(PromotionType record);

    PromotionType selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PromotionType record);

    /**
     * 逻辑删除
     * @param id
     * @return
     */
    int deleteSoftlyByPrimaryKey(Integer id);
    
    // 分页查询
    List<PromotionType> selectProTypeList(PromotionTypeSearchForm form);
    int selectProTypeCount(PromotionTypeSearchForm form);
}