package services.sales;

import java.util.List;
import java.util.Map;

import entity.sales.OperateRecord;

public interface IOperateRecordService {
	public boolean saveOperateRecord(OperateRecord record);

	public List<OperateRecord> getOperateRecordByParam(Map<String,Object> param);

	public List<OperateRecord> findOperateRecordByCondition(OperateRecord record);
}
