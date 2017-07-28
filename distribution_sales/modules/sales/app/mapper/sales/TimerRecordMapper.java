package mapper.sales;

import entity.sales.TimerRecord;
import org.apache.ibatis.annotations.Param;

public interface TimerRecordMapper {

    /**
     * 插入
     * @param record
     * @return
     */
    int saveRecord(TimerRecord record);


    /**
     * 查询
     * @param type
     * @return
     */
    TimerRecord getRecord(@Param("executeType") String type);

    /**
     * 更新
     * @param record
     * @return
     */
    int upRecord(TimerRecord record);
}