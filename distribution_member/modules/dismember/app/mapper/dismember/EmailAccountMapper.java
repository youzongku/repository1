package mapper.dismember;

import entity.dismember.EmailAccount;

public interface EmailAccountMapper extends BaseMapper<EmailAccount> {
    int deleteByPrimaryKey(Integer iid);

    int insert(EmailAccount record);

    int insertSelective(EmailAccount record);

    EmailAccount select(EmailAccount account);

    int updateByPrimaryKeySelective(EmailAccount record);

    int updateByPrimaryKey(EmailAccount record);
}