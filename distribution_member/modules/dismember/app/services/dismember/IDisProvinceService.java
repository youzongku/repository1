package services.dismember;


import java.util.List;

import entity.dismember.DisProvince;

/**
 * Created by LSL on 2015/12/21.
 */
public interface IDisProvinceService {

    /**
     * 获取所有省份
     * @return
     */
    List<DisProvince> getAllProvinces();

	DisProvince getProvinces(String key);

	String getChinaArea(String param);

}
