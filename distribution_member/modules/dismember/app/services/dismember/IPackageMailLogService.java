package services.dismember;

import entity.dismember.PackageMailLog;

import java.util.List;

/**
 * @author longhuashen
 * @since 2017/5/25
 */
public interface IPackageMailLogService {

    void savePackageMailLog(PackageMailLog packageMailLog);

    List<PackageMailLog> getPackageMailLogsByMemberId(Integer id);
}
