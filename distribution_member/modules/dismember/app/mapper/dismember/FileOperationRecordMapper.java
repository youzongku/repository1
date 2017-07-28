package mapper.dismember;

import java.util.List;

import entity.dismember.FileOperationRecord;

public interface FileOperationRecordMapper extends BaseMapper<FileOperationRecord> {
    int deleteByPrimaryKey(Integer id);

    int insert(FileOperationRecord record);

    int insertSelective(FileOperationRecord record);

    FileOperationRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FileOperationRecord record);

    int updateByPrimaryKey(FileOperationRecord record);
    
    List<FileOperationRecord> selectByApplyId(Integer applyId);
}