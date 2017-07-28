package mapper.contract;

import java.util.List;

import entity.contract.QuotedOprecord;

public interface QuotedOprecordMapper {
	
    int insertSelective(QuotedOprecord record);

	List<QuotedOprecord> queryRecord(Integer qid);
}