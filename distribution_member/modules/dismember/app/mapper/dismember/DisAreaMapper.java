package mapper.dismember;

import entity.dismember.DisArea;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DisAreaMapper extends BaseMapper<DisArea> {

    /**
     * 查询指定城市下的区/县/县级市
     * @param cityId
     * @return
     */
    List<DisArea> getAreasByCity(@Param("cityId")Integer cityId);
    
    
    List<DisArea> getAllAreas();
}