package mapper.dismember;

import java.util.List;
import java.util.Map;

import dto.dismember.WithdrawBalanceDto;
import org.apache.ibatis.annotations.Param;

import entity.dismember.DisApply;

public interface DisApplyMapper extends BaseMapper<DisApply> {

	List<DisApply> queryApply(Map<String, Object> param);
	
	Integer queryApplyCount(Map<String, Object> param);

	List<DisApply> queryApplys(@Param("list")List<Integer> ids);
	
	List<String> getRechargeAuditUser(Integer menuid);
	
	/**
	 * 根据在线支付单号获取申请信息
	 * @param onlinePayNo
	 * @return
	 */
	DisApply getApplyByOnlinePayNo(@Param("onlinePayNo")String onlinePayNo);

	Integer queryWithdrawCount(Map<String, Object> map);

	List<WithdrawBalanceDto> queryWithdrawRecord(Map<String, Object> map);

	/**
	 * 根据当前月获取申请人提现申请数据
	 * @param disApply
	 * @return
	 * @author huchuyin
	 * @date 2016年9月26日 下午5:15:51
	 */
	Integer getApplyCountByCurMonth(DisApply disApply);

	/**
	 * 查询处理中的提现到M站的申请
	 * @Author LSL on 2016-09-28 17:19:06
	 */
	List<DisApply> findWithdrawToMsiteApply();
	
	/**
	 * 根据流水号获取申请记录
	 * @author zbc
	 * @since 2017年1月16日 上午10:08:29
	 */
	List<DisApply> getBytransferNumber(@Param("transferNumber")String tranNo);
}