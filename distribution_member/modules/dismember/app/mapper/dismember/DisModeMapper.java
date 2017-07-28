package mapper.dismember;

import java.util.List;

import entity.dismember.DisMode;

public interface DisModeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DisMode record);

    int insertSelective(DisMode record);

    DisMode selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DisMode record);

    int updateByPrimaryKey(DisMode record);
    
    List<DisMode> selectAll();
}