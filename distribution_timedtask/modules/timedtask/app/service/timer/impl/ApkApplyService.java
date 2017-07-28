package service.timer.impl;

import java.util.Date;
import java.util.List;

import mapper.timer.ApkApplyQueueMapper;
import mapper.timer.ApkVersionMapper;
import service.timer.IApkApplyService;

import com.google.inject.Inject;

import entity.timer.ApkApplyQueue;
import entity.timer.ApkVersion;


public class ApkApplyService implements IApkApplyService {

	@Inject
	private ApkApplyQueueMapper apkApplyQueueMapper;
	
	@Inject
	private ApkVersionMapper apkVersionMapper;
	
	@Override
	public int saveOrUpdateActive(ApkApplyQueue apkApplyQueue) {
		if(apkApplyQueue.getId() != null){
			return apkApplyQueueMapper.updateByPrimaryKeySelective(apkApplyQueue);
		}else{
			return apkApplyQueueMapper.insertSelective(apkApplyQueue);
		}
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
