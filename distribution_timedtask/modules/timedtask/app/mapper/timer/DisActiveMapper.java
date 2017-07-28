package mapper.timer;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import entity.timer.DisActive;


public interface DisActiveMapper extends BaseMapper<DisActive> {
    int deleteByPrimaryKey(Integer id);

    int insert(DisActive record);

    int insertSelective(DisActive record);

    DisActive selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DisActive record);

    int updateByPrimaryKey(DisActive record);
    
    List<DisActive> queryPageActive(@Param("param")Map<String,Object> param);

    int queryTotalCount(@Param("param")Map<String,Object> param);

	List<DisActive> queryOutTimeActive(@Param("param")Map<String, Object> param2); 
	List<DisActive> queryInnerTimeActive(@Param("param")Map<String, Object> param2);
}