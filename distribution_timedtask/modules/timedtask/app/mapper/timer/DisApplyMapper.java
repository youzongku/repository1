package mapper.timer;

import java.util.List;

import entity.timer.DisApply;


public interface DisApplyMapper extends BaseMapper<DisApply> {
	
	/**
	 * 查询处理中的提现到M站的申请
	 * @Author LSL on 2016-09-28 17:19:06
	 */
	List<DisApply> findWithdrawToMsiteApply();
	
}