package mapper.dismember;

import java.util.List;

import entity.dismember.AdminOperateRecord;

public interface AdminOperateRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AdminOperateRecord record);

    int insertSelective(AdminOperateRecord record);

    AdminOperateRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AdminOperateRecord record);

    int updateByPrimaryKey(AdminOperateRecord record);
    
    List<AdminOperateRecord> selectByAdminId(Integer adminId);
}