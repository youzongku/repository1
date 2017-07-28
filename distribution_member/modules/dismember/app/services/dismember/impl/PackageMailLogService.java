package services.dismember.impl;

import com.google.inject.Inject;
import entity.dismember.PackageMailLog;
import mapper.dismember.PackageMailLogMapper;
import services.dismember.IPackageMailLogService;

import java.util.List;

/**
 * @author longhuashen
 * @since 2017/5/25
 */
public class PackageMailLogService implements IPackageMailLogService {

    @Inject
    private PackageMailLogMapper packageMailLogMapper;

    @Override
    public void savePackageMailLog(PackageMailLog packageMailLog) {
        packageMailLogMapper.insertSelective(packageMailLog);
    }

    @Override
    public List<PackageMailLog> getPackageMailLogsByMemberId(Integer id) {
        return packageMailLogMapper.findPackageMailLogsByMemberId(id);
    }
}
