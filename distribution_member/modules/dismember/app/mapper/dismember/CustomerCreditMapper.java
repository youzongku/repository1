package mapper.dismember;

import entity.dismember.CustomerCredit;
import entity.dismember.DisMember;

public interface CustomerCreditMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CustomerCredit record);

    int insertSelective(CustomerCredit record);

    CustomerCredit selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CustomerCredit record);

    int updateByPrimaryKey(CustomerCredit record);
    
    CustomerCredit getCreditConfig(DisMember member);
}