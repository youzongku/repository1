package mapper.marketing.promotion;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import dto.marketing.promotion.pvlg.value.FinalPvlgValue;
import entity.marketing.promotion.PrivilegeInstance;
/**
 * 优惠实例mapper
 * @author huangjc
 * @date 2016年7月25日
 */
public interface PrivilegeInstanceMapper {
	
	int updateBatch(List<FinalPvlgValue> finalPvlgValueList);

    int insert(PrivilegeInstance record);
    
    int insertBatch(List<PrivilegeInstance> records);

    int deleteSoftlyByPrimaryKey(Integer id);
    
    PrivilegeInstance selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PrivilegeInstance record);
    
	List<PrivilegeInstance> selectByActInstId(@Param("actInstId")int actInstId);
	
	PrivilegeInstance selectByCondtInstId(@Param("condtInstId")int condtInstId);
	
	List<PrivilegeInstance> selectByCondtInstIdList(@Param("list")List<Integer> condtInstIdList);
	
	List<PrivilegeInstance> byIdGetPri(List<Integer> list);

}