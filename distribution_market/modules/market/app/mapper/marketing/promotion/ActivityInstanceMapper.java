package mapper.marketing.promotion;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import dto.marketing.promotion.ActInstWithProTypeDto;
import entity.marketing.promotion.ActivityInstance;

/**
 * 活动实例mapper
 * @author huangjc
 * @date 2016年7月25日
 */
public interface ActivityInstanceMapper {
	
	public List<ActivityInstance> selectByProActId(@Param("proActId")Integer proActId);
	
	public int deleteSoftlyByPrimaryKey(Integer id);
	
	public int deleteSoftlyByIdList(@Param("idList")List<Integer> idList);
	
	public int deleteSoftlyByProTypeId(@Param("proTypeId")Integer proTypeId);

	public int insert(ActivityInstance record);

	public ActivityInstance selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ActivityInstance record);

    public List<ActInstWithProTypeDto> getActivityInstanceAndType(Integer proActId);
    
	public int getActInstCountByProActId(@Param("proActId")int proActId);
	
}