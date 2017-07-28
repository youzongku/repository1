package services.dismember;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import dto.dismember.ApplyDto;
import dto.dismember.WithdrawBalanceDto;
import entity.dismember.DisApply;
import entity.dismember.DisWithdrawAccount;
import entity.dismember.OperationRecord;
import entity.dismember.ShopSite;
import play.mvc.Http.MultipartFormData.FilePart;
import vo.dismember.Page;

public interface IApplyService {

	/**
	 * 发送申请
	 * @param apply
	 * @return
	 */
	public Map<String, Object> sendApply(DisApply apply);
	
	/**
	 * 在线充值申请
	 * @param apply
	 * @return
	 */
	public Map<String, Object> sendOnlineApply(DisApply apply);
	
	/**
	 * 修改申请--管理员审核通过后在资金账户添加金额
	 * @param apply
	 * @return
	 */
	public Map<String, Object> updateApply(DisApply apply,String opEmail);

	/**
	 * 更新账户余额入口
	 * @param apply
	 * @return
	 */
	public Map<String, Object> updateAccount(DisApply apply);

	/**
	 * 查询申请记录
	 * @param param
	 * @return
	 */
	public Page<ApplyDto> queryApply(Map<String, Object> param);

	/**
	 * 余额支付
	 * @param apply
	 * @return
	 */
	@Deprecated
	public Map<String, Object> payment(DisApply apply);

	/**
	 * 查询申请操作记录
	 * @param applyId
	 * @return
	 */
	public List<OperationRecord> queryOperations(Integer applyId);

	/**
	 * 批量审批初审状态通过的申请
	 * @param ids
	 * @return
	 */
	public Map<String, Object> batchAudit(List<Integer> ids,String opEmail);

	/**
	 * @param param
	 * @return
	 */
	public List<ApplyDto> query(Map<String, Object> param);
	/**
	 * 退款接口 
	 * @param apply
	 * @return
	 */
	public Map<String, Object> freightRefund(DisApply apply);
	/**
	 * 提醒拥有初审复审审核权限的相关后台人员审核
	 * @param flag
	 * @return
	 */
	Map<String, Object> remindBuserAudit(Integer flag);

	public Map<String, Object> applyOpenMobile(ShopSite shopSite);

	/**
	 * 根据email得到我的移动客户端的相关信息
	 * @param params
	 * @return
	 */
	public Map<String, Object> getMobileApplyInfo(Map<String, String> params);

	public Map<String, Object> modifyMobileInfo(ShopSite shopSite);

	public Map<String, Object> saveMoneyFromMsite(Map<String, String> params);

	/**
	 * 审核提现申请，保存审核结果，冻结余额变更，新增操作记录和交易记录。
	 * {"applyId":"","auditState":"","auditRemark":"","auditReason":"",
	 * "transferAmount":"","transferNumber":"","transferTime":"","operator":""}
	 * @Author LSL on 2016-09-20 14:30:11
	 */
	public String auditWithdraw(JSONObject params);

    /**
	 * 提现申请
	 * @param param
	 * @return
	 */
	public Integer applyWithdraw(String param);
	
	/**
	 * 获取绑定的银行卡账号
	 * @param account
	 * @return
	 * @author huchuyin
	 * @date 2016年9月22日 上午10:41:55
	 */
	public String getBindBankCardList(DisWithdrawAccount account);
	
	/**
	 * 解除绑定的银行卡
	 * @param id
	 * @return
	 * @author huchuyin
	 * @date 2016年9月22日 上午10:42:36
	 */
	public String delBindBankCard(Integer id);

	/**
	 * 查询提现记录，用于导出提现申请
	 * @Author LSL on 2016-09-22 15:07:10
	 */
	public List<WithdrawBalanceDto> queryWithdrawRecord(Map<String, String[]> paramMap);

    /**
     * 保存提现限制
     * @Author LSL on 2016-09-22 16:33:52
     */
    public String saveWLimit(JSONObject params);

    /**
     * 获取通用提现限制
     * @Author LSL on 2016-09-22 16:33:52
     */
    public String getCommonWLimit();

	/**
	 * 新增审核通过的线下充值申请记录
	 * @Author LSL on 2016-10-21 15:19:49
	 */
	public String addOfflineApply(String param);

	public File getApplyFile(Integer id);

	/**
	 * 发送充值申请，带截图
	 * @author zbc
	 * @since 2016年11月30日 下午2:22:11
	 */
	public Map<String, Object>  sendApply(FilePart file, Map<String, String[]> params, String email);

	public Page<WithdrawBalanceDto> getWithdrawRecord(JSONObject params);

	public String checktranNo(String tno);

	/**
	 * 新版支付逻辑，支持账期功能 
	 * @author zbc
	 * @since 2017年2月21日 上午11:31:01
	 */
	public Map<String, Object> _payment(DisApply apply);

	/**
	 * 后台欢迎页面展示待审核数量
	 * @param email
	 * @return
	 */
	public Map<String, Object> backstageWelcome(String email);

}
