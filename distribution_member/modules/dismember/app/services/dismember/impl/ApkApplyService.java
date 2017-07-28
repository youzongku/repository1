package services.dismember.impl;

import java.util.Date;
import java.util.List;

import com.google.inject.Inject;

import mapper.dismember.ApkApplyQueueMapper;
import mapper.dismember.ApkVersionMapper;
import entity.dismember.ApkApplyQueue;
import entity.dismember.ApkVersion;
import services.dismember.IApkApplyService;

public class ApkApplyService implements IApkApplyService {

	@Inject
	private ApkApplyQueueMapper apkApplyQueueMapper;
	
	@Inject
	private ApkVersionMapper apkVersionMapper;
	
	@Override
	public int saveOrUpdateActive(ApkApplyQueue apkApplyQueue) {
		if(apkApplyQueue.getId() != null){
			return apkApplyQueueMapper.updateByPrimaryKeySelective(apkApplyQueue);
		}
		
		return apkApplyQueueMapper.insertSelective(apkApplyQueue);
	}

	@Override
	public List<ApkApplyQueue> getPriorApply(int limit) {
		return apkApplyQueueMapper.getPriorApply(limit);
	}

	@Override
	public int getApplyNeedToRebuiltBeforeYou(String account) {
		return apkApplyQueueMapper.getApplyNeedToRebuiltBeforeYou(account);
	}

	@Override
	public ApkApplyQueue selectByAccount(String account) {
		return apkApplyQueueMapper.selectByAccount(account);
	}

	@Override
	public ApkVersion apkUpgrade(String appName, int code, String channelName) {
		ApkVersion apkVersionData= apkVersionMapper.selectApkVersionByParam(appName,channelName);
		if(apkVersionData==null || code>=apkVersionData.getCode()){
			return null;
		}
		if(apkVersionData.getUpdateTime()==null){
			apkVersionData.setUpdateTime(new Date());
		}
		return apkVersionData;
	}
	
}
