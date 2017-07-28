package mapper.dismember;

import java.util.List;

import entity.dismember.AccountOperationRecord;

public interface AccountOperationRecordMapper{
    int deleteByPrimaryKey(Integer id);

    int insert(AccountOperationRecord record);

    int insertSelective(AccountOperationRecord record);

    AccountOperationRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AccountOperationRecord record);

    int updateByPrimaryKey(AccountOperationRecord record);

	List<AccountOperationRecord> selectByAccountId(Integer accountId);
}