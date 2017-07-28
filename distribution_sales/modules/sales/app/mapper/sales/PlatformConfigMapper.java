package mapper.sales;

import entity.sales.PlatformConfig;

import java.util.List;
import java.util.Map;

public interface PlatformConfigMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PlatformConfig record);

    int insertSelective(PlatformConfig record);

    PlatformConfig selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PlatformConfig record);

    int updateByPrimaryKey(PlatformConfig record);

    PlatformConfig findPlatformConfigByCodeAndKey(Map<String, Object> map);

    List<PlatformConfig> findPlatformConfigsByCode(String platformCode);
}