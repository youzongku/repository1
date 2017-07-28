package services.dismember;

import entity.dismember.DisCity;

import java.util.List;

/**
 * Created by LSL on 2015/12/21.
 */
public interface IDisCityService {

    /**
     * 获取指定省份下的城市
     * @param proId
     * @return
     */
    List<DisCity> getCitiesByProvince(Integer proId);

	List<DisCity> getAllCities();

}
