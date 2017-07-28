package services.dismember;

import entity.dismember.DisArea;

import java.util.List;

/**
 * Created by LSL on 2015/12/21.
 */
public interface IDisAreaService {

    /**
     * 获取指定城市下的区/县/县级市
     * @param cityId
     * @return
     */
    List<DisArea> getAreasByCity(Integer cityId);

	String addArea(String name, Integer cityId);

	List<DisArea> getAllAreas();

}
