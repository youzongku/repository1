package mapper.dismember;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.dismember.VipInviteCode;

public interface VipInviteCodeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(VipInviteCode record);

    int insertSelective(VipInviteCode record);

    VipInviteCode selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(VipInviteCode record);

    int updateByPrimaryKey(VipInviteCode record);
    
    int batchInsert(List<String> num);
    
    VipInviteCode selectBycode(@Param("code")String code);
}