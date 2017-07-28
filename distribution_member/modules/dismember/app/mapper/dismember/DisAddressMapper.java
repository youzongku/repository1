package mapper.dismember;

import entity.dismember.DisAddress;

import java.util.List;

public interface DisAddressMapper extends BaseMapper<DisAddress> {

    /**
     * 根据条件获取地址信息
     * @param address
     * @return
     */
    List<DisAddress> getAddressesByCondition(DisAddress address);

}