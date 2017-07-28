package mapper.dismember;

import java.util.List;

import entity.dismember.OperationRecord;

public interface OperationRecordMapper extends BaseMapper<OperationRecord> {
    int deleteByPrimaryKey(Integer id);

    int insert(OperationRecord record);

    int insertSelective(OperationRecord record);

    OperationRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OperationRecord record);

    int updateByPrimaryKey(OperationRecord record);

	List<OperationRecord> query(Integer applyId);
}