package mapper.marketing.promotion;

import java.util.List;

import entity.marketing.promotion.ConditionInstanceExt;

public interface ConditionInstanceExtMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ConditionInstanceExt record);
    
    int insertBatch(List<ConditionInstanceExt> records);

    ConditionInstanceExt selectByPrimaryKey(Integer id);
    
    ConditionInstanceExt selectByCondtInstId(Integer condtInstId);
    
    List<ConditionInstanceExt> selectByCondtInstIdList(List<Integer> condtInstIdList);

    ConditionInstanceExt selectByActInstId(Integer actInstId);

    int updateByPrimaryKeySelective(ConditionInstanceExt record);

}