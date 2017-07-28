package mapper.dismember;

import entity.dismember.EmailType;

public interface EmailTypeMapper extends BaseMapper<EmailType> {
    int deleteByPrimaryKey(Integer iid);

    int insert(EmailType record);

    int insertSelective(EmailType record);

    EmailType selectByPrimaryKey(Integer iid);

    int updateByPrimaryKeySelective(EmailType record);

    int updateByPrimaryKey(EmailType record);
}