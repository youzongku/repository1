package mapper.timer;

import org.apache.ibatis.annotations.Param;

import entity.timer.ApkVersion;


public interface ApkVersionMapper {

	public ApkVersion selectApkVersionByParam(@Param("appName")String appName, @Param("channelName")String channelName);

	public ApkVersion getMaxApkVersion(@Param("appName") String appName);

	public int insertSelective(ApkVersion registApk);

}
