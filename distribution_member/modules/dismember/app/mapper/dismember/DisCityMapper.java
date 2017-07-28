package mapper.dismember;

import entity.dismember.DisCity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DisCityMapper extends BaseMapper<DisCity> {

    /**
     * 查询指定省份下的城市
     * @param proId
     * @return
     */
    List<DisCity> getCitiesByProvince(@Param("proId")Integer proId);

	List<DisCity> getAllCities();

}