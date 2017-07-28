package services.dismember;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import dto.dismember.CreditDto;
import dto.dismember.ExportCreditDto;
import entity.dismember.DisCredit;
import vo.dismember.Page;

/**
 * @author hanfs
 * 描述：用户信用额度服务接口
 *2016年4月20日
 */
public interface ICreditService {
	
	/**
	 * 描述：获取所有用的信用额度
	 * 2016年4月20日
	 * @param params 查询条件
	 * @return
	 */
	public Page<CreditDto> getCreditsByPage(Map<String, Object> params);
	
	/**
	 * 描述：通过信用额度id获取信用额度信息
	 * 2016年4月20日
	 * @param id 信用额度主键id
	 * @return
	 */
	public CreditDto getCreditInfo(Integer id);
	
	/**
	 * 描述：添加信用额度
	 * 2016年4月20日
	 * @param credit 信用额度
	 * @return
	 */
	public Map<String,Object> addCredit(DisCredit credit);
	
	/**
	 * 描述：修改信用额度
	 * 2016年4月20日
	 * @param credit  信用额度
	 * @param currEmail 当前用户邮箱
	 * @return
	 */
	public Map<String,Object> updateCredit(String currEmail,DisCredit credit);
	
	/**
	 * 描述：根据条件获取用户额度导出数据
	 * 2016年4月21日
	 * @param params 查詢用戶信用额度条件
	 * @return
	 */
	public List<ExportCreditDto> getExportCreditData(Map<String, Object> params);
	
	/**
	 * 描述：根据分销商账号获取可用的信用额度
	 * 2016年4月22日
	 * @param params 查詢用戶信用额度条件
	 * @return
	 */
	List<DisCredit> getDisCreditInfo(Map<String, Object> params);
	
	/**
	 * 描述：通过用户邮箱判断用户是否还款（true:已还款，false:未还款）
	 * 2016年5月10日
	 * @param paramJson
	 * @return
	 */
	boolean isRepay(JsonNode paramJson);
	
	/**
	 * 描述：通过用户邮箱删除用户所有额度及操作日志
	 * 2016年5月12日
	 * @param paramJson
	 */
	boolean delCreditAndRecordByEmail(JsonNode paramJson);

	/**
	 * 描述：切换用户的激活状态
	 * 2016年5月18日
	 * @return
	 */
	public Map<String, Object> changeActivated(String email, DisCredit credit);

	public String getCreditByEmail(String email);
}
