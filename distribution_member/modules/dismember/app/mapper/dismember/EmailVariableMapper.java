package mapper.dismember;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.dismember.EmailVariable;

public interface EmailVariableMapper extends BaseMapper<EmailVariable> {
    int deleteByPrimaryKey(Integer iid);

    int insert(EmailVariable record);

    int insertSelective(EmailVariable record);

    List<EmailVariable> select(@Param("ctype")String ctype);

    int updateByPrimaryKeySelective(EmailVariable record);

    int updateByPrimaryKey(EmailVariable record);
}