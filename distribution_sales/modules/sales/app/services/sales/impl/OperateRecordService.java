package services.sales.impl;

import java.util.List;
import java.util.Map;

import com.google.inject.Inject;

import entity.sales.OperateRecord;
import mapper.sales.OperateRecordMapper;
import services.sales.IOperateRecordService;

public class OperateRecordService implements IOperateRecordService {

	@Inject private OperateRecordMapper operateRecordMapper;
	
	@Override
	public boolean saveOperateRecord(OperateRecord record) {
		return operateRecordMapper.insert(record)>0;
	}

	@Override
	public List<OperateRecord> getOperateRecordByParam(Map<String,Object> param) {
		return operateRecordMapper.select(param);
	}

	@Override
	public List<OperateRecord> findOperateRecordByCondition(OperateRecord record) {
		return operateRecordMapper.findOperateRecordByCondition(record);
	}

}
