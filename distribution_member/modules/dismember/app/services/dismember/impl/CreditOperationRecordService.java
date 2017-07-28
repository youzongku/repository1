package services.dismember.impl;

import java.util.List;

import com.google.inject.Inject;

import entity.dismember.CreditOperationRecord;
import mapper.dismember.CreditOperationRecordMapper;
import services.dismember.ICreditOperationRecordService;

public class CreditOperationRecordService implements ICreditOperationRecordService {
	
	@Inject
	private CreditOperationRecordMapper creditOperationRecordMapper;

	@Override
	public boolean addOperationRecord(CreditOperationRecord creditOperationRecord) {
		int result = creditOperationRecordMapper.insertSelective(creditOperationRecord);
		return result>0;
	}

	@Override
	public List<CreditOperationRecord> getOperationRecordsByEmail(String email,Integer operateType) {
		return creditOperationRecordMapper.getRecordsByEmail(email,operateType);
	}

}
