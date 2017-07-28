package services.dismember.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.dismember.*;
import mapper.dismember.*;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.libs.Json;
import services.dismember.IDisMemberService;
import services.dismember.IEmailService;
import services.dismember.ILoginService;
import services.dismember.IWAccountService;
import session.ISessionService;
import utils.dismember.AESOperator;
import utils.dismember.HttpUtil;
import utils.dismember.IDUtils;
import utils.dismember.SMSManager;
import vo.dismember.LoginContext;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import constant.dismember.Constant;

/**
 * Created by luwj on 2016/9/20.
 */
public class WAccountService implements IWAccountService{

    @Inject
    ILoginService loginService;
    @Inject
    DisWithdrawAccountMapper mapper;
    @Inject
    IEmailService iEmailService;
    @Inject
    IDisMemberService iDisMemberService;
    @Inject
    ISessionService sessionService;
    @Inject
    DisEmailVerifyMapper disEmailVerifyMapper;
    @Inject
    PhoneVerifyMapper phoneVerifyMapper;

    @Inject
    private EmailAccountMapper emailMapper;

    @Inject
    private EmailTemplateMapper templateMapper;

    /**
     * 校验银行帐号是否存在
     * @param node
     * @return
     */
    public JSONObject existWAccountNo(JSONObject node){
        JSONObject result = new JSONObject();
        Integer exist = 0;
        String _email = "";
        try {
            if(node.containsKey("accountUser") &&
                    StringUtils.isNotBlank(node.getString("accountUser"))
                    && node.containsKey("withdrawAccount") &&
                    StringUtils.isNotBlank(node.getString("withdrawAccount"))
                    && node.containsKey("accountUnit") &&
                    StringUtils.isNotBlank(node.getString("accountUnit"))
                    && node.containsKey("province") &&
                    StringUtils.isNotBlank(node.getString("province"))
                    && node.containsKey("city") &&
                    StringUtils.isNotBlank(node.getString("city"))){
                String withdrawAccount = node.getString("withdrawAccount");
                String accountUser = node.getString("accountUser");
                String accountUnit = node.getString("accountUnit");
                String province = node.getString("province");
                String city = node.getString("city");
                LoginContext lc = loginService.getLoginContext(1);
                String email = lc.getEmail();
                DisWithdrawAccount account = new DisWithdrawAccount();
                account.setWithdrawAccount(withdrawAccount);
                account.setAccountUser(accountUser);
                account.setIsBind(1);//绑定
                account.setDistributorEmail(email);
                account = mapper.getWAccounts(account);
                if(account != null) {
                    exist = 4;//已绑定
                }else {//发送验证码到注册帐号
                    if(email.contains("@")){
                        //发送邮件
                        exist = sendEmailCode(email, accountUser, withdrawAccount,
                                accountUnit, province, city);
                        _email = email.substring(0,2) + email.substring(2,email.lastIndexOf("@")).replaceAll(".","*") + email.substring(email.lastIndexOf("@"));
                    }else {
                        //发送短信--1:提现绑定帐户
                        exist = sendTelCode(withdrawAccount, 1);
                        _email = email.substring(email.length()-4);
                    }
                }
            }else{//参数错误
                exist = 3;
            }
        }catch (Exception e){
            e.printStackTrace();
            Logger.error(">>addWAccountNo>>>Exception>>>>"+e);
            exist = 5;
        }
        result.put("exist", exist);
        result.put("account", _email);
        return result;
    }

    /**
     * 添加提现银行帐号
     * @param param : 参数
     * @return :
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public Integer addWAccountNo(String param){
        Integer insert = 0;
        try {
            JsonNode node = new ObjectMapper().readTree(param);
            if(node.has("accountUser") &&
                    StringUtils.isNotBlank(node.get("accountUser").asText())
                    && node.has("accountUnit") &&
                    StringUtils.isNotBlank(node.get("accountUnit").asText())
                    && node.has("withdrawAccount") &&
                    StringUtils.isNotBlank(node.get("withdrawAccount").asText())
                    && node.has("code") &&
                    StringUtils.isNotBlank(node.get("code").asText())
                    && node.has("province") &&
                    StringUtils.isNotBlank(node.get("province").asText())
                    && node.has("city") &&
                    StringUtils.isNotBlank(node.get("city").asText())){
                String code = node.get("code").asText();
                String withdrawAccount = node.get("withdrawAccount").asText();
                LoginContext lc = loginService.getLoginContext(1);
                Map map = new HashMap<>();
                map.put("phone", lc.getEmail());
                map.put("types", 1);
                PhoneVerify verify = phoneVerifyMapper.getRecord(map);
                if(verify != null){
                    if(new Date().getTime() > verify.getValidate().getTime()){
                        insert = 7;//超过5分钟失效
                    }else if(!withdrawAccount.equals(verify.getWano())){
                        insert = 8;//验证码对应绑定一个卡号，不匹配则验证码已经失效
                    }else if(!code.equals(verify.getCode())){
                        insert = 6;//验证码错误
                    }else {
                        String accountUser = node.get("accountUser").asText();
                        String province = node.get("province").asText();
                        String city = node.get("city").asText();
                        DisWithdrawAccount account = new DisWithdrawAccount();
                        account.setWithdrawAccount(withdrawAccount);
                        account.setAccountUser(accountUser);
                        account.setDistributorEmail(lc.getEmail());
                        account = mapper.getWAccounts(account);
                        if (account == null) {//查询该帐号是否已经绑定
                            account = new DisWithdrawAccount();
                            account.setAccountUnit(node.get("accountUnit").asText());
                            account.setAccountUser(node.get("accountUser").asText());
                            account.setCreateTime(new Date());
                            account.setDistributorEmail(lc.getEmail());
                            account.setWithdrawAccount(withdrawAccount);
                            account.setIsBind(1);//绑定
                            account.setAccountType(0);
                            account.setAccountProvince(province);
                            account.setAccountCity(city);
                            insert = mapper.insertSelective(account);
                        } else if (account.getIsBind() != 1) {//该帐号已经解绑
                            account.setAccountUnit(node.get("accountUnit").asText());
                            account.setCreateTime(new Date());
                            account.setIsBind(1);//重新绑定
                            account.setAccountProvince(province);
                            account.setAccountCity(city);
                            mapper.updateByPrimaryKeySelective(account);
                        } else {//该帐号已经绑定
                            insert = 4;
                        }
                    }
                }
            }else{//参数错误
                insert = 3;
            }
        }catch (Exception e){
            e.printStackTrace();
            Logger.error(">>addWAccountNo>>>Exception>>>>"+e);
            insert = 5;
        }
        return insert;
    }

    /**
     * 提现绑定银行帐号确认邮件及添加银行帐号
     * @param param : 参数
     * @return :
     */
    public Integer activateWBEmail(String param){
        int insert = 0;
        Logger.debug(">>activateWBEmail>>>>param>>"+param);
        try {
            String decStr = AESOperator.getInstance().decrypt(param);
            Logger.debug(">>activateWBEmail>>>decStr>>>" + decStr);
            if (decStr.contains("&") && decStr.contains("=")) {
                Map<String, String> map = new HashMap<>();
                String[] strs = decStr.split("&");
                for (int i = 0; i < strs.length; i++) {
                    map.put(strs[i].split("=")[0], strs[i].split("=")[1]);
                }
                String code = map.get("code");
                String email = map.get("email");
                DisEmailVerify verify = new DisEmailVerify();
                verify.setSendType(3);
                verify.setCemail(email);
                verify = disEmailVerifyMapper.getVerify(verify);
                if (verify != null) {
                    if (verify.getDvaliddate().getTime() >= new Date().getTime()) {
                        if (code.equalsIgnoreCase(verify.getCactivationcode())) {
                            String accountUser = map.get("accountUser");
                            String withdrawAccount = map.get("withdrawAccount");
                            String accountUnit = map.get("accountUnit");
                            String province = map.get("province");
                            String city = map.get("city");
                            DisWithdrawAccount account = new DisWithdrawAccount();
                            account.setWithdrawAccount(withdrawAccount);
                            account.setAccountUser(accountUser);
                            account.setDistributorEmail(email);
                            account = mapper.getWAccounts(account);
                            if (account == null) {//查询该帐号是否已经绑定
                                account = new DisWithdrawAccount();
                                account.setAccountUnit(accountUnit);
                                account.setAccountUser(accountUser);
                                account.setCreateTime(new Date());
                                account.setDistributorEmail(email);
                                account.setWithdrawAccount(withdrawAccount);
                                account.setIsBind(1);//绑定
                                account.setAccountType(0);
                                account.setAccountProvince(province);
                                account.setAccountCity(city);
                                insert = mapper.insertSelective(account);
                            } else if (account.getIsBind() != 1) {//该帐号已经解绑
                                account.setAccountUnit(accountUnit);
                                account.setCreateTime(new Date());
                                account.setIsBind(1);//重新绑定
                                account.setAccountProvince(province);
                                account.setAccountCity(city);
                                insert = mapper.updateByPrimaryKeySelective(account);
                            } else {//该帐号已经绑定
                                insert = 4;
                            }
                        } else {
                            insert = 6;//验证码错误
                        }
                    } else {
                        insert = 7;//验证地址已失效
                    }
                }
            } else {//数据被修改或不全
                insert = 8;
            }
        }catch (Exception e){
            insert = 5;
        }
        return insert;
    }

    /**
     * 发送提现绑定帐户确认邮件
     * @param email
     * @param accountUser
     * @param withdrawAccount
     * @param accountUnit
     * @param province
     * @param city
     * @return
     */
    private int sendEmailCode(String email, String accountUser,
                              String withdrawAccount, String accountUnit,
                              String province, String city){
        //校验是否超过每天发送次数限制
        if(!iEmailService.moreThanLimit(3, email, 3)){
        	return 6;//发送次数超限
        }
        
        String code = IDUtils.getUUID();// 验证码
        //TODO:url地址还需要修改，新增校验地址接口
        String url = HttpUtil.getHostUrl() + "/member/wbActiveEmail?data=";
        //加密窜
        String paramStr = "email=" +email + "&code=" + code + "&accountUser=" +accountUser + "&withdrawAccount=" +
                withdrawAccount + "&accountUnit=" + accountUnit + "&province=" + province
                + "&city=" + city;
        //生成加密窜
        String encoStr = AESOperator.getInstance().encrypt(paramStr);
        Logger.debug(">>>sendEmail>>url:>>>" + url + encoStr);
        // 邮件变量值
        Map<String, Object> objectmap = Maps.newHashMap();
        objectmap.put("url", url + encoStr);
        objectmap.put("toemail", email);
        // 如果发送成功
        boolean isOk = iDisMemberService.sendEamil(objectmap, Constant.EMAIL_SMTP, Constant.BIND_ACCOUNT_NO, email);
        if(isOk)
            iEmailService.saveSendRecord(email, paramStr, 3, 2, code, Calendar.HOUR);
        return 9;//邮箱,区分邮箱，手机，便于前端展示
    }

    /**
     * 发送提现绑定帐户短信验证码
     * @return :
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public int sendTelCode(String withdrawAccount, int types) throws Exception{
        String email = loginService.getLoginContext(1).getEmail();
        Map map = new HashMap<>();
        map.put("phone", email);
        map.put("types", types);
        List<PhoneVerify> lists = phoneVerifyMapper.getRecords(map);
        if(lists != null && lists.size() >= 3){
            return 7;//超限
        }
        
        String code = IDUtils.randomNumber(6);

        EmailAccount emailAccountParam = new EmailAccount();
        emailAccountParam.setCtype(Constant.SEND_MSG);
        EmailAccount emailAccount = emailMapper.select(emailAccountParam);
        if (emailAccount == null) {
            Logger.error("--------------------->sendTelCode:短信发送失败！获取不到短信配置！");
            throw new RuntimeException("-------------->sendTelCode:短信发送失败！获取不到短信配置！");
        }

//        SMSManager.send("【通淘国际】亲爱的用户，您正在进行提现绑定银行卡操作。" +
//                " 验证码是"+code+"，五分钟内有效", email);

        EmailTemplate template = templateMapper.select(Constant.BIND_CARD);
        if (null == template) {
            Logger.error("-------------->sendTelCode:未配置短信模板：" + new Date());
            throw new RuntimeException("-------------->sendTelCode:未配置短信模板!");
        }

        String content = template.getCcontent();
        Logger.info("content----------->"+Json.toJson(content));
        if (!content.contains("code")) {
            Logger.error("-------------->sendTelCode:短信模板配置有误：" + new Date());
            throw new RuntimeException("-------------->sendTelCode:短信模板配置有误!");
        }
        content = content.replaceAll("code", code).replace("{{", "").replace("}}", "");
        SMSManager.send(content, emailAccount, email);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);
        PhoneVerify verify = new PhoneVerify();
        verify.setCode(code);
        verify.setPhone(email);
        verify.setTypes(types);
        verify.setWano(withdrawAccount);
        verify.setValidate(calendar.getTime());
        verify.setCreateDate(new Date());
        phoneVerifyMapper.insert(verify);
        return 8;//手机
    }

	@Override
	public String reSendEmail(DisEmailVerify emailVerify) {
		Map<String,Object> resultMap = Maps.newHashMap();
		resultMap.put("suc",true);
		//邮件最多发送三次
		if(!iEmailService.moreThanLimit(3, emailVerify.getCemail(), 3)) {
			//发送次数超限
        	resultMap.put("suc",false);
        	resultMap.put("msg","操作过于频繁，如果你依然未收到邮件，请于明日再进行操作！");
        	return Json.toJson(resultMap).toString();
        }
		
		//查询当前用户的最新发送邮件的数据
		Logger.info("reSendEmail email:"+emailVerify.getCemail()+" sendType:"+emailVerify.getSendType());
		DisEmailVerify verifyResult = disEmailVerifyMapper.getLastVerifyByEmail(emailVerify);
		//获取随机校验码
        String code = IDUtils.getUUID();
        //获取发送邮件链接的参数
        String paramStr = verifyResult.getSendParams();
        //将参数中的校验码替换为现有校验码
        StringBuffer sbParam = new StringBuffer(paramStr);
        sbParam.replace(paramStr.indexOf("&code=") + 6, paramStr.indexOf("&accountUser="), code);
        //链接
        String url = HttpUtil.getHostUrl() + "/member/wbActiveEmail?data=";
        //参数加密
        String encoStr = AESOperator.getInstance().encrypt(sbParam.toString());
        Logger.info(this.getClass().getName()+">>>reSendEmail>>url:>>>" + url + encoStr);
        // 邮件变量值
        Map<String, Object> objectmap = Maps.newHashMap();
        objectmap.put("url", url + encoStr);
        objectmap.put("toemail", emailVerify.getCemail());
        //执行发送邮件
        iDisMemberService.sendEamil(objectmap, Constant.EMAIL_SMTP, Constant.BIND_ACCOUNT_NO, emailVerify.getCemail());
        //保存发送邮件记录
        iEmailService.saveSendRecord(emailVerify.getCemail(),sbParam.toString(),3, 2, code, Calendar.HOUR);
		return Json.toJson(resultMap).toString();
	}

}
