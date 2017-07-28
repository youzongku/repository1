package services.dismember.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.mybatis.guice.transactional.Transactional;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.common.io.Files;
import com.google.inject.Inject;

import constant.dismember.Constant;
import dto.dismember.AdminDto;
import dto.dismember.MemberForm;
import dto.dismember.ResultDto;
import entity.dismember.AdminOperateRecord;
import entity.dismember.ApkVersion;
import entity.dismember.CustomerCredit;
import entity.dismember.CustomerType;
import entity.dismember.DisAccount;
import entity.dismember.DisApplyFile;
import entity.dismember.DisEmailVerify;
import entity.dismember.DisMember;
import entity.dismember.DisMemberMenu;
import entity.dismember.DisMode;
import entity.dismember.DisRank;
import entity.dismember.DisRegisterApply;
import entity.dismember.DisRole;
import entity.dismember.DisSalesman;
import entity.dismember.DisSalesmanMember;
import entity.dismember.EmailAccount;
import entity.dismember.EmailTemplate;
import entity.dismember.EmailVariable;
import entity.dismember.FileOperationRecord;
import entity.dismember.Invoice;
import entity.dismember.LoginHistory;
import entity.dismember.PhoneVerify;
import entity.dismember.ShopSite;
import entity.dismember.UserRankHistory;
import entity.dismember.VipInviteCode;
import entity.dismember.PackageMailLog;
import filters.common.CookieTrackingFilter;
import mapper.dismember.AdminOperateRecordMapper;
import mapper.dismember.ApkVersionMapper;
import mapper.dismember.CustomerCreditMapper;
import mapper.dismember.CustomerTypeMapper;
import mapper.dismember.DisAccountMapper;
import mapper.dismember.DisApplyFileMapper;
import mapper.dismember.DisEmailVerifyMapper;
import mapper.dismember.DisMemberMapper;
import mapper.dismember.DisMemberMenuMapper;
import mapper.dismember.DisModeMapper;
import mapper.dismember.DisRankMapper;
import mapper.dismember.DisRegisterApplyMapper;
import mapper.dismember.DisRoleMapper;
import mapper.dismember.DisSalesmanMapper;
import mapper.dismember.DisSalesmanMemberMapper;
import mapper.dismember.EmailAccountMapper;
import mapper.dismember.EmailTemplateMapper;
import mapper.dismember.EmailVariableMapper;
import mapper.dismember.FileOperationRecordMapper;
import mapper.dismember.InvoiceMapper;
import mapper.dismember.LoginHistoryMapper;
import mapper.dismember.PhoneVerifyMapper;
import mapper.dismember.ShopSiteMapper;
import mapper.dismember.UserRankHistoryMapper;
import mapper.dismember.VipInviteCodeMapper;
import mapper.dismember.PackageMailLogMapper;
import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.Http.Context;
import play.mvc.Http.MultipartFormData.FilePart;
import services.base.utils.JsonFormatUtils;
import services.dismember.ICaptchaService;
import services.dismember.IDisMemberService;
import services.dismember.ILoginService;
import session.ISessionService;
import utils.dismember.AESOperator;
import utils.dismember.DateUtils;
import utils.dismember.EmailUtil;
import utils.dismember.HttpUtil;
import utils.dismember.IDUtils;
import utils.dismember.JsonCaseUtil;
import utils.dismember.MD5Util;
import utils.dismember.SMSManager;
import vo.dismember.LoginContext;
import vo.dismember.LoginContextFactory;
import vo.dismember.Page;

/**
 * Created by xuse on 2015/11/26.
 */
public class DisMemberService implements IDisMemberService {
	
	private static String filePath = "";
	@Inject
    private ILoginService loginService;
	
	@Inject
	private ICaptchaService captchaService;
	
	@Inject
	private ISessionService sessionService;

	@Inject
	private DisMemberMapper disMemberMapper;

	@Inject
	private ApkVersionMapper apkVersionMapper;
	
	@Inject
	private DisAccountMapper disAccountMapper;

	@Inject
	private DisEmailVerifyMapper disEmailVerifyMapper;

	@Inject
	private LoginHistoryMapper loginHistoryMapper;

	@Inject
	private DisRoleMapper disRoleMapper;

	@Inject
	private DisRankMapper disRankMapper;

	@Inject
	private EmailAccountMapper emailMapper;

	@Inject
	private EmailTemplateMapper templateMapper;

	@Inject
	private EmailVariableMapper variableMapper;
	
	@Inject
	private DisModeMapper modeMapper;
	
	@Inject
	private DisMemberMenuMapper disMemberMenuMapper;
	
	@Inject
	private PhoneVerifyMapper phoneVerifyMapper;
	
	@Inject
	private DisSalesmanMemberMapper disSalesmanMemberMapper;
	
	@Inject
	private CustomerCreditMapper creditConfing;
	
	@Inject
	private UserRankHistoryMapper userRankHistoryMapper;
	
	@Inject
	private CustomerTypeMapper customerTypeMapper;

    @Inject
    private ShopSiteMapper shopSiteMapper;

    @Inject
    private DisRegisterApplyMapper disRegisterApplyMapper;
    
    @Inject
    private DisApplyFileMapper disApplyFileMapper;
    
    @Inject
    private DisSalesmanMapper disSalesmanMapper;
    
    @Inject
    private FileOperationRecordMapper fileOperationRecordMapper;
    
    @Inject
    private VipInviteCodeMapper vipMapper;
    
    @Inject
    private EventBus ebus;
    
    @Inject
    private InvoiceMapper invoiceMapper;
    
    @Inject
    private AdminOperateRecordMapper adminRecordMapper;

	@Inject
	private PackageMailLogMapper packageMailLogMapper;

    private static String phpLoginToken = Play.application().configuration().getConfig("php").getString("loginToken");
    private static String phpLoginTokenEncoded = MD5Util.MD5Encode(phpLoginToken+"php2bbc", MD5Util.CHARSET_UTF_8);
    
	@Override
	@Transactional
	public JsonNode register(Map<String, String[]> params) {
		String errorCode = "0";
		String errorInfo = "";
		Map<String, String> map = Maps.newHashMap();

		// 都云涛添加注释：不再校验店铺等信息
		if (params.containsKey("email") 
				&& params.containsKey("passWord") 
				&& params.containsKey("tel")) {
			String email = params.get("email")[0].trim().toLowerCase();
			String passWord = params.get("passWord")[0].trim();
			//密码必须为6-20个字符，且至少包含数字、大写、小写字母等三种或以上字符 by huchuyin 2016-10-6
			if(StringUtils.isEmpty(passWord)
					|| (passWord.length() < 6 || passWord.length() > 20)
					|| !utils.dismember.StringUtils.containsLetterNum(passWord)) {
				map.put("errorCode", "1");
				map.put("errorInfo", "密码必须为6-20个字符，且至少包含数字、大写、小写字母等三种或以上字符！");
				Logger.info(">>>register>>>map>>>>" + map.toString());
				return Json.toJson(map);
			}
			//End by huchuyin 2016-10-6
			// 都云涛添加注释一下两行：流程修改，注册时不再需要店铺信息和手机
			String tel = params.get("tel")[0].trim();
			//添加注册邀请码
			String registerInviteCode = Strings.isNullOrEmpty(params.get("registerInviteCode")[0])?"":params.get("registerInviteCode")[0].trim();

			// 查看邮箱是否已经注册
			DisMember member = new DisMember();
			member.setRoleId(2);
			member.setEmail(email);
			member = disMemberMapper.getMember(member);
			if (member != null && StringUtils.isNotBlank(member.getPassWord())) {
				errorCode = "1";
				errorInfo = "该账号已经被注册";
			} else {
				try {
					// 保存用户信息
					DisMember disMember = new DisMember();
					disMember.setPassWord(MD5Util.MD5Encode(passWord, MD5Util.CHARSET_UTF_8));
					disMember.setUserName(IDUtils.getUUID());
					disMember.setEmail(email);
					disMember.setTelphone(tel);
					disMember.setCreateDate(new Date());
					disMember.setRoleId(2);// 2为分销商角色，1为管理员角色
					//注册设置默认邀请码
					if(StringUtils.isBlank(registerInviteCode)) {
						Configuration config = Play.application().configuration()
								.getConfig("invitecode");
						registerInviteCode = config == null ? "4bd284" : config.getString("code");
					}
					disMember.setRegisterInviteCode(registerInviteCode);//设置他人的注册邀请码，由客户手动输入
					disMember.setSelfInviteCode(IDUtils.getUUID().substring(0, 6));//设置自身邀请码，由系统随机生成
					// 设置用户账号等级为默认等级
					DisRank rank = disRankMapper.getDefaultRank();
					if (rank != null) {
						disMember.setRankId(rank.getId());
					} else {
						Logger.info("activedEmail default rank isn't set.");
					}
					//add by zbc 电商 和 vip 默认为线上
					disMember.setAttributionType(Constant.USER_ATTR_TYPE_ONLINE);
					save(disMember);
					// Ebean.rollbackTransaction();

					// 生成帐户
					DisAccount disAccount = new DisAccount();
					disAccount.setBalance(new BigDecimal(0));
					disAccount.setFrozen(false);// 是否冻结账户
					disAccount.setEmail(disMember.getEmail());
					disAccountMapper.insertSelective(disAccount);

					// 发送激活邮件
					String code = IDUtils.getUUID();// 验证码
					map.put("code", code);
					String url = HttpUtil.getHostUrl() +
							"/member/activedEmail?email=" + email + "&code=" + code;
					Logger.debug("register email url-->" + url);
					// 邮件变量值
					Map<String, Object> objectmap = Maps.newHashMap();
					objectmap.put("url", url);
					objectmap.put("toemail", email);
					// 如果发送成功
					sendEamil(objectmap, Constant.EMAIL_SMTP, Constant.ACTIVATE, email);

					DisEmailVerify disEmailVerify = new DisEmailVerify();
					disEmailVerify.setCemail(email);
					disEmailVerify.setBisending(true);
					disEmailVerify.setCactivationcode(code);
					disEmailVerify.setIdaynumber(1);
					Calendar calendar = Calendar.getInstance();
					disEmailVerify.setDsenddate(calendar.getTime());
					disEmailVerify.setDcreatedate(calendar.getTime());
					//注册激活邮件有效时间为1天
					calendar.add(Calendar.DATE, 1);
					disEmailVerify.setDvaliddate(calendar.getTime());
					disEmailVerify.setSendType(0);
					disEmailVerifyMapper.insertSelective(disEmailVerify);
				} catch (Exception e) {
					errorCode = "1";
					errorInfo = "注册失败,异常";
					Logger.error(e + "");
				}
			}
			map.put("email", email);
		} else {
			errorCode = "1";
			errorInfo = "注册失败,参数错误";
		}
		map.put("errorCode", errorCode);
		map.put("errorInfo", errorInfo);
		Logger.debug(">>>register>>>map>>>>" + map.toString());
		return Json.toJson(map);
	}

	/**
	 * @param objectmap
	 *            变量值
	 * @param emailType
	 *            邮件类型
	 * @param tempType
	 *            模板类型
	 * @param toemail
	 *            收件人
	 * @return
	 */
	@Override
	public boolean sendEamil(Map<String, Object> objectmap, String emailType, String tempType, String toemail) {
		EmailAccount emailAccount = new EmailAccount();
		emailAccount.setCtype(emailType);//smtp
		EmailTemplate template = templateMapper.select(tempType);
		if (null == template) {
			Logger.info("未配置邮件模板：" + new Date());
			return false;
		}
		String content = template.getCcontent();
		Logger.info("content----------->"+Json.toJson(content));
		String title = template.getCtitle();
		Logger.info("title-------->"+Json.toJson(title));
		List<EmailVariable> variables = variableMapper.select(tempType);
		String date = DateUtils.date2string(new Date(), DateUtils.FORMAT_FULL_DATETIME);
		Logger.info("date------------->"+date);
		//有些邮件中需要显示发送邮件的时间
		String year = date.substring(0, 4);
		String month = date.substring(5 ,7);
		String day =  date.substring(8, 10);
		String hourse = date.substring(11, 13);
		String minute = date.substring(14, 16);
		String second = date.substring(17);
		if(content.contains("year") || content.contains("month") || content.contains("day") 
				|| content.contains("hourse") || content.contains("minute") || content.contains("second")){
			content = content.replaceAll("year", year).replace("{{", "").replace("}}", "");
			content = content.replaceAll("month", month).replace("{{", "").replace("}}", "");
			content = content.replaceAll("day", day).replace("{{", "").replace("}}", "");
			content = content.replaceAll("hourse", hourse).replace("{{", "").replace("}}", "");
			content = content.replaceAll("minute", minute).replace("{{", "").replace("}}", "");
			content = content.replaceAll("second", second).replace("{{", "").replace("}}", "");
		}
		if (null != variables && variables.size() > 0) {
			for (EmailVariable emailVariable : variables) {
				String variable = emailVariable.getCname();
				Object value = objectmap.get(variable);
				if (title.contains(variable)) {
					if (null != value) {
						title = title.replaceAll(variable, value.toString()).replace("{{", "").replace("}}", "");
					}
				}
				if (content.contains(variable)) {
					if (null != value) {
						content = content.replaceAll(variable, value.toString()).replace("{{", "").replace("}}", "");
					}
				}
			}
		}
		Logger.info("content---------->"+Json.toJson(content));
		return EmailUtil.send(title, content, emailMapper.select(emailAccount), toemail);
	}

	@Override
	public Map<String, String> activedEmail(String email, String code, String host) {
		String errorCode = "0";
		String errorInfo = "";
		// 判断
		DisMember disMember = new DisMember();
		disMember.setRoleId(2);
		disMember.setEmail(email);
		disMember = disMemberMapper.getMember(disMember);
		if (disMember != null) {
			if (!disMember.getIsActived()) {
				DisEmailVerify disEmailVerify = new DisEmailVerify();
				disEmailVerify.setCemail(email);
				disEmailVerify.setSendType(0);
				disEmailVerify = disEmailVerifyMapper.getVerify(disEmailVerify);
				if (disEmailVerify != null) {
					// 判断邮件是否已经失效
					Calendar calendar = Calendar.getInstance();
					if (calendar.getTimeInMillis() <= disEmailVerify.getDvaliddate().getTime()) {
						if (disEmailVerify.getCactivationcode().equals(code)) {// 判断激活码是否一致
							// 将用户账号的激活状态设置为已激活
							disMember.setIsActived(true);
							update(disMember);
							//用户注册成功时，自动登录
							//1.记录登录历史
							LoginHistory longhistory = new LoginHistory();
							longhistory.setDtimestamp(new Date());
							longhistory.setCemail(email);
							longhistory.setCclientip(host);
							loginHistoryMapper.insertSelective(longhistory);
							  //2.保存登录状态
							loginService.setLoginContext(1, LoginContextFactory.initLC(disMember.getId()+"", disMember.getUserName(), email,disMember.getDistributionMode()+"",disMember.getComsumerType()));
							setSession(1, disMember);
						}
					} else {
						errorCode = "1";
						errorInfo = "邮件已经失效，请重新发送激活邮件激活帐户";
					}
				}
			} else {
				errorInfo = "会员已经激活成功";
				//用户注册成功时，自动登录
				//1.记录登录历史
				LoginHistory longhistory = new LoginHistory();
				longhistory.setDtimestamp(new Date());
				longhistory.setCemail(email);
				longhistory.setCclientip(host);
				loginHistoryMapper.insertSelective(longhistory);
				  //2.保存登录状态
				loginService.setLoginContext(1, LoginContextFactory.initLC(disMember.getId()+"", disMember.getUserName(), email,disMember.getDistributionMode()+"",disMember.getComsumerType()));
			}
		} else {
			errorCode = "1";
			errorInfo = "会员激活失败";
		}
		Map<String, String> map = Maps.newHashMap();
		map.put("email", email);
		map.put("errorCode", errorCode);
		map.put("errorInfo", errorInfo);
		return map;
	}

	@Override
	public Map<String, String> login(JsonNode node, String host,Integer times) {
		String errorCode = "0";
		String errorInfo = "";
		Map<String, String> map = Maps.newHashMap();
		String email = node.get("email").asText();
		String passWord = node.get("passWord").asText();
		//登陆错误三次需要校验验证码
		if(times > 3) {
			String captcha = node.has("captcha") ? node.get("captcha").asText() : "";
			if(!captchaService.verify(captcha)) {
				map.put("errorCode", "3");
				map.put("errorInfo", "请输入正确的验证码");
				return map;
			}
		}
		DisMember disMember = new DisMember();
		disMember.setRoleId(2);
		disMember.setEmail(email);
		disMember = disMemberMapper.getMember(disMember);
		if (disMember != null) {
			if (!disMember.getPassWord().equals(MD5Util.MD5Encode(passWord, MD5Util.CHARSET_UTF_8))) {
				errorCode = "1";
				errorInfo = "用户名或密码错误";
			} else {
				if (!disMember.getIsActived()) {
					errorCode = "2";
					errorInfo = "用户尚未激活";
					map.put("email", email);
				} else {
					// 记录登录记录
					LoginHistory loginHistory = new LoginHistory();
					loginHistory.setDtimestamp(new Date());
					loginHistory.setCemail(email);
					loginHistory.setCclientip(host);
					loginHistoryMapper.insertSelective(loginHistory);
					
					map.put("id", String.valueOf(disMember.getId()));
					map.put("username", disMember.getUserName());
					map.put("email", disMember.getEmail());
					map.put("distributionmode", disMember.getDistributionMode() + "");
					map.put("distributionType", disMember.getComsumerType() + "");
					//add by xuse
					setSession(1,disMember);
				}				
			}
		} else {
			//如果不存在此用户，再去查询注册申请表
			Map<String, Object> applyParam = Maps.newHashMap();
			applyParam.put("account", email);
			List<DisApplyFile> files = null;
			List<DisRegisterApply> applys = disRegisterApplyMapper.getApplysByConditon(applyParam);
			if (applys.size() > 0 && applys.get(0).getPassWord().equals(passWord)) {//表示此账号已经被注册申请过
				for (DisRegisterApply apply : applys) {
					files = disApplyFileMapper.getFileByApplyId(apply.getId());
					for (DisApplyFile file : files){
						file.setTypeDesc(file.getType());
					}
					apply.setFiles(files);
					apply.setStatusDesc(apply.getStatus());
					apply.setCreateDateDesc(new DateTime(apply.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
				}
				map.put("errorCode", "4");
				map.put("data", Json.toJson(applys).toString());
				return map;
			}
			errorCode = "1";
			errorInfo = "用户名或密码错误";
		}
		if(("1".equals(errorCode) || "2".equals(errorCode)) && times >= 3) {
			map.put("times", "over");
		}
		map.put("errorCode", errorCode);
		map.put("errorInfo", errorInfo);
		return map;
	}
	
	private void setSession(int mark, DisMember disMember) {
		sessionService.set(mark + CookieTrackingFilter.getLongTermCookie(Context.current()),
				Json.toJson(disMember).toString());
	}

	@Override
	public Map<String, Object> loginWithoutPwd(String email, String phpLoginTokenParam, String host) {
		Map<String, Object> map = Maps.newHashMap();
		
		if(StringUtils.isEmpty(email) || StringUtils.isEmpty(phpLoginTokenParam)){
			map.put("errorCode", "1");
			map.put("errorInfo", "用户名或登录token为空");
			return map;
		}
		// 比较登录凭证
		if(!phpLoginTokenParam.equals(phpLoginTokenEncoded)){
			map.put("errorCode", "2");
			map.put("errorInfo", "登录token验证失败");
			return map;
		}
		
		DisMember disMember = new DisMember();
		disMember.setRoleId(2);
		disMember.setEmail(email);
		disMember = disMemberMapper.getMember(disMember);
		if(disMember==null || !disMember.getIsActived()){
			map.put("errorCode", "3");
			map.put("errorInfo", "登录失败，不存在此用户");
			return map;
		}
		
		// 记录登录记录
		LoginHistory loginHistory = new LoginHistory();
		loginHistory.setDtimestamp(new Date());
		loginHistory.setCemail(email);
		loginHistory.setCclientip(host);
		loginHistoryMapper.insertSelective(loginHistory);

		map.put("errorCode", "0");
		map.put("memberInfo", disMember);
		return map;
	}

	@Override
	public JsonNode updateInfo(MemberForm form) {
		Map<String, String> map = Maps.newHashMap();
		DisMember oldMember = new DisMember();
		oldMember = disMemberMapper.selectByPrimaryKey(form.getId());
		Logger.debug("Service updateInfo MemberForm-->" + Json.toJson(form).toString());
		if (oldMember == null){
			map.put("errorCode", "1");
			map.put("errorInfo", "更新失败，没有查询到指定用户");
			return Json.toJson(map);
		}
		
		DisMember newMember = new DisMember();
		BeanUtils.copyProperties(form, newMember);
		newMember.setEmail(oldMember.getEmail());
		Logger.debug("Service updateInfo disMember-->" + Json.toJson(newMember).toString());
		newMember.setLastUpdateDate(new Date());
		int row = update(newMember);
		Logger.debug("Service updateInfo row-->" + row);
		if (row != 1) {
			map.put("errorCode", "1");
			map.put("errorInfo", "更新失败");
			return Json.toJson(map);
		}
		
		if (form.getNickName() != null && 
				!form.getNickName().equals(oldMember.getNickName())) {//更改了昵称，需要同步销售单和采购单上的昵称
			DisMember member = new DisMember();
			member.setEmail(oldMember.getEmail());
			member.setNickName(form.getNickName());
			ebus.post(member);
		}
		map.put("errorCode", "0");
		map.put("errorInfo", "");
		return Json.toJson(map);
	}
	
	@Override
	public Map<String, String> getInfo(String email) {
		DisMember disMember = new DisMember();
		disMember.setRoleId(2);
		disMember.setEmail(email);
		disMember = disMemberMapper.getMember(disMember);
		Map<String, String> map = Maps.newHashMap();
		if (disMember != null) {
			map.put("id", String.valueOf(disMember.getId()));
			map.put("birthday", disMember.getBirthday());
			map.put("nickName", disMember.getNickName());
			map.put("realName", disMember.getRealName());
			map.put("gender", String.valueOf(disMember.getGender()));
			map.put("email", disMember.getEmail());
			map.put("tel", disMember.getTelphone());
			map.put("profile", disMember.getProfile());
			map.put("headImg", disMember.getHeadImg());

			//是否包邮 2017-5-27
			map.put("isPackageMail", String.valueOf(disMember.getIsPackageMail()));


			// 获取等级id
			Integer rankId = disMember.getRankId();
			// 如果用户的等级id为空则将默认折扣赋予用户 规避历史数据产生的错误
			if (rankId == null || rankId.equals(0)) {
				DisRank defaultRank = disRankMapper.getDefaultRank();
				rankId = defaultRank.getId();
				disMember.setRankId(rankId);
				update(disMember);
			}
			CustomerCredit confing = creditConfing.getCreditConfig(disMember);
			map.put("creditConfig", confing != null?Json.toJson(confing).toString():null);
			// 获取折扣（等级折扣/定制折扣）
			Double discount = null;
			// 已经定制折扣，则取定制折扣值，否则取等级折扣值
			if (disMember.getIsCustomized()) {
				discount = Double.valueOf(disMember.getCustomizeDiscount());
			} else {
				DisRank rank = disRankMapper.selectByPrimaryKey(rankId);
				discount = Double.valueOf(rank.getDiscount());
			}
			map.put("discount", utils.dismember.StringUtils.getString(discount, false));
			map.put("comsumerType", disMember.getComsumerType()+"");
			map.put("registerInviteCode", disMember.getRegisterInviteCode());
			map.put("distributionMode", disMember.getDistributionMode() + "");
			map.put("attributionType", disMember.getAttributionType()+"");
			String selfInviteCode = disMember.getSelfInviteCode();
			//如果用户自身的邀请码为空则更新值
			if (Strings.isNullOrEmpty(selfInviteCode)) {
				DisMember member = new DisMember();
				selfInviteCode = IDUtils.getUUID().substring(0, 6);
				member.setId(disMember.getId());
				member.setSelfInviteCode(selfInviteCode);
				int result = update(member);
				Logger.info("update inviteCode result:"+(result>0?"successful":"failure"));
			}
			map.put("selfInviteCode", selfInviteCode);
		}
		return map;
	}
	
	
	@Override
	public Map<String, Object> getCreditInfo(String email) {
		Map<String,Object> res = Maps.newHashMap();
		Map<String,String> map = getInfo(email);
		if(map.isEmpty()){
			res.put("suc", false);
			res.put("msg", "不存在该分销商，请重新填入分销商邮箱");
			return res;
		}
		
		res.put("suc", true);
		res.put("info", map);
		return res;
	}

	@Override
	public JsonNode sendEmailAgain(String email) {
		Map<String, Object> map = Maps.newHashMap();
		if (StringUtils.isBlank(email)) {
			map.put("errorCode", "1");
			map.put("errorInfo", "参数错误");
			return Json.toJson(map);
		}
		
		DisMember disMember = new DisMember();
		disMember.setRoleId(2);
		disMember.setEmail(email);
		disMember = disMemberMapper.getMember(disMember);
		if (disMember == null) {
			map.put("errorCode", "1");
			map.put("errorInfo", "不存在此分销商");
			return Json.toJson(map);
		}
		
		if (disMember.getIsActived()) {
			map.put("errorCode", "1");
			map.put("errorInfo", "帐户已经激活,无须发送激活邮件");
			return Json.toJson(map);
		}
		DisEmailVerify disEmailVerify = new DisEmailVerify();
		disEmailVerify.setCemail(email);
		disEmailVerify.setSendType(0);
		disEmailVerify = disEmailVerifyMapper.getVerify(disEmailVerify);
		if (disEmailVerify.getIdaynumber() >= 3) {
			map.put("errorCode", "1");
			map.put("errorInfo", "操作过于频繁，如果你依然未收到邮件，请于明日再进行操作！");
			return Json.toJson(map);
		}
		
		// 发送激活邮件
		String code = IDUtils.getUUID();// 验证码
		String url = HttpUtil.getHostUrl() +
				"/member/activedEmail?email=" + email + "&code=" + code;
		Logger.debug(">>>register>>url:>>>" + url);
		/** 发送邮件 **/
		// 邮件变量值
		Map<String, Object> objectmap = Maps.newHashMap();
		objectmap.put("url", url);
		objectmap.put("toemail", email);
		// 如果发送成功
		sendEamil(objectmap, Constant.EMAIL_SMTP, Constant.ACTIVATE, email);

		DisEmailVerify verify = new DisEmailVerify();
		verify.setCemail(email);
		verify.setBisending(true);
		verify.setCactivationcode(code);
		verify.setIdaynumber(disEmailVerify.getIdaynumber() + 1);
		Calendar calendar = Calendar.getInstance();
		verify.setDsenddate(calendar.getTime());
		verify.setDcreatedate(calendar.getTime());
		verify.setSendType(0);
		//注册激活邮件有效时间为1天
		calendar.add(Calendar.DATE, 1);
		verify.setDvaliddate(calendar.getTime());
		disEmailVerifyMapper.insertSelective(verify);
		map.put("errorCode", "0");
		map.put("errorInfo", "");
		return Json.toJson(map);
	}

	@Override
	public boolean verifyUser(String user) {
		DisMember disMember = new DisMember();
		// disMember.setRoleId(2);
		disMember.setEmail(user);
		disMember = disMemberMapper.getMember(disMember);
		return disMember != null && disMember.getId() != null ? true : false;
	}

	@Override
	public Map<String, String> adminLogin(Map<String, String> params) {
		Map<String, String> result = Maps.newHashMap();
		DisMember disMember = new DisMember();
		disMember.setEmail(params.get("user"));
		disMember = disMemberMapper.getMember(disMember);
		boolean flag = disMember != null && disMember.getId() != null && disMember.getRoleId() != null
				&& disMember.getRoleId() != 2;
		if(!flag){
			result.put("success", "false");
			result.put("message", "用户名或密码错误");
			return result;
		}
		if(disMember.getIsDisabled()){
			result.put("success", "false");
			result.put("message", "此账号禁用，登录失败！");
			return result;
		}

		DisRole role = disRoleMapper.selectByPrimaryKey(disMember.getRoleId());
		if (null == role || !role.getIsactive()) {
			result.put("success", "false");
			result.put("message", "该角色被禁用，请联系管理员。");
			return result;
		}
		
		String pw_md5 = MD5Util.MD5Encode(params.get("cipher"), MD5Util.CHARSET_UTF_8);
		if (!pw_md5.equals(disMember.getPassWord())) {
			result.put("success", "false");
			result.put("message", "用户名或密码错误");
			return result;
		}
		
		// 登录成功，记录登录记录
		LoginHistory loginHistory = new LoginHistory();
		loginHistory.setDtimestamp(new Date());
		loginHistory.setCemail(disMember.getEmail());
		loginHistory.setCclientip(params.get("host"));
		loginHistoryMapper.insertSelective(loginHistory);

		result.put("success", "true");
		setSession(2, disMember);
		result.put("id", String.valueOf(disMember.getId()));
		result.put("roleId", String.valueOf(disMember.getRoleId()));
		result.put("username", disMember.getUserName());
		result.put("email", disMember.getEmail());
		return result;
	}
	
	@Override
	public Map<String, Object> adminLoginWithoutPwd(String email, String phpLoginTokenParam, String host) {
		Map<String, Object> map = Maps.newHashMap();
		
		if(StringUtils.isEmpty(email) || StringUtils.isEmpty(phpLoginTokenParam)){
			map.put("errorCode", "1");
			map.put("errorInfo", "用户名或登录token为空");
			return map;
		}
		// 比较登录凭证
		if(!phpLoginTokenParam.equals(phpLoginTokenEncoded)){
			map.put("errorCode", "2");
			map.put("errorInfo", "登录token验证失败");
			return map;
		}
		
		DisMember disMember = new DisMember();
		disMember.setEmail(email);
		disMember = disMemberMapper.getMember(disMember);
		boolean flag = disMember != null && disMember.getId() != null && disMember.getRoleId() != null
				&& disMember.getRoleId() != 2;
		if(!flag){
			map.put("errorCode", "3");
			map.put("errorInfo", "登录失败，请联系管理员");
			return map;
		}
		
		DisRole role = disRoleMapper.selectByPrimaryKey(disMember.getRoleId());
		if (null == role || !role.getIsactive()) {
			map.put("errorCode", "4");
			map.put("errorInfo", "该角色被禁用，请联系管理员");
			return map;
		}
		
		// 登录成功，记录登录记录
		LoginHistory loginHistory = new LoginHistory();
		loginHistory.setDtimestamp(new Date());
		loginHistory.setCemail(disMember.getEmail());
		loginHistory.setCclientip(host);
		loginHistoryMapper.insertSelective(loginHistory);
		
		map.put("errorCode", "0");
		map.put("memberInfo", disMember);
		return map;
	}

	@Override
	public Page<AdminDto> getAdminsByPage(Map<String, Object> params) {
		int rows = disMemberMapper.getCountByPage(params);
		if(rows <= 0) {
			return new Page<AdminDto>((Integer) params.get("currPage"), (Integer) params.get("pageSize"), rows, null);
		}
		List<DisMember> members = disMemberMapper.getMembersByPage(params);
		List<DisRole> roles = disRoleMapper.getRolesByPage(null);
		Map<Integer, DisRole> roleMap = Maps.uniqueIndex(roles, r -> r.getId());
		List<AdminDto> dtos = Lists.newArrayList();
		for (DisMember member : members) {
			AdminDto dto = new AdminDto();
			dto.setId(member.getId());
			dto.setNick(member.getNickName());
			dto.setRealName(member.getRealName());
			dto.setLoginName(member.getEmail());
			dto.setBactived(member.getIsActived());
			dto.setRoleId(member.getRoleId());
			dto.setRole(roleMap.get(member.getRoleId()).getRoleName());
			dto.setCreateTime(new DateTime(member.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
			dto.setLogin(member.getLogin());// 最后登录时间
			dto.setTelphone(member.getTelphone());
			dto.setComsumerType(member.getComsumerType());
			dto.setEmail(member.getEmail());
			dto.setWorkNo(member.getWorkNo());
			dto.setRegisterInviteCode(member.getRegisterInviteCode());
			dto.setIsDisabled(member.getIsDisabled());
			dto.setUserCode(member.getUserCode());
			dto.setIsPackageMail(member.getIsPackageMail());
			String selfInviteCode = member.getSelfInviteCode();
			//如果用户自身的邀请码为空则更新值
			if (Strings.isNullOrEmpty(selfInviteCode)) {
				DisMember m = new DisMember();
				selfInviteCode = IDUtils.getUUID().substring(0, 6);
				m.setId(member.getId());
				m.setSelfInviteCode(selfInviteCode);
				int result = update(member);
				Logger.info("update inviteCode result:"+(result>0?"successful":"failure"));
			}
			dto.setSelfInviteCode(selfInviteCode);
			dto.setDistributionMode(member.getDistributionMode());
			dto.setDistributionModeDesc(member.getDistributionModeDesc());
			dto.setCreateUser(member.getCreateUser());
			dto.setIfAddPermision(member.getIfAddPermision());
			dto.setRegisterMan(member.getRegisterMan());
			dto.setIsBackRegister(member.getIsBackRegister());
			dto.setSalesmanErp(member.getSalesmanErp());
			dto.setAttributionType(member.getAttributionType());
			//如果是普通分销商
			if(member.getRoleId() == 2){
				try {
					DisAccount account =  disAccountMapper.getDisAccountByEmail(member.getEmail());
					dto.setIsFrozen(account!= null?account.isFrozen():null);
				} catch (Exception e) {
					Logger.error("查询异常", e);
				}
			}
			dtos.add(dto);
		}
		return new Page<AdminDto>((Integer) params.get("currPage"), (Integer) params.get("pageSize"), rows, dtos);
	}

	@Override
	public DisEmailVerify getEmailVerifySuccessRecord(Map<String, String> params) {
		DisEmailVerify disEmailVerify = new DisEmailVerify();
		disEmailVerify.setCactivationcode(params.get("d"));
		return disEmailVerifyMapper.getVerify(disEmailVerify);
	}

	@Override
	public Map<String, String> getAdminLoginHistory(String email) {
		Map<String, String> map = Maps.newHashMap();//返回值
		Map<String,Object> dmember = Maps.newHashMap();
		dmember.put("email", email);
		List<DisMember> relatedMember = disMemberMapper.relatedMember(dmember);
		if(CollectionUtils.isEmpty(relatedMember)) {
			map.put("recent", "暂无");
			map.put("number", "0");
			return map;
		}
		map.put("email", email);
		map.put("roleName", relatedMember.get(0).getRole());
		List<LoginHistory> loginHistorys = loginHistoryMapper.getRecentHistory(email);
		if (loginHistorys == null || loginHistorys.size() == 0) {
			map.put("recent", "暂无");
			map.put("number", "0");
			return map;
		}
		if (loginHistorys.size() == 1) {
			map.put("recent", new DateTime(loginHistorys.get(0).getDtimestamp()).toString("yyyy-MM-dd HH:mm"));
		} else {
			map.put("recent", new DateTime(loginHistorys.get(1).getDtimestamp()).toString("yyyy-MM-dd HH:mm"));
		}
		DateTime dateTime = new DateTime(loginHistorys.get(0).getDtimestamp());
		long millis = dateTime.minusMonths(1).getMillis();
		int loginNumber = 0;
		for (LoginHistory loginHistory : loginHistorys) {
			loginNumber += millis < loginHistory.getDtimestamp().getTime() ? 1 : 0;
		}
		map.put("number", String.valueOf(loginNumber));
		return map;
	}

	@Override
	public Map<String, Object> changePayPasswordByEmail(Map<String, String> params) {
		Map <String, Object> result = Maps.newHashMap();
		result.put("suc", false);
		result.put("msg", "未知错误");
		DisEmailVerify firstverify = new DisEmailVerify();
		String email = params.get("email");
		String code = IDUtils.getUUID();
		String url = HttpUtil.getUrl() + "/personal/change_pay_password.html?e=" + email
				+ "&c=" + code;
		Map<String, Object> objectmap = Maps.newHashMap();
		objectmap.put("url", url);
		objectmap.put("toemail", email);
		DisEmailVerify verify = new DisEmailVerify();
		verify.setCemail(email);
		verify.setSendType(2);//支付密码发送邮件类型
		verify = disEmailVerifyMapper.getVerify(verify);
		if (verify != null){
			String today = DateUtils.date2string(new Date(), DateUtils.FORMAT_DATE_DB);
			String verifyDay =  DateUtils.date2string(verify.getDcreatedate(), DateUtils.FORMAT_DATE_DB);
			Logger.info("now-----------> " + Json.toJson(today).toString());
			Logger.info("verifyDay------> " + Json.toJson(verifyDay).toString());
			Logger.info("次数---------->" + Json.toJson(verify.getIdaynumber()));
			if (verify.getIdaynumber() >= 3 && today.equals(verifyDay)){//表示是同一天
				result.put("suc", false);
				result.put("msg", "您操作过于频繁，请明天重试");
				return result;
			} else {
				if (today.equals(verifyDay)) {
					boolean success = this.sendEamil(objectmap, Constant.EMAIL_SMTP, Constant.RESET_PAY_PASSWORD , email);
					if(success){
						firstverify.setCemail(email);
						firstverify.setBisending(true);
						firstverify.setCactivationcode(code);
						firstverify.setIdaynumber(verify.getIdaynumber() + 1);//每天发送邮箱的次数
						DateTime dateTime = new DateTime();
						firstverify.setDsenddate(dateTime.toDate());
						firstverify.setDcreatedate(dateTime.toDate());
						//重置支付密码验证邮件有效时间为2小时
						dateTime = dateTime.plusHours(2);
						firstverify.setDvaliddate(dateTime.toDate());
						firstverify.setSendType(2);
						disEmailVerifyMapper.insertSelective(firstverify);
						result.put("suc", true);
						result.put("email", verify.getCemail());
						return result;
					}
				} else {
					//说明是第二天第一次发此类型邮件
					boolean success = this.sendEamil(objectmap, Constant.EMAIL_SMTP, Constant.RESET_PAY_PASSWORD , email);
					if(success){
						firstverify.setCemail(email);
						firstverify.setBisending(true);
						firstverify.setCactivationcode(code);
						firstverify.setIdaynumber(1);//每天发送邮箱的次数
						DateTime dateTime = new DateTime();
						firstverify.setDsenddate(dateTime.toDate());
						firstverify.setDcreatedate(dateTime.toDate());
						//重置支付密码验证邮件有效时间为2小时
						dateTime = dateTime.plusHours(2);
						firstverify.setDvaliddate(dateTime.toDate());
						firstverify.setSendType(2);
						disEmailVerifyMapper.insertSelective(firstverify);
						result.put("suc", true);
						result.put("email", firstverify.getCemail());
						return result;
					}
				}
			}
		} else {
			//说明是第一次发此类型邮件
			boolean success = this.sendEamil(objectmap, Constant.EMAIL_SMTP, Constant.RESET_PAY_PASSWORD , email);
			if(success){
				firstverify.setCemail(email);
				firstverify.setBisending(true);
				firstverify.setCactivationcode(code);
				firstverify.setIdaynumber(1);//每天发送邮箱的次数
				DateTime dateTime = new DateTime();
				firstverify.setDsenddate(dateTime.toDate());
				firstverify.setDcreatedate(dateTime.toDate());
				//重置支付密码验证邮件有效时间为2小时
				dateTime = dateTime.plusHours(2);
				firstverify.setDvaliddate(dateTime.toDate());
				firstverify.setSendType(2);
				disEmailVerifyMapper.insertSelective(firstverify);
				result.put("suc", true);
				result.put("email", firstverify.getCemail());
				return result;
			}
		}
		return result;
	}
	
	@Override
	public Map<String, Object> checkedPayPwdEmailHours(Map<String, String> params) {
		Map<String, Object> result = Maps.newHashMap();
		result.put("suc", 2);
		result.put("msg", "未知错误");
		//verify:当前邮件的url所对应的邮件对象
		DisEmailVerify verify = new DisEmailVerify();
		verify.setCemail(params.get("email"));
		verify.setCactivationcode(params.get("code"));
		verify.setSendType(2);
		verify = disEmailVerifyMapper.getVerify(verify);
		if(null == verify) {
			return result;
		}
		//newestEmail最新的邮件对象
		DisEmailVerify newestEmail = new DisEmailVerify();
		newestEmail.setSendType(2);
		newestEmail.setCemail(params.get("email"));
		newestEmail = disEmailVerifyMapper.getVerify(newestEmail);
		//判断当前邮件是不是最新邮件
		if(null != newestEmail && newestEmail.getDcreatedate().compareTo(verify.getDcreatedate()) > 0){//说明verify不是最新的
			result.put("suc", 5);
			result.put("msg", "该邮件不是最新邮件，请点击最新邮件里的链接");
			return result;
		}
		//确保邮件里的url不能被第二次点击
		DisEmailVerify disverify = new DisEmailVerify();
		disverify.setIid(verify.getIid());
		disverify.setCmark(StringUtils.isNotEmpty(verify.getCmark()) ? Integer.valueOf(verify.getCmark()) + 1 + "" : 1 + "");
		disEmailVerifyMapper.updateByPrimaryKeySelective(disverify);
		Logger.info("是否更新了----------->" + Json.toJson(disverify.getCmark()));
		if (verify != null){
			if(disverify.getCmark() != null && Integer.valueOf(disverify.getCmark()) > 1){
				result.put("suc", 4);
				result.put("msg", "此链接不能重复点击");
				return result;
			}
			Logger.info("有效日期--------->" + Json.toJson(verify.getDvaliddate()).toString());
			Logger.info("今日日期--------->" + Json.toJson(new Date()).toString());
			int check = verify.getDvaliddate().compareTo(new Date());
			if (check > 0){
				result.put("suc", 7);
				result.put("msg", "时间有效");
			} else {
				result.put("suc", 3);
				result.put("msg", "此链接已超时，请重新发送邮件");
			}
		} 
		return result;
	}

	@Override
	public boolean sendModifyCellphoneVerifyEmail(String email) {
		int row = 0;
		String code = IDUtils.getUUID();
		String url = HttpUtil.getUrl()+ "/personal/change_phone_new.html?e=" + email
				+ "&c=" + code;
		Map<String, Object> objectmap = Maps.newHashMap();
		objectmap.put("url", url);
		objectmap.put("toemail", email);
		// 如果发送成功
		boolean success = this.sendEamil(objectmap, Constant.EMAIL_SMTP, Constant.CHANGETEL, email);
		if (success) {
			DisEmailVerify verify = new DisEmailVerify();
			verify.setCemail(email);
			verify.setBisending(true);
			verify.setCactivationcode(code);
			verify.setIdaynumber(1);// 每天发送邮箱次数
			DateTime dateTime = new DateTime();
			verify.setDsenddate(dateTime.toDate());
			verify.setDcreatedate(dateTime.toDate());
			//修改手机号验证邮件有效时间为2小时
			dateTime = dateTime.plusHours(2);
			verify.setDvaliddate(dateTime.toDate());
			verify.setSendType(1);
			row = disEmailVerifyMapper.insertSelective(verify);
			Logger.info("sendModifyCellphoneVerifyEmail url--->" + url);
		}
		return row == 1;
	}

	@Override
	public boolean verifyEmailCode(String email, String code) {
		DisEmailVerify disEmailVerify = new DisEmailVerify();
		disEmailVerify.setCemail(email);
		disEmailVerify.setCactivationcode(code);
		DisEmailVerify dev = disEmailVerifyMapper.getVerify(disEmailVerify);
		if(dev != null && dev.getCactivationcode().equalsIgnoreCase(code)){
			Logger.info("dev----------------->"+dev.getCactivationcode());
			return true;
		}
		
		return false;
	}

	@Override
	public Map<String, Object> updateCipher(String email, Integer userId) {
		Map<String, Object> result = Maps.newHashMap();
		DisMember disMember = new DisMember();
		disMember.setEmail(email);
		disMember = disMemberMapper.getMember(disMember);
		DisMember user = new DisMember();
		user.setId(userId);
		user = disMemberMapper.getMember(user);
		user.setPassWord(MD5Util.MD5Encode(Constant.ADMINPWD, MD5Util.CHARSET_UTF_8));
		update(user);
		result.put("success", true);
		result.put("msg", "重置用户【" + user.getEmail() + "】的密码成功，初始密码为：aa8888");
		saveAdminRecord(email, user.getId(), "重置密码");
		return result;
	}

	@Override
	public boolean checkAdminUser(String email) {
		DisMember disMember = new DisMember();
		disMember.setEmail(email);
		disMember = disMemberMapper.getMember(disMember);
		return disMember.getRoleId() == 1;
	}

	@Override
	public Map<String, Object> insertUser(DisMember member,String operateFlag,String admin) {
		Map<String, Object> map = Maps.newHashMap();
		if (member != null) {
			Logger.info(this.getClass().getName() + " insertUser ifAddPermision===" + member.getIfAddPermision());
			//校验输入的用户信息 by huchuyin 2016-9-19
			if("user".equals(operateFlag)) {
				String errorInfo = this.validateMember(member);
				if(StringUtils.isNotBlank(errorInfo)) {
					map.put("success", false);
					map.put("msg", errorInfo);
					return map;
				}
			}
			if (member.getId() != null) {
				Logger.info(this.getClass().getName()+" insertUser Update");
				DisMember oldMember = new DisMember();
				oldMember.setId(member.getId());
				oldMember = disMemberMapper.getMember(oldMember);
				UserRankHistory history = new UserRankHistory(); 
				String operateDesc = "";
				if(member.getDistributionMode()!=null&&!oldMember.getDistributionMode().equals(member.getDistributionMode())){
					DisMode oldModel=modeMapper.selectByPrimaryKey(oldMember.getDistributionMode());
                	DisMode newModel=modeMapper.selectByPrimaryKey(member.getDistributionMode());
                	operateDesc+="模式由 "+oldModel.getDisMode()+" → "+newModel.getDisMode();
                	// add by lzl 修改分销商模式要通知M站
                	ShopSite shopSite = new ShopSite();	 
               		shopSite.setDisemail(oldMember.getEmail());
               		shopSite.setDistributionMode(member.getDistributionMode());
               		changeSiteInfo(shopSite);
				}
				if(member.getComsumerType()!=null&&!oldMember.getComsumerType().equals(member.getComsumerType())){
					CustomerType oldType = customerTypeMapper.selectByPrimaryKey(oldMember.getComsumerType());
					CustomerType newType = customerTypeMapper.selectByPrimaryKey(member.getComsumerType());
					operateDesc +="分销商类型由 "+oldType.getCustomerName()+"→ "+newType.getCustomerName();
				}
				if(StringUtils.isNotEmpty(operateDesc)){
					history.setEmail(oldMember.getEmail());
					history.setCreateTime(new Date());
					history.setOperator(admin);
					history.setOperateDesc(operateDesc);
					userRankHistoryMapper.insertSelective(history);
				}
				update(member);
				if("user".equals(operateFlag)) {
					//若修改的用户有附加权限，则新增附加权限表数据 by huchuyin 2016-9-14
					if(member.getIfAddPermision()) {
						//删除原有绑定的权限
						int count = disMemberMenuMapper.delMemberMenuByMem(member.getId());
						Logger.info(this.getClass().getName()+" insertUser[deleteMemMenuCount]==="+count);
						//保存附加权限
						if(StringUtils.isNotBlank(member.getMenuIds())) {
							Logger.info(this.getClass().getName()+" insertUser menuIds==="+member.getMenuIds());
							this.saveMemMenu(member);
						}
					} else {
						//若修改的用户没有附加权限，则删除附加权限表数据
						int count = disMemberMenuMapper.delMemberMenuByMem(member.getId());
						Logger.info(this.getClass().getName()+" insertUser[deleteMemMenuCount]==="+count);
					}
				}
				//End by huchuyin 2016-9-14
				map.put("success", true);
				map.put("msg", "修改用户成功");
			} else {
				Logger.info(this.getClass().getName()+" insertUser Add");
				//用户新增时，验证邮箱唯一性
				Integer count = disMemberMapper.getMemCountByEmail(member.getEmail());
				Logger.info(this.getClass().getName()+" insertUser email:"+member.getEmail()+" count:"+count);
				if(count > 0) {
					map.put("success", false);
					map.put("msg", "用户名已存在，无法添加用户！");
					return map;
				}
				member.setPassWord(MD5Util.MD5Encode(Constant.ADMINPWD, MD5Util.CHARSET_UTF_8));
				member.setIsActived(true);
				member.setUserName(IDUtils.getUUID());
				//增加创建人 by huchuyin 2016-9-13
				LoginContext lc = loginService.getLoginContext(Constant.LOGIN_FROM_MARK_BACK);
				member.setCreateUser(lc.getEmail());
				save(member);
				//若新增的用户有附加权限，则新增附加权限表数据
				if(member.getIfAddPermision()) {
					if(StringUtils.isNotBlank(member.getMenuIds())) {
						Logger.info(this.getClass().getName()+" insertUser menuIds==="+member.getMenuIds());
						this.saveMemMenu(member);
					}
				}
				map.put("success", true);
				map.put("msg", "新增用户【" + member.getEmail() + "】成功,初始密码为:" + Constant.ADMINPWD);
			}
		}
		return map;
	}

	@Override
	public Map<String, Object> deleteUser(Integer id) {
		Logger.info("删除用户：" + id);
		Map<String, Object> map = Maps.newHashMap();
		int line = 0;
		//校验若用户有与员工关联，则不能删除
		Integer count = disMemberMapper.getMemberSalesCount(id);
		if(count > 0) {
			map.put("success", false);
			map.put("msg","该用户存在员工关联，不可删除！");
			return map;
		}
		//查询用户信息
		DisMember disMember = disMemberMapper.selectByPrimaryKey(id);
		if(disMember != null) {
			//删除用户与子用户
			line = disMemberMapper.delMemberAndChildMem(disMember);
		}
		//删除用户后，若用户有附加权限，则将附加权限一并删除 by huchuyin 2016-9-14
		map.put("success", true);
		map.put("msg", line == 0 ? "删除用户失败" : "删除用户成功");
		Logger.info("删除用户个数： " + line + "," + map.get("msg"));
		return map;
	}

	@Override
	public Map<String, Object> resetPwd(Map<String, String> params) {
		Map<String, Object> result = Maps.newHashMap();
		DisMember member = null;
		Logger.info("修改Password参数：" + params.toString());
		if (params.containsKey("email")) {
			member = new DisMember();
			member.setEmail(params.get("email"));
			member = disMemberMapper.getMember(member);
			//密码必须为6-20个字符，且至少包含数字、大写、小写字母等三种或以上字符 by huchuyin 2016-10-6
			if(StringUtils.isEmpty(params.get("newcode"))
					|| (params.get("newcode").length() < 6 || params.get("newcode").length() >20)
					|| !utils.dismember.StringUtils.containsLetterNum(params.get("newcode"))) {
				result.put("success", true);
				result.put("code", 5);
				return result;
			}
			//End by huchuyin 2016-10-6
			if (params.get("newcode") == "" || !params.get("surecode").equals(params.get("newcode"))) {
				result.put("success", true);
				result.put("code", 2);// 两次输入密码不一致
				return result;
			}
			String oldcode = MD5Util.MD5Encode(params.get("oldcode"), MD5Util.CHARSET_UTF_8);
			if (!oldcode.equals(member.getPassWord())) {
				result.put("success", true);
				result.put("code", 3);// 旧密码错误
				return result;
			}
			member.setPassWord(MD5Util.MD5Encode(params.get("newcode"), MD5Util.CHARSET_UTF_8));
			update(member);
			result.put("success", true);
			result.put("code", 4);// 修改成功
			Logger.info(member.getEmail() + "密码修改成功。");
		}
		return result;
	}

	@Override
	public Map<String, Object> getCustomerServiceAccount(String email) {   
		Map<String, Object> result = Maps.newHashMap();
		if (StringUtils.isEmpty(email)) {
			result.put("account", "");
			return result;
		}
		DisMember member = new DisMember();
		member.setEmail(email);
		member = disMemberMapper.getMember(member);
		if (member != null) {
			if (member.getSalesmanErp() != null) {//说明在组织架构这个分销商被这个员工所关联
				result.put("account", member.getSalesmanErp());
				return result;
			}
			if(member.getComsumerType() == 3) {
				result.put("account", member.getErpAccount() == null ? "" : member.getErpAccount());
				return result;
			}

			// 根据注册邀请码查询
			if (StringUtils.isEmpty(member.getRegisterInviteCode())) {
				result.put("account", "");
				return result;
			}
			getCustAccount(member.getSelfInviteCode(), member.getRegisterInviteCode(), result);
		}
		return result;
	}

	/**
	 * @param selfCode   A的私有码
	 * @param registerInviteCode  A的注册码
	 * @param custAccount
	 */
	private void getCustAccount(String selfCode, String registerInviteCode, Map<String, Object> custAccount) {
		// 根据注册邀请码查询
		if (!StringUtils.isEmpty(registerInviteCode)) {
			DisMember member = new DisMember();
			member.setSelfInviteCode(registerInviteCode);
			member = disMemberMapper.getMember(member);
			if (member != null) {
				String registerCode = member.getRegisterInviteCode();// B的注册码
				String mselfCode = member.getSelfInviteCode();// B的私有码
				// 死循环 A和B互相邀请
				if (selfCode.equals(registerCode) && registerInviteCode.equals(mselfCode)) {
					Logger.info("用户互相邀请，需要联系客户重新填写邀请码:" + member.getEmail());
					custAccount.put("account", "");
				} else {
					if (StringUtils.isEmpty(registerCode) || registerInviteCode.equals(registerCode) || member.getComsumerType() == 3) {
						custAccount.put("account", member.getErpAccount() == null ? "" : member.getErpAccount());
					} else {
						getCustAccount(mselfCode, registerCode, custAccount);
					}
				}
			}
		}
	}

	/**
	 * 注册
	 */
	@Override
	public Map<String, Object> checktelRegister(Map<String, String> params) {
		Map<String, Object> result = Maps.newHashMap();
		DisMember member1 = null;
		member1 = new DisMember();
		member1.setEmail(params.get("email"));
		DisMember member2 = disMemberMapper.getMember(member1);
		if(member2 != null){
			result.put("suc",false);
			result.put("msg","账号已存在");
			return result;
		}
		
		result.put("suc",true);
		result.put("msg", "该账号可以注册");
		return result;
	}

	@Override
	public JsonNode telRegister(Map<String, String> params) {
		String errorCode = "0";
		String errorInfo = "";
		Map<String, String> map = Maps.newHashMap();
		
		boolean paramsCheck = params.containsKey("email") && params.containsKey("passWord") 
				&& params.containsKey("smsc") && params.containsKey("registerInviteCode");
		
		if (!paramsCheck){
			map.put("errorCode", "1");
			map.put("errorInfo", "注册失败,参数错误");
			Logger.info(">>>register>>>map>>>>" + map.toString());
			return Json.toJson(map);
		}
		
		// 校验邀请码
		String registerInviteCode = params.get("registerInviteCode").trim();
		Map<String, Object> checkRegisterInviteCodeResult = checkRegisterInviteCode(registerInviteCode);
		if(!((boolean) checkRegisterInviteCodeResult.get("suc"))){
			map.put("errorCode", "1");
			map.put("errorInfo", checkRegisterInviteCodeResult.get("msg").toString());
			Logger.info(">>>register>>>map>>>>" + map.toString());
			return Json.toJson(map);
		}
		// 是否是vip邀请码
		boolean isVipInviteCode = (boolean) checkRegisterInviteCodeResult.get("isVipInviteCode");
		
		String email = params.get("email");
		String passWord = params.get("passWord");
		//密码必须为6-20个字符，且至少包含数字、大写、小写字母等三种或以上字符 by huchuyin 2016-10-6
		if(StringUtils.isEmpty(passWord)
				|| (passWord.length() < 6 || passWord.length() > 20)
				|| !utils.dismember.StringUtils.containsLetterNum(passWord)) {
			map.put("errorCode", "1");
			map.put("errorInfo", "密码必须为6-20个字符，且至少包含数字、大写、小写字母等三种或以上字符！");
			Logger.info(">>>register>>>map>>>>" + map.toString());
			return Json.toJson(map);
		}
		
		//校验手机号码与验证码
		String smsc = params.get("smsc");
		String resultStr = this.checkSmsCode(smsc, email, Constant.SMS_TYPE_PHONE_REG);
		Map<String,Object> checkMap = Json.fromJson(Json.parse(resultStr),Map.class);
		if(!(Boolean) checkMap.get("suc")) {
			map.put("errorCode", "1");
			map.put("errorInfo", (String) checkMap.get("msg"));
			return (Json.toJson(map));
		}
		
		// 查看邮箱是否已经注册
		DisMember member = new DisMember();
		member.setRoleId(2);
		member.setEmail(email);
		member = disMemberMapper.getMember(member);
		Map<String,Object> applyMap = Maps.newHashMap();
		applyMap.put("account", email);
		List<DisRegisterApply> applys = disRegisterApplyMapper.getApplysByConditon(applyMap);
		if ((member != null && StringUtils.isNotBlank(member.getPassWord())) || (applys != null && applys.size() > 0)) {
			errorCode = "1";
			errorInfo = "该号码已经被注册或申请";
			map.put("email", email);
			map.put("errorCode", errorCode);
			map.put("errorInfo", errorInfo);
			Logger.info(">>>register>>>map>>>>" + map.toString());
			return Json.toJson(map);
		}
		
		try {
			// 保存用户信息
			DisMember disMember = new DisMember();
			disMember.setPassWord(MD5Util.MD5Encode(passWord, MD5Util.CHARSET_UTF_8));
			disMember.setUserName(IDUtils.getUUID());
			disMember.setEmail(email);
			disMember.setTelphone(email);
			disMember.setCreateDate(new Date());
			disMember.setIsActived(true);//手机注册默认激活
			disMember.setRoleId(2);// 2为分销商角色，1为管理员角色
			disMember.setRegisterMan(email);
			// 电商 和 vip 模式 默认为线上
			disMember.setAttributionType(Constant.USER_ATTR_TYPE_ONLINE);
			if(isVipInviteCode){
				// 8位的是vip邀请码
				VipInviteCode code = vipMapper.selectBycode(registerInviteCode);
				//如果有vip邀请码默认是VIP模式
				if(code !=null){
					disMember.setDistributionMode(5);
					code.setInUse(true);
					code.setCount(code.getCount()+1);
					code.setUpdateDate(new Date());
					vipMapper.updateByPrimaryKeySelective(code);
				}
				// vip邀请码注册的使用默认的erp账号
				disMember.setSalesmanErp("10176");
			}else{
				// 根据邀请码所属的分销商来递归查询erp账户
				disMember.setSalesmanErp(getCustomerServiceAccount((String) checkRegisterInviteCodeResult.get("email")).get("account").toString());
			}
			
			disMember.setRegisterInviteCode(registerInviteCode);//设置他人的注册邀请码，由客户手动输入
			disMember.setSelfInviteCode(IDUtils.getUUID().substring(0, 6));//设置自身邀请码，由系统随机生成

			DisRank rank = disRankMapper.getDefaultRank();
			if (rank != null) {
				disMember.setRankId(rank.getId());
			} else {
				Logger.info("activedEmail default rank isn't set.");
			}
			int id = save(disMember);
			//设置客户编码
			String userCode = this.generateCustomerCode(0, 0, 0, 1, id);
			disMember.setUserCode(userCode);
			disMemberMapper.updateByPrimaryKeySelective(disMember);

			//生产apkVersion内容
			this.saveApkVersionData(email);
			
			// 生成帐户
			DisAccount disAccount = new DisAccount();
			disAccount.setBalance(new BigDecimal(0));
			disAccount.setFrozen(false);
			disAccount.setEmail(disMember.getEmail());
			disAccountMapper.insertSelective(disAccount);
			Map<String,Object> postParams = Maps.newHashMap();
			postParams.put("email",email);
			//绑定购物车
			String res = HttpUtil.httpPost(Json.toJson(postParams).toString(), HttpUtil.getHostUrl()+"/cart/addDisCart");	
			Logger.info("========绑定购物车：[{}]======="+res);
			//手机注册成功之后，自动登录
			DisMember loginMember = null;
			loginMember = disMemberMapper.getMember(disMember);
			if (loginMember == null ) {
				errorCode = "1";
				errorInfo = "注册成功，自动登录失败";
			} else {
				//1.记录登录历史
				LoginHistory longhistory = new LoginHistory();
				longhistory.setDtimestamp(new Date());
				longhistory.setCemail(email);
				longhistory.setCclientip(params.get("host"));
				loginHistoryMapper.insertSelective(longhistory);
				// 2.保存登录状态
				loginService.setLoginContext(1,
						LoginContextFactory.initLC(loginMember.getId() + "", loginMember.getUserName(), email,
								loginMember.getDistributionMode() + "", loginMember.getComsumerType()));
				setSession(1, loginMember);
			}
		} catch (Exception e) {
			errorCode = "1";
			errorInfo = "注册失败,异常";
			Logger.error(e + "");
		}
		map.put("email", email);
		map.put("errorCode", errorCode);
		map.put("errorInfo", errorInfo);
		Logger.info(">>>register>>>map>>>>" + map.toString());
		return Json.toJson(map);
	}
	/**
	 * 生产apkVersion内容
	 * @param email
	 */
	private void saveApkVersionData(String email) {
		String string = Play.application().configuration().getConfig("apk").getString("apkdownloadurl");
		ApkVersion registApk=new ApkVersion();
		//获取当前tongtao最新版本的实例
		ApkVersion tongtaoMaxVersion= apkVersionMapper.getMaxApkVersion("tongtao-android");
		registApk.setAppName(tongtaoMaxVersion.getAppName());
		registApk.setChannelName("account_"+email);
		registApk.setCode(tongtaoMaxVersion.getCode());
		registApk.setDescription(tongtaoMaxVersion.getDescription());
		registApk.setForceUpdate(tongtaoMaxVersion.getForceUpdate());
		registApk.setSize(tongtaoMaxVersion.getSize());
		registApk.setUrl(string+email);
		registApk.setVersion(tongtaoMaxVersion.getVersion());
		registApk.setCreateTime(new Date());
		registApk.setApkName(tongtaoMaxVersion.getApkName());
		apkVersionMapper.insertSelective(registApk);
	}

	/**
	 * 校验邀请码
	 * @param registerInviteCode
	 * @return
	 */
	private Map<String, Object> checkRegisterInviteCode(
			String registerInviteCode) {
		Map<String, Object> map = Maps.newHashMap();
		// 检查邀请码是否存在
		if (StringUtils.isEmpty(registerInviteCode)) {
			map.put("suc", false);
			map.put("msg", "请输入邀请码");
			return map;
		}
		
		DisMember querySelfInviteCodeParam = new DisMember();
		querySelfInviteCodeParam.setSelfInviteCode(registerInviteCode);
		DisMember disMember = disMemberMapper.getMember(querySelfInviteCodeParam);
		if (disMember == null) {// 为null说明可能是vip邀请码
			VipInviteCode vipCode = vipMapper.selectBycode(registerInviteCode);
			if (vipCode == null) {
				map.put("suc", false);
				map.put("msg", "邀请码[" + registerInviteCode + "]不存在");
				return map;
			}

			if (vipCode.getInUse()) {// 被使用
				map.put("suc", false);
				map.put("msg", "邀请码[" + registerInviteCode + "]已被使用");
				return map;
			}

			map.put("suc", true);
			map.put("isVipInviteCode", true);
			return map;
		}
		
		// 普通邀请码
		map.put("suc", true);
		map.put("email", disMember.getEmail());
		map.put("isVipInviteCode", false);
		return map;
	}

	@Override
	public String backstageRegister(String param) {
		JsonNode node = Json.parse(param);
		ObjectNode result = Json.newObject();
		String email = node.get("telphone").asText();
		String passWord = node.get("password").asText();
		// 查看邮箱是否已经注册
		DisMember member = new DisMember();
		member.setRoleId(2);
		member.setEmail(email);
		member = disMemberMapper.getMember(member);
		Map<String,Object> applyMap = Maps.newHashMap();
		applyMap.put("account", email);
		List<DisRegisterApply> applys = disRegisterApplyMapper.getApplysByConditon(applyMap);
		if ((member != null && StringUtils.isNotBlank(member.getPassWord())) || (applys != null && applys.size() > 0)) {
			result.put("suc", false);
			result.put("msg", "该用户已被注册");
			return result.toString();
		}
		// 保存用户信息
		DisMember disMember = new DisMember();
		disMember.setPassWord(MD5Util
				.MD5Encode(passWord, MD5Util.CHARSET_UTF_8));
		disMember.setUserName(IDUtils.getUUID());
		disMember.setEmail(email);
		disMember.setTelphone(email);
		disMember.setCreateDate(new Date());
		disMember.setIsActived(true);// 手机注册默认激活
		disMember.setRoleId(2);// 2为分销商角色，1为管理员角色
		disMember.setSalesmanErp(node.get("salesmanErp").asText());
		disMember.setIsBackRegister(true);
		disMember.setRegisterMan(node.get("registerMan").asText());
		Configuration config = Play.application().configuration()
				.getConfig("invitecode");
		String registerInviteCode = config == null ? "4bd284" : config
				.getString("code");
		disMember.setRegisterInviteCode(registerInviteCode);// 设置他人的注册邀请码，由客户手动输入
		disMember.setSelfInviteCode(IDUtils.getUUID().substring(0, 6));// 设置自身邀请码，由系统随机生成
		DisRank rank = disRankMapper.getDefaultRank();
		if (rank != null) {
			disMember.setRankId(rank.getId());
		} else {
			Logger.info("activedEmail default rank isn't set.");
		}
		Integer distributionMode = JsonCaseUtil.jsonToInteger(node.get("distributionMode")) != null?
				JsonCaseUtil.jsonToInteger(node.get("distributionMode")):1;
		//电商 或者 vip 默认线上		
		Integer attributionType = distributionMode == 1|| distributionMode == 5?
				Constant.USER_ATTR_TYPE_ONLINE:Constant.USER_ATTR_TYPE_OFFLINE;
		disMember.setAttributionType(attributionType);
		disMember.setDistributionMode(distributionMode);
		int id = save(disMember);
		//设置用户编码
		String userCode = this.generateCustomerCode(0, 0, 0, distributionMode, id);
		disMember.setUserCode(userCode);
		disMemberMapper.updateByPrimaryKeySelective(disMember);
		//生产apkVersion内容
		this.saveApkVersionData(email);
		// 生成帐户
		DisAccount disAccount = new DisAccount();
		disAccount.setBalance(new BigDecimal(0));
		disAccount.setFrozen(false);
		disAccount.setEmail(disMember.getEmail());
		int accountFlag = disAccountMapper.insertSelective(disAccount);
		Logger.info("绑定账号结果------>" + Json.toJson(accountFlag).toString());
		Map<String, Object> postParams = Maps.newHashMap();
		postParams.put("email", email);
		// 员工关联分销商
		DisSalesmanMember salesman = new DisSalesmanMember();
		salesman.setMemberid(disMember.getId());
		salesman.setSalesmanid(node.get("salesmanId").asInt());
		int salesmanMemberFlag = disSalesmanMemberMapper
				.insertSelective(salesman);
		Logger.info("员工分销商关联结果--------->"
				+ Json.toJson(salesmanMemberFlag).toString());
/******************************* add by zbc 发票信息 start ***************************************/
		//删除已有发票信息
		invoiceMapper.deleteByEmail(email);
		//并新增发票信息
		Invoice ivo = new Invoice();
		ivo.setEmail(email);
		ivo.setInvoiceBank(JsonCaseUtil.jsonToString(node.get("invoiceBank")));
		ivo.setInvoiceBankAccount(JsonCaseUtil.jsonToString(node.get("invoiceBankAccount")));
		ivo.setInvoiceCompanyAddr(JsonCaseUtil.jsonToString(node.get("invoiceCompanyAddr")));
		ivo.setInvoiceTaxNumber(JsonCaseUtil.jsonToString(node.get("invoiceTaxNumber")));
		ivo.setInvoiceTel(JsonCaseUtil.jsonToString(node.get("invoiceTel")));
		ivo.setInvoiceTitle(JsonCaseUtil.jsonToString(node.get("invoiceTitle")));
		invoiceMapper.insertSelective(ivo);
/******************************* add by zbc 发票信息 end ***************************************/
		// 手机注册成功之后给用户发送短信
		try {
			EmailAccount emailAccountParam = new EmailAccount();
			emailAccountParam.setCtype(Constant.SEND_MSG);
			EmailAccount emailAccount = emailMapper.select(emailAccountParam);
			if (emailAccount == null) {
				Logger.error("--------------------->backstageRegister:短信发送失败！获取不到短信配置！");
				result.put("suc", false);
				result.put("msg", "短信发送失败");
				return result.toString();
			}

//			SMSManager.send("【通淘国际】已为您成功注册分销账号！访问https://www.tomtop.com.cn来管理您的分销平台。用户名："
//					+ email + "  密码：" + passWord, email);
			EmailTemplate template = templateMapper.select(Constant.SUCCESS_REGISTRATION);
			if (null == template) {
				Logger.error("-------------->backstageRegister:未配置短信模板：" + new Date());
				result.put("suc", false);
				result.put("msg", "短信发送失败");
				return result.toString();
			}

			String content = template.getCcontent();
			Logger.info("content----------->"+Json.toJson(content));
			if (!content.contains("email") || !content.contains("passWord")) {
				Logger.error("-------------->backstageRegister:短信模板配置有误：" + new Date());
				result.put("suc", false);
				result.put("msg", "短信发送失败");
				return result.toString();
			}
			content = content.replaceAll("email", email).replace("{{", "").replace("}}", "");
			content = content.replaceAll("passWord", passWord).replace("{{", "").replace("}}", "");
			SMSManager.send(content, emailAccount, email);

			result.put("suc", true);
			result.put("msg", "注册用户成功");
		} catch (Exception e) {
			Logger.error("短信发送失败！");
			Logger.error(e.getMessage());
			result.put("suc", false);
			result.put("msg", "短信发送失败");
		}
		return result.toString();
	}

	@Override
	public Map<String, Object> checkTelFindPWD(Map<String, String> params) {
		Map<String,Object> result = Maps.newHashMap();
		String email = params.get("email");
		// 查看账号是否存在
		DisMember member1 = new DisMember();
		member1.setRoleId(2);
		member1.setEmail(email);
		DisMember member2 = disMemberMapper.getMember(member1);
		if( member2 == null){
			result.put("suc",false);
			result.put("msg","账号不存在!");
			return result;
		}
		
		result.put("suc", true);
		result.put("email", email);
		result.put("code", params.get("captcha"));
		return result;
	}

	@Override
	public Map<String, Object> sendCheckCode(Map<String, String> params) {
		Map<String,Object> result = Maps.newHashMap();
		String email = params.get("email");
		String cell = params.get("cell");
		String smsc = params.get("smsc");
		// 查看账号是否存在
		DisMember member1 = new DisMember();
		member1.setRoleId(2);
		member1.setEmail(email);
		DisMember member2 = disMemberMapper.getMember(member1);
		if(member2 == null ){
			result.put("suc",false);
			result.put("msg","账号不存在!");
			return result;
		}
		
		if(!member2.getTelphone().equals(cell)){
			result.put("suc",false);
			result.put("msg","手机号码错误!");
			return result;
		}

		result.put("suc", true);
		//加密邮箱与验证码，手机号码 by huchuyin 2016-10-10
		Logger.info(this.getClass().getName() + " sendCheckCode email===" + email + " smsc===" + smsc);
		String enEmail = AESOperator.getInstance().encrypt(email);
		String enSmsc = AESOperator.getInstance().encrypt(smsc);
		String enCell = AESOperator.getInstance().encrypt(cell);
		//生成UUID
		String code = IDUtils.getUUID();
		Logger.info(this.getClass().getName() + " sendCheckCode code UUID===" + code);
		result.put("code", code);
		result.put("email", enEmail);
		result.put("smsc", enSmsc);
		result.put("cell", enCell);
		return result;
	}

	@Override
	public List<DisMode> getMode() {
		return modeMapper.selectAll();
	}

	@Override
	public DisMember getMember(String email) {
		DisMember disMember = new DisMember();
		disMember.setEmail(email);
		return disMemberMapper.getMember(disMember);
	}
	
	/**
	 * 保存用户附加权限
	 * @param member
	 * @author huchuyin
	 * @date 2016年9月14日 上午9:15:56
	 */
	private void saveMemMenu(DisMember member) {
		List<DisMemberMenu> memberMenuList = new ArrayList<DisMemberMenu>();
		//将前台获取的权限ID分割字符串
		String[] menuIds = member.getMenuIds().split(",");
		DisMemberMenu memMenu = null;
		//循环权限ID，保存到列表中以便批量保存到数据库
		for(String menuId:menuIds) {
			memMenu = new DisMemberMenu();
			memMenu.setMemberId(member.getId());
			memMenu.setMenuId(Integer.valueOf(menuId));
			memberMenuList.add(memMenu);
		}
		Logger.info(this.getClass().getName()+" saveMemMenu[memberMenuList]==="+memberMenuList.size());
		//批量保存附加权限
		int count = disMemberMenuMapper.batchAddMemMenu(memberMenuList);
		Logger.info(this.getClass().getName() + " saveMemMenu[addMemberMenuCount]===" + count);
	}
	
	@Override
	public DisMember getMemberById(Integer memberId) {
		return disMemberMapper.selectByPrimaryKey(memberId);
	}
	
	/**
	 * 校验用户信息
	 * @param disMember
	 * @return
	 * @author huchuyin
	 * @date 2016年9月19日 下午6:26:41
	 */
	private String validateMember(DisMember disMember) {
		String errorInfo = "";
		//用户名
		if(disMember.getId() == null) {
			if(StringUtils.isBlank(disMember.getEmail())) {
				errorInfo += "用户名不能为空<br/>";
			}
		}
		//工号
		if(StringUtils.isBlank(disMember.getWorkNo())) {
			errorInfo += "工号不能为空<br/>";
		}
		//电话
		if(StringUtils.isBlank(disMember.getTelphone())) {
			errorInfo += "电话不能为空<br/>";
		} else if(!disMember.getTelphone().matches(Constant.REX_TELPHONE)) {
			errorInfo += "电话号码由11位数字组成，首位为“1”，第二位为“3”、“4”、“5”、“8”，其他位为0-9！<br />";
		}
		//用户角色
		if(disMember.getRoleId() == null || disMember.getRoleId() == 0) {
			errorInfo += "用户角色不能为空<br/>";
		}
		//是否有附加权限
		if(disMember.getIfAddPermision() == null) {
			errorInfo += "是否有附加权限不能为空<br/>";
		} else {
			if(disMember.getIfAddPermision() && StringUtils.isBlank(disMember.getMenuIds())) {
				errorInfo += "附加权限不能为空<br/>";
			}
		}
		return errorInfo;
	}

	@Override
	public String sendMessageForTel(String params) {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("suc", true);
		JsonNode node = Json.parse(params);
		//手机号码
		String tel = node.path("tel").asText();
		//发送短信类型
		int types = node.path("types").asInt();
		//查询发送短信记录
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("phone", tel);
		paramMap.put("types", types);
        List<PhoneVerify> lists = phoneVerifyMapper.getRecords(paramMap);
        //若发送短信记录当天已超过三次，则不可以再次发送
        if(lists != null && lists.size() >= 3){
        	resultMap.put("suc", false);
        	resultMap.put("msg", "此手机号当天发送短信已超过三次，不允许再发送！");
			return Json.toJson(resultMap).toString();
        }
        String code = IDUtils.randomNumber(6);
        try {

			EmailAccount emailAccountParam = new EmailAccount();
			emailAccountParam.setCtype(Constant.SEND_MSG);
			EmailAccount emailAccount = emailMapper.select(emailAccountParam);
			if (emailAccount == null) {
				Logger.error("--------------------->sendMessageForTel:短信发送失败！获取不到短信配置！");
				resultMap.put("suc", false);
				resultMap.put("msg", "短信发送失败");
				return Json.toJson(resultMap).toString();
			}

//			SMSManager.send("【通淘国际】亲爱的用户，您的验证码是"+code + "，5分钟内有效。", tel);


			EmailTemplate template = templateMapper.select(Constant.CODE);
			if (null == template) {
				Logger.error("-------------->sendMessageForTel:未配置短信模板：" + new Date());
				resultMap.put("suc", false);
				resultMap.put("msg", "短信发送失败");
				return Json.toJson(resultMap).toString();
			}

			String content = template.getCcontent();
			Logger.info("content----------->"+Json.toJson(content));
			if (!content.contains("code")) {
				Logger.error("-------------->sendMessageForTel:短信模板配置有误：" + new Date());
				resultMap.put("suc", false);
				resultMap.put("msg", "短信发送失败");
				return Json.toJson(resultMap).toString();
			}
//			String msg = "【通淘国际】亲爱的用户，您的验证码是"+code + "，5分钟内有效。";

			content = content.replaceAll("code", code).replace("{{", "").replace("}}", "");
			SMSManager.send(content, emailAccount, tel);

			resultMap.put("msg", "短信已发送，请查收");
		} catch (Exception e) {
			resultMap.put("suc", false);
			resultMap.put("msg", "短信发送失败");
			Logger.error(this.getClass().getName()+" sendMessageForTel 短信发送失败！");
			e.printStackTrace();
		}
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);
        PhoneVerify verify = new PhoneVerify();
        verify.setCode(code);
        verify.setPhone(tel);
        verify.setTypes(types);
        verify.setValidate(calendar.getTime());
        verify.setCreateDate(new Date());
        phoneVerifyMapper.insert(verify);
		return Json.toJson(resultMap).toString();
	}

	@Override
	public String checkSmsCode(String smsc, String tel, int types) {
		Logger.info(this.getClass().getName()+" checkSmsCode smsc==="+smsc+" tel==="+tel+" types==="+types);
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("suc",true);
		Map<String,Object> map = new HashMap<String,Object>();
        map.put("phone", tel);
        map.put("types", types);
        PhoneVerify verify = phoneVerifyMapper.getRecord(map);
        if(verify == null) {
        	resultMap.put("suc",false);
        	resultMap.put("msg","短信验证码输入错误！");
        	return Json.toJson(resultMap).toString();
        }
        if(new Date().getTime() > verify.getValidate().getTime()){
        	resultMap.put("suc",false);
        	resultMap.put("msg","验证码已失效，请重新发送短信获取验证码！");
        	return Json.toJson(resultMap).toString();
        } 
        if(!smsc.equals(verify.getCode())) {
        	resultMap.put("suc",false);
        	resultMap.put("msg","短信验证码输入错误！");
        	return Json.toJson(resultMap).toString();
        }
        
        Logger.info(this.getClass().getName()+" checkSmsCode 短信验证码输入成功");
		resultMap.put("suc", true);
		resultMap.put("msg", "短信验证成功");
		return Json.toJson(resultMap).toString();
	}
	
	/**
	 * 更新分销商的站点信息
	 * @param shopSite
	 */
	public void changeSiteInfo(ShopSite shopSite){
		JsonNode params = Json.toJson(shopSite);
		try {
			Configuration config = null;
			String url = "";
			String res = "";
			config = Play.application().configuration().getConfig("msite");
			url = config.getString("host") + "/service/bbc/updateSiteInfo";
			res = utils.dismember.HttpUtil.httpPost(params.toString(), url);
			Logger.info("移动端推送结果---------》" + Json.toJson(res));
			JsonNode response = Json.parse(res);
			if (response != null && response.get("suc").asBoolean() == true) {
				 shopSiteMapper.updateByEmail(shopSite);
			}
		} catch (Exception e) {
			Logger.error("推送网站信息到M站失败(更新)", e);			
		}
	}

	/**
	 * 前台申请注册成为经销商
	 */
	@Override
	public String registerApply(Map<String, String[]> params, List<FilePart> files) {
		ObjectNode result = Json.newObject();
		String account = setString(params.get("account"));
		DisRegisterApply apply = new DisRegisterApply();
		apply.setAccount(account);
		apply.setPassWord(setString(params.get("password")));
		apply.setStatus(0);
		apply.setCreateDate(new Date());
		apply.setApplyRemark(setString(params.get("remark")));
		apply.setRegisterMan(account);
		apply.setRegisterInviteCode(setString(params.get("invitationCode")));

		if (params.containsKey("provinceCode")) {
			if(setString(params.get("provinceCode")) != null) {
				apply.setProvinceCode(Integer.valueOf(setString(params.get("provinceCode"))));
			}
		}

		if (params.containsKey("cityCode")) {
			if(setString(params.get("cityCode")) != null) {
				apply.setCityCode(Integer.valueOf(setString(params.get("cityCode"))));
			}
		}

		if (params.containsKey("areaCode")) {
			if(setString(params.get("areaCode")) != null) {
				apply.setAreaCode(Integer.valueOf(setString(params.get("areaCode"))));
			}
		}
		apply.setDistributionMode(2);
		result = handleRegisterApply(apply, files);
		return result.toString();
	}
	
	private String setString(String[] strings) {
		return strings != null && strings.length > 0 ? "".equals(strings[0])?null:strings[0] : null;
	}

	/**
	 * 得到所有的注册申请记录
	 */
	@SuppressWarnings("deprecation")
	@Override
	public String getRegisterApplys(String param) {
		ObjectNode result = Json.newObject();
		JsonNode node = Json.parse(param);
		Map<String, Object> map = Maps.newHashMap();
		Integer currPage = node.has("currPage") ? Integer.valueOf(node.get("currPage").asInt()) : 1;
		Integer pageSize = node.has("pageSize") ? Integer.valueOf(node.get("pageSize").asInt()) : 10;
		try {
			if (node.has("sregdate") && node.get("sregdate")!=null && 
					utils.dismember.StringUtils.isNotBlankOrNull(node.get("sregdate").asText())) {
				String sregdate = node.get("sregdate").asText();
				Calendar ca = Calendar.getInstance();
				ca.setTime(DateUtils.string2date(sregdate,DateUtils.FORMAT_DATE_PAGE));
				map.put("sregdate", DateUtils.date2string(ca.getTime(),DateUtils.FORMAT_DATE_PAGE));
			}
			if (node.has("eregdate") && node.get("eregdate")!=null && 
					utils.dismember.StringUtils.isNotBlankOrNull(node.get("eregdate").asText())) {
				String sregdate = node.get("eregdate").asText();
				Calendar ca = Calendar.getInstance();
				ca.setTime(DateUtils.string2date(sregdate,DateUtils.FORMAT_DATE_PAGE));
				map.put("eregdate", DateUtils.date2string(ca.getTime(),DateUtils.FORMAT_DATE_PAGE));
			}
			if (node.has("sapplydate") && node.get("sapplydate")!=null && 
					utils.dismember.StringUtils.isNotBlankOrNull(node.get("sapplydate").asText())) {
				String sregdate = node.get("sapplydate").asText();
				Calendar ca = Calendar.getInstance();
				ca.setTime(DateUtils.string2date(sregdate,DateUtils.FORMAT_DATE_PAGE));
				map.put("sapplydate", DateUtils.date2string(ca.getTime(),DateUtils.FORMAT_DATE_PAGE));
			}
			if (node.has("eapplydate") && node.get("eapplydate")!=null && 
					utils.dismember.StringUtils.isNotBlankOrNull(node.get("eapplydate").asText())) {
				String sregdate = node.get("eapplydate").asText();
				Calendar ca = Calendar.getInstance();
				ca.setTime(DateUtils.string2date(sregdate,DateUtils.FORMAT_DATE_PAGE));
				map.put("eapplydate", DateUtils.date2string(ca.getTime(),DateUtils.FORMAT_DATE_PAGE));
			}
			map.put("currPage", currPage);
			map.put("pageSize", pageSize);
			map.put("isbackRegister", node.has("isbackRegister") ? node.get("isbackRegister").asBoolean() : null);
			map.put("status", node.has("status") ? node.get("status").asInt() : null);
			map.put("search", node.has("search") ? node.get("search").asText() : null);
			map.put("sidx",utils.dismember.StringUtils.getStringParam(node, "sidx", null));
			map.put("sord",utils.dismember.StringUtils.getStringParam(node, "sord", null));
			List<DisRegisterApply> applys = disRegisterApplyMapper.getApplysByConditon(map);
			for (DisRegisterApply apply : applys) {
				apply.setCreateDateDesc(new DateTime(apply.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
				apply.setStatusDesc(apply.getStatus());
				if (apply.getRegisterDate() != null) {
					apply.setRegisterDateDesc(new DateTime(apply.getRegisterDate()).toString("yyyy-MM-dd HH:mm"));
				}
			}
			int rows = disRegisterApplyMapper.getCountByConditon(map);
			result.put("suc", true);
			result.put("page", Json.toJson(new Page<DisRegisterApply>(currPage, pageSize, rows, applys)));
			return result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("EXCEPTION", e);
			result.put("suc", false);
			result.put("msg", "查询注册申请列表失败");
			return result.toString();
		}
	}

	/**
	 * 根据id得到注册申请的详情
	 */
	@SuppressWarnings("deprecation")
	@Override
	public String getDetail(Integer id) {
		ObjectNode result = Json.newObject();
		DisRegisterApply apply = disRegisterApplyMapper.selectByPrimaryKey(id);
		if (apply == null) {
			result.put("suc", false);
			result.put("msg", "没有查询到指定详情");
			return result.toString();
		}
		apply.setStatusDesc(apply.getStatus());
		apply.setCreateDateDesc(new DateTime(apply.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
		if (apply.getRegisterDate() != null) {
			apply.setRegisterDateDesc(new DateTime(apply.getRegisterDate()).toString("yyyy-MM-dd HH:mm"));
		}
		if (apply.getUpdateDate() != null) {
			apply.setUpdateDateDesc(new DateTime(apply.getUpdateDate()).toString("yyyy-MM-dd HH:mm"));
		}
		List<DisApplyFile> files = disApplyFileMapper.getFileByApplyId(apply.getId());
		for (DisApplyFile file : files) {
			file.setTypeDesc(file.getType());
		}
		apply.setFiles(files);
		result.put("suc", true);
		result.put("data", Json.toJson(apply));
		Invoice ivo  = invoiceMapper.selectByEmail(apply.getAccount());
		if(ivo != null){
			result.put("ivo", Json.toJson(ivo));	
		}
		return result.toString();
	}

	/**
	 * 处理注册申请的审核(审核通过，审核不通过)
	 */
	@Override
	public ResultDto<?> auditApply(String node,String auditMan) {
		try {
			JsonNode param = Json.parse(node);
			Integer id = JsonCaseUtil.jsonToInteger(param.get("id"));
			DisRegisterApply apply = disRegisterApplyMapper.selectByPrimaryKey(id);
			if(apply == null){
				return new ResultDto<>(false,"注册申请不存在");
			}
			Integer status = JsonCaseUtil.jsonToInteger(param.get("status"));
			String reason = JsonCaseUtil.jsonToString(param.get("reason"));
			Integer attributionType = JsonCaseUtil.jsonToInteger(param.get("attributionType"));
			Integer salesmanId = JsonCaseUtil.jsonToInteger(param.get("salesmanId"));
			apply.setStatus(status);
			apply.setAuditRemark(JsonCaseUtil.jsonToString(param.get("remark")));
			apply.setUpdateDate(new Date());
			apply.setAuditMan(auditMan);
			apply.setAuditReason(reason);
			apply.setSalesmanId(salesmanId);
			boolean  flag;
			if(status == 2){
				if(attributionType == null||Lists.newArrayList(1,2).indexOf(attributionType) == -1){
					return new ResultDto<>(false,"请选择用户归属");
				}
				if(salesmanId == null){	
					return new ResultDto<>(false,"请选择业务员");
				}
				ResultDto<?> auditRes = auditPass(apply,attributionType);
				if(auditRes.getCode() != 100){
					return auditRes;
				}
				//审核通过
				apply.setRegisterDate(new Date());
				flag = disRegisterApplyMapper.updateByPrimaryKeySelective(apply)>0;
				return new ResultDto<>(flag, "审核"+(flag?"成功":"失败"));
			}else if(status == 1){
				if(reason == null ){
					return new ResultDto<>(false,"请选择审核理由");
				}
				flag = disRegisterApplyMapper.updateByPrimaryKeySelective(apply)>0;
				return new ResultDto<>(flag, "审核"+(flag?"成功":"失败"));
			}else{
				return new ResultDto<>(false,"审核状态错误");
			}
		} catch (Exception e) {
			Logger.info("注册审核异常:{}",e);
			return new ResultDto<>(false,"注册审核异常");
		}
	}
	
	/**
	 * 注册申请通过---注册一个经销商
	 * @param result
	 */
	public ResultDto<?> auditPass (DisRegisterApply apply,Integer attributionType){
		String email = apply.getAccount();
		String passWord = apply.getPassWord();
		// 查看邮箱是否已经注册
		DisMember member = new DisMember();
		member.setRoleId(2);
		member.setEmail(email);
		member = disMemberMapper.getMember(member);
		if (member != null && StringUtils.isNotBlank(member.getPassWord())) {
			return new  ResultDto<>(false, "该用户已被注册");
		}
		// 保存用户信息
		DisMember disMember = new DisMember();
		//add by zbc 设置用户归属
		disMember.setAttributionType(attributionType);
		disMember.setPassWord(MD5Util
				.MD5Encode(passWord, MD5Util.CHARSET_UTF_8));
		disMember.setUserName(IDUtils.getUUID());
		disMember.setEmail(email);
		disMember.setTelphone(email);
		disMember.setCreateDate(new Date());
		disMember.setIsActived(true);// 手机注册默认激活
		disMember.setRoleId(2);// 2为分销商角色，1为管理员角色
		disMember.setIsBackRegister(apply.getIsBackRegister());
		disMember.setRegisterMan(apply.getRegisterMan());
		disMember.setDistributionMode(apply.getDistributionMode());
		String registerInviteCode = apply.getRegisterInviteCode();
		if (StringUtils.isBlank(registerInviteCode)) {
			Configuration config = Play.application().configuration()
					.getConfig("invitecode");
			registerInviteCode = config == null ? "4bd284" : config
					.getString("code");
		}
		disMember.setRegisterInviteCode(registerInviteCode);// 设置他人的注册邀请码，由客户手动输入
		disMember.setSelfInviteCode(IDUtils.getUUID().substring(0, 6));// 设置自身邀请码，由系统随机生成
		DisRank rank = disRankMapper.getDefaultRank();
		if (rank != null) {
			disMember.setRankId(rank.getId());
		} else {
			Logger.info("activedEmail default rank isn't set.");
		}
		DisSalesman salesman = disSalesmanMapper.selectByPrimaryKey(apply
				.getSalesmanId());
		if (salesman != null) {
			disMember.setSalesmanErp(salesman.getErp());
		} else {
			return new  ResultDto<>(false, "没有查询到指定员工,审核失败");
		}
		int id = save(disMember);
		//生成客户编码
		if(apply.getProvinceCode()!= null && apply.getCityCode() != null && apply.getAreaCode() != null && disMember.getDistributionMode() != null){
			String userCode = this.generateCustomerCode(apply.getProvinceCode(), apply.getCityCode(), apply.getAreaCode(), disMember.getDistributionMode(),id);
			disMember.setUserCode(userCode);
		}
		disMemberMapper.updateByPrimaryKeySelective(disMember);

		// 生成帐户
		DisAccount disAccount = new DisAccount();
		disAccount.setBalance(new BigDecimal(0));
		disAccount.setFrozen(false);
		disAccount.setEmail(disMember.getEmail());
		disAccount.setPayPass(MD5Util
				.MD5Encode(passWord, MD5Util.CHARSET_UTF_8));
		int accountFlag = disAccountMapper.insertSelective(disAccount);
		Logger.info("绑定账号结果------>" + Json.toJson(accountFlag).toString());
		Map<String, Object> postParams = Maps.newHashMap();
		postParams.put("email", email);
		// 关联员工
		DisSalesmanMember salesmanMember = new DisSalesmanMember();
		salesmanMember.setMemberid(disMember.getId());
		salesmanMember.setSalesmanid(salesman.getId());
		disSalesmanMemberMapper.insertSelective(salesmanMember);
		ResultDto<?> result = new ResultDto<>(true, "注册成功");
		// 手机注册成功之后给用户发送短信
		try {
			//2017-5-15 14:30:44如果后台注册不发送短信
			if(!apply.getIsBackRegister()) {
				EmailAccount emailAccountParam = new EmailAccount();
				emailAccountParam.setCtype(Constant.SEND_MSG);
				EmailAccount emailAccount = emailMapper.select(emailAccountParam);
				if (emailAccount == null) {
					Logger.error("------------------>auditPass:短信发送失败！获取不到短信配置！");
					return result;
				}
				EmailTemplate template = templateMapper.select(Constant.SUCCESS_REGISTRATION);
				if (null == template) {
					Logger.error("-------------->auditPass:未配置短信模板：" + new Date());
					return result;
				}
				String content = template.getCcontent();
				Logger.info("content----------->"+Json.toJson(content));
				if (!content.contains("email") || !content.contains("passWord")) {
					Logger.error("-------------->auditPass:短信模板配置有误：" + new Date());
					return result;
				}
				content = content.replaceAll("email", email).replace("{{", "").replace("}}", "");
				content = content.replaceAll("passWord", passWord).replace("{{", "").replace("}}", "");
				SMSManager.send(content, emailAccount, email);
			}
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		return result;
	}

	@Override
	public File getAuditFile(Integer id) {
		return new File(disApplyFileMapper.selectByPrimaryKey(id).getUrl());
	}

	/**
	 * 注册申请待审核状态变为电商模式的分销商
	 */
	@Override
	public String becomeOrdinaryUser(Integer id, String host) {
		ObjectNode result = Json.newObject();
		DisRegisterApply apply = disRegisterApplyMapper.selectByPrimaryKey(id);
		if(apply==null){
			result.put("suc", false);
			result.put("msg", "没有查询到申请信息");
			return result.toString();
		}
		
		String email = apply.getAccount();
		DisMember member = new DisMember();
		member.setRoleId(2);
		member.setEmail(email);
		member = disMemberMapper.getMember(member);
		if (member != null && StringUtils.isNotBlank(member.getPassWord())) {
			result.put("suc", false);
			result.put("msg", "该用户已被注册");
			return result.toString();
		}
		
		// 保存用户信息
		String passWord = apply.getPassWord();
		DisMember disMember = new DisMember();
		disMember.setPassWord(MD5Util
				.MD5Encode(passWord, MD5Util.CHARSET_UTF_8));
		disMember.setUserName(IDUtils.getUUID());
		disMember.setEmail(email);
		disMember.setTelphone(email);
		disMember.setCreateDate(new Date());
		disMember.setIsActived(true);// 手机注册默认激活
		disMember.setRoleId(2);// 2为分销商角色，1为管理员角色
		disMember.setIsBackRegister(false);
		disMember.setRegisterMan(email);
		//生成电商分销商  默认线上
		disMember.setAttributionType(Constant.USER_ATTR_TYPE_ONLINE);
		String registerInviteCode = apply.getRegisterInviteCode();
		if (StringUtils.isBlank(registerInviteCode)) {
			Configuration config = Play.application().configuration()
					.getConfig("invitecode");
			registerInviteCode = config == null ? "4bd284" : config
					.getString("code");
		}
		disMember.setRegisterInviteCode(registerInviteCode);// 设置他人的注册邀请码，由客户手动输入
		disMember.setSelfInviteCode(IDUtils.getUUID().substring(0, 6));// 设置自身邀请码，由系统随机生成
		DisRank rank = disRankMapper.getDefaultRank();
		if (rank != null) {
			disMember.setRankId(rank.getId());
		} else {
			Logger.info("activedEmail default rank isn't set.");
		}
		save(disMember);
		// 生成帐户
		DisAccount disAccount = new DisAccount();
		disAccount.setBalance(new BigDecimal(0));
		disAccount.setFrozen(false);
		disAccount.setEmail(disMember.getEmail());
		int accountFlag = disAccountMapper.insertSelective(disAccount);
		Logger.info("绑定账号结果------>" + Json.toJson(accountFlag).toString());
		Map<String, Object> postParams = Maps.newHashMap();
		postParams.put("email", email);
		// 绑定购物车
		String res = HttpUtil.httpPost(Json.toJson(postParams).toString(),
				HttpUtil.getHostUrl() + "/cart/addDisCart");
		Logger.info("========绑定购物车：[{}]=======" + res);
		// 改待审核状态为已取消
		DisRegisterApply newApply = new DisRegisterApply();
		newApply.setId(apply.getId());
		newApply.setUpdateDate(new Date());
		newApply.setStatus(3);
		disRegisterApplyMapper.updateByPrimaryKeySelective(newApply);
		// 手机注册成功之后，自动登录
		DisMember loginMember = null;
		loginMember = disMemberMapper.getMember(disMember);
		if (loginMember == null) {
			result.put("suc", false);
			result.put("msg", "注册成功,自动登录失败");
		} else {
			// 1.记录登录历史
			LoginHistory longhistory = new LoginHistory();
			longhistory.setDtimestamp(new Date());
			longhistory.setCemail(email);
			longhistory.setCclientip(host);
			loginHistoryMapper.insertSelective(longhistory);
			// 2.保存登录状态
			loginService.setLoginContext(1,
					LoginContextFactory.initLC(loginMember.getId() + "", loginMember.getUserName(), email,
							disMember.getDistributionMode() + "", disMember.getComsumerType()));
			setSession(1, loginMember);
			result.put("suc", true);
			result.put("msg", "注册成为普通分销商成功");
		}
		return result.toString();
	}

	
	/**
	 * 后台注册申请成为经销商
	 */
	@Override
	public String backRegisterApply(Map<String, String[]> params, List<FilePart> files, String email) {
		ObjectNode result = Json.newObject();
		DisRegisterApply apply = new DisRegisterApply();
		apply.setAccount(setString(params.get("account")));
		apply.setPassWord(setString(params.get("password")));
		apply.setStatus(0);
		apply.setCreateDate(new Date());
		if (params.get("salesmanId") != null) {
			apply.setSalesmanId(Integer.valueOf(setString(params.get("salesmanId"))));
		}
		apply.setRegisterMan(email);
		apply.setApplyRemark(setString(params.get("remark")));
		apply.setIsBackRegister(true);

		if (params.containsKey("provinceCode")) {
			if(setString(params.get("provinceCode")) != null) {
				apply.setProvinceCode(Integer.valueOf(setString(params.get("provinceCode"))));
			}
		}

		if (params.containsKey("cityCode")) {
			if(setString(params.get("cityCode")) != null) {
				apply.setCityCode(Integer.valueOf(setString(params.get("cityCode"))));
			}
		}

		if (params.containsKey("areaCode")) {
			if(setString(params.get("areaCode")) != null) {
				apply.setAreaCode(Integer.valueOf(setString(params.get("areaCode"))));
			}
		}

		if (params.containsKey("distributionMode")) {
			if(setString(params.get("distributionMode")) != null) {
				apply.setDistributionMode(Integer.valueOf(setString(params.get("distributionMode"))));
			}
		}

		//处理注册和文件
		result = handleRegisterApply(apply, files);
		if(result.get("suc").asBoolean()){
			invoiceMapper.deleteByEmail(apply.getAccount());
			Invoice ivo = new Invoice();
			ivo.setEmail(apply.getAccount());
			ivo.setInvoiceBank(setString(params.get("invoiceBank")));
			ivo.setInvoiceBankAccount(setString(params.get("invoiceBankAccount")));
			ivo.setInvoiceCompanyAddr(setString(params.get("invoiceCompanyAddr")));
			ivo.setInvoiceTaxNumber(setString(params.get("invoiceTaxNumber")));
			ivo.setInvoiceTel(setString(params.get("invoiceTel")));
			ivo.setInvoiceTitle(setString(params.get("invoiceTitle")));
			invoiceMapper.insertSelective(ivo);
		}
		return result.toString();
	}
	
	//处理文件和注册申请的工具方法
	public ObjectNode handleRegisterApply(DisRegisterApply apply, List<FilePart> files){
		ObjectNode result = Json.newObject();
		if(!(null != apply.getIsBackRegister()&&
				apply.getIsBackRegister())){
			// 校验邀请码
			String registerInviteCode = apply.getRegisterInviteCode();
			if (StringUtils.isEmpty(registerInviteCode)) {
				result.put("suc", false);
				result.put("msg", "请输入邀请码");
				return result;
			}
		}
		String account = apply.getAccount();
		//先查询member表看该用户是否被注册过
		DisMember dismember = new DisMember();
		dismember.setRoleId(2);
		dismember.setEmail(account);
		dismember = disMemberMapper.getMember(dismember);
		if (dismember != null) {
			result.put("suc", false);
			result.put("msg", "该号码已经被注册或申请");
			return result;
		}
		
		Map<String,Object> applyMap = Maps.newHashMap();
		applyMap.put("account", account);
		applyMap.put("status", -1);
		//查询该账户下是否存在待审核或审核通过的记录
		List<DisRegisterApply> applys = disRegisterApplyMapper.getApplysByConditon(applyMap);
		if (applys != null && applys.size() > 0) {
			result.put("suc", false);
			result.put("msg", "该号码已经被注册或申请");
			return result;
		}
		
		int flag = disRegisterApplyMapper.insertSelective(apply);
		if (flag == 0) {
			result.put("suc", false);
			result.put("msg", "注册申请失败");
			return result;
		}
		
		try {
			if (utils.dismember.StringUtils.isBlankOrNull(filePath)) {
				Configuration config = Play.application().configuration().getConfig("b2bSPA");
				filePath = config.getString("imagePath");
			}
			DisApplyFile applyFile;
			List<DisApplyFile> list = new ArrayList<DisApplyFile>();
			File origin, folder, target;
			for (FilePart part : files) {
				applyFile = new DisApplyFile();
				applyFile.setCreateDate(new Date());
				applyFile.setName(part.getFilename());
				applyFile.setType(part.getKey());
				applyFile.setApplyId(apply.getId());
				origin = part.getFile();
				folder = new File(filePath + File.separator + account);
				if (!folder.exists()) {
					folder.mkdirs();
				}
				target = new File(filePath + File.separator + account + File.separator +part.getFilename());
				target.createNewFile();
				Files.copy(origin, target);
				applyFile.setUrl(target.getAbsolutePath());
				list.add(applyFile);
			}
			int fileFlag = disApplyFileMapper.batchInsert(list);
			if (fileFlag == 0) {
				result.put("suc", false);
				result.put("msg", "文件上传失败");
				return result;
			}
			result.put("suc", true);
			result.put("msg", "申请成功");
			return result;
		} catch (IOException e) {
			Logger.error(e.getMessage());
			result.put("suc", false);
			result.put("msg", "文件上传失败");
			return result;
		}
	}

	@Override
	public String modifyApplyFiles(Map<String, String[]> params, List<FilePart> files, String email) {
		ObjectNode result = Json.newObject();
		String account = setString(params.get("tel"));
		Integer applyId = Integer.valueOf(setString(params.get("applyId")));
		String operationDesc =  email;
		try {
			if (files.size() > 0) {
				operationDesc += "修改附件：";
				if (utils.dismember.StringUtils.isBlankOrNull(filePath)) {
					Configuration config = Play.application().configuration().getConfig("b2bSPA");
					filePath = config.getString("imagePath");
				}
				DisApplyFile applyFile;
				List<DisApplyFile> list = new ArrayList<DisApplyFile>();
				File origin, folder, target;
				for (FilePart part : files) {
					applyFile = new DisApplyFile();
					applyFile.setUpdateDate(new Date());
					applyFile.setName(part.getFilename());
					applyFile.setType(part.getKey());
					applyFile.setApplyId(applyId);
					applyFile.setTypeDesc(part.getKey());
					origin = part.getFile();
					folder = new File(filePath + File.separator + account);
					if (!folder.exists()) {
						folder.mkdirs();
					}
					target = new File(filePath + File.separator + account + File.separator +part.getFilename());
					target.createNewFile();
					Files.copy(origin, target);
					applyFile.setUrl(target.getAbsolutePath());
					operationDesc += applyFile.getTypeDesc()+",";
					list.add(applyFile);
				}
				int fileFlag = disApplyFileMapper.batchUpdate(list);
				if (fileFlag == 0) {
					result.put("suc", false);
					result.put("msg", "文件修改失败");
					return result.toString();
				}
				operationDesc = operationDesc.substring(0,operationDesc.length() - 1)+"</br>";
			}
			// 修改备注
			String remark = setString(params.get("remark"));
			DisRegisterApply apply = disRegisterApplyMapper.selectByPrimaryKey(applyId);
			apply.setApplyRemark(remark);
			disRegisterApplyMapper.updateByPrimaryKeySelective(apply);
			operationDesc += "修改备注："+(remark != null?remark:"空")+"</br>";
			//修改发票信息
			Invoice ivo = invoiceMapper.selectByEmail(account);
			//标记是否新增 或更新
			boolean inst = ivo == null;
			if(inst){
				ivo = new Invoice();
				ivo.setEmail(account);
			}
			ivo.setInvoiceTaxNumber(setString(params.get("invoiceTaxNumber")));
			ivo.setInvoiceBank(setString(params.get("invoiceBank")));
			ivo.setInvoiceBankAccount(setString(params.get("invoiceBankAccount")));
			ivo.setInvoiceCompanyAddr(setString(params.get("invoiceCompanyAddr")));
			ivo.setInvoiceTel(setString(params.get("invoiceTel")));
			ivo.setInvoiceTitle(setString(params.get("invoiceTitle")));
			boolean modify = inst?invoiceMapper.insertSelective(ivo)>0:invoiceMapper.updateByPrimaryKey(ivo)>0;
			if(modify){
				operationDesc += "修改银行开户银行："+(ivo.getInvoiceBank() != null ?ivo.getInvoiceBank():"空")+"</br>";
				operationDesc += "修改发票抬头："+(ivo.getInvoiceTitle() != null ?ivo.getInvoiceTitle():"空")+"</br>";
				operationDesc += "修改纳税号："+(ivo.getInvoiceTaxNumber() != null ?ivo.getInvoiceTaxNumber():"空")+"</br>";
				operationDesc += "修改联系电话："+(ivo.getInvoiceTel() != null ?ivo.getInvoiceTel():"空")+"</br>";
				operationDesc += "修改公司地址："+(ivo.getInvoiceCompanyAddr() != null ?ivo.getInvoiceCompanyAddr():"空");
			};
			//记录文件操作日志
			FileOperationRecord record = new FileOperationRecord();
			record.setApplyId(applyId);
			record.setOpdesc(operationDesc.substring(0,operationDesc.length() - 1));
			record.setOperateTime(new Date());
			record.setOperator(email);
			fileOperationRecordMapper.insertSelective(record);
			result.put("suc", true);
			result.put("msg", "文件修改成功");
			return result.toString();
		} catch (IOException e) {
			Logger.error(e.getMessage());
			result.put("suc", false);
			result.put("msg", "文件修改失败");
			return result.toString();
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public String getApplyFileHistory(Integer applyId) {
		ObjectNode result = Json.newObject();
		List<FileOperationRecord> records = fileOperationRecordMapper.selectByApplyId(applyId);
		if (records != null && records.size() > 0) {
			for (FileOperationRecord record : records) {
				record.setOperateTimeDesc(new DateTime(record.getOperateTime()).toString("yyyy-MM-dd HH:mm:dd"));
			}
		}
	    result.put("suc", true);
	    result.put("data", Json.toJson(records));
		return result.toString();
	}
	
	@Override
	public DisMember getDismember(String disEmail) {
		DisMember param = new DisMember();
		param.setEmail(disEmail);
        param.setRoleId(2);//分销商
        DisMember disMember = disMemberMapper.getMember(param);
		return disMember;
	}

	@Override
	public int save(DisMember member) {
		disMemberMapper.insertSelective(member);
		return member.getId();
	}

	@Override
	public int update(DisMember member) {
		return disMemberMapper.updateByPrimaryKeySelective(member);
	}

	@Override
	public String getClientCode() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.MINUTE, 10);
		String clientStr = "BBC" + calendar.getTimeInMillis() + "BBC";
		return AESOperator.getInstance().encrypt(clientStr);
	}

	@Override
	public boolean checkSign(String sign) {
		if(StringUtils.isEmpty(sign)) {
			return false;
		}
		String target = AESOperator.getInstance().decrypt(sign);
		long markTime = Long.parseLong(target.replaceAll("BBC", ""));
		if(markTime < System.currentTimeMillis()) {
			return false;
		}
		return true;
	}

	@Override
	public ResultDto<?> disableUser(String email, String operator) {
		try {
			DisMember disMember = new DisMember();
			disMember.setEmail(email);
			disMember = disMemberMapper.getMember(disMember);
			//查不到用户，或者用户角色为分销商时，不能进行该操作
			if(disMember == null|| disMember.getRoleId() == null || disMember.getRoleId() == 2){
				return ResultDto.newIns().suc(false).msg("该后台用户不存在");
			}
			if(disMember.getRoleId() == 1){
				return  ResultDto.newIns().suc(false).msg("超级管理员不可禁用");
			}
			disMember.setIsDisabled(!disMember.getIsDisabled());
			disMemberMapper.updateByPrimaryKeySelective(disMember);
			String opdesc = (disMember.getIsDisabled()?"禁用":"启用")+"后台用户";
			//插入操作日志
			saveAdminRecord(operator, disMember.getId(), opdesc);
			return  ResultDto.newIns().suc(true).msg(opdesc+"成功");
		} catch (Exception e) {
			return  ResultDto.newIns().suc(false).msg("更新后台用户状态异常");
		}
	}

	/**
	 * 保存后台用户操作日志
	 * @author zbc
	 * @since 2017年4月28日 下午3:17:24
	 */
	private void saveAdminRecord(String operator,Integer adminId, String opdesc) {
		adminRecordMapper.insertSelective(new AdminOperateRecord(operator, opdesc, adminId));
	}

	@Override
	public ResultDto<?> adminRecord(Integer id) {
		try {
			return ResultDto.newIns().suc(true).obj(adminRecordMapper.selectByAdminId(id));
		} catch (Exception e) {
			Logger.info("查看后台用户操作日志异常{}",e);
			return ResultDto.newIns().msg("查看日志异常").suc(false);
		}
		
	}

	@Override
	public String generateCustomerCode(Integer provinceCode, Integer cityCode, Integer areaCode, int distributionMode, int id) {
		Logger.info("------------------generateCustomerCode------------->provinceCode:[{}], cityCode:[{}], areaCode:[{}], distributionMode:[{}]", provinceCode, cityCode, areaCode, distributionMode);

		StringBuilder customerCode = new StringBuilder();
		switch (distributionMode) {
			case 1:
				customerCode.append(Constant.ECOMMERCE_CHANNEL);
				customerCode.append(Constant.ONLINE);
				customerCode.append(Constant.ECOMMERCE);
				customerCode.append(String.format("%05d", id));
				break;
			case 2:
				customerCode.append(String.format("%02d", provinceCode));
				customerCode.append(String.format("%03d", cityCode));
				customerCode.append(String.format("%04d", areaCode));
				customerCode.append(Constant.OFFLINE);
				customerCode.append(Constant.DEALER);
				customerCode.append(String.format("%05d", id));
				break;
			case 3:
				customerCode.append(String.format("%02d", provinceCode));
				customerCode.append(String.format("%03d", cityCode));
				customerCode.append(String.format("%04d", areaCode));
				customerCode.append(Constant.OFFLINE);
				customerCode.append(Constant.KA);
				customerCode.append(String.format("%05d", id));
				break;
			case 4:
				customerCode.append(String.format("%02d", provinceCode));
				customerCode.append(String.format("%03d", cityCode));
				customerCode.append(String.format("%04d", areaCode));
				customerCode.append(Constant.OFFLINE);
				customerCode.append(Constant.IMPORT);
				customerCode.append(String.format("%05d", id));
				break;
			case 5:
				customerCode.append(Constant.VIP_CHANNEL);
				customerCode.append(Constant.ONLINE);
				customerCode.append(Constant.VIP);
				customerCode.append(String.format("%05d", id));
				break;
		}
		return customerCode.toString();
	}

	@Override
	public Map<String, Object> setMemberUserCode(JsonNode jsonNode, String operator) {
		Map<String, Object> result = Maps.newHashMap();

		if (!jsonNode.has("id") || jsonNode.get("id") == null) {
			result.put("suc", false);
			result.put("msg", "参数错误！");
		}

		Integer id = jsonNode.get("id").asInt();

		DisMember disMember = disMemberMapper.selectByPrimaryKey(id);
		if (disMember == null) {
			result.put("suc", false);
			result.put("msg", "该分销商不存在！");
			return result;
		}


		if (jsonNode.has("provinceCode") && jsonNode.has("cityCode") && jsonNode.has("areaCode")) {
			if (jsonNode.get("provinceCode") != null && jsonNode.get("cityCode") != null && jsonNode.get("areaCode") != null) {
				Integer provinceCode = jsonNode.get("provinceCode").asInt();
				Integer cityCode = jsonNode.get("cityCode").asInt();
				Integer areaCode = jsonNode.get("areaCode").asInt();
				String userCode = this.generateCustomerCode(provinceCode, cityCode, areaCode, disMember.getDistributionMode(), disMember.getId());
				disMember.setUserCode(userCode);
			}
		}

		int num = disMemberMapper.updateByPrimaryKeySelective(disMember);
		if(num > 0 && !StringUtils.isEmpty(disMember.getUserCode())) {
			//设置操作日志
			UserRankHistory history = new UserRankHistory();
			history.setEmail(disMember.getEmail());
			history.setCreateTime(new Date());
			history.setOperator(operator);
			history.setOperateDesc("设置客户编码");
			userRankHistoryMapper.insertSelective(history);

			result.put("suc", true);
			result.put("msg", "设置用户编码成功!");
			return result;
		}

		result.put("suc", false);
		result.put("msg", "设置用户编码失败！");
		return result;
	}

	@Override
	public Page<AdminDto> allDistributions(Map<String, Object> userMap) {
		int rows = disMemberMapper.relatedMemberCount(userMap);
		if(rows <= 0) {
			return new Page<AdminDto>((Integer) userMap.get("currPage"), (Integer) userMap.get("pageSize"), rows, null);
		}
		
		List<DisMember> members = disMemberMapper.relatedMember(userMap);
		List<AdminDto> dtos = Lists.newArrayList();
		AdminDto dto = null;
		for (DisMember member : members) {
			dto = new AdminDto();
			BeanUtils.copyProperties(member, dto);
			dto.setNick(member.getNickName());
			dtos.add(dto);
		}
		return new Page<AdminDto>((Integer) userMap.get("currPage"), (Integer) userMap.get("pageSize"), rows, dtos);
	}

	@Override
	public Map<String, Object> setIsPackageMail(JsonNode json, String operator) {
		Map<String, Object> result = Maps.newHashMap();
		if (!StringUtils.isNumeric(json.get("id").asText()) || !StringUtils.isNumeric(json.get("isPackageMail").asText())) {
			result.put("suc", false);
			result.put("msg", "数据格式错误！");
			return result;
		}
		Integer id = json.get("id").asInt();
		DisMember disMember = disMemberMapper.selectByPrimaryKey(id);
		if (disMember == null) {
			result.put("suc", false);
			result.put("msg", "该分销商不存在！");
			return result;
		}

		disMember.setIsPackageMail((short) json.get("isPackageMail").asInt());
		int num = disMemberMapper.updateByPrimaryKeySelective(disMember);

		Logger.info("--------------------------->：【{}】", disMember.getIsPackageMail());
		if (num < 1) {
			result.put("suc", false);
			result.put("msg", "设置失败！");
			return result;
		}

		//生成操作日志
		PackageMailLog packageMailLog = new PackageMailLog();
		packageMailLog.setOperator(operator);
		packageMailLog.setMemberId(disMember.getId());
		packageMailLog.setIsPackageMail(disMember.getIsPackageMail());
		packageMailLog.setRemark(json.has("remark") ? json.get("remark").asText().trim() : null);
		packageMailLog.setCreateTime(new Date());

		packageMailLogMapper.insertSelective(packageMailLog);

		result.put("suc", true);
		result.put("msg", "设置成功！");
		return result;
	}

	@Override
	public Map<String, Object> batchSetIsPackageMail(JsonNode jsonNode, String operator) {
		Map<String, Object> result = Maps.newHashMap();

		int isPackageMail = jsonNode.get("isPackageMail").asInt();
		JsonNode memberListNode = jsonNode.get("memberIdList");
		String remark = jsonNode.has("remark") ? jsonNode.get("remark").asText().trim() : "";

		List<PackageMailLog> packageMailLogList = Lists.newArrayList();

		Logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>batchSetIsPackageMail 批量设置的个数：【{}】" , memberListNode.size());
		List<Integer> failIdList = Lists.newArrayList();
		for (Iterator<JsonNode> it = memberListNode.iterator(); it.hasNext(); ) {
			JsonNode nextNode = it.next();

			Integer id = nextNode.asInt();
			DisMember disMember = disMemberMapper.selectByPrimaryKey(id);
			if (disMember != null) {
				disMember.setIsPackageMail((short) isPackageMail);

				int num = disMemberMapper.updateByPrimaryKeySelective(disMember);
				if (num > 0) {
					PackageMailLog packageMailLog = new PackageMailLog();
					packageMailLog.setMemberId(disMember.getId());
					packageMailLog.setIsPackageMail((short) isPackageMail);
					packageMailLog.setOperator(operator);
					packageMailLog.setRemark(remark);
					packageMailLog.setCreateTime(new Date());
					packageMailLogList.add(packageMailLog);
				} else {
					failIdList.add(id);
				}
			} else {
				Logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>batchSetIsPackageMail 分销商不存在：【{}】", id);
				failIdList.add(id);
			}
		}

		Logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>批量设置运费失败id：【{}】", failIdList);
		Logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>批量设置运费个数：【{}】， 成功个数：【{}】， 失败个数：【{}】",
				memberListNode.size(), memberListNode.size() - failIdList.size(), failIdList.size());

		if (CollectionUtils.isNotEmpty(packageMailLogList)) {
			packageMailLogMapper.batchInsert(packageMailLogList);
		}

		result.put("suc", true);
		result.put("msg", "设置成功！");
		return result;
	}

	@Override
	public ResultDto<?> updateComsumerInfo(String string, String admin) {
		try {
			DisMember member = JsonFormatUtils.jsonToBean(string, DisMember.class);
			if(member == null ){
				return new ResultDto<>(false,"参数有误");
			}
			DisMember oldMember = disMemberMapper.selectByPrimaryKey(member.getId());
			if(oldMember == null){
				return new ResultDto<>(false,"分销商不存在");
			}
			UserRankHistory history = new UserRankHistory(); 
			String operateDesc = "";
			boolean syncMsite = false;
			if(member.getDistributionMode()!=null&&!oldMember.getDistributionMode().equals(member.getDistributionMode())){
				DisMode oldModel=modeMapper.selectByPrimaryKey(oldMember.getDistributionMode());
	        	DisMode newModel=modeMapper.selectByPrimaryKey(member.getDistributionMode());
	        	operateDesc+="模式由 "+oldModel.getDisMode()+" → "+newModel.getDisMode();
	        	syncMsite = true;
			}
			if(member.getComsumerType()!=null&&!oldMember.getComsumerType().equals(member.getComsumerType())){
				CustomerType oldType = customerTypeMapper.selectByPrimaryKey(oldMember.getComsumerType());
				CustomerType newType = customerTypeMapper.selectByPrimaryKey(member.getComsumerType());
				operateDesc +="分销商类型由 "+oldType.getCustomerName()+"→ "+newType.getCustomerName();
			}
			if(member.getAttributionType()!=null&&!oldMember.getAttributionType().equals(member.getAttributionType())){
				operateDesc+="用户归属由 "+Constant.USER_ATTR_TYPE_MAP.get(oldMember.getAttributionType())+" → "
							+Constant.USER_ATTR_TYPE_MAP.get(member.getAttributionType());
			}
			if(StringUtils.isNotEmpty(operateDesc)){
				member.setLastUpdateDate(new Date());
				history.setEmail(oldMember.getEmail());
				history.setCreateTime(new Date());
				history.setOperator(admin);
				history.setOperateDesc(operateDesc);
				userRankHistoryMapper.insertSelective(history);
				if(update(member)>0 &&  syncMsite){
		       		changeSiteInfo(new ShopSite(oldMember.getEmail(), member.getDistributionMode()));
				}
				return new ResultDto<>(true,"修改分销商类型成功");
			}else{
				return new ResultDto<>(true,"没有任何修改");
			}
		} catch (Exception e) {
			Logger.info("修改分销商类型异常:{}",e);
			return new ResultDto<>(false,"修改分销商类型异常");
		}
	}
	
}
