package mapper.dismember;

import entity.dismember.DisRank;

import java.util.List;
import java.util.Map;

public interface DisRankMapper extends BaseMapper<DisRank> {

    /**
     * 获取所有等级数据
     * @return
     */
    List<DisRank> getAllRanks();

    /**
     * 分页查询等级数据记录条数
     * @return
     */
    int getCountByPage(Map<String, Object> map);

    /**
     * 分页查询等级数据
     * @return
     */
    List<DisRank> getRanksByPage(Map<String, Object> map);

    /**
     * 查询默认等级数据
     * @return
     */
    DisRank getDefaultRank();
    
    /**
     * 通过等级名称或等级折扣查询等级信息
     * @return
     */
    List<DisRank> getRanksByNameAndDis(Map<String, Object> map);

}