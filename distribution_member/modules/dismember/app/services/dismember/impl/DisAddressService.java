package services.dismember.impl;

import com.google.inject.Inject;
import entity.dismember.DisAddress;
import mapper.dismember.DisAddressMapper;
import play.Logger;
import services.dismember.IDisAddressService;

import java.util.List;
import java.util.Map;

/**
 * Created by LSL on 2015/12/25.
 */
public class DisAddressService implements IDisAddressService {

    @Inject
    private DisAddressMapper disAddressMapper;

    @Override
    public List<DisAddress> getAddresses(Map<String, String> params) {
        Logger.info("getAddresses params-->" + params.toString());
        DisAddress address = new DisAddress();
        address.setShopId(params.containsKey("shopId") ? Integer.valueOf(params.get("shopId")) : null);
        address.setEmail(params.containsKey("email") ? params.get("email") : null);
        address.setIsDefault(params.containsKey("isDefault") ? Boolean.valueOf(params.get("isDefault")) : null);
        return disAddressMapper.getAddressesByCondition(address);
    }

    @Override
    public DisAddress addAddress(Map<String, String> params) {
        Logger.info("addAddress params-->" + params.toString());
        DisAddress address = new DisAddress();
        address.setShopId(params.containsKey("shopId") ? Integer.valueOf(params.get("shopId")) : null);
        address.setEmail(params.containsKey("email") ? params.get("email") : null);
        address.setIsDefault(params.containsKey("default") ? Boolean.valueOf(params.get("default")) : null);
        address.setProvinceId(params.containsKey("proId") ? Integer.valueOf(params.get("proId")) : null);
        address.setCityId(params.containsKey("cityId") ? Integer.valueOf(params.get("cityId")) : null);
        address.setAreaId(params.containsKey("areaId") ? Integer.valueOf(params.get("areaId")) : null);
        address.setStreet(params.containsKey("street") ? params.get("street") : null);
        address.setZipCode(params.containsKey("zipCode") ? Integer.valueOf(params.get("zipCode")) : null);
        address.setCreateUser(params.containsKey("createUser") ? params.get("createUser") : null);
        int line = disAddressMapper.insertSelective(address);
        return line == 1 ? address : null;
    }

    @Override
    public boolean updateAddress(Map<String, String> params) {
        Logger.info("updateAddress params-->" + params.toString());
        Integer id = params.containsKey("id") ? Integer.valueOf(params.get("id")) : null;
        DisAddress address = disAddressMapper.selectByPrimaryKey(id);
        if (address == null) {
            Logger.info("指定店铺不存在");
            return false;
        }
        
        address.setShopId(params.containsKey("shopId") ? Integer.valueOf(params.get("shopId")) : null);
        address.setEmail(params.containsKey("email") ? params.get("email") : null);
        address.setIsDefault(params.containsKey("default") ? Boolean.valueOf(params.get("default")) : null);
        address.setProvinceId(params.containsKey("proId") ? Integer.valueOf(params.get("proId")) : null);
        address.setCityId(params.containsKey("cityId") ? Integer.valueOf(params.get("cityId")) : null);
        address.setAreaId(params.containsKey("areaId") ? Integer.valueOf(params.get("areaId")) : null);
        address.setStreet(params.containsKey("street") ? params.get("street") : null);
        address.setZipCode(params.containsKey("zipCode") ? Integer.valueOf(params.get("zipCode")) : null);
        int line = disAddressMapper.updateByPrimaryKeySelective(address);
        return line == 1;
    }

    @Override
    public boolean deleteAddress(Integer id) {
        int line = disAddressMapper.deleteByPrimaryKey(id);
        return line == 1;
    }
}
