package mapper.timer;

import java.util.List;

import entity.timer.OperateRecord;

public interface OperateRecordMapper {

	List<OperateRecord> selectByOrderIdList(List<Integer> orderIdList);

	int insertSelective(OperateRecord record);
}
