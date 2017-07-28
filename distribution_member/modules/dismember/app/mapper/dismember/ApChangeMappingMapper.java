package mapper.dismember;

import entity.dismember.ApChangeMapping;

public interface ApChangeMappingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ApChangeMapping record);

    int insertSelective(ApChangeMapping record);

    ApChangeMapping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ApChangeMapping record);

    int updateByPrimaryKey(ApChangeMapping record);
}