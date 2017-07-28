package mapper.marketing.promotion;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.marketing.promotion.RelPromotionTypePrivilegeCondition;
/**
 * 促销类型与条件/优惠关系mapper
 * @author huangjc
 * @date 2016年7月25日
 */
public interface RelPromotionTypePrivilegeConditionMapper {
	/**
	 * 删除促销类型与优惠关系
	 * @param pvlgId 优惠id
	 * @param proTypeId 促销类型id
	 * @return
	 */
	int deleteProTypeAndPvlgRel(@Param("pvlgId")Integer pvlgId,@Param("proTypeId")Integer proTypeId);
	/**
	 * 删除促销类型与条件关系
	 * @param condtId 条件id
	 * @param proTypeId 促销类型id
	 * @return
	 */
	int deleteProTypeAndCondtRel(@Param("condtId")Integer condtId,@Param("proTypeId")Integer proTypeId);
	
	/**
	 * 删除促销类型与条件/优惠的关系
	 * @param proTypeId 促销类型id
	 * @return
	 */
	int deleteByProTypeId(@Param("proTypeId")Integer proTypeId);
	
	List<RelPromotionTypePrivilegeCondition> selectByProTypeId(Integer proTypeId);
	
    int insertBatch(List<RelPromotionTypePrivilegeCondition> recordList);

    RelPromotionTypePrivilegeCondition selectByPrimaryKey(Integer id);
}