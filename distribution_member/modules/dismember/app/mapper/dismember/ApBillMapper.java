package mapper.dismember;

import entity.dismember.ApBill;

public interface ApBillMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ApBill record);

    int insertSelective(ApBill record);

    ApBill selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ApBill record);

    int updateByPrimaryKey(ApBill record);
    
    ApBill selectByApId(Integer id);
}