package mapper.dismember;

import entity.dismember.ApChange;

public interface ApChangeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ApChange record);

    int insertSelective(ApChange record);

    ApChange selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ApChange record);

    int updateByPrimaryKey(ApChange record);
}