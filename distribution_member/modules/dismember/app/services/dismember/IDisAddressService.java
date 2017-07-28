package services.dismember;

import entity.dismember.DisAddress;

import java.util.List;
import java.util.Map;

/**
 * Created by LSL on 2015/12/25.
 */
public interface IDisAddressService {

    /**
     * 根据条件获取地址信息
     * @param params
     * @return
     */
    List<DisAddress> getAddresses(Map<String, String> params);

    /**
     * 新增地址信息
     * @param params
     * @return
     */
    DisAddress addAddress(Map<String, String> params);

    /**
     * 更新指定地址信息
     * @param params
     * @return
     */
    boolean updateAddress(Map<String, String> params);

    /**
     * 删除指定地址信息
     * @param id
     * @return
     */
    boolean deleteAddress(Integer id);

}
