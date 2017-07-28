package mapper.dismember;


import org.apache.ibatis.annotations.Param;

import entity.dismember.PaymentMapper;

public interface PaymentMapperMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PaymentMapper record);

    int insertSelective(PaymentMapper record);

    PaymentMapper selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PaymentMapper record);

    int updateByPrimaryKey(PaymentMapper record);
    
    int deleteBycondId(@Param("condId")Integer id);

}