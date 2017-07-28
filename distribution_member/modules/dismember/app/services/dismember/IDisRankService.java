package services.dismember;

import com.fasterxml.jackson.databind.JsonNode;
import dto.dismember.RankDto;
import dto.dismember.UserRankHistoryDto;
import entity.dismember.DisRank;
import vo.dismember.Page;

import java.util.List;
import java.util.Map;

/**
 * Created by LSL on 2016/2/5.
 */
public interface IDisRankService {

    /**
     * 获取所有等级数据
     * @return
     */
    List<DisRank> getAllRanks();

    /**
     * 分页获取等级数据
     * @return
     */
    Page<RankDto> getRanksByPage(Map<String, Object> params);

    /**
     * 新增或更新等级数据
     * sign:1——>新增，sign:2——>更新
     * @return
     */
    int addOrUpdateRank(Map<String, String> params);

    /**
     * 删除指定等级数据
     * @return
     */
    boolean deleteRankById(Integer rankId);

    /**
     * 更新指定用户的等级
     * @return
     */
    boolean updateUserRank(JsonNode params);

    /**
     * 获取指定用户的等级变更历史
     * @return
     */
    List<UserRankHistoryDto> getURHsByEmail(String email);
    
    /**
     * 根据校验等级折扣等级名称是否存在，并返回
     * @param rankName 等级名称
     * @param discount 等级折扣值
     * @param id 等级id
     * @return
     */
    Map<String,Object> checkIsExistOfRank(Integer id,String rankName,Integer discount);

}
