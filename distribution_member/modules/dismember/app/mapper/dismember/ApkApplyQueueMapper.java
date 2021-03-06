package mapper.dismember;

import java.util.List;

import entity.dismember.ApkApplyQueue;

public interface ApkApplyQueueMapper extends BaseMapper<ApkApplyQueue>{
	
	public List<ApkApplyQueue> getPriorApply(int limit);
	
	public int getApplyNeedToRebuiltBeforeYou(String account);
	
	public int isApplyExist(String account);
	
	public ApkApplyQueue selectByAccount(String account);

}