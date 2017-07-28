package mapper.dismember;

import entity.dismember.UserRankHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserRankHistoryMapper{

	UserRankHistory selectByPrimaryKey(Integer id);

    int insert(UserRankHistory record);

    int insertSelective(UserRankHistory record);

    int updateByPrimaryKeySelective(UserRankHistory record);

    int updateByPrimaryKey(UserRankHistory record);
	
	int deleteByPrimaryKey(Integer id);
    /**
     * 查询指定用户的等级变更历史
     * @return
     */
    List<UserRankHistory> getURHsByEmail(@Param("email")String email);

}