package mapper.dismember;

import entity.dismember.DisChinaArea;
import entity.dismember.DisProvince;

import java.util.List;
import java.util.Map;

public interface DisProvinceMapper extends BaseMapper<DisProvince> {

    /**
     * 查询所有省份
     * @return
     */
    List<DisProvince> getAllProvinces();

	DisProvince getProvince(String key);

	List<DisChinaArea> getChinaArea(Map<String, Object> search);

}