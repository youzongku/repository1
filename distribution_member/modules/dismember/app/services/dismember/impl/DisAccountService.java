package services.dismember.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.joda.time.DateTime;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import constant.dismember.Constant;
import dto.dismember.AccountDto;
import dto.dismember.ResultDto;
import entity.dismember.AccountOperationRecord;
import entity.dismember.AccountPeriodMaster;
import entity.dismember.DisAccount;
import entity.dismember.DisBill;
import entity.dismember.DisCredit;
import entity.dismember.DisMember;
import entity.dismember.DisSalesman;
import entity.dismember.EmpSalesManMapper;
import entity.dismember.LoginHistory;
import entity.dismember.UserRankHistory;
import mapper.dismember.AccountOperationRecordMapper;
import mapper.dismember.AccountPeriodMasterMapper;
import mapper.dismember.DisAccountMapper;
import mapper.dismember.DisCreditMapper;
import mapper.dismember.DisMemberMapper;
import mapper.dismember.DisSalesmanMapper;
import mapper.dismember.EmpSalesManMapperMapper;
import mapper.dismember.LoginHistoryMapper;
import mapper.dismember.UserRankHistoryMapper;
import play.Logger;
import play.libs.Json;
import services.dismember.ICaptchaService;
import services.dismember.IDisAccountService;
import services.dismember.IDisBillService;
import services.dismember.ILoginService;
import services.dismember.ISequenceService;
import utils.dismember.IDUtils;
import utils.dismember.JsonCaseUtil;
import utils.dismember.MD5Util;
import vo.dismember.LoginContext;
import vo.dismember.Page;

/**
 * Created by LSL on 2016/1/5.
 */
public class DisAccountService implements IDisAccountService {

    @Inject
    private DisAccountMapper disAccountMapper;
    @Inject
    private DisMemberMapper disMemberMapper;
    @Inject
    private LoginHistoryMapper loginHistoryMapper;
    @Inject
    private DisCreditMapper disCreditMapper;
    @Inject
    private ILoginService loginService;
    @Inject
	private ICaptchaService captchaService;
    @Inject
    private DisSalesmanMapper disSalesmanMapper;
    @Inject
    private AccountOperationRecordMapper accountOperationRecordMapper;
    @Inject
    private ISequenceService sequenceService;
    @Inject
    private AccountPeriodMasterMapper  accountPeriodMapper;
    @Inject
    private IDisBillService billService;
    @Inject
    private UserRankHistoryMapper userRankHistoryMapper;
	@Inject
	private EmpSalesManMapperMapper empMapper;
	@Inject
	private EventBus ebus;

    @Override
    public boolean updateDisAccount(Map<String, String> params) {
        Logger.info("updateDisAccount params-->" + params.toString());
        String email = params.containsKey("email") ? params.get("email") : null;
        DisAccount account = disAccountMapper.getDisAccountByEmail(email);
        if (account == null || account.isFrozen()) {
            Logger.info("当前用户" + email + "资金账户不存在或已冻结");
            return false;
        }
        
        account.setPayPass(params.containsKey("code") ? MD5Util.MD5Encode(params.get("code"), MD5Util.CHARSET_UTF_8) : null);
        account.setBalance(params.containsKey("money") ? BigDecimal.valueOf(Double.valueOf(params.get("money"))) : null);
        account.setUpdateDate(new Date());
        int line = disAccountMapper.updateByPrimaryKeySelective(account);
        Logger.info("updateDisAccount line-->" + line);
        return line == 1;
    }
    
    @Override
	public Map<String, Object> resetPayPassword(Map<String, String> params) {
    	Logger.info("resetPayPassword------------>" + params.toString());
		Map<String, Object> result = Maps.newHashMap();
		String email = params.get("email");
		String password = params.get("password");
		
		if(StringUtils.isEmpty(password)
				|| (password.length() < 6 || password.length() > 20)
				|| !utils.dismember.StringUtils.containsLetterNum(password)) {
			result.put("suc", 1);
			result.put("msg", "密码必须为6-20个字符，且至少包含数字、大写、小写字母等三种或以上字符！");
			return result;
		}
		
		DisAccount account = disAccountMapper.getDisAccountByEmail(email);
		if (account == null || account.isFrozen()) {
			Logger.info("当前用户" + email + "资金账户不存在或已冻结");
			result.put("suc", 0);
			result.put("msg", "当前用户" + email + "资金账户不存在或已冻结");
			return result;
		}
		
		String newpwd = MD5Util.MD5Encode(password, MD5Util.CHARSET_UTF_8);
		if (newpwd.equals(account.getPayPass())) {
			Logger.info("新密码与旧密码相同");
			result.put("suc", 1);
			result.put("msg", "新密码与旧密码相同");
			return result;
		}
		
    	account.setPayPass(newpwd);
    	account.setUpdateDate(new Date());
    	disAccountMapper.updateByPrimaryKeySelective(account);
		result.put("suc", 2);
		result.put("msg", "支付密码重置成功");
		return result;
	}

    @Override
    public Map<String, String> getAccountInfo(String email) {
        String errorCode = "0";
        String errorInfo = "";
        Map<String,String> map = Maps.newHashMap();
        DisMember disMember = new DisMember();
        disMember.setRoleId(2);
        disMember.setEmail(email);
        disMember = disMemberMapper.getMember(disMember);
        if(disMember != null) {
            map.put("id",String.valueOf(disMember.getId()));
            map.put("headImg", disMember.getHeadImg());
            DisAccount disAccount = disAccountMapper.getDisAccountByEmail(disMember.getEmail());
            BigDecimal balance = disAccount.getBalance();
            // change by zbc  (账期可用额度加上月)
            AccountPeriodMaster accountPeriod = accountPeriodMapper.getValidAp(email);
			balance = accountPeriod != null
				? balance.add(accountPeriod.getTotalLimit().subtract(accountPeriod.getUsedLimit())) :
					balance;
			map.put("isFrozen", disAccount.isFrozen()+"");
            map.put("balance", balance.doubleValue() + "");
            map.put("hasKeyWord",disAccount.getPayPass() == null || disAccount.getPayPass().equals("") ? "false" : "true");
            List<LoginHistory> loginHistorys = loginHistoryMapper.getRecentHistory(disMember.getEmail());
            if(loginHistorys != null && loginHistorys.size() > 0){
                if (loginHistorys.size() == 1) {
                    map.put("recent", new DateTime(loginHistorys.get(0).getDtimestamp()).toString("yyyy-MM-dd HH:mm"));
                } else {
                    map.put("recent", new DateTime(loginHistorys.get(1).getDtimestamp()).toString("yyyy-MM-dd HH:mm"));
                }
            }
        }
        map.put("errorCode", errorCode);
        map.put("errorInfo",errorInfo);
        return map;
    }

    @Override
    public DisAccount checkFrozen(String email) {
        DisAccount account = disAccountMapper.getDisAccountByEmail(email);
        if(null != account){
            List<LoginHistory> loginHistory = loginHistoryMapper.getRecentHistory(email);
            if(null != loginHistory && loginHistory.size() > 0){
                if(loginHistory.size() == 1){
                    account.setHistorys(loginHistory.get(0));
                }else{
                    account.setHistorys(loginHistory.get(1));
                }
            }
            //返回值不能带出密码
            account.setPayPass(null);
        }
        return account;
    }

    /**
     * 校验支付密码
     * @param code : 密码
     * @return :
     */
    public String checkPayPwd(String code,LoginContext lc,String payCaptcha){
    	Map<String,Object> resultMap = new HashMap<String,Object>();
    	//获取账户信息
    	DisAccount disAccount = disAccountMapper.getDisAccountByEmail(lc.getEmail());
		if(disAccount == null) {
			resultMap.put("suc", false);
			resultMap.put("msg", "未查询到账户！");
			return Json.toJson(resultMap).toString();
		}
		
		Logger.info(this.getClass().getName()+" checkPayPwd email------>"+lc.getEmail());
		//查询输入失败次数，若大于等于5次，则查询禁用时间，若当前时间小于禁用时间，则提示账户已锁定
		Logger.info(this.getClass().getName()+" checkPayPwd InputErrorNumTimes------>"+disAccount.getInputErrorNumTimes());
		if(disAccount.getInputErrorNumTimes() >= Constant.INPUT_ERROR_NUM_TIMES_LOCK) {
			Date curTime = new Date();
			Date disableTime = disAccount.getDisableTime();
			Logger.info(this.getClass().getName()+" checkPayPwd curTime==="+curTime+" disableTime==="+disableTime);
			if(disableTime != null && curTime.before(disableTime)) {
				resultMap.put("suc", false);
				resultMap.put("msg", "账户已锁定，请1小时后再输入！");
				return Json.toJson(resultMap).toString();
			}
		}
		//若输入失败次数大于等于3次，则校验验证码
		if(disAccount.getInputErrorNumTimes() >= Constant.INPUT_ERROR_NUM_TIMES_CODE) {
			if(!captchaService.verify(payCaptcha)) {
				resultMap.put("suc", false);
				resultMap.put("msg", "请输入正确的验证码");
				return Json.toJson(resultMap).toString();
			}
		}
        
        if(disAccount != null) {
        	boolean isOk = MD5Util.MD5Encode(code, MD5Util.CHARSET_UTF_8).equals(disAccount.getPayPass());
            if(isOk) {
            	//若密码正确，则更新账户的输入次数重置为0，且将禁用时间清空
            	DisAccount account = new DisAccount();
            	account.setId(disAccount.getId());
				account.setInputErrorNumTimes(0);
				account.setDisableTime(null);
				int line = disAccountMapper.updateInputErrorTimes(account);
				Logger.info(this.getClass().getName()+" [checkPayPwd updateInputErrorTimes===]"+line);
            	resultMap.put("suc", true);
            	resultMap.put("msg", disAccount.getBalance().toString());
            } else {
            	//若密码错误，则每次给输入失败次数加1，若增加后的数据大于等于5次，则更新禁用时间为当前时间加一小时，且提示账户锁定
            	DisAccount account = new DisAccount();
				Integer numberTimes = disAccount.getInputErrorNumTimes() + 1;
				if(numberTimes >= Constant.INPUT_ERROR_NUM_TIMES_LOCK) {
					DateTime dateTime = new DateTime().plusHours(Constant.PWS_INPUT_LOCK_TIME);
					account.setDisableTime(dateTime.toDate());
				}
				account.setId(disAccount.getId());
				account.setInputErrorNumTimes(numberTimes);
				int line = disAccountMapper.updateByPrimaryKeySelective(account);
				Logger.info(this.getClass().getName()+" [checkPayPwd updateCount===]"+line);
				if(numberTimes < Constant.INPUT_ERROR_NUM_TIMES_LOCK) {
					resultMap.put("msg", "支付密码输入错误，还可以输入"+(Constant.INPUT_ERROR_NUM_TIMES_LOCK-numberTimes)+"次");
				} else {
					resultMap.put("msg", "支付密码输入错误，账户已锁定，请一小时后再输入！");
				}
				resultMap.put("suc", false);
            }
        }
        return Json.toJson(resultMap).toString();
    }

	@Override
	public DisAccount getAccountByEmail(String email) {
		return disAccountMapper.getDisAccountByEmail(email);
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getAllAccount(String email, Map<String, String[]> node) {
		ObjectNode result = Json.newObject();
		// 判断账号是否关联了组织架构
		Map<String, Object> map = Maps.newHashMap();
		map.put("account", email);
		// 获取账号关联的员工
		List<DisSalesman> list = disSalesmanMapper.querySalesmansByCondition(map);
		// 获取nodeType 为2的员工
		Map<Boolean, List<DisSalesman>> empMap = list.stream().collect(
				Collectors.partitioningBy(e -> e.getNodeType() == 2));
		List<DisSalesman> empList = empMap.get(true);
		List<EmpSalesManMapper> esList = Lists.newArrayList();
		if (!CollectionUtils.isEmpty(empList)) {
			esList = empMapper.selectBySaleManIds(Lists
					.transform(empList, i -> i.getId()));
		}
		for(EmpSalesManMapper emp:esList){
			map = Maps.newHashMap();
			map.put("empId",emp.getEmpId());
			list.addAll(disSalesmanMapper.querySalesmansByCondition(map));
		}
		Integer currPage = utils.dismember.StringUtils.getIntegerParam(node,"page",null);
		Integer pageSize = utils.dismember.StringUtils.getIntegerParam(node,"rows",null);
		Map<String, Object> userMap = Maps.newHashMap();
		userMap.put("search", utils.dismember.StringUtils.getStringParam(node,"search",""));
		userMap.put("list", list);
		userMap.put("comsumerType", node.get("comsumerType") != null && node.get("comsumerType").length > 0 ? Integer.parseInt(node.get("comsumerType")[0]) : null);
		userMap.put("distributionMode", node.get("distributionMode") != null && node.get("distributionMode").length > 0 ? Integer.parseInt(node.get("distributionMode")[0]) : null);
		userMap.put("currPage", currPage);
		userMap.put("pageSize", pageSize);
		String sort = utils.dismember.StringUtils.getStringParam(node, "sidx", null);
		String filter = utils.dismember.StringUtils.getStringParam(node, "sord", null);
		userMap.put("sort", sort);
		userMap.put("filter", filter);
		List<AccountDto> accounts = disAccountMapper.getAccounts(userMap);
		Integer total = disAccountMapper.getAccountsCount(userMap);
		
	    if (accounts != null) {
	    	 for (AccountDto dto : accounts) {
	 	    	dto.setComsumerTypeName(dto.getComsumerType());	
	 	    	dto.setDistributionModeDesc(dto.getDistributionMode());
	 	    }
	    }
	    
	    result.put("mark", 3);
	    result.put("data", Json.toJson(new Page<>(currPage, pageSize, total, accounts)));
		return result.toString();
	}

	/**
	 * 核减用户余额
	 */
	@Override
	public String reduceAccountByEmalil(String operator, String param) {
		ObjectNode result = Json.newObject();
		JsonNode node = Json.parse(param);
		String email = node.get("email").asText();
		BigDecimal reduceAmount = new BigDecimal(node.get("reduceAmount").asDouble());
		String reduceMark=null;
		//添加备注 2017-05-02 xingjk
		if(node.has("reduceMark")&&!"".equals(node.get("reduceMark").asText())){
			reduceMark=node.get("reduceMark").asText();
		}
		reduceAmount = reduceAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
		DisAccount account = disAccountMapper.getDisAccountByEmail(email);
		if(account==null){
			result.put("suc", false);
			result.put("msg", "没有查询到指定账户");
			return result.toString();
		}
		
		DisAccount upAccount  = new DisAccount();
		upAccount.setId(account.getId());
		BigDecimal balance = account.getBalance().subtract(reduceAmount);//剩余的钱
		upAccount.setBalance(balance.setScale(2, BigDecimal.ROUND_HALF_UP));
		int flag = disAccountMapper.updateByPrimaryKeySelective(upAccount);
		Logger.info("reduceAccountByEmalil   核减标识----->"+ flag);
		if (flag == 1) {
			//生成操作记录
			AccountOperationRecord  record = new AccountOperationRecord();
			record.setAccountId(account.getId());
			record.setOperateTime(new Date());
			record.setOperator(operator);
			record.setOpdesc(operator + " 核减金额：￥"+reduceAmount+"元，账户余额：￥"+upAccount.getBalance()+"元");
			record.setReduceMark(reduceMark);
			accountOperationRecordMapper.insertSelective(record);
			//生成交易记录
			DisBill bill = new DisBill();
			String serialNumber = IDUtils.getOnlineTopUpCode("TX", sequenceService.selectNextValue("WITHDRAW_AMOUNT_NO"));				bill.setPurpose("7");
		    bill.setSerialNumber(serialNumber);
			bill.setSources(0);//子交易记录
			bill.setAccountId(account.getId());
			bill.setPaymentType("余额核减");
			bill.setAmount(reduceAmount);//涉及金额
			bill.setBalance(balance);
			BigDecimal useCredit = BigDecimal.ZERO;//查询信用额度
			DisCredit credit = disCreditMapper.getDisCreditInfo(email);
			if (credit != null) {
				useCredit = credit.getTotalCreditLimit().subtract(credit.getTotalUsedAmount());
			}
			bill.setCreditLimitBalance(useCredit.setScale(2, BigDecimal.ROUND_HALF_UP));
			bill.setCreateDate(new Date());
			flag = billService.save(bill);
			Logger.debug("reduceAccountByEmalil    [insert child DisBill]line----->" + flag);
			bill.setId(null);
			bill.setSources(3);//总交易记录
			bill.setPaymentType("核减");
			flag = billService.save(bill);
			Logger.debug("reduceAccountByEmalil    [insert main DisBill]line----->" + flag);
			result.put("suc", true);
			result.put("msg", "核减余额成功");
		}
		return result.toString();
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getReduceAccountHistory(Integer accountId) {
		ObjectNode result = Json.newObject();
		List<AccountOperationRecord> records = accountOperationRecordMapper.selectByAccountId(accountId);
		if (records != null && records.size() > 0) {
			for (AccountOperationRecord record : records){
				record.setOperateTimeDesc(new DateTime(record.getOperateTime()).toString("yyyy-MM-dd HH:mm:dd"));
			}
		}
		result.put("suc", true);
	    result.put("data", Json.toJson(records));
		return result.toString();
	}

	@Override
	public ResultDto<T> unfreeze(String em) {
		try {
			DisAccount account = disAccountMapper.getDisAccountByEmail(em);
			if(account == null){
				return new ResultDto<>(false,"操作失败");
			}
			
			account.setFrozen(false);
			account.setUpdateDate(new Date());
			disAccountMapper.updateByPrimaryKeySelective(account);
			setUserHistory(em, "解除账户冻结");
			return new ResultDto<>(true,"操作成功");
		} catch (Exception e) {
			Logger.info("解除账户冻结异常"+e);
			return new ResultDto<>(false,"系统异常");
		}
	}

	@Override
	public ResultDto<T> changeNickName(String string) {
		try {
			JsonNode json = Json.parse(string);
			String email = json.get("em").asText();
			DisMember disMember = new DisMember();
			disMember.setRoleId(2);
			disMember.setEmail(email);
			disMember = disMemberMapper.getMember(disMember);
			if(disMember == null){
				return new ResultDto<>(false,"该分销商不存在");
			}
			
			String oldNick =  disMember.getNickName() != null?disMember.getNickName():"空";
			disMember.setNickName(json.get("nickName").asText());
			disMember.setLastUpdateDate(new Date());
			disMemberMapper.updateByPrimaryKeySelective(disMember);
			setUserHistory(email, "修改分销商昵称:"+oldNick+"->"+disMember.getNickName());
			ebus.post(disMember);
			return new ResultDto<>(true,"修改昵称成功");
		} catch (Exception e) {
			Logger.info("修改昵称异常"+e);
			return new ResultDto<>(false,"修改昵称异常");
		}
	}
	
	

	/* 
	 * 账期冻结金额：当账期剩余到达该金额时冻结账户
	 * (non-Javadoc)
	 * @see services.dismember.IDisAccountService#queryFrozenAmount(java.lang.String)
	 */
	@Override
	public Double queryPeriodFrozen(String email) {
		DisAccount account = disAccountMapper.getDisAccountByEmail(email);
		if(account == null || null == account.getPeriodFrozen()) {
			return 0.0;
		}
		return account.getPeriodFrozen();
	}

	@Override
	public ResultDto<T> setPeriodFrozen(String json) {
		if(StringUtils.isEmpty(json)) {
			return new ResultDto<>(false, "非法参数，请重新确认后提交。");
		}
		JsonNode node = Json.parse(json);
		String email = JsonCaseUtil.jsonToString(node.get("account"));
		Double amount = JsonCaseUtil.jsonToDouble(node.get("amount"));
		if(amount == null || StringUtils.isEmpty(email)) {
			return new ResultDto<>(false, "非法参数，请重新确认后提交。");
		}
		DisAccount account = new DisAccount();
		account.setEmail(email);
		account.setPeriodFrozen(amount);
		//更新
		disAccountMapper.updatePeriodFrozen(account);
		//记录日志
		setUserHistory(email,"设置账期冻结额度为【"+amount+"】");
		return new ResultDto<>(true, "设置账期冻结额度成功。");
	}

	private void setUserHistory(String email, String desc) {
		UserRankHistory history = new UserRankHistory();
		history.setEmail(email);
		history.setCreateTime(new Date());
		history.setOperator(loginService.getLoginContext(Constant.LOGIN_FROM_MARK_BACK).getEmail());
		history.setOperateDesc(desc);
		userRankHistoryMapper.insertSelective(history);
	}
}
