package mapper.dismember;

import dto.dismember.PaymentMethodDto;
import entity.dismember.PaymentCondition;

public interface PaymentConditionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PaymentCondition record);

    int insertSelective(PaymentCondition record);

    PaymentCondition selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PaymentCondition record);

    int updateByPrimaryKey(PaymentCondition record);

    PaymentCondition selectByParma(PaymentMethodDto condit);
}