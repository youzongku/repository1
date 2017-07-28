package services.dismember;

import java.util.List;

import entity.dismember.ApkApplyQueue;
import entity.dismember.ApkVersion;

public interface IApkApplyService {
	
	/**
	 * 新增或修改apk打包申请记录，判断依据是传入apkApplyQueue的ID属性是否有值，有则更新，无则新增
	 * @param active
	 * @return
	 */
	public int saveOrUpdateActive(ApkApplyQueue apkApplyQueue);

	/**
	 * 获取预先的，未打包的，并且无打包异常的申请
	 * @return
	 */
	public List<ApkApplyQueue> getPriorApply(int limit);
	
	/**
	 * 用户之前获取所有待打包的申请，用来预估等待时间
	 * @return
	 */
	public int getApplyNeedToRebuiltBeforeYou(String account);
	
	/**
	 * 根据账户选取打包申请
	 * @param account
	 * @return
	 */
	public ApkApplyQueue selectByAccount(String account);

	/**
	 * apk升级
	 * @param appName app名称
	 * @param code 版本号
	 * @param channelName 渠道名称
	 * @return
	 */
	public ApkVersion apkUpgrade(String appName, int code, String channelName);

}
