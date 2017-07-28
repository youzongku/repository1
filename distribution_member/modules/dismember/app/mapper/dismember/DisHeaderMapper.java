package mapper.dismember;

import java.util.List;

import entity.dismember.DisHeader;

public interface DisHeaderMapper extends BaseMapper<DisHeader> {
    int deleteByPrimaryKey(Integer id);

    int insert(DisHeader record);

    int insertSelective(DisHeader record);

    DisHeader selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DisHeader record);

    int updateByPrimaryKey(DisHeader record);
    
    List<DisHeader> getHeader(DisHeader disHeader);
}