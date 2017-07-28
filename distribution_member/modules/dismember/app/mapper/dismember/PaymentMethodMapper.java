package mapper.dismember;

import java.util.List;

import entity.dismember.PaymentCondition;
import entity.dismember.PaymentMethod;

public interface PaymentMethodMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PaymentMethod record);

    int insertSelective(PaymentMethod record);

    PaymentMethod selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PaymentMethod record);

    int updateByPrimaryKey(PaymentMethod record);

	List<PaymentMethod> select();

	List<PaymentMethod> selectByCondit(PaymentCondition condit);
}