package mapper.dismember;

import entity.dismember.PackageMailLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PackageMailLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PackageMailLog record);

    int insertSelective(PackageMailLog record);

    PackageMailLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PackageMailLog record);

    int updateByPrimaryKey(PackageMailLog record);

    List<PackageMailLog> findPackageMailLogsByMemberId(Integer id);

    int batchInsert(List<PackageMailLog> list);
}