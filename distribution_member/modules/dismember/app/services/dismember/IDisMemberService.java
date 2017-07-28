package services.dismember;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import dto.dismember.AdminDto;
import dto.dismember.MemberForm;
import dto.dismember.ResultDto;
import entity.dismember.DisEmailVerify;
import entity.dismember.DisMember;
import entity.dismember.DisMode;
import play.mvc.Http.MultipartFormData.FilePart;
import vo.dismember.Page;

/**
 * Created by luwj on 2015/11/26.
 */   
public interface IDisMemberService {
	/**
	 * 获取分销商
	 * @param disEmail
	 * @return
	 */
	public DisMember getDismember(String disEmail);

    /**
     * 注册
     * @param node
     * @return
     */
    JsonNode register(Map<String, String[]> node);

    /**
     * 激活
     * @param email
     * @param code
     * @param host
     * @return
     */
    Map<String, String> activedEmail(String email, String code, String host);

    /**
     * 登录
     * @param node
     * @param times 
     * @return
     */
    Map<String, String> login(JsonNode node, String host, Integer times);
    
    /**
     * 提供给php使用的免密登录，在其他地方禁止使用
     * @param node
     * @param host
     * @return
     */
    Map<String, Object> loginWithoutPwd(String email, String phpLoginTokenParam, String host);

    /**
     * 更新用户信息
     * @param form
     * @return
     */
    JsonNode updateInfo(MemberForm form);

    /**
     * 获取用户信息
     * @param email
     * @return
     */
    Map<String, String> getInfo(String email);

    /**
     * 重发激活邮件
     * @param email
     * @return
     */
    JsonNode sendEmailAgain(String email);

    /**
     * 验证指定用户名是否存在
     * @param user
     * @return
     */
    boolean verifyUser(String user);

    /**
     * 后台管理员登录
     * @param params
     * @return
     */
    Map<String, String> adminLogin(Map<String, String> params);
    /**
     * 提供给php使用的免密登录，在其他地方禁止使用
     * @param params
     * @return
     */
    Map<String, Object> adminLoginWithoutPwd(String email, String phpLoginTokenParam, String host);

    /**
     * 分页获取后台用户信息
     * @return
     */
    Page<AdminDto> getAdminsByPage(Map<String, Object> params);

    /**
     * 获取邮箱验证成功记录
     * @return
     */
	DisEmailVerify getEmailVerifySuccessRecord(Map<String, String> params);

    /**
     * 获取管理员用户登录历史信息
     * @param email
     * @return
     */
    Map<String, String> getAdminLoginHistory(String email);

    /**
     * 发送修改手机号验证邮件
     * @return
     */
    boolean sendModifyCellphoneVerifyEmail(String email);

    /**
     * 验证邮件记录与指定验证码
     * @return
     */
    boolean verifyEmailCode(String email, String code);

    /**
     * 修改管理员密码
     * @param userId 
     * @return
     */
    Map<String, Object> updateCipher(String email, Integer userId);

	DisMember getMember(String email);

	boolean checkAdminUser(String email);

	Map<String, Object> insertUser(DisMember member,String operateFlag, String string);

	Map<String, Object> deleteUser(Integer id);

	Map<String, Object> resetPwd(Map<String, String> params);

	boolean sendEamil(Map<String, Object> objectmap, String emailType, String tempType, String toemail);
	
	/**
	 * 根据email客服账号查询
	 * @param email 客服账号
	 * @return
	 */
	Map<String, Object> getCustomerServiceAccount(String email);
    
	/**
	 * 发送修改支付密码验证邮箱
	 * @return
	 */
	Map<String, Object> changePayPasswordByEmail(Map<String, String> params);

	Map<String, Object> checkedPayPwdEmailHours(Map<String, String> params);
	/**
	 * 手机号码注册验证
	 * @param node
	 * @return
	 */
	Map<String, Object> checktelRegister(Map<String, String> node);
	/**
	 * 手机号码注册
	 * @param node
	 * @return
	 */
	JsonNode telRegister(Map<String, String> node);
	/**
	 * 手机找回密码验证码验证
	 * @param params
	 * @return
	 */
	Map<String, Object> checkTelFindPWD(Map<String, String> params);
	/**
	 * 手机找回密码 短信验证 以及 手机号码 有效性验证
	 * @param params
	 * @return
	 */
	Map<String, Object> sendCheckCode(Map<String, String> params);

	List<DisMode> getMode();

	/**
	 * 根据主键ID获取用户信息
	 * @param memberId
	 * @return
	 * @author huchuyin
	 * @date 2016年9月18日 下午2:37:54
	 */
	DisMember getMemberById(Integer memberId);
	
	/**
	 * 发送短信
	 * @param params
	 * @return
	 * @author huchuyin
	 * @date 2016年10月10日 下午7:35:15
	 */
	String sendMessageForTel(String params);
	
	/**
	 * 校验短信验证码信息
	 * @param smsc
	 * @param tel
	 * @param types
	 * @return
	 * @author huchuyin
	 * @date 2016年10月10日 下午8:51:26
	 */
	String checkSmsCode(String smsc,String tel,int types);

	String backstageRegister(String param);

	Map<String, Object> getCreditInfo(String email);

	/**
	 * 注册申请成为经销商
	 * @param params
	 * @param files
	 * @return
	 * @author liaozl
	 */
	String registerApply(Map<String, String[]> params, List<FilePart> files);

	/**
	 * 得到所有的申请记录
	 * @param param
	 * @return
	 */
	String getRegisterApplys(String param);

	/**
	 * 根据id得到指定的申请详情
	 * @param id
	 * @return
	 */
	String getDetail(Integer id);

	ResultDto<?> auditApply(String node, String email);

	/**
	 * 根据id得到用户注册经销商时，上传的文件
	 * @param id
	 * @return
	 */
	File getAuditFile(Integer id);

	/**
	 * 由注册申请变为注册成普通分销商
	 * @param id 申请注册记录id
	 * @param host
	 * @return
	 */
	String becomeOrdinaryUser(Integer id,String host);

	/**
	 * 后台申请成为经销商
	 * @param params
	 * @param files
	 * @param email
	 * @return
	 */
	String backRegisterApply(Map<String, String[]> params, List<FilePart> files, String email);

	String modifyApplyFiles(Map<String, String[]> params, List<FilePart> files, String email);

    String getApplyFileHistory(Integer applyId);
    
    int save(DisMember member);
    
    int update(DisMember member);

	/**
	 * 生成调用发送短信接口令牌
	 * @return
	 */
	public String getClientCode();

	/**
	 * 短信接口令牌
	 * @param sign
	 * @return
	 */
	public boolean checkSign(String sign);

	/**
	 * 
	 * @author zbc
	 * @since 2017年4月28日 下午12:20:29
	 */
	public ResultDto<?> disableUser(String email,String operator);

	/**
	 * 
	 * @author zbc
	 * @since 2017年5月2日 上午9:33:44
	 */
	public ResultDto<?> adminRecord(Integer id);

	/**
	 * 生成客户编码
	 *
	 * @param provinceCode
	 * @param cityCode
	 * @param areaCode
	 * @param distributionMode
	 * @return
	 */
	String generateCustomerCode(Integer provinceCode, Integer cityCode, Integer areaCode, int distributionMode, int id);

	/**
	 * 设置用户编码
	 *
	 * @param jsonNode
	 * @return
	 */
	Map<String, Object> setMemberUserCode(JsonNode jsonNode, String operator);

	/**
	 * getAdminsByPage接口查询条件太多，速度慢
	 * @param userMap
	 * @return
	 */
	public Page<AdminDto> allDistributions(Map<String, Object> userMap);

	/**
	 * 单个设置运费
	 *
	 * @param json
	 * @return
	 */
	Map<String, Object> setIsPackageMail(JsonNode json, String operator);

	/**
	 * 批量设置运费
	 *
	 * @param jsonNode
	 * @param operator
	 * @return
	 */
	Map<String, Object> batchSetIsPackageMail(JsonNode jsonNode, String operator);

	/**
	 * 后台修改分销商类型
	 * @author zbc
	 * @since 2017年6月21日 上午9:47:40
	 * @param string
	 * @param email
	 * @return
	 */
	public ResultDto<?> updateComsumerInfo(String string, String admin);
}
