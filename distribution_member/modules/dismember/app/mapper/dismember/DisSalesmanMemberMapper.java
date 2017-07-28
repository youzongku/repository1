package mapper.dismember;

import java.util.List;

import entity.dismember.DisSalesmanMember;

public interface DisSalesmanMemberMapper extends BaseMapper<DisSalesmanMember> {
    int deleteByPrimaryKey(Integer id);

    int insert(DisSalesmanMember record);

    int insertSelective(DisSalesmanMember record);

    DisSalesmanMember selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DisSalesmanMember record);

    int updateByPrimaryKey(DisSalesmanMember record);
    
    List <DisSalesmanMember> getDisSalesmanMember(DisSalesmanMember record);
    
    int  deleteByCondition(DisSalesmanMember record);
    
    int getCountByCondition(DisSalesmanMember record);
}