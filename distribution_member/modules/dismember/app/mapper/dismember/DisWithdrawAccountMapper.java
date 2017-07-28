package mapper.dismember;

import java.util.List;

import entity.dismember.DisWithdrawAccount;

public interface DisWithdrawAccountMapper extends BaseMapper<DisWithdrawAccount> {
	
	/**
	 * 解除绑定的银行卡
	 * @param id
	 * @return
	 * @author huchuyin
	 * @date 2016年9月22日 上午10:43:40
	 */
	int delBindBangCard(Integer id);
	
	/**
	 * 获取银行卡列表
	 * @param disWithdrawAccount
	 * @return
	 * @author huchuyin
	 * @date 2016年9月22日 上午11:18:44
	 */
	List<DisWithdrawAccount> getWAccountsList(DisWithdrawAccount disWithdrawAccount);

    /**
     * 查询单个提现账户信息
     * @Author LSL on 2016-09-24 15:34:22
     */
	DisWithdrawAccount getWAccounts(DisWithdrawAccount account);
}