package mapper.dismember;

import org.apache.ibatis.annotations.Param;

import entity.dismember.EmailTemplate;

public interface EmailTemplateMapper extends BaseMapper<EmailTemplate> {
    int deleteByPrimaryKey(Integer iid);

    int insert(EmailTemplate record);

    int insertSelective(EmailTemplate record);

    EmailTemplate select(@Param("ctype")String ctype);

    int updateByPrimaryKeySelective(EmailTemplate record);

    int updateByPrimaryKey(EmailTemplate record);
}