package mapper.marketing.promotion;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.marketing.promotion.ConditionJudgementType;
/**
 * 条件判断类型mapper
 * @author huangjc
 * @date 2016年7月25日
 */
public interface ConditionJudgementTypeMapper {

    int insert(ConditionJudgementType record);

    int deleteSoftlyByPrimaryKey(Integer id);

    ConditionJudgementType selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ConditionJudgementType record);
    
    List<ConditionJudgementType> selectByCondtId(@Param("condtId")Integer condtId);

}