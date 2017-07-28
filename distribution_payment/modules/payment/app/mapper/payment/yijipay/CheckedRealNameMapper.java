package mapper.payment.yijipay;


import entity.payment.yijipay.CheckedRealName;

import java.util.Map;

public interface CheckedRealNameMapper {

    CheckedRealName getByNameIdCard(Map map);

    int save(CheckedRealName checkedRealName);
}