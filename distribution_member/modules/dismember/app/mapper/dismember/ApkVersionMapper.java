package mapper.dismember;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.dismember.ApkVersion;

public interface ApkVersionMapper {

	public ApkVersion selectApkVersionByParam(@Param("appName")String appName, @Param("channelName")String channelName);

	public ApkVersion getMaxApkVersion(@Param("appName") String appName);
	
	public int updateByPrimaryKeySelective(ApkVersion oldApkVersion);
	
	public int insertSelective(ApkVersion registApk);

	/**
	 * 查找所有apkVersion
	 * @return
	 */
	public List<ApkVersion> getAll();

	

}
