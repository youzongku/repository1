package mapper.marketing.promotion;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.marketing.promotion.RelConditionJudgementType;
/**
 * 条件与条件判断类型mapper
 * @author huangjc
 * @date 2016年7月25日
 */
public interface RelConditionJudgementTypeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RelConditionJudgementType record);

    RelConditionJudgementType selectByPrimaryKey(Integer id);
    
    List<RelConditionJudgementType> selectByCondtId(@Param("condtId")Integer condtId);

    /**
     * 删除条件与条件判断类型关系
     * @param condtJgmntTypeId 条件判断类型关系id
     * @param condtId 条件id
     * @return
     */
    int deleteRel(@Param("condtJgmntTypeId")Integer condtJgmntTypeId,
    		@Param("condtId")Integer condtId);
}