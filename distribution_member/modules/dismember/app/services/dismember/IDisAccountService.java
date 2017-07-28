package services.dismember;

import java.util.Map;

import org.apache.poi.ss.formula.functions.T;

import dto.dismember.ResultDto;
import entity.dismember.DisAccount;
import vo.dismember.LoginContext;

/**
 * Created by LSL on 2016/1/5.
 */
public interface IDisAccountService {

    /**
     * 更新指定用户资金账户信息
     * @param params
     * @return
     */
    boolean updateDisAccount(Map<String, String> params);

    /**
     * 指定用户资金账户信息
     * @param email
     * @return
     */
    Map<String, String> getAccountInfo(String email);

    /**
     * 检查资金账户是否被冻结
     * @param email
     * @return
     */
    DisAccount checkFrozen(String email);

	Map<String, Object> resetPayPassword(Map<String, String> params);

    /**
     * 校验支付密码
     * @param code : 密码
     * @return :
     */
    public String checkPayPwd(String code, LoginContext lc,String payCaptcha);
    
    /**
     * 获取账户信息
     * @param email
     * @return
     * @author huchuyin
     * @date 2016年10月7日 下午5:34:53
     */
    public DisAccount getAccountByEmail(String email);

    /**
     * 得到所有的账户信息
     * @author lzl
     * @since 2016年12月7日上午11:57:31
     */
	String getAllAccount(String email, Map<String, String[]> node);

	/**
	 * 核减账户余额
	 * @author lzl
	 * @since 2016年12月14日上午11:01:14
	 */
	String reduceAccountByEmalil(String operator, String param);

	/**
	 * 得到账户核减历史记录
	 * @author lzl
	 * @since 2016年12月14日下午2:15:18
	 */
	String getReduceAccountHistory(Integer accountId);

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月7日 下午5:07:28
	 */
	ResultDto<T> unfreeze(String em);

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月29日 下午3:30:41
	 */
	ResultDto<T> changeNickName(String string);

	/**
	 * 账期冻结金额：当账期剩余到达该金额时冻结账户
	 * @param email
	 * @return
	 */
	Double queryPeriodFrozen(String email);

	/**
	 * 设置账期冻结金额，当账期剩余到达该金额时冻结账户
	 * @param string
	 * @return
	 */
	ResultDto<T> setPeriodFrozen(String json);
    
}
