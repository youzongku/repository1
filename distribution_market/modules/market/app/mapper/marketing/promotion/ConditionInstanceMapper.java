package mapper.marketing.promotion;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import dto.marketing.promotion.condt.value.FinalCondtValue;
import entity.marketing.promotion.ConditionInstance;
/**
 * 条件实例mapper
 * @author huangjc
 * @date 2016年7月25日
 */
public interface ConditionInstanceMapper {
	
	int updatePriorityByPrimaryKey(@Param("id")int id, @Param("priority")int priority);
	
	int updateBatch(List<FinalCondtValue> finalCondtValueList);

	int deleteSoftlyByPrimaryKey(Integer id);
	
    int insert(ConditionInstance record);
    
    int insertBatch(List<ConditionInstance> records);

    ConditionInstance selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ConditionInstance record);
    
	/**
	 * 根据活动实例id获取条件实例
	 * @param actInstId
	 * @return
	 */
	List<ConditionInstance> selectByActInstId(@Param("actInstId")Integer actInstId);

}