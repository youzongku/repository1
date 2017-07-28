package mapper.timer;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.timer.ApOptRecord;

public interface ApOptRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ApOptRecord record);

    int insertSelective(ApOptRecord record);

    ApOptRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ApOptRecord record);

    int updateByPrimaryKey(ApOptRecord record);
    
    /**
     * 子账期操作日志
     * @author zbc
     * @since 2017年2月27日 下午12:25:56
     */
    List<ApOptRecord> selectBySlaveId(@Param("slaveId")Integer slaveId);

    /**
     * 账期操作日志
     * @author zbc
     * @since 2017年2月27日 下午12:26:08
     */
    List<ApOptRecord> selectByMasterId(@Param("masterId")Integer masterId);
}