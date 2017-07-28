package handlers.dismember;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.gradle.internal.impldep.com.google.common.collect.Lists;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;

import constant.dismember.Constant;
import entity.dismember.AccountPeriodMaster;
import entity.dismember.AccountPeriodSlave;
import entity.dismember.ApReminderSetting;
import entity.dismember.DisMember;
import entity.dismember.EmailAccount;
import entity.dismember.EmailTemplate;
import events.dismember.AccountPeriodTextReminderEvent;
import mapper.dismember.AccountPeriodMasterMapper;
import mapper.dismember.AccountPeriodSlaveMapper;
import mapper.dismember.ApReminderSettingMapper;
import mapper.dismember.DisMemberMapper;
import mapper.dismember.EmailAccountMapper;
import mapper.dismember.EmailTemplateMapper;
import play.Logger;
import utils.dismember.DateUtils;
import utils.dismember.SMSManager;

/**
 * 账期短信提醒
 * @author Administrator
 *
 */
public class AccountPeriodTextReminderHandler {
	
	@Inject
	private ApReminderSettingMapper reminderSettingMapper;
	
	@Inject
	private AccountPeriodMasterMapper accountPeriodMasterMapper;
	
	@Inject
	private AccountPeriodSlaveMapper accountPeriodSlaveMapper;
	
	@Inject
	private EmailAccountMapper emailMapper;
	
	@Inject
	private EmailTemplateMapper templateMapper;
	
	@Inject
	private DisMemberMapper disMemberMapper;

	/**
	 * 发送短信
	 * @param event
	 */
	@Subscribe
	public void sendMessage(AccountPeriodTextReminderEvent event){
		// 获取有效的
		List<ApReminderSetting> settings = reminderSettingMapper.selectAll(true);
		
		boolean notPermitted = CollectionUtils.isEmpty(settings);
		if (notPermitted) {
			Logger.info("账期短信提醒：没有开启的账期短信提醒配置");
			return;
		}
		
		SendMsgContext checkTemplate = checkTemplate();
		if (!checkTemplate.isSuc()) {
			Logger.info("账期短信提醒：没有短信配置或配置短信模板");
			return;
		}
		
		// 有效的账期
		List<AccountPeriodSlave> accountPeriodSlaveList = accountPeriodSlaveMapper.selectValid();
		if (CollectionUtils.isEmpty(accountPeriodSlaveList)) {
			Logger.info("账期短信提醒：没有有效账期");
		}
		Logger.info("账期短信提醒：有效账期数量为：{}", accountPeriodSlaveList.size());
		
		// 给账期设置分销商
		List<Integer> masterIdList = Lists.transform(accountPeriodSlaveList, slave->slave.getMasterId());
		List<AccountPeriodMaster> masterList = accountPeriodMasterMapper.selectByIds(masterIdList);
		Map<Integer, String> masterId2Account = masterList.stream().collect(Collectors.toMap(master->master.getId(), master->master.getAccount()));
		accountPeriodSlaveList.stream().forEach(slave->{
			Integer masterId = slave.getMasterId();
			slave.setAccount(masterId2Account.get(masterId));
		});
		
		List<AccountPeriodSlave> allSlavesMatched = Lists.newArrayList();
		// 循环配置
		for (ApReminderSetting setting : settings) {
			// 获取符合条件的
			List<AccountPeriodSlave> slavesMatched = canSendTextAP(setting.getAccount(), setting.getDaysAgo(), accountPeriodSlaveList);
			if (CollectionUtils.isEmpty(slavesMatched)) {
				Logger.info("账期短信提醒：{}没有符合条件的账期需要发送短息提醒",setting.getAccount());
				continue;
			}

			Logger.info("账期短信提醒：{}符合的账期数量：{}条",setting.getAccount(),slavesMatched.size());
			allSlavesMatched.addAll(slavesMatched);
		}
		
		if (CollectionUtils.isEmpty(allSlavesMatched)) {
			Logger.info("账期短信提醒：没有符合条件的账期需要发送短息提醒");
			return;
		}
		
		// 获取分销商账号
		Set<String> accountSet = allSlavesMatched.stream().map(slave->slave.getAccount()).collect(Collectors.toSet());
		List<DisMember> dismemberList = disMemberMapper.selectByEmailList(Lists.newArrayList(accountSet));

		Map<String, List<AccountPeriodSlave>> slavesByAccount = allSlavesMatched.stream().collect(Collectors.groupingBy(slave->slave.getAccount()));
		Map<String, String> account2Telphone = dismemberList.stream()
				.collect(Collectors.toMap(dismember -> dismember.getEmail(), dismember -> dismember.getTelphone()));

		Map<String, List<String>> telphone2ContractPeriodDateList = telphone2ContractPeriodDateList(slavesByAccount, account2Telphone);
		if (telphone2ContractPeriodDateList.size() == 0) {
			Logger.info("账期短信提醒：没有合适数据（手机号和合同账期）用于发送短信");
			return;
		}

		Logger.info("账期短信提醒：要发送短信提醒的有：{}", telphone2ContractPeriodDateList);
		
		// 发送短信
		sendText(telphone2ContractPeriodDateList, checkTemplate.getEmailAccount(), checkTemplate.getTemplate());
	}

	/**
	 * 筛选指定分销商的可以发送短信的账期
	 * @param account 指定分销商
	 * @param daysAgo 天数
	 * @param accountPeriodSlaveList 所有有效的账期
	 * @return
	 */
	private List<AccountPeriodSlave> canSendTextAP(String account, int daysAgo, List<AccountPeriodSlave> accountPeriodSlaveList) {
		LocalDate now = LocalDate.now();
		
		// 筛选出指定分销商的
		List<AccountPeriodSlave> slavesMatched = accountPeriodSlaveList.stream()
			.filter(slave->Objects.equal(account, slave.getAccount()))
			.filter(slave->{
				LocalDate contractPeriodDate = DateUtils.toLocalDate(slave.getContractPeriodDate());
				// 计算当前时间距离账期时间还有几天
				int daysInterval = new Long(now.until(contractPeriodDate, ChronoUnit.DAYS)).intValue();
				if (daysInterval < 0) {
					return false;
				}
				
				daysInterval = daysInterval + 1;
				// 这时可以发短信提醒的条件：合同账期是3.14日，那么提醒的日期是3.12、3.13、3.14 三天的每天中午十二点
				boolean canSendText = daysInterval <= daysAgo;
				return canSendText;
			}).collect(Collectors.toList());
		
		return slavesMatched;
	}

	/**
	 * 发送短信
	 * @param telphone2ContractPeriodDate 手机号码 = [账期时间（xxxx年xx月xx日）,...]
	 * @param emailAccount 短信账户
	 * @param template 短信模板
	 */
	private void sendText(Map<String, List<String>> telphone2ContractPeriodDateList, EmailAccount emailAccount,
			EmailTemplate template) {
		int sendSuccessCount = 0;
		// 发送短信
		for (Map.Entry<String, List<String>> entry : telphone2ContractPeriodDateList.entrySet()) {
			for (String contractPeriodDate : entry.getValue()) {
				try {
					String content = template.getCcontent().replaceAll("yyyyMMdd", contractPeriodDate).replace("{{", "").replace("}}", "");
					String phone = entry.getKey();
					SMSManager.send(content, emailAccount, phone);
					sendSuccessCount++;
				} catch (Exception e) {
					// 这里只会跳过发生异常的那个
					Logger.info("账期短信提醒：{}发送账期提醒失败，短信内容为：{}，异常为：{}", entry.getKey(), entry.getValue(), e);
				}
			}
		}
		Logger.error("账期短信提醒：成功发送{}条短信", sendSuccessCount);
	}
	
	/**
	 * 检查短信配置及模板配置是否合理
	 * @return
	 */
	private SendMsgContext checkTemplate(){
		EmailAccount emailAccountParam = new EmailAccount();
		emailAccountParam.setCtype(Constant.SEND_MSG);
		EmailAccount emailAccount = emailMapper.select(emailAccountParam);
		if (emailAccount == null) {
			Logger.error("账期短信提醒:短信发送失败！获取不到短信配置！");
			return new SendMsgContext(false);
		}
		
		EmailTemplate template = templateMapper.select(Constant.AP_REMINDER);
		if (null == template || StringUtils.isEmpty(template.getCcontent())
				|| !template.getCcontent().contains("yyyyMMdd")) {
			Logger.error("账期短信提醒:未配置短信模板：" + new Date());
			return new SendMsgContext(false);
		}

		return new SendMsgContext(true,emailAccount,template);
	}
	
	private Map<String, List<String>> telphone2ContractPeriodDateList(Map<String, List<AccountPeriodSlave>> slavesByAccount,
			Map<String, String> account2Telphone) {

		Map<String, List<String>> result = Maps.newHashMap();
		for (Map.Entry<String, String> entry : account2Telphone.entrySet()) {
			String account = entry.getKey();
			String telphone = entry.getValue();
			
			List<AccountPeriodSlave> slaveList = slavesByAccount.get(account);
			if (CollectionUtils.isNotEmpty(slaveList)) {
				List<String> contractPeriodDateList = Lists.transform(slaveList, slave->{
					return DateUtils.date2string(slave.getContractPeriodDate(), DateUtils.FORMAT_LOCAL_DATE);
				});
				result.put(telphone, contractPeriodDateList);
			}
		}
		
		return result;
	}
	
	private class SendMsgContext {
		boolean suc;
		private EmailAccount emailAccount;
		private EmailTemplate template;
		private SendMsgContext(boolean suc){
			this(suc,null,null);
		}
		private SendMsgContext(boolean suc, EmailAccount emailAccount, EmailTemplate template){
			this.suc = suc;
			this.emailAccount = emailAccount;
			this.template = template;
		}
		public boolean isSuc() {
			return suc;
		}
		public EmailAccount getEmailAccount() {
			return emailAccount;
		}
		public EmailTemplate getTemplate() {
			return template;
		}
	}
}
