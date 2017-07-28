package services.dismember.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.common.io.Files;
import com.google.inject.Inject;

import constant.dismember.Constant;
import dto.dismember.ApplyDto;
import dto.dismember.WithdrawBalanceDto;
import entity.dismember.AccountPeriodMaster;
import entity.dismember.ApChange;
import entity.dismember.CreditOperationRecord;
import entity.dismember.DisAccount;
import entity.dismember.DisApply;
import entity.dismember.DisBill;
import entity.dismember.DisCredit;
import entity.dismember.DisMember;
import entity.dismember.DisWithdrawAccount;
import entity.dismember.DisWithdrawLimit;
import entity.dismember.EmailAccount;
import entity.dismember.EmailTemplate;
import entity.dismember.OperationRecord;
import entity.dismember.OrderByAp;
import entity.dismember.ShopSite;
import events.dismember.BackUpApOrderDetailEvent;
import mapper.dismember.AccountPeriodMasterMapper;
import mapper.dismember.ApChangeMapper;
import mapper.dismember.CreditOperationRecordMapper;
import mapper.dismember.DisAccountMapper;
import mapper.dismember.DisApplyMapper;
import mapper.dismember.DisBillMapper;
import mapper.dismember.DisCreditMapper;
import mapper.dismember.DisMemberMapper;
import mapper.dismember.DisWithdrawAccountMapper;
import mapper.dismember.DisWithdrawLimitMapper;
import mapper.dismember.EmailAccountMapper;
import mapper.dismember.EmailTemplateMapper;
import mapper.dismember.OperationRecordMapper;
import mapper.dismember.OrderByApMapper;
import mapper.dismember.ShopSiteMapper;
import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.Http.MultipartFormData.FilePart;
import services.dismember.IApplyService;
import services.dismember.ICaptchaService;
import services.dismember.ILoginService;
import services.dismember.ISequenceService;
import utils.dismember.CreditTypes;
import utils.dismember.DateUtils;
import utils.dismember.HttpUtil;
import utils.dismember.IDUtils;
import utils.dismember.MD5Util;
import utils.dismember.PriceFormatUtil;
import utils.dismember.SMSManager;
import vo.dismember.LoginContext;
import vo.dismember.Page;

public class ApplyService implements IApplyService {

	@Inject
	private DisApplyMapper applyMapper;
	@Inject
	private DisBillMapper billMapper;
	@Inject
	private OperationRecordMapper opMapper;
	@Inject
	private DisCreditMapper disCreditMapper;
	@Inject
	private CreditOperationRecordMapper creditOperationRecordMapper;
	@Inject
	private ShopSiteMapper shopSiteMapper;
	@Inject
	private DisMemberMapper disMemberMapper;
	@Inject
	private ISequenceService sequenceService;
	@Inject
	private ILoginService loginService;
    @Inject
    private DisWithdrawAccountMapper withdrawAccountMapper;
    @Inject
    private DisWithdrawLimitMapper withdrawLimitMapper;
	@Inject
	private DisAccountMapper disAccountMapper;
	@Inject
	private ICaptchaService captchaService;
	@Inject
	private AccountPeriodMasterMapper accountPeriodMapper;
	@Inject
	private ApChangeMapper apChangeMapper;
	@Inject
	private OrderByApMapper orderByApMapper;
    @Inject
    private DisBillService billService;
	@Inject
	private EmailAccountMapper emailMapper;
	@Inject
	private EmailTemplateMapper templateMapper;
	@Inject
	private EventBus ebus;

	private static String filePath = "";
    
	@Override
	public Map<String, Object> sendOnlineApply(DisApply apply) {
		Map<String, Object> result = Maps.newHashMap();
		
		apply.setAuditState(Constant.APPLY_ONLINE_UNPAID);
		apply.setReviewState(Constant.AUDIT_PASS);
		apply.setApplyType("1");// 暂时设置为充值类型
		apply.setCreatedate(new Date());
		apply.setTransferDesc("在线充值");
		apply.setActualAmount(apply.getTransferAmount());
		try {
			apply.setOnlineApplyNo(IDUtils.getOnlineTopUpCode("CZ", sequenceService.selectNextValue("ONLINE_APPLY_NO")));
			
			applyMapper.insertSelective(apply);
			Logger.info("分销商【" + apply.getEmail() + "】发送在线充值申请成功！");

			result.put("success", true);
			result.put("bean", apply);// 成功
			return result;
		} catch (Exception e) {
			Logger.error("Send Apply Fail :" + apply.getEmail() + ",errorInfo:" + e);
			result.put("success", false);
			result.put("code", 1);
			return result;
		}
	}
	
	@Override
	public Map<String, Object> sendApply(DisApply apply) {
		Map<String, Object> result = Maps.newHashMap();
		
		apply.setAuditState(Constant.AUDIT_PENDING);// 申请状态：待审核
		apply.setReviewState(Constant.AUDIT_REVIEW);
		apply.setApplyType("1");// 暂时设置为充值类型
		apply.setCreatedate(new Date());
		try {
			applyMapper.insertSelective(apply);
			Logger.info("分销商【" + apply.getEmail() + "】发送申请成功！");
			//出现充值待初审
			remindBuserAudit(27);
			result.put("success", true);
			result.put("code", 0);// 成功
			return result;
		} catch (Exception e) {
			Logger.error("Send Apply Fail :" + apply.getEmail() + ",errorInfo:" + e);
			result.put("success", false);
			result.put("code", 1);
			return result;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * services.dismember.IApplyService#updateApply(entity.dismember.DisApply)
	 */
	@Override
	public Map<String, Object> updateApply(DisApply apply, String opEamil) {
		//如果在线充值    直接更新余额并生成交易记录
		if(apply.getOnlineApplyNo() != null){
			String tradeNo = apply.getTransferNumber();
			apply = applyMapper.getApplyByOnlinePayNo(apply.getOnlineApplyNo());
			
			if(apply.getAuditState() == Constant.APPLY_ONLINE_PAID){
				Map<String, Object> result = Maps.newHashMap();
				result.put("success", false);
				result.put("code", 1);//支付成功的在线充值不再更新
				return result;
			}
			
			// 修改审核状态（修改为在线支付已完成），更新交易号
			apply.setAuditState(Constant.APPLY_ONLINE_PAID);
			apply.setTransferNumber(tradeNo);
			apply.setTransferTime(new Date());
			applyMapper.updateByPrimaryKeySelective(apply);
			
			updateAccount(apply);
			
			Map<String, Object> result = Maps.newHashMap();
			result.put("success", true);
			result.put("code", 2);// 修改成功
			return result;
		}
		
		Map<String, Object> result = Maps.newHashMap();
		Integer auditState = apply.getAuditState();
		Integer reviewState = apply.getReviewState();
		Logger.info("处理审核,初审状态 :" + auditState + ",复审状态：" + reviewState + ",申请ID:" + apply.getId() + ",处理时间：" + now());
		apply.setUpdatedate(new Date());
		boolean flag = false;
		if (null != auditState) {
			if (auditState == Constant.AUDIT_NOT_PASS) {
				apply.setReviewState(Constant.AUDIT_NOT_PASS);
			} else {
				apply.setReviewState(Constant.AUDIT_REVIEW);
				flag = true;
			}
		}
		// 修改审核
		applyMapper.updateByPrimaryKeySelective(apply);
		if(flag){
			//初审通过，复审为待初审，此时发送短信
			remindBuserAudit(28);
		}
		// 生成操作记录
		createOperateRecord(apply.getId(), opEamil, auditState, reviewState);
		// 生成交易记录
		if (null != reviewState && reviewState == Constant.AUDIT_PASS) {
			// 更新余额并生成交易记录
			updateAccount(apply);
		}
		result.put("success", true);
		result.put("code", 2);// 修改成功
		return result;
	}

	private String now() {
		return DateUtils.date2FullDateTimeString(new Date());
	}

	/**
	 * 创建申请操作记录
	 * 
	 * @param id
	 * @param opEamil
	 * @param auditState
	 * @param reviewState
	 */
	public void createOperateRecord(Integer id, String opEamil, Integer auditState, Integer reviewState) {
		// 生成操作记录
		OperationRecord op = new OperationRecord();
		op.setApplyId(id);
		op.setOperator(opEamil);
		op.setOpdate(new Date());
		String desc = auditState != null ? opEamil + "操作初审状态变为："
				+ Constant.APPLY_STATE_MAP.get(auditState)
				: reviewState != null ? opEamil + "操作复审状态变为："
						+ Constant.APPLY_STATE_MAP.get(reviewState) : "";
		op.setOpdesc(desc);
		opMapper.insertSelective(op);
	}
	
	/**
	 * 新版充值 资金流向
     * 查找是否欠款的账期，按照时间顺序 从早到晚，依次还清 最后才是余额还款
	 * @author zbc
	 * @since 2017年2月22日 下午3:58:20
	 */
	public Map<String, Object> _handleGivenMoney(BigDecimal amount,DisAccount account,DisBill bill){
		Map<String,Object> result = Maps.newHashMap();
		BigDecimal balance = account.getBalance();
		BigDecimal applyAmount = amount;
		String email = account.getEmail();
		//查询欠款账期
		List<AccountPeriodMaster> list = accountPeriodMapper.getUsedAps(email);
		BigDecimal creditLimitBalance = BigDecimal.ZERO;
		Date updateDate = new Date();
		if(list.size()>0){
			BigDecimal refundLeft = applyAmount;//还款剩余
			BigDecimal curRefund = BigDecimal.ZERO;//当前还款
			//账期还款，余下充值余额
			for(AccountPeriodMaster el:list){
				if(BigDecimal.ZERO.compareTo(refundLeft) > 0){
					break;
				}else if(refundLeft.compareTo(el.getUsedLimit()) > 0){
					//如果还款剩余大于 已用额度 即 当前账期还款  为已用额度
					curRefund = el.getUsedLimit();
					//还款剩余减到当前已用
				}else{
					//如果还款剩余 小于 当前已用额度 就 当前账期还款 为 还款剩余
					curRefund = refundLeft;
				}
				//更新账期可用金额	
				updateAp(bill, null, updateDate, curRefund,el,ApChange.REFUND);
				refundLeft = applyAmount.subtract(curRefund);
			}
			if(refundLeft.compareTo(BigDecimal.ZERO) > 0){
				account.setBalance(refundLeft);
				account.setUpdateDate(updateDate);
				disAccountMapper.updateByPrimaryKeySelective(account);
				Logger.info("充值成功，账户存入" + refundLeft + "元");
				// 生成交易记录
				createBill(DisBill.SOURCES_BALANCE, "余额充值", refundLeft, account.getBalance(), bill,null);
			}
		}else{
			//充值余额
			account.setBalance(balance.add(amount));
			account.setUpdateDate(new Date());
			disAccountMapper.updateByPrimaryKeySelective(account);
			Logger.info("充值成功，账户存入" + amount + "元");
			// 生成交易记录
			createBill(DisBill.SOURCES_BALANCE, "余额充值", amount, account.getBalance(), bill,null);
		}
		result.put("suc", true);
		result.put("code", 4);
		AccountPeriodMaster accountPeriod = accountPeriodMapper.getValidAp(email);
		creditLimitBalance = accountPeriod != null
				? accountPeriod.getTotalLimit().subtract(accountPeriod.getUsedLimit()) : BigDecimal.ZERO;
		// 生成交易记录 该条交易记录为当前申请的总额度
		createBill(DisBill.SOURCES_ALL, "充值", applyAmount, account.getBalance(), bill,creditLimitBalance);
		return result;
	}

	/**
	 * @param bill                          交易记录实体
	 * @param balance                       支付金额  充值调用为 null
	 * @param updateDate                    交易时间
	 * @param currentUsedAmount             账期使用额度
	 * @param el                            账期实体
	 * @param type                          交易类型 0：支付 1 ：充值
	 * 更新账期可用金额
	 * @author zbc
	 * @since 2017年2月22日 下午6:32:13
	 */
	private void updateAp(DisBill bill, BigDecimal amount, Date updateDate,BigDecimal currentUsedAmount,
			AccountPeriodMaster el,Integer type) {
		el.setUsedLimit(el.getUsedLimit().subtract(new BigDecimal(caculteType(type) + currentUsedAmount)));
		el.setUpdateDate(updateDate);
		if(type == ApChange.REFUND){
			//统计还款总额
			el.setRechargeLeft(el.getRechargeLeft().add(currentUsedAmount));
			el.setRechargeTotal(el.getRechargeTotal().add(currentUsedAmount));
		}
		//剩余账期额度
		if(accountPeriodMapper.updateByPrimaryKeySelective(el) > 0){
			// 生成交易记录
			createBill(DisBill.SOURCES_PERMANNENT,"账期"+(type == ApChange.PAY?"支付":"充值"),currentUsedAmount,el.getTotalLimit().subtract(el.getUsedLimit()), bill, null);
			//插入账期相关数据
			saveApData(amount, bill.getSerialNumber(), el,currentUsedAmount,type);
		}
	}
	
	/**
	 * (旧)处理充值资金
	 * @param amount 充值的金额
	 * @param account 指定的账号对象
	 * @param bill  交易记录对象
	 * @return
	 * 充值资金流向规则： 1.充值资金的资金最先还临时额度里已使用的额度（债务），如果还有剩余，消永久额度使用的额度，最后 还有剩余存入账户
	 * 2.一个分销商可能有:1.永久额度，临时额度 ， 2.临时额度 3.永久 4.两种额度都没有
	 */
	@Deprecated
	public Map<String, Object> handleGivenMoney(BigDecimal amount,DisAccount account,DisBill bill){
		Map<String,Object> result = Maps.newHashMap();
		BigDecimal balance = account.getBalance();
		BigDecimal applyAmount = amount;
		DisCredit shortCredit = getCredit(account.getEmail(), CreditTypes.TEMPORARY, 1 , null);// 获取临时额度mapp文件中默认查询此字段is_finished为false
		DisCredit longCredit = getCredit(account.getEmail(), CreditTypes.PERMANNENT, null , null);// 获取永久额度
		DisCredit credit = shortCredit == null ? longCredit : shortCredit;
		if (amount != null && amount.compareTo(BigDecimal.ZERO) != 0) {
			DisAccount dis = new DisAccount();// 此分销商对应的账户的更新
			dis.setId(account.getId());
			dis.setUpdateDate(new Date());
			if (null != credit) {
				// 充值金额>credit已使用额度？（1.消除 credit已使用额度，2.并判断永久额度是否存在）
				Logger.info("credit--------->" + credit.getUsedAmount());
				BigDecimal nowUsed = credit.getUsedAmount();
				if (amount.compareTo(nowUsed) >= 0) {
					if (nowUsed.compareTo(BigDecimal.ZERO) != 0) {// 已用额度不等于0，说明credit此额度需要充值，等于0的话不走此if，下面会有处理
						amount = amount.subtract(nowUsed);// 消掉credit此额度后的充值剩余的钱(本应是更新了之后再计算，因为预知是全消，所以先计算）
						credit.setUsedAmount(new BigDecimal(0));
						credit.setUpdatedate(new Date());
						if (credit.getEndTime() != null && credit.getEndTime().before(new Date())
								&& credit.getRedit() == 1) {
							credit.setLimitState(3);
							credit.setIsFinished(true);
						}
						disCreditMapper.updateByPrimaryKeySelective(credit);
						createCreditRecord(credit, nowUsed, account.getEmail(), 1);// 创建额度信息更改日志
						// 生成交易记录
						createBill(credit.getRedit(), "额度还款", nowUsed, credit.getCreditLimit(), bill,null);
					} else {
						// 在credit已使用额度为0并且为临时的情况下，而此额度信息里的endtime（还款日期）又到了，就应该为已还款和已失效
						if (credit.getEndTime() != null && credit.getEndTime().before(new Date())
								&& credit.getRedit() == 1) {
							credit.setLimitState(3);
							credit.setIsFinished(true);
							credit.setUpdatedate(new Date());
							disCreditMapper.updateByPrimaryKeySelective(credit);
						}
					}
					if (null == longCredit) {
						if (amount.compareTo(BigDecimal.ZERO) != 0) {
							balance = balance.add(amount);// 充值剩余的钱加到余额中
							dis.setBalance(balance);
							dis.setUpdateDate(new Date());
							dis.setId(account.getId());
							disAccountMapper.updateByPrimaryKeySelective(dis);
							Logger.info("充值成功，临时已使用额度" + credit.getUsedAmount() + "元，账户存入" + amount + "元");
							// 生成交易记录
							createBill(0, "余额充值", amount, balance, bill,null);
						}
						result.put("suc", true);
						result.put("code", 4);
					} else {// 说明刚才消的是 shortcredit，此次要消longCredit已使用的额度
						// 判断amount是否大于 longCredit已使用的额度
						if (amount.compareTo(longCredit.getUsedAmount()) >= 0) {
							if (longCredit.getUsedAmount().compareTo(BigDecimal.ZERO) != 0) {
								nowUsed = longCredit.getUsedAmount();
								amount = amount.subtract(longCredit.getUsedAmount());// 此处应该是先更新永久额度，再来计算充值的剩余的钱，但是这里永久全消，可以提前计算
								// 消掉longCredit全部额度，并且 把剩余的amount添加到余额
								longCredit.setUsedAmount(new BigDecimal(0));
								longCredit.setUpdatedate(new Date());
								disCreditMapper.updateByPrimaryKeySelective(longCredit);// 更新longCredit额度信息
								createCreditRecord(longCredit, longCredit.getUsedAmount(), account.getEmail(), 1);// 创建额度信息更改
								// 生成交易记录
								createBill(longCredit.getRedit(), "额度还款", nowUsed, longCredit.getCreditLimit(), bill,null);
							}
							if (amount.compareTo(BigDecimal.ZERO) != 0) {
								dis.setBalance(balance.add(amount));
								dis.setUpdateDate(new Date());
								dis.setId(account.getId());
								disAccountMapper.updateByPrimaryKeySelective(dis);// 更新账户信息
								Logger.info("充值成功，临时已使用额度" + credit.getUsedAmount() + "元，永久已使用"
										+ longCredit.getUsedAmount() + "元，账户存入" + amount + "元");
								// 生成交易记录
								createBill(0, "余额充值", amount, balance.add(amount), bill,null);

							}
							result.put("suc", true);
							result.put("code", 4);
						} else {
							// 消掉longCredit部分 已使用的额度
							if (amount.compareTo(BigDecimal.ZERO) != 0) {// 表示充值剩余的钱恰好为0，那充值就没有意义
								longCredit.setUsedAmount(longCredit.getUsedAmount().subtract(amount));
								longCredit.setUpdatedate(new Date());
								longCredit.setId(longCredit.getId());
								disCreditMapper.updateByPrimaryKeySelective(longCredit);
								createCreditRecord(longCredit, longCredit.getUsedAmount(), account.getEmail(), 1);// 创建额度信息更改
								// 日志
								Logger.info("充值成功，临时已使用额度" + shortCredit == null ? new BigDecimal(0).toString()
										: credit.getUsedAmount() + "元，longCredit" + amount + "元");
								// 生成交易记录
								createBill(longCredit.getRedit(), "额度还款", amount,
										longCredit.getCreditLimit().subtract(longCredit.getUsedAmount()), bill,null);
							}
							result.put("suc", true);
							result.put("code", 4);
						}
					}
				} else {
					// 消除部分credit已使用的信用额度
					credit.setUsedAmount(credit.getUsedAmount().subtract(amount));
					credit.setUpdatedate(new Date());
					disCreditMapper.updateByPrimaryKeySelective(credit);
					Logger.info(
							"充值成功，" + (credit.getRedit() == 1 ? "临时已使用额度" : "永久已使用额度") + credit.getUsedAmount() + "元");
					createCreditRecord(credit, credit.getUsedAmount(), account.getEmail(), 1);// 创建额度信息更改
					// 日志
					// 生成交易记录
					createBill(credit.getRedit(), "额度还款", amount,
							credit.getCreditLimit().subtract(credit.getUsedAmount()), bill,null);
					result.put("suc", true);
					result.put("code", 4);
				}
			} else {
				// 充值余额
				dis.setBalance(balance.add(amount));
				dis.setUpdateDate(new Date());
				dis.setId(account.getId());
				disAccountMapper.updateByPrimaryKeySelective(dis);
				Logger.info("充值成功，账户存入" + amount + "元");
				// 生成交易记录
				createBill(0, "余额充值", amount, dis.getBalance(), bill,null);
				result.put("suc", true);
				result.put("code", 4);
			}
		} else {
			Logger.info("金额不规范，修改余额失败");
			result.put("success", false);
			result.put("code", 1);// 修改成功
		}

		DisAccount accountByEmail = disAccountMapper.getDisAccountByEmail(account.getEmail());
		DisCredit crdit = disCreditMapper.getDisCreditInfo(accountByEmail.getEmail());
		BigDecimal creditLimitBalance = BigDecimal.ZERO;
		if(crdit != null){
			creditLimitBalance = crdit.getTotalCreditLimit().subtract(crdit.getTotalUsedAmount());
		}
		// 生成交易记录 该条交易记录为当前申请的总额度
		createBill(3, "充值", applyAmount, accountByEmail.getBalance(), bill,creditLimitBalance);
		return result;
	}

	@Override
	/**
	 * 充值资金流向规则： 1.充值资金的资金最先还临时额度里已使用的额度（债务），如果还有剩余，消永久额度使用的额度，最后 还有剩余存入账户
	 * 2.一个分销商可能有:1.永久额度，临时额度 ， 2.临时额度 3.永久 4.两种额度都没有
	 */
	public Map<String, Object> updateAccount(DisApply apply) {
		// 根据申请ID查询申请的其他字段信息
		DisApply disApply = applyMapper.selectByPrimaryKey(apply.getId());
		// 根据申请的email查询账户
		DisAccount account = disAccountMapper.getDisAccountByEmail(disApply.getEmail());
		// 申请金额 分销商申请金额
		BigDecimal amount = disApply.getActualAmount();
		DisBill bill = new DisBill(account.getId(), amount, disApply.getApplyType(), disApply.getTransferNumber(),
				"", apply.getId(), disApply.getTransferCard());
		int count = 0;
		String remark = "";
		if(disApply.getApplyRemark() != null){
			count++;
			remark += count+"、"+disApply.getApplyRemark()+",";
		}
		if(disApply.getAuditRemark()!= null){
			count++;
			remark += count+"、"+disApply.getAuditRemark()+",";
		}
		if(disApply.getReAuditRemark()!= null){
			count++;
			remark += count+"、"+disApply.getReAuditRemark()+",";
		}
		if(count > 0){
			bill.setRemark(remark);
		}
		Logger.info(
				"处理用户【" + disApply.getEmail() + "】账号信息，申请类型为【" + disApply.getApplyType() + "】处理金额为【" + amount + "】");
		return _handleGivenMoney(amount,account,bill);
	}

	@Override
	public Page<ApplyDto> queryApply(Map<String, Object> param) {
		Integer rows = applyMapper.queryApplyCount(param);
		return new Page<>(Integer.parseInt(param.get("currPage") + ""), Integer.parseInt(param.get("pageSize") + ""),
				rows, query(param));
	}

	private List<ApplyDto> trans(List<DisApply> applys) {
		ApplyDto dto = null;
		List<ApplyDto> list = new ArrayList<>();
		for (DisApply dis : applys) {
			dto = new ApplyDto();
			dto.setId(dis.getId());// id
			dto.setName(dis.getDistributorName());// 账户开户名
			dto.setAuditState(Constant.APPLY_STATE_MAP.get(dis.getAuditState()));// 初审状态
			dto.setTransferAmount(dis.getTransferAmount());// 付款金额
			dto.setTransTime(dis.getTransferTime() == null ? "" : DateUtils.date2FullDateTimeString(dis.getTransferTime()));// 实际付款日期
			dto.setTransferNumber(dis.getTransferNumber());// 付款流水号
			dto.setTransferCard(dis.getTransferCard());// 付款账户
			dto.setReceiptCard(dis.getReceiptCard());// 收款账户
			dto.setReceiptName(dis.getReceiptName());// 收款方
			dto.setEmail(dis.getEmail());// 用户名
			dto.setActualTime(dis.getActualDate() == null ? dis.getTransTime() : DateUtils.date2FullDateTimeString(dis.getActualDate()));// 实际到账日期
			dto.setScreenshotUrl(dis.getScreenshotUrl());// 截图路径
			BigDecimal actualAmount =  dis.getActualAmount() != null ? dis.getActualAmount().setScale(2,BigDecimal.ROUND_HALF_UP) : null;
			dto.setActualAmount(actualAmount);// 实际到账金额
			dto.setAuditReasons(dis.getAuditReasons());// 审核理由
			dto.setReviewState(Constant.APPLY_STATE_MAP.get(dis.getReviewState()));// 复审状态
			String state = dis.getReviewState() != null && dis.getReviewState() == 4 ? "待审核" : Constant.APPLY_STATE_MAP.get(dis.getReviewState());
			dto.setState(state);
			dto.setAudit(dis.getAuditState());
			dto.setAuditRemark(dis.getAuditRemark());
			dto.setReAuditRemark(dis.getReAuditRemark());
			dto.setTransferType(dis.getTransferType());
			dto.setOnlineApplyNo(dis.getOnlineApplyNo());
			dto.setApplyRemark(dis.getApplyRemark());
			dto.setApplyMan(dis.getApplyMan());
			dto.setNickName(dis.getNickName());
			list.add(dto);
		}
		return list;
	}

	/**
	 * 旧版支付，支持信用额度，
	 */
	@Deprecated
	@Override
	public Map<String, Object> payment(DisApply apply) {
		Map<String, Object> result = Maps.newHashMap();
		String email = apply.getEmail();
		// 根据申请的email查询账户
		DisAccount account = disAccountMapper.getDisAccountByEmail(email);
		if (null == account) {
			result.put("success", false);
			result.put("info", "未查询到账户");
			result.put("code", 3);
			return result;
		}

		// web安全整改，若密码输入错误三次，则加验证码，若错误五次，则锁定账户一小时 by huchuyin 2016-10-8
		// 查询输入失败次数，若大于等于5次，则查询禁用时间，若当前时间小于禁用时间，则提示账户已锁定
		Logger.info(this.getClass().getName()
				+ " payment InputErrorNumTimes------>"
				+ account.getInputErrorNumTimes());
		if (account.getInputErrorNumTimes() >= Constant.INPUT_ERROR_NUM_TIMES_LOCK) {
			Date curTime = new Date();
			Date disableTime = account.getDisableTime();
			Logger.info(this.getClass().getName() + " payment curTime==="
					+ curTime + " disableTime===" + disableTime);
			if (disableTime != null && curTime.before(disableTime)) {
				result.put("suc", false);
				result.put("info", "账户已锁定，请1小时后再输入！");
				result.put("code", 8);
				return result;
			}
		}

		// 若输入失败次数大于等于3次，则校验验证码
		if (account.getInputErrorNumTimes() >= Constant.INPUT_ERROR_NUM_TIMES_CODE) {
			if (!captchaService.verify(apply.getPayCaptcha())) {
				result.put("suc", false);
				result.put("info", "请输入正确的验证码");
				result.put("code", 9);
				return result;
			}
		}

		// 校验支付密码是否正确
		if (apply.getIsBackStage() == null
				&& !apply.getPassword().equals(account.getPayPass())) {
			// 若密码错误，则每次给输入失败次数加1，若增加后的数据大于等于5次，则更新禁用时间为当前时间加一小时，且提示账户锁定
			DisAccount accountParam = new DisAccount();
			Integer numberTimes = account.getInputErrorNumTimes() + 1;
			if (numberTimes >= Constant.INPUT_ERROR_NUM_TIMES_LOCK) {
				DateTime dateTime = new DateTime().plusHours(Constant.PWS_INPUT_LOCK_TIME);
				accountParam.setDisableTime(dateTime.toDate());
			}
			accountParam.setId(account.getId());
			accountParam.setInputErrorNumTimes(numberTimes);
			int line = disAccountMapper.updateByPrimaryKeySelective(accountParam);
			Logger.info(this.getClass().getName() + " [payment updateCount===]" + line);
			if (numberTimes < Constant.INPUT_ERROR_NUM_TIMES_LOCK) {
				result.put("info", "支付密码错误，还可以输入"
						+ (Constant.INPUT_ERROR_NUM_TIMES_LOCK - numberTimes)
						+ "次");
			} else {
				result.put("info", "支付密码错误，账户已锁定，请一小时后再输入！");
			}
			result.put("success", false);
			result.put("code", 7);
			return result;
		}

		// 若密码正确，则更新账户的输入次数重置为0，且将禁用时间清空
		DisAccount accountParam = new DisAccount();
		accountParam.setId(account.getId());
		accountParam.setInputErrorNumTimes(0);
		accountParam.setDisableTime(null);
		int line = disAccountMapper.updateInputErrorTimes(accountParam);
		Logger.info(this.getClass().getName() + " [payment updateInputErrorTimes===]" + line);
		// End by huchuyin 2016-10-8

		BigDecimal balance = account.getBalance();
		BigDecimal payment = apply.getTransferAmount();
		// 判断是否已经支付过
		DisBill bill = new DisBill();// 交易记录
		bill.setSerialNumber(apply.getTransferNumber());
		List<DisBill> billRecord = billMapper.queryBill(bill);
		if (null != billRecord && billRecord.size() > 0) {
			result.put("success", false);
			result.put("code", 6);
			result.put("info", "该订单已支付。");// 修改成功
			Logger.info("改订单已经支付：" + apply.getTransferNumber());
			return result;
		}
		
		bill.setPurpose(apply.getApplyType());// 申请类型：1、充值 2、提现 3、采购支付 4、退款
		// 5、支付运费 6、采购支付含运费
		bill.setSourceCard(account.getEmail());// 账户源
		bill.setAccountId(account.getId());// 账户ID

		if (balance.compareTo(payment) >= 0) {// 可用余额支付
			DisAccount dis = new DisAccount();
			dis.setId(account.getId());
			dis.setBalance(balance.subtract(payment));
			dis.setUpdateDate(new Date());
			disAccountMapper.updateByPrimaryKeySelective(dis);
			Logger.info("用户【" + email + "】支付成功，金额为：" + payment);
			// 生成交易记录
			createBill(DisBill.SOURCES_BALANCE, "余额支付", payment, dis.getBalance(), bill, null);
			result.put("success", true);
			result.put("code", 4);
			result.put("balance", dis.getBalance().doubleValue());
			result.put("info", "支付成功！账户余额为：" + dis.getBalance());// 修改成功
		} else {
			// 1、支付：使用余额+永久额度->余额+永久额度+临时额度->余额+临时额度
			Logger.info("用户【" + email + "】余额不足，需要支付金额为："
					+ payment.doubleValue());
			DisCredit permanentCredit = getCredit(email, CreditTypes.PERMANNENT, null, true);// 获取永久额度
			DisCredit temporaryCredit = getCredit(email, CreditTypes.TEMPORARY, null, true);// 获取临时额度
			// 优先使用永久额度
			DisCredit credit = permanentCredit == null ? temporaryCredit : permanentCredit;
			if (null != credit) {
				// 待支付金额大于（可用信用额度+余额）
				if (payment.compareTo(credit.getCreditLimit().subtract(credit.getUsedAmount()).add(balance)) > 0) {
					boolean flag = credit.getRedit() == CreditTypes.PERMANNENT 
							? (temporaryCredit == null 
									? false
									: (payment.compareTo(amountAvailable(balance, permanentCredit, temporaryCredit)) > 0 ? false : true)
							  )
							: false;
					
					// 如果为true则说明永久额度和临时额度同时存在 且永久额度不够需要临时额度补充
					if (flag) {
						// 使用临时额度支付剩余额度，余额和永久额度都为0
						DisAccount dis = new DisAccount();
						dis.setId(account.getId());
						dis.setBalance(BigDecimal.ZERO);
						if (balance.compareTo(BigDecimal.ZERO) > 0) {
							dis.setUpdateDate(new Date());
							disAccountMapper.updateByPrimaryKeySelective(dis);
							// 生成交易记录
							createBill(DisBill.SOURCES_BALANCE, "余额支付", balance, BigDecimal.ZERO,bill, null);
						}

						// 更新永久额度
						DisCredit newcredit = new DisCredit();
						newcredit.setId(credit.getId());
						newcredit.setUsedAmount(credit.getCreditLimit());// 已使用额度
						newcredit.setLimitState(2);// 使用中
						disCreditMapper.updateCredit(newcredit);
						if (credit.getCreditLimit().compareTo(credit.getUsedAmount()) != 0) {
							createCreditRecord(credit,newcredit.getUsedAmount(), email, 2);
							// 生成交易记录
							createBill(credit.getRedit(),"额度支付",credit.getCreditLimit().subtract(credit.getUsedAmount()),BigDecimal.ZERO, bill, null);
						}

						// 更新临时额度
						// 新临时额度已使用 = 旧临时额度已使用 +
						// (支付金额-余额-(临时额度总额-旧临时额度已使用))
						BigDecimal shortUsed = payment.subtract(balance)
								.subtract(credit.getCreditLimit().subtract(credit.getUsedAmount()));
						temporaryCredit.setUsedAmount(temporaryCredit.getUsedAmount().add(shortUsed));
						temporaryCredit.setLimitState(2);// 使用中
						disCreditMapper.updateCredit(temporaryCredit);
						createCreditRecord(temporaryCredit, temporaryCredit.getUsedAmount(), email, 2);
						// 新增交易记录
						createBill(temporaryCredit.getRedit(), "额度支付", shortUsed,
								temporaryCredit.getCreditLimit().subtract(temporaryCredit.getUsedAmount()), bill, null);

						result.put("success", true);
						result.put("code", 4);
						result.put("balance", dis.getBalance().doubleValue());
						result.put("info", "支付成功！账户余额为：" + dis.getBalance().doubleValue() + ",剩余信用额度为："
								+ (temporaryCredit.getCreditLimit().subtract(temporaryCredit.getUsedAmount())));
					} else {
						result.put("success", false);
						result.put("code", 5);
						result.put("balance", balance.doubleValue());
						result.put("info", "账户余额不足，请预先充值，谢谢。");
						return result;
					}
				} else {
					Logger.info("用户【" + email + "】使用信用额度支付，当前账号余额为：" + balance + "额度类型为：" + credit.getRedit()
							+ ",信用额度为：" + credit.getCreditLimit().doubleValue());
					DisAccount dis = new DisAccount();
					dis.setId(account.getId());
					dis.setBalance(BigDecimal.ZERO);
					// 使用临时额度支付 剩余额度
					if (balance.compareTo(BigDecimal.ZERO) > 0) {
						dis.setUpdateDate(new Date());
						disAccountMapper.updateByPrimaryKeySelective(dis);
						// 创建交易记录
						createBill(0, "余额支付", balance, BigDecimal.ZERO, bill, null);
					}
					DisCredit newcredit = new DisCredit();
					newcredit.setId(credit.getId());
					newcredit.setEmail(email);// 分销账户
					// 已使用额度
					newcredit.setUsedAmount(credit.getUsedAmount().add(payment.subtract(balance)));
					newcredit.setLimitState(2);// 使用中
					newcredit.setUpdatedate(new Date());
					disCreditMapper.updateCredit(newcredit);
					// 信用额度剩余
					BigDecimal creditbalance = credit.getCreditLimit().subtract(newcredit.getUsedAmount());
					// 生成交易记录
					createBill(credit.getRedit(), "额度支付", payment.subtract(balance), creditbalance, bill, null);
					// 生产操作记录
					createCreditRecord(credit, newcredit.getUsedAmount(), email, 2);
					
					result.put("success", true);
					result.put("code", 4);
					result.put("balance", dis.getBalance().doubleValue());
					
					BigDecimal creditLeft = creditbalance;
					if(credit.getRedit()==CreditTypes.PERMANNENT && temporaryCredit!=null){
						// 要加上临时的
						creditLeft = creditLeft.add(temporaryCredit.getCreditLimit().subtract(temporaryCredit.getUsedAmount()));
					}
					result.put("info", "支付成功！账户余额为：" + dis.getBalance().doubleValue() + ",剩余信用额度为：" + creditLeft);
				}
			} else {
				// 额度不足
				result.put("success", false);
				result.put("code", 5);
				result.put("balance", balance.doubleValue());
				result.put("info", "账户余额不足，请预先充值，谢谢。");
				return result;
			}
		}
		
		boolean paySuccess = Objects.nonNull(result.get("code")) && ((Integer) result.get("code")) == 4;
		if(paySuccess){
			Logger.info("最终支付成功，记录支付记录");
			DisAccount accountByEmail = disAccountMapper.getDisAccountByEmail(email);
			DisCredit credit = disCreditMapper.getDisCreditInfo(email);
			BigDecimal creditLimitBalance = BigDecimal.ZERO;
			if (credit != null) {
				creditLimitBalance = credit.getTotalCreditLimit().subtract(credit.getTotalUsedAmount());
			}
			// 生成交易记录 该条交易记录为当前支付总数
			// 3表示充值或者支付的总额，该条记录只是标记当前操作总额，不会充值或支付
			createBill(DisBill.SOURCES_ALL, "支付", payment, accountByEmail.getBalance(), bill, creditLimitBalance);
		}
		return result;
	}
	
	/**
	 * 处理返回值map
	 * @author zbc
	 * @since 2017年2月21日 上午11:48:47
	 */
	public Map<String,Object> resultMap(String[] keys,Object... obj){
		Map<String,Object> res = Maps.newHashMap();
		for(int i = 0;i <keys.length;i++){
			res.put(keys[i], obj[i]);
		}
		return res;
	}
	/**
	 * 新版支付逻辑，支持账期功能
	 */
	@Override
	public Map<String, Object> _payment(DisApply apply) {
		String email = apply.getEmail();
		//定义返回值key值
		String[] resultKeys = new String[]{"success","info","code"};
		Map<String,Object> result = null;
		// 根据申请的email查询账户
		DisAccount account = disAccountMapper.getDisAccountByEmail(email);
		if (null == account) {
			return resultMap(resultKeys,false,"未查到账户",3) ;
		}
		if (account.isFrozen()) {
			return resultMap(resultKeys,false,"该账户被冻结",3) ;
		}
		
		//校验支付密码，以及错误次数
		result = checkErrorNumTimes(resultKeys,apply,account);
		if(result != null){
			return result;
		}
		
		/******************** 判断是否已经支付过 start ************************/
		DisBill bill = new DisBill();// 交易记录
		bill.setSerialNumber(apply.getTransferNumber());
		List<DisBill> billRecord = billMapper.queryBill(bill);
		if (null != billRecord && billRecord.size() > 0) {
			Logger.info("改订单已经支付：" + apply.getTransferNumber());
			return resultMap(resultKeys, false,"该订单已支付",6);
		}
		
		/******************** 判断是否已经支付过 end **************************/
		return paymentLogic(apply,resultKeys, account);
	}

	/**
	 * 封装支付逻辑处理
	 * @author zbc
	 * @since 2017年2月21日 下午3:07:48
	 */
	private Map<String, Object> paymentLogic(DisApply apply, String[] resultKeys, DisAccount account) {
		Map<String, Object> result = null;
		String email = apply.getEmail();
		BigDecimal balance = account.getBalance();
		BigDecimal payment = apply.getTransferAmount();
		String orderNo = apply.getTransferNumber();
		//数组变长  追加key值
		resultKeys = Arrays.copyOf(resultKeys, resultKeys.length+1) ;
		resultKeys[resultKeys.length-1] = "balance";
		DisBill bill = new DisBill();// 交易记录
		// 交易类型：1、充值 2、提现 3、采购支付 4、退款 5、支付运费 6、采购支付含运费
		bill.setPurpose(apply.getApplyType());
		bill.setSourceCard(account.getEmail());// 账户源
		bill.setAccountId(account.getId());// 账户ID
		bill.setSerialNumber(orderNo);
		//添加备注
		bill.setRemark(apply.getApplyRemark());
		if (balance.compareTo(payment) >= 0) {// 可用余额支付
			result = balancePay(resultKeys, account, balance, payment, bill);
		} else {
			//判断是否有可使用账期
			Logger.info("用户【" + email + "】余额不足，需要支付金额为：" + payment.doubleValue());
			//TODO 修改逻辑
			AccountPeriodMaster accountPeriod = accountPeriodMapper.getValidAp(email);
			if(accountPeriod == null){
				// 可用账期额度不存在
				return resultMap(resultKeys, false,"账户余额不足，请预先充值，谢谢。",5,balance.doubleValue());	
			}
			
			result = accountPeriodPay(resultKeys, account, balance, payment, bill, accountPeriod, apply);
		}
		
		boolean paySuccess = Objects.nonNull(result.get("code")) && ((Integer) result.get("code")) == 4;
		if(paySuccess){
			Logger.info("最终支付成功，记录支付记录");
			DisAccount accountByEmail = disAccountMapper.getDisAccountByEmail(email);
			AccountPeriodMaster accountPeriod = accountPeriodMapper.getValidAp(email);
			BigDecimal creditLimitBalance = accountPeriod != null
					? accountPeriod.getTotalLimit().subtract(accountPeriod.getUsedLimit()) : BigDecimal.ZERO;
			// 生成交易记录 该条交易记录为当前支付总数
			// 3表示充值或者支付的总额，该条记录只是标记当前操作总额，不会充值或支付
			createBill(DisBill.SOURCES_ALL, "支付", payment, accountByEmail.getBalance(), bill, creditLimitBalance);
		}
		return result;
	}

	/**
	 * 封装 账期支付逻辑
	 * @author zbc
	 * @since 2017年2月22日 下午12:27:37
	 */
	private Map<String, Object> accountPeriodPay(String[] resultKeys, DisAccount account, BigDecimal balance,
			BigDecimal payment, DisBill bill, AccountPeriodMaster accountPeriod, DisApply apply) {
		BigDecimal totalLimit = accountPeriod.getTotalLimit();
		BigDecimal usedLimit = accountPeriod.getUsedLimit();
		// 待支付金额大于（可用账期额度+余额）
		if (payment.compareTo(totalLimit.subtract(usedLimit).add(balance)) > 0) {
			return resultMap(resultKeys, false,"账户余额不足，请预先充值，谢谢。",5,balance.doubleValue());	
		}
		Date updateDate = new Date();
		//更新用户余额
		DisAccount dis = new DisAccount();
		dis.setId(account.getId());
		dis.setBalance(BigDecimal.ZERO);
		// 当设置的冻结额度大于剩余额度时冻结账户
		Double periodFrozen = account.getPeriodFrozen();
		if (balance.compareTo(BigDecimal.ZERO) > 0) {
			// 生成交易记录
			createBill(DisBill.SOURCES_BALANCE, "余额支付", balance, BigDecimal.ZERO,bill, null);
		}
		//当前使用账期额度
		BigDecimal currentUsedAmount = payment.subtract(balance);
		//更新账期可用金额
		updateAp(bill,payment,updateDate,currentUsedAmount,accountPeriod,ApChange.PAY);
		dis.setFrozen(periodFrozen >= PriceFormatUtil.setScale2(totalLimit.subtract(accountPeriod.getUsedLimit())).doubleValue());
		dis.setUpdateDate(updateDate);
		disAccountMapper.updateByPrimaryKeySelective(dis);
		
		//更新账期额度与余额
		return resultMap(resultKeys, true,
				"支付成功！账户余额为：" + dis.getBalance().doubleValue() + ",剩余账期额度为：" + totalLimit.subtract(usedLimit), 4,
				balance.doubleValue());
	}
	
	/**
	 * 1、插入使用账期的订单数据
	 * 2、插入账期变化记录
	 * TODO 3、初始化账单信息，若已存在更新账单信息 
	 * 插入账期相关数据
	 * @author zbc
	 * @since 2017年2月22日 下午6:00:35
	 */
	private void saveApData(BigDecimal amount,String orderNo,AccountPeriodMaster accountPeriod, BigDecimal currentUsedAmount,Integer type) {
		Integer apId = accountPeriod.getId();
		Date updateDate = accountPeriod.getUpdateDate();
		String email = accountPeriod.getAccount();
		String nickName = accountPeriod.getNickName();
		//支付是保存订单
		if(type == ApChange.PAY){
			OrderByAp orderByAp = new OrderByAp(amount, currentUsedAmount, updateDate, apId, email, nickName, orderNo);
			orderByApMapper.insertSelective(orderByAp);
			
			// 备份采购单/发货单
			ebus.post(new BackUpApOrderDetailEvent(orderByAp.getId(), orderNo));
		}
		// 账期变化记录
		apChangeMapper.insertSelective(new ApChange(new BigDecimal(caculteType(type) + currentUsedAmount),
				accountPeriod.getTotalLimit(), updateDate, email, apId, type));
	}

	/**
	 * 根据账期变化类型获取 计算方式
	 * @author zbc
	 * @since 2017年2月23日 下午2:10:35
	 */
	private String caculteType(Integer type){
		return type == ApChange.PAY ? Constant.MINUS : Constant.PLUS;
	}
	/**
	 * 余额支付逻辑
	 * @author zbc
	 * @since 2017年2月21日 下午3:24:47
	 */
	private Map<String, Object> balancePay(String[] resultKeys, DisAccount account,BigDecimal balance,
			BigDecimal payment, DisBill bill) {
		DisAccount dis = new DisAccount();
		dis.setId(account.getId());
		dis.setBalance(balance.subtract(payment));
		dis.setUpdateDate(new Date());
		disAccountMapper.updateByPrimaryKeySelective(dis);
		Logger.info("用户【" + account.getEmail() + "】支付成功，金额为：" + payment);
		// 生成交易记录
		createBill(DisBill.SOURCES_BALANCE, "余额支付", payment, dis.getBalance(), bill, null);
		Map<String, Object> result = resultMap(resultKeys, true,"支付成功！账户余额为：" + dis.getBalance(),4,dis.getBalance().doubleValue());
		return result;
	}

	/**
	 * 密码正确时 清空错误次数
	 * @author zbc
	 * @since 2017年2月21日 下午3:00:48
	 */
	private void clearErrorNumTimes(DisAccount account) {
		// 若密码正确，则更新账户的输入次数重置为0，且将禁用时间清空
		DisAccount accountParam = new DisAccount();
		accountParam.setId(account.getId());
		accountParam.setInputErrorNumTimes(0);
		accountParam.setDisableTime(null);
		int line = disAccountMapper.updateInputErrorTimes(accountParam);
		Logger.info(this.getClass().getName() + " [payment updateInputErrorTimes===]" + line);
	}

	/**
	 * zbc 封装
	 * web安全整改，若密码输入错误三次，则加验证码，若错误五次，则锁定账户一小时 by huchuyin 2016-10-8
	 * @author 
	 * @since 2017年2月21日 下午2:47:14
	 */
	private Map<String, Object> checkErrorNumTimes(String[] resultKeys,DisApply apply,DisAccount account) {
		String info = null;
		Logger.info(this.getClass().getName() + " payment InputErrorNumTimes------>" + account.getInputErrorNumTimes());
		// 查询输入失败次数，若大于等于5次，则查询禁用时间，若当前时间小于禁用时间，则提示账户已锁定
		if (account.getInputErrorNumTimes() >= Constant.INPUT_ERROR_NUM_TIMES_LOCK) {
			Date curTime = new Date();
			Date disableTime = account.getDisableTime();
			Logger.info(this.getClass().getName() + " payment curTime==="
					+ curTime + " disableTime===" + disableTime);
			if (disableTime != null && curTime.before(disableTime)) {
				return resultMap(resultKeys,false,"账户已锁定，请1小时后再输入！",8);
			}
		}
		// 若输入失败次数大于等于3次，则校验验证码
		if (account.getInputErrorNumTimes() >= Constant.INPUT_ERROR_NUM_TIMES_CODE) {
			if ((apply.getIsBackStage() == null||!apply.getIsBackStage())&&!captchaService.verify(apply.getPayCaptcha())) {
				return resultMap(resultKeys,false, "请输入正确的验证码",9);
			}
		}
		// 校验支付密码是否正确
		if ((apply.getIsBackStage() == null||!apply.getIsBackStage()) && !apply.getPassword().equals(account.getPayPass())) {
			// 若密码错误，则每次给输入失败次数加1，若增加后的数据大于等于5次，则更新禁用时间为当前时间加一小时，且提示账户锁定
			DisAccount accountParam = new DisAccount();
			Integer numberTimes = account.getInputErrorNumTimes() + 1;
			if (numberTimes >= Constant.INPUT_ERROR_NUM_TIMES_LOCK) {
				DateTime dateTime = new DateTime().plusHours(Constant.PWS_INPUT_LOCK_TIME);
				accountParam.setDisableTime(dateTime.toDate());
			}
			accountParam.setId(account.getId());
			accountParam.setInputErrorNumTimes(numberTimes);
			int line = disAccountMapper.updateByPrimaryKeySelective(accountParam);
			Logger.info(this.getClass().getName() + " [payment updateCount===]" + line);
			if (numberTimes < Constant.INPUT_ERROR_NUM_TIMES_LOCK) {
				info = "支付密码错误，还可以输入" + (Constant.INPUT_ERROR_NUM_TIMES_LOCK - numberTimes) + "次";
			} else {
				info = "支付密码错误，账户已锁定，请一小时后再输入！";
			}
			return resultMap(resultKeys, false,info,7);
		}
		clearErrorNumTimes(account);
		return null;
	}

	/**
	 * 可以金额（余额+永久额度+临时额度）
	 * @param balance
	 * @param permanentCredit
	 * @param temporaryCredit
	 * @return
	 */
	private BigDecimal amountAvailable(BigDecimal balance,
			DisCredit permanentCredit, DisCredit temporaryCredit) {
		BigDecimal amountAvailable = BigDecimal.ZERO;
		// 余额
		if(balance!=null){
			amountAvailable = amountAvailable.add(balance);
		}
		// 永久
		if (permanentCredit != null) {
			amountAvailable = amountAvailable
					.add(permanentCredit.getCreditLimit().subtract(permanentCredit.getUsedAmount()));
		}
		// 临时
		if (permanentCredit != null) {
			amountAvailable = amountAvailable
					.add(temporaryCredit.getCreditLimit().subtract(temporaryCredit.getUsedAmount()));
		}
		return amountAvailable;
	}

	/**
	 * @param sources
	 *            资金来源（0：余额 1：临时额度 2：永久额度）
	 * @param paymentType
	 * @param amount
	 *            涉及金额
	 * @param balance
	 *            剩余
	 * @param bill
	 */
	private void createBill(Integer sources, String paymentType, BigDecimal amount, BigDecimal balance,
			DisBill bill,BigDecimal creditLimitBalance) {
		// 生成交易记录
		bill.setSources(sources);// 余额支付
		bill.setPaymentType(paymentType);
		bill.setAmount(amount);// 涉及金额
		bill.setBalance(balance);// 余额
		bill.setCreateDate(new Date());
		bill.setCreditLimitBalance(creditLimitBalance);
		billService.save(bill);
		Logger.info("支付成功生成交易记录：" + bill.toString());
	}

	/**
	 * 
	 * @param credit
	 * @param usedAmount
	 * @param email
	 * @param flag
	 *            充值：1，支付：2
	 */
	private void createCreditRecord(DisCredit credit, BigDecimal usedAmount, String email, Integer flag) {
		CreditOperationRecord record = new CreditOperationRecord();
		record.setCredit(credit.getRedit());// 额度类型
		record.setOperatorEmail(credit.getEmail());
		record.setOperatorResult(2);// 支付
		record.setUserEmail(email);
		record.setComments(flag == 2 ? "消费，此额度已使用" + usedAmount + "元" : "充值，此额度已使用" + usedAmount + "");
		Logger.info("信用额度变更记录：" + record.toString());
		creditOperationRecordMapper.insertSelective(record);
	}

	/**
	 * @param email
	 *            分销商邮箱
	 * @param type
	 *            额度类型
	 * @param optype
	 *            操作类型<1：申请> ---增加该字段的原因是充值和支付查询条件不一致
	 * @return
	 */
	private DisCredit getCredit(String email, Integer type, Integer optype , Boolean flag) {
		DisCredit credit = new DisCredit();
		credit.setEmail(email);// 分销账户
		credit.setRedit(type);// 额度类型
		credit.setIsActivated(flag);
		credit = disCreditMapper.getDisCredit(credit, optype);
		return credit;
	}

	@Override
	public List<OperationRecord> queryOperations(Integer applyId) {
		List<OperationRecord> list = opMapper.query(applyId);
		return transRecord(list);
	}

	private List<OperationRecord> transRecord(List<OperationRecord> list) {
		List<OperationRecord> newList = new ArrayList<>();
		OperationRecord op = null;
		for (OperationRecord record : list) {
			op = new OperationRecord();
			op.setOpDateStr(DateUtils.date2FullDateTimeString(record.getOpdate()));
			op.setOpdesc(record.getOpdesc());
			newList.add(op);
		}
		return newList;
	}

	@Override
	public Map<String, Object> batchAudit(List<Integer> ids, String opEmail) {
		Logger.info(opEmail + "开始执行批量审批申请，ids:" + ids + ",开始时间为：" + now());
		Map<String, Object> result = Maps.newHashMap();
		if (null == ids || ids.size() == 0) {
			result.put("success", false);
			return result;
		}
		
		// 审核初审状态为通过的申请
		List<DisApply> applys = applyMapper.queryApplys(ids);
		if (null == applys || applys.size() == 0){
			result.put("success", false);
			result.put("code", 1);
			return result;
		}
		
		DisApply newApply = null;
		for (DisApply app : applys) {
			Logger.info("审批，ID ：" + app.getId() + "，金额：" + app.getActualAmount());
			newApply = new DisApply();
			newApply.setId(app.getId());
			newApply.setUpdatedate(new Date());
			newApply.setReviewState(Constant.AUDIT_PASS);
			// 修改审核
			applyMapper.updateByPrimaryKeySelective(newApply);
			// 生成操作记录
			createOperateRecord(app.getId(), opEmail, null, Constant.AUDIT_PASS);
			// 更新余额并生成交易记录
			updateAccount(newApply);
		}
		Logger.info(opEmail + "执行批量审批申请结束，ids:" + ids.toString() + ",结束时间为：" + now());
		result.put("success", true);
		return result;
	}

	@Override
	public List<ApplyDto> query(Map<String, Object> param) {
		return trans(applyMapper.queryApply(param));
	}

	/**
	 * 提现申请
	 * @param param
	 * @return
	 */
	public Integer applyWithdraw(String param){
		int insert = 0;
		LoginContext lc = loginService.getLoginContext(1);
		try {
			JsonNode node = new ObjectMapper().readTree(param);
            String disemail = lc.getEmail();
			boolean flag = node.has("wAmount") && StringUtils.isNotBlank(node.get("wAmount").asText())
					&& node.has("wAmountId") && StringUtils.isNotBlank(node.get("wAmountId").asText())
					&& node.has("counterFee") && StringUtils.isNotBlank(node.get("counterFee").asText());
            if (!flag) {
				//参数错误
				return 3;
			}
            
            DisAccount disAccount = disAccountMapper.getDisAccountByEmail(lc.getEmail());
			Double wAmount = node.get("wAmount").asDouble();
			//提现账户ID
			Integer wAmountId = node.get("wAmountId").asInt();
			//查询提现账户详情
			DisWithdrawAccount dwa = withdrawAccountMapper.selectByPrimaryKey(wAmountId);
			//提现申请前，需校验提现次数是否超过当月限制或提交的金额是否低于最多限额 by huchuyin 2016-9-26
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			//当月第一天
	        Calendar firstCal = Calendar.getInstance();   
	        firstCal.set(Calendar.DAY_OF_MONTH,1);
	        String firstDay = format.format(firstCal.getTime());
	        //当月最后一天
	        Calendar lastCal = Calendar.getInstance();   
	        lastCal.set(Calendar.DAY_OF_MONTH, lastCal.getActualMaximum(Calendar.DAY_OF_MONTH)); 
	        String lastDay = format.format(lastCal.getTime());
	        DisApply paramApply = new DisApply();
	        paramApply.setCurMonthFirstDay(firstDay);
	        paramApply.setCurMonthLastDay(lastDay);
	        paramApply.setEmail(disemail);
	        //提现标识
	        paramApply.setApplyType(Constant.APPLY_TYPE_WITHDRAW);
	        Integer applyCount = applyMapper.getApplyCountByCurMonth(paramApply);
	        //获取提现限制数据
	        DisWithdrawLimit dwl = withdrawLimitMapper.getCommonWithdrawLimit();
	        if(dwl == null) {
	        	dwl = new DisWithdrawLimit();
	        	dwl.setPermonthTimes(10);
	        	dwl.setPertimeLeast(new BigDecimal(500));
	        }
	        //校验提现的银行卡是否有效，若无效，则不能提交申请
	        DisWithdrawAccount account = new DisWithdrawAccount();
	        account.setId(node.get("wAmountId").asInt());
	        account.setIsBind(Constant.FLAG_BIND_BANK);
	        account.setIfEffective(true);
	        account = withdrawAccountMapper.getWAccounts(account);
	        //End by huchuyin 2016-9-26
			if(disAccount == null || disAccount.isFrozen()){
				return 4;//帐户异常
			} 
			if(disAccount.getBalance().doubleValue() < wAmount){
				return 6;//余额不足
			} 
			if (dwa != null && !new Integer(2).equals(dwa.getAccountType()) && applyCount >= dwl.getPermonthTimes()) {
				//超过提现限制次数
				return 2;
			} 
			if (dwa != null && !new Integer(2).equals(dwa.getAccountType())
					&& new BigDecimal(wAmount).compareTo(dwl.getPertimeLeast()) == -1) {
				//低于提现最低金额
				return 7;
			} 
			if(account == null) {
				//银行卡为无效卡
				return 8;
			}
			
			DisApply apply = new DisApply();
            String applyNo = IDUtils.getOnlineTopUpCode("TX", sequenceService.selectNextValue("WITHDRAW_AMOUNT_NO"));
			apply.setOnlineApplyNo(applyNo);
			apply.setWithdrawAccountId(wAmountId);
			apply.setWithdrawAmount(new BigDecimal(wAmount));
			apply.setCreatedate(new Date());
			apply.setAuditState(0);//待审核
			apply.setApplyType("2");//提现
			apply.setEmail(disemail);//登录名
			//手续费
			Double counterFee = node.get("counterFee").asDouble();
			apply.setCounterFee(new BigDecimal(counterFee));
			insert = applyMapper.insertSelective(apply);
			if(dwa!=null  && new Integer(2).equals(dwa.getAccountType())) {
				insert = 10;
			}
            disAccount.setBalance(disAccount.getBalance().subtract(new BigDecimal(wAmount)));
            BigDecimal froAmount = new BigDecimal(0);
			if (disAccount.getFrozenAmount() != null) {
				froAmount = disAccount.getFrozenAmount();
			}
            disAccount.setFrozenAmount(froAmount.add(new BigDecimal(wAmount)));
            disAccountMapper.updateByPrimaryKey(disAccount);
            //若提现账户为M站，则发送申请信息到M站，并处理同步返回。
            if (dwa != null && new Integer(2).equals(dwa.getAccountType())) {
                JSONObject params = new JSONObject();
                params.put("disemail", String.valueOf(disemail));
                params.put("orderNo", String.valueOf(applyNo));
                params.put("amount", new BigDecimal(wAmount).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                params.put("timestamp", String.valueOf(new Date().getTime()));
                String res = HttpUtil.sendWithdrawApply(params);
                Logger.debug("applyWithdraw    res----->" + res);
                if (JSON.parseObject(res).getBoolean("result")) {
                    apply.setAuditState(Constant.AUDIT_PASS);
                    apply.setAuditReasons("已确认");
                    apply.setUpdatedate(new Date());
                    int line = applyMapper.updateByPrimaryKeySelective(apply);
                    Logger.debug("applyWithdraw    [update DisApply]line----->" + line);

                    if (line == 1) {
                        froAmount = disAccount.getFrozenAmount();
                        disAccount.setFrozenAmount(froAmount.subtract(new BigDecimal(wAmount)));
                        line = disAccountMapper.updateByPrimaryKeySelective(disAccount);
                        Logger.debug("applyWithdraw    [update DisAccount]line----->" + line);

                        //新增交易记录
                        DisBill bill = new DisBill();
                        bill.setAmount(new BigDecimal(wAmount));
                        bill.setPurpose("2");
                        String serialNumber = IDUtils.getOnlineTopUpCode("SN", sequenceService.selectNextValue("WITHDRAW_AMOUNT_NO"));
                        bill.setSerialNumber(serialNumber);
                        bill.setPaymentType("余额提现");
                        bill.setApplyId(apply.getId());
                        bill.setSourceCard(apply.getEmail());
                        bill.setBalance(disAccount.getBalance());
                        bill.setAccountId(disAccount.getId());
						bill.setSources(0);//子交易记录
						line = billService.save(bill);
						Logger.debug("applyWithdraw    [insert child DisBill]line----->" + line);
						bill.setId(null);
						bill.setSources(3);//总交易记录
						line = billService.save(bill);
						Logger.debug("applyWithdraw    [insert main DisBill]line----->" + line);
                    }
                }
            }
		}catch (Exception e){
			e.printStackTrace();
			Logger.error(">>applyWithdraw>>>>Exception>>>"+e);
			insert = 5;//异常
		}
		return insert;
	}

	/**
	 * {
	 * email(分销商邮箱)
	 * pwd(支付密码)
	 * TransferNumber(支付单号)
	 * applyType(交易类型 4、 退款)
	 * }
	 * @param apply
	 * @return
	 * 步骤 ： 1 根据分销商 账号 与支付密码 判断 是否匹配 匹配则进进入下一步，不匹配值返回，并提示 2.运费直接走充值流程 标记
	 * 为退款类型 返回结果
	 */
	public Map<String, Object> freightRefund(DisApply apply) {
		String email = apply.getEmail();
		String[] resultKeys = new String[]{"suc","code"};
		// 根据申请的email查询账户
		DisAccount account = disAccountMapper.getDisAccountByEmail(email);
		if (null == account) {
			return resultMap(resultKeys, false,3);
		}
		// 校验支付密码是否正确
		if (apply.getIsBackStage() == null&&!apply.getIsBackStage()) {
			if (!apply.getPassword().equals(account.getPayPass())) {
				return resultMap(resultKeys, false,7);
			}
		}
		//校验是否已经退过款
		DisBill bill = new DisBill();
		bill.setSerialNumber(apply.getTransferNumber());
		bill.setPurpose(apply.getApplyType());
		List<DisBill> bills = billMapper.queryBill(bill);
		if(bills.size() > 0){//如果有退款记录，不能还款
			return resultMap(resultKeys, false,6);
		}
		// 交易金额
		BigDecimal refundPayment = apply.getTransferAmount();
		DisBill newBill = new DisBill(account.getId(), apply.getTransferAmount(), apply.getApplyType(), apply.getTransferNumber(),
				"", null, apply.getTransferCard());
		//支付流程
		return _handleGivenMoney(refundPayment, account, newBill);
	}

	/**
	 * 是否是初审
	 * @author zbc
	 * @since 2017年2月23日 下午3:34:25
	 */
	private boolean firstAudit(Integer flag){
		return flag == 27;
	}
	
	/**
	 * TODO 依赖menuid 需优化
	 * 27代表充值初审权限
	 * 28代表充值复审权限
	 */
	@Override
	public Map<String, Object> remindBuserAudit(Integer flag) {
		String[] resultKeys = {"suc","msg"};
		List<String> telphones = new ArrayList<String>();
		Set<String> phones = new HashSet<String>();
		String tels = "";
		String auditMsg = firstAudit(flag)?"初审":"复审";
		telphones = applyMapper.getRechargeAuditUser(flag);//27代表充值初审权限，对应t_dis_menu的id
		if (telphones != null && telphones.size() > 0){
			phones.addAll(telphones);
			Logger.info("充值"+auditMsg+"手机号码" + Json.toJson(telphones).toString());
		}
		for(String telphone : phones){
			tels += telphone + ",";
		}
		tels = tels.substring(0, tels.length()-1);
		Logger.info("手机号码" + Json.toJson(tels).toString());
		try {
			EmailAccount emailAccountParam = new EmailAccount();
			emailAccountParam.setCtype(Constant.SEND_MSG);
			EmailAccount emailAccount = emailMapper.select(emailAccountParam);
			if (emailAccount == null) {
				Logger.error("--------------------->remindBuserAudit:短信发送失败！获取不到短信配置！");
				return resultMap(resultKeys, false, "短信发送失败");
			}
//			SMSManager.send("【通淘国际】尊敬的用户，近期Bbc后台网站上有充值申请需要您去"+auditMsg, tels);

			EmailTemplate template = templateMapper.select(Constant.SMS_RECHARGE);
			if (null == template) {
				Logger.error("-------------->remindBuserAudit:未配置短信模板：" + new Date());
				return resultMap(resultKeys, false,"短信发送失败");
			}

			String content = template.getCcontent();
			Logger.info("content----------->"+Json.toJson(content));
			if (!content.contains("msg")) {
				Logger.error("-------------->remindBuserAudit:短信模板配置有误：" + new Date());
				return resultMap(resultKeys, false,"短信发送失败");
			}
			
			content = content.replaceAll("msg", auditMsg).replace("{{", "").replace("}}", "");
			SMSManager.send(content, emailAccount, tels);
			Logger.info("短信发送成功");
			return resultMap(resultKeys, true,"短信发送成功");
		} catch (Exception e) {
			Logger.error("短信发送失败！");
			Logger.error(e.getMessage());
			return resultMap(resultKeys, false,"短信发送失败");
		}
	}

	@Override
	public Map<String, Object> applyOpenMobile(ShopSite shopSiteDto) {
		Map<String, Object> result = Maps.newHashMap();
		DisMember member = new DisMember();
		member.setEmail(shopSiteDto.getDisemail());
		member.setRoleId(2);
		member = disMemberMapper.getMember(member);
		if (member != null){
			shopSiteDto.setApplydate(new Date());
			shopSiteDto.setStatus(1);//表示待审核
			shopSiteDto.setDistributionMode(member.getDistributionMode());
			//将数据推送到M网站
			JsonNode params = Json.toJson(shopSiteDto);
			try {
				Configuration config = Play.application().configuration().getConfig("msite");
				String url = config.getString("host") + "/service/bbc/saveAdminSite";
				String res = utils.dismember.HttpUtil.httpPost(params.toString(), url);
				Logger.info("移动端推送结果---------》" + Json.toJson(res));
				JsonNode response = Json.parse(res);
				if (response != null && response.get("result").asBoolean() == true) {
					shopSiteDto.setSiteurl(shopSiteDto.getSiteurl() != null ? shopSiteDto.getSiteurl().toLowerCase() : null);
					int flag = shopSiteMapper.insertSelective(shopSiteDto);
					if (flag != 1) {
						result.put("suc", false);
						result.put("msg", "移动客户端申请失败");
						return result;
					}
					result.put("suc", true);
					result.put("msg", "更新成功");
				}
			} catch (Exception e) {
				Logger.error("推送网站信息到M站失败", e);			
			}
		}
		return result;
	}

	@Override
	public Map<String, Object> getMobileApplyInfo(Map<String, String> param) {
		Map <String, Object> result = Maps.newHashMap();
		ShopSite shopSiteDto = new ShopSite();
		if (param.get("disemail") != null) {
			shopSiteDto.setDisemail(param.get("disemail"));
		}
		if (param.get("siteurl") != null) {
			shopSiteDto.setSiteurl(param.get("siteurl"));
		}
		if (param.get("status") != null) {
			shopSiteDto.setStatus(Integer.parseInt(param.get("status")));
		}
		ShopSite shopSite = shopSiteMapper.selectByCondition(shopSiteDto);
		if (shopSite  == null) {
			result.put("code",3);
			result.put("msg", "无移动客户端相关信息");
			return result;
		}
		
		shopSite.setApplydateStr(DateUtils.date2string(shopSite.getApplydate(), DateUtils.FORMAT_FULL_DATETIME));
		result.put("code", 4);
		result.put("data", shopSite);
		return result;
	}

	@Override
	public Map<String, Object> modifyMobileInfo(ShopSite shopSiteDto) {
		Map<String, Object> result = Maps.newHashMap();
		int flag = shopSiteMapper.updateByEmail(shopSiteDto);
		Logger.info("flag-----------" +flag);
		if (flag == 0) {
			result.put("suc", false);
			result.put("msg", "移动端更新失败");
			return result;
		} 
		
		result.put("suc", true);
		result.put("msg", "更新成功");
		return result;
	}

	/**
	 * 把从M站提现的钱存入指定账户
	 */
	@Override
	public Map<String, Object> saveMoneyFromMsite(Map<String, String> params) {
		Map<String, Object> result = Maps.newHashMap();
		Logger.info("");
		List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            if(!key.equals("key")){
	            String value = String.valueOf(params.get(key));
	            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
	                prestr = prestr + key + "=" + value;
	            } else {
	                prestr = prestr + key + "=" + value + "&";
	            }
            }
        }
        String data_md5 = MD5Util.MD5Encode(prestr+"msite", 
				MD5Util.CHARSET_UTF_8);
		if (!data_md5.equals(params.get("key"))){
			result.put("suc", false);
			result.put("msg", "密文不匹配");
			return result;
		}
		DisAccount account = disAccountMapper.getDisAccountByEmail(params.get("diseamil"));
		if (account == null) {
			result.put("suc", false);
			result.put("msg", "根据此账号"+params.get("diseamil")+"，没有查询到指定关联账户");
			return result;
		}
		
		// 申请金额 分销商申请金额
		BigDecimal amount = new BigDecimal(params.get("amount"));
		DisBill bill = new DisBill(account.getId(), amount, "1", params.get("accountNo"),
				"", null, null);
		return _handleGivenMoney(amount,account,bill);
	}

	@Override
	public String auditWithdraw(JSONObject params) {
        Logger.debug("auditWithdraw    params----->" + params.toString());
        JSONObject result = new JSONObject();
        Integer id = params.getInteger("applyId");
        Integer auditState = params.getInteger("auditState");
        DisApply apply = applyMapper.selectByPrimaryKey(id);
        
        if (apply == null || auditState == null) {
	        result.put("suc", false);
	        result.put("msg", "当前申请记录不存在或审核结果不明确");
	        return result.toString();
        }
        
        DisAccount account = disAccountMapper.getDisAccountByEmail(apply.getEmail());
		if (account.isFrozen()) {
            result.put("suc", false);
            result.put("msg", "当前申请提现的分销商余额账户已被冻结，禁止提现");
            return result.toString();
		}
		
		DisWithdrawAccount dwa = withdrawAccountMapper.selectByPrimaryKey(apply.getWithdrawAccountId());
		if (dwa.getAccountType() == 2) {
			result.put("suc", false);
			result.put("msg", "提现到M站可结算余额的提现申请禁止人工审核或处理");
			return result.toString();
		}
		
		String auditRemark = params.getString("auditRemark");
		String operator = params.getString("operator");
		String auditReason = params.getString("auditReason");
		BigDecimal withdrawAmount = apply.getWithdrawAmount();
		BigDecimal frozenAmount = account.getFrozenAmount();
		
		// 审核通过
		if (auditState == Constant.AUDIT_PASS) {
			BigDecimal transferAmount = params.getBigDecimal("transferAmount").setScale(2, BigDecimal.ROUND_HALF_UP);
			if (transferAmount.compareTo(apply.getWithdrawAmount().setScale(2, BigDecimal.ROUND_HALF_UP)) != 0) {
				result.put("suc", false);
				result.put("msg", "转账金额与当前申请的提现金额不一致");
				return result.toString();
			}
			
			String transferNumber = params.getString("transferNumber");
			Date transferTime = params.getDate("transferTime");
			apply.setAuditState(Constant.AUDIT_PASS);
			apply.setTransferAmount(transferAmount);
			apply.setTransferNumber(transferNumber);
			apply.setTransferTime(transferTime);
			apply.setAuditReasons(auditReason);
			apply.setAuditRemark(auditRemark);
			apply.setUpdatedate(new Date());
			//保存审核结果
			int line = applyMapper.updateByPrimaryKeySelective(apply);
			Logger.debug("auditWithdraw    [update DisApply]line----->" + line);
			//冻结余额变更
			if (line < 1) {
				result.put("suc", false);
				result.put("msg", "审核结果保存失败，无法变更冻结余额");
				return result.toString();
			}
			
			account.setFrozenAmount(frozenAmount.subtract(withdrawAmount));
			line = disAccountMapper.updateByPrimaryKeySelective(account);
			Logger.debug("auditWithdraw    [update DisAccount]line----->" + line);
			//新增操作记录
			OperationRecord or = new OperationRecord();
			or.setApplyId(id);
			or.setOperator(operator);
			or.setOpdate(new Date());
			or.setOpdesc(operator + "将待审核状态变更为：审核通过");
			line = opMapper.insertSelective(or);
			Logger.debug("auditWithdraw    [insert OperationRecord]line----->" + line);
			//新增交易记录
			DisBill bill = new DisBill();
			bill.setAmount(withdrawAmount);
			bill.setPurpose("2");
			bill.setSerialNumber(transferNumber);
			bill.setPaymentType("余额提现");
			bill.setApplyId(id);
			bill.setSourceCard(apply.getEmail());
			bill.setBalance(account.getBalance());
			bill.setAccountId(account.getId());
			bill.setSources(0);//子交易记录
			line = billService.save(bill);
			Logger.debug("auditWithdraw    [insert child DisBill]line----->" + line);
			bill.setId(null);
			bill.setSources(3);//总交易记录
			line = billService.save(bill);
			Logger.debug("auditWithdraw    [insert main DisBill]line----->" + line);
			result.put("suc", true);
			return result.toString();
		} 
		
		// 审核不通过
		if (auditState == Constant.AUDIT_NOT_PASS) {
			apply.setAuditState(Constant.AUDIT_NOT_PASS);
			apply.setAuditReasons(auditReason);
			apply.setAuditRemark(auditRemark);
			apply.setUpdatedate(new Date());
			//保存审核结果
			int line = applyMapper.updateByPrimaryKeySelective(apply);
			Logger.debug("auditWithdraw    [update DisApply]line----->" + line);
			//冻结余额变更，新增操作记录和交易记录
			
			if (line < 1) {
				result.put("suc", false);
				result.put("msg", "审核结果保存失败，无法变更冻结余额");
				return result.toString();
			}
			
			//若理由为账户异常，则将绑定的卡号设置为无效卡号 by huchuyin 2016-9-28
			if("账户异常".equals(auditReason)) {
				DisWithdrawAccount withdrawAccount = new DisWithdrawAccount();
				Integer accountId = params.getInteger("accountId");
				Logger.debug("auditWithdraw [账户异常] accountId----->" + accountId);
				if(accountId != null) {
					withdrawAccount.setId(accountId);
					withdrawAccount.setIfEffective(false);
					line = withdrawAccountMapper.updateByPrimaryKeySelective(withdrawAccount);
					Logger.debug("auditWithdraw    [update DisWithdrawAccount]line----->" + line);
				}
			}
			//End by huchuyin 2016-9-28
			BigDecimal balance = account.getBalance();
			account.setFrozenAmount(frozenAmount.subtract(withdrawAmount));
			account.setBalance(balance.add(withdrawAmount));
			line = disAccountMapper.updateByPrimaryKeySelective(account);
			Logger.debug("auditWithdraw    [update DisAccount]line----->" + line);
			//新增操作记录
			OperationRecord or = new OperationRecord();
			or.setApplyId(id);
			or.setOperator(operator);
			or.setOpdate(new Date());
			or.setOpdesc(operator + "将待审核状态变更为：审核不通过");
			line = opMapper.insertSelective(or);
			Logger.debug("auditWithdraw    [insert OperationRecord]line----->" + line);
			result.put("suc", true);
			return result.toString();
		}
		return result.toString();
	}

    @Override
    public List<WithdrawBalanceDto> queryWithdrawRecord(Map<String, String[]> paramMap) {
        Logger.debug("queryWithdrawRecord    paramMap----->" + Json.toJson(paramMap).toString());
        Map<String, Object> queryMap = Maps.newHashMap();
        if (paramMap.containsKey("applyType")) {
            queryMap.put("applyType", paramMap.get("applyType")[0]);
        }
        if (paramMap.containsKey("search")) {
            queryMap.put("search", paramMap.get("search")[0]);
        }
        if (paramMap.containsKey("auditState") && !Strings.isNullOrEmpty(paramMap.get("auditState")[0])) {
            queryMap.put("auditState", Integer.valueOf(paramMap.get("auditState")[0]));
        }
        if (paramMap.containsKey("createDate") && !Strings.isNullOrEmpty(paramMap.get("createDate")[0])) {
            Calendar ca = Calendar.getInstance();
            ca.add(Calendar.MONTH, -Integer.valueOf(paramMap.get("createDate")[0]));
            queryMap.put("createDate", DateUtils.date2FullDateTimeString(ca.getTime()));
        }
        if (paramMap.containsKey("updateDate") && !Strings.isNullOrEmpty(paramMap.get("updateDate")[0])) {
            Calendar ca = Calendar.getInstance();
            ca.add(Calendar.MONTH, -Integer.valueOf(paramMap.get("updateDate")[0]));
            queryMap.put("updateDate", DateUtils.date2FullDateTimeString(ca.getTime()));
        }
        Logger.debug("queryWithdrawRecord    queryMap----->" + Json.toJson(queryMap).toString());
        List<WithdrawBalanceDto> list = applyMapper.queryWithdrawRecord(queryMap);
        if(null == list) {
        	return Lists.newArrayList();
        }
        return list;
    }

    @Override
    public String saveWLimit(JSONObject params) {
        Logger.debug("saveWLimit    params----->" + params.toString());
        JSONObject result = new JSONObject();
        DisWithdrawLimit dwl = JSON.parseObject(params.toString(), DisWithdrawLimit.class);
        if(dwl == null){
        	result.put("suc", false);
            result.put("msg", "请求参数格式错误");
            return result.toString();
        }
        
        //执行更新
		if (dwl.getId() != null) {
			// 更新人
			dwl.setUpdateUser(params.getString("operator"));
			// 更新时间
			dwl.setUpdateTime(new Date());
			int line = withdrawLimitMapper.updateByPrimaryKeySelective(dwl);
			Logger.debug("saveWLimit    [update DisWithdrawLimit]line----->" + line);
		} else {
			// 执行新增
			// 创建人
			dwl.setCreateUser(params.getString("operator"));
			int line = withdrawLimitMapper.insertSelective(dwl);
			Logger.debug("saveWLimit    [insert DisWithdrawLimit]line----->" + line);
		}
		result.put("suc", true);
        return result.toString();
    }

    @Override
    public String getCommonWLimit() {
        JSONObject result = new JSONObject();
        DisWithdrawLimit dwl = withdrawLimitMapper.getCommonWithdrawLimit();
        if (dwl == null) {
        	result.put("suc", false);
            result.put("msg", "通用提现限制不存在");
            return result.toString();
        }
        
        result.put("suc", true);
        result.put("msg", dwl);
        return result.toString();
    }

	@Override
	public String getBindBankCardList(DisWithdrawAccount account) {
		Map<String,Object> map = Maps.newHashMap();
		map.put("suc",true);
		Logger.info(this.getClass().getName()+" getBindBankCardList [email,bindFlag]"+account.getDistributorEmail()+","+account.getIsBind());

        //查询当前分销商是否开通M站，已开通则在提现账户表中新增/更新M站提现账户，未开通/已关闭则不操作/解绑M站提现账户
        ShopSite ss = new ShopSite();
        ss.setDisemail(account.getDistributorEmail());
        ss.setStatus(Constant.AUDIT_PASS);
        ss = shopSiteMapper.selectByCondition(ss);

        DisWithdrawAccount dwa = new DisWithdrawAccount();
        dwa.setDistributorEmail(account.getDistributorEmail());
        dwa.setAccountType(2);
        dwa = withdrawAccountMapper.getWAccounts(dwa);
        if (ss != null && ss.getIsUsable()) {
            if (dwa != null) {
                //绑定M站提现账户
                dwa.setIsBind(Constant.FLAG_BIND_BANK);//绑定
                int line = withdrawAccountMapper.updateByPrimaryKeySelective(dwa);
                Logger.debug("getBindBankCardList    [update DisWithdrawAccount]line----->" + line);
            } else {
                //新增并绑定M站提现账户
                dwa = new DisWithdrawAccount();
                dwa.setAccountUnit("M站可结算余额");
                dwa.setCreateTime(new Date());
                dwa.setDistributorEmail(account.getDistributorEmail());
                dwa.setWithdrawAccount(account.getDistributorEmail());
                dwa.setIsBind(Constant.FLAG_BIND_BANK);//绑定
                dwa.setAccountType(2);
                int line = withdrawAccountMapper.insertSelective(dwa);
				Logger.debug("getBindBankCardList    [insert DisWithdrawAccount]line----->" + line);
            }
        } else {
            //未开通或已关闭M站则直接解绑M站提现账户
            if (dwa != null) {
                //解绑M站提现账户
                dwa.setIsBind(Constant.FLAG_UNBIND_BANK);//解绑
                int line = withdrawAccountMapper.updateByPrimaryKeySelective(dwa);
                Logger.debug("getBindBankCardList    [update DisWithdrawAccount]line----->" + line);
            }
        }

		//根据邮箱获取登录者的银行卡绑定账号
		List<DisWithdrawAccount> accountList = withdrawAccountMapper.getWAccountsList(account);
		if(accountList != null && !accountList.isEmpty()) {
			//列表数据
			map.put("list",accountList);
			map.put("count",accountList.size());
		} else {
			//总数
			map.put("count",0);
		}
		return Json.toJson(map).toString();
	}

	@Override
	public String delBindBankCard(Integer id) {
		Map<String,Object> map = Maps.newHashMap();
		map.put("suc",true);
		//解除绑定操作
		int count = withdrawAccountMapper.delBindBangCard(id);
		Logger.info(this.getClass().getName()+" [delBindBankCard count]==="+count);
		return Json.toJson(map).toString();
	}

	@Override
	public String addOfflineApply(String param) {
		JSONObject result = new JSONObject();
		try {
			JSONObject params = JSON.parseObject(param);
			String operator = params.getString("operator");
			DisApply da = new DisApply();
			da.setTransferCard(params.getString("transferCard"));
			da.setTransferTime(params.getDate("transferTime"));
			da.setTransferNumber(params.getString("transferNumber"));
			da.setTransferAmount(params.getBigDecimal("receivedAmount"));
			da.setAuditState(2);
			da.setTransferType(params.getString("transferType"));
			da.setTransferDesc("线下充值");
			da.setCreatedate(new Date());
			da.setUpdatedate(new Date());
			da.setIsConfirm(true);
			da.setEmail(params.getString("email"));
			da.setApplyType("1");
			da.setActualAmount(params.getBigDecimal("receivedAmount"));
			da.setReviewState(2);
			da.setScreenshotUrl(params.getString("screenshotUrl"));
			da.setAuditReasons("已到帐");
			da.setAuditRemark(params.getString("auditRemark"));
			da.setReAuditRemark(params.getString("auditRemark"));
			da.setApplyRemark(params.getString("applyRemark"));
			da.setRecipientId(params.getInteger("recipientCardId"));
			da.setReceiptCard(params.getString("recipientAccount"));
			da.setActualDate(params.getDate("receivedTime"));
			int line = applyMapper.insertSelective(da);
			Logger.debug("addOfflineApply    [insert DisApply]line----->" + line);
			//生成操作记录
			createOperateRecord(da.getId(), operator, 2, null);
			createOperateRecord(da.getId(), operator, null, 2);
			//更新余额并生成交易记录
			this.updateAccount(da);
			BigDecimal orderAmount = params.getBigDecimal("orderAmount");
			DisAccount account = disAccountMapper.getDisAccountByEmail(params.getString("email"));
			if (account.getBalance().compareTo(orderAmount) >= 0) {
				//余额充足
				DisBill bill = new DisBill();
				bill.setPurpose("3");//1、充值 2、提现 3、采购支付 4、退款 5、支付运费 6、采购支付含运费
				bill.setSourceCard(account.getEmail());// 账户源
				bill.setAccountId(account.getId());// 账户ID
				bill.setSerialNumber(params.getString("purchaseNo"));

				account.setBalance(account.getBalance().subtract(orderAmount));
				Date payTime = new Date();
				account.setUpdateDate(payTime);
				line = disAccountMapper.updateByPrimaryKeySelective(account);
				Logger.debug("addOfflineApply    [update DisAccount]line----->" + line);
				//生成交易记录，子交易记录
				createBill(0, "余额支付", orderAmount, account.getBalance(), bill, null);
				//主交易记录
				bill.setId(null);
				createBill(3, "余额支付", orderAmount, account.getBalance(), bill, null);
				result.put("suc", true);
				result.put("pay", true);
				result.put("payTime", new DateTime(payTime).toString("yyyy-MM-dd HH:mm:ss"));
				result.put("msg", "余额支付采购单成功");
			} else {
				//余额不足
				result.put("suc", true);
				result.put("pay", false);
				result.put("msg", "余额不足");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.debug("addOfflineApply    Exception----->", e);
			result.put("suc", false);
			result.put("msg", "系统异常");
		}
		Logger.debug("addOfflineApply    result----->" + result.toString());
		return result.toString();
	}

	@Override
	public File getApplyFile(Integer id) {
		String url = applyMapper.selectByPrimaryKey(id).getScreenshotUrl();
		if(StringUtils.isEmpty(url)){
			return null;
		}
		
		return new File(url);
	}

	@Override
	public Map<String, Object> sendApply(FilePart file, Map<String, String[]> params, String applyMan) {
		Map<String, Object> result = Maps.newHashMap();
		try {
			String email = StringUtils.isNotBlank(setString(params.get("selectedEmail"))) ? setString(params.get("selectedEmail")) : applyMan;
			String transferNumber = setString(params.get("transferNumber"));
			BigDecimal transferAmount = setBigDecimal(params.get("transferAmount"));
			String transferCard = setString(params.get("transferCard"));
			String transTime = setString(params.get("transTime"));
			if(transferAmount == null){
				result.put("success", false);
				result.put("code", 1);
				result.put("msg","付款金额不能为空");
				return result;
			}
			if(StringUtils.isNotBlank(transferNumber)&&applyMapper.getBytransferNumber(transferNumber).size() >0){
				result.put("success", false);
				result.put("code", 1);
				result.put("msg","付款流水号不能重复");
				return result;
			}
			if (file == null
					|| (file != null
							&& (file.getFilename().endsWith(".png")
									|| file.getFilename().endsWith(".jpg") || file
									.getFilename().endsWith(".bmp")) && file
							.getFile().length() <= (2 * 1024 * 1024))) {
				DisApply apply = new DisApply();
				apply.setTransferNumber(transferNumber);
				apply.setTransferAmount(transferAmount);
				apply.setTransferCard(transferCard);
				apply.setEmail(email);
				apply.setDistributorName(setString(params.get("distributorName")));
				apply.setTransferType(setString(params.get("transferType")));
				apply.setApplyRemark(setString(params.get("applyRemark")));
				String recipientId = setString(params.get("recipientId"));
				apply.setRecipientId(StringUtils.isEmpty(recipientId) ? null : Integer.valueOf(recipientId));
				Logger.info("分销商【" + apply.getEmail() + "】发送申请,涉及金额为：" + apply.getTransferAmount());
				if(StringUtils.isNotEmpty(transTime)){
					apply.setTransferTime(DateUtils.string2date(transTime, "yyyy-MM-dd hh:mm:ss"));
				}
				String filepath = file == null ? "" : uploadFile(file, email);
				apply.setAuditState(Constant.AUDIT_PENDING);// 申请状态：待审核
				apply.setReviewState(Constant.AUDIT_REVIEW);
				apply.setApplyType("1");// 暂时设置为充值类型
				apply.setCreatedate(new Date());
				apply.setScreenshotUrl(filepath);
				apply.setApplyMan(applyMan);
				applyMapper.insertSelective(apply);
				Logger.info("分销商【" + apply.getEmail() + "】发送申请成功！");
				//出现充值待初审
				remindBuserAudit(27);
				result.put("success", true);
				result.put("code", 0);// 成功
			}else{
				result.put("success", false);
				result.put("code", 1);// 成功
				result.put("msg","文件格式错误");
			}
		} catch (Exception e) {
			Logger.error("Send Apply Fail :" + applyMan + ",errorInfo:" + e);
			result.put("success", false);
			result.put("code", 1);
			result.put("msg","系统异常");
		}
		return result;
	}

	private String setString(String[] strings) {
		return strings != null && strings.length > 0 ? strings[0].trim() : "";
	}
	private BigDecimal setBigDecimal(String[] strings) {
		return strings != null && strings.length > 0 ? new BigDecimal(strings[0].trim()) : null;
	}
	
	private String uploadFile(FilePart file, String distributor) throws IOException {
		try {
			if(StringUtils.isEmpty(filePath)) {
				Configuration config = Play.application().configuration().getConfig("b2bSPA");
				filePath = config.getString("imagePath");
			}
			String path = filePath + File.separator + distributor;
			File folder = new File(path);
			Logger.info("文件路径：" + path);
			if (!folder.exists()) {
				folder.mkdirs();
				Logger.info("文件路径创建成功：" + path);
			}
			File target = new File(filePath + File.separator + distributor + File.separator + System.currentTimeMillis() + "-" +file.getFilename());
			target.createNewFile();
			Files.copy(file.getFile(), target);
			return target.getAbsolutePath();
		} catch (Exception e) {
			Logger.error("创建文件夹错误：" + e);
			return "";
		}
		
	}

	@Override
	public Page<WithdrawBalanceDto> getWithdrawRecord(JSONObject params) {
		Map<String, Object> queryMap = Maps.newHashMap();
        Integer currPage = params.containsKey("currPage") ? params.getInteger("currPage") : 1;
        Integer pageSize = params.containsKey("pageSize") ? params.getInteger("pageSize") : 10;
        queryMap.put("currPage", currPage);
        queryMap.put("pageSize", pageSize);
        queryMap.put("sort",params.getString("sidx"));
        queryMap.put("filter",params.getString("sord"));
		if(params.containsKey("applyType") && StringUtils.isNotBlank(params.getString("applyType")))
			queryMap.put("applyType", params.getString("applyType"));
		//状态
		if(params.containsKey("auditState") && StringUtils.isNotBlank(params.getString("auditState")))
			queryMap.put("auditState", params.getInteger("auditState"));
		//申请时间
		if (params.containsKey("createDate") && StringUtils.isNotBlank(params.getString("createDate"))) {
			Calendar ca = Calendar.getInstance();
			ca.add(Calendar.MONTH, -params.getInteger("createDate"));
			queryMap.put("createDate", DateUtils.date2FullDateTimeString(ca.getTime()));
		}
		//审核时间
		if (params.containsKey("updateDate") && StringUtils.isNotBlank(params.getString("updateDate"))) {
			Calendar ca = Calendar.getInstance();
			ca.add(Calendar.MONTH, -params.getInteger("updateDate"));
			queryMap.put("updateDate", DateUtils.date2FullDateTimeString(ca.getTime()));
		}
		//转帐时间
		if (params.containsKey("transferTime") && StringUtils.isNotBlank(params.getString("transferTime"))) {
			Calendar ca = Calendar.getInstance();
			ca.add(Calendar.MONTH, -params.getInteger("transferTime"));
			queryMap.put("transferTime", DateUtils.date2FullDateTimeString(ca.getTime()));
		}
		//模糊查询
		if(params.containsKey("search") && StringUtils.isNotBlank(params.getString("search")))
			queryMap.put("search", params.getString("search"));
		//当前登录人 by huchuyin 2016-9-29
		if (params.containsKey("distributorEmail") && StringUtils.isNotBlank(params.getString("distributorEmail"))) {
			queryMap.put("distributorEmail",params.getString("distributorEmail"));
		}
		//End by huchuyin 2016-9-29
		//是否M站提现账号，'1':是，'0':否
		if(params.containsKey("ismswa") && StringUtils.isNotBlank(params.getString("ismswa")))
			queryMap.put("ismswa", params.getString("ismswa"));
		Logger.debug("getWithdrawRecord    queryMap----->" + Json.toJson(queryMap).toString());
		Integer rows = applyMapper.queryWithdrawCount(queryMap);
		List<WithdrawBalanceDto> lists = applyMapper.queryWithdrawRecord(queryMap);
		return new Page<WithdrawBalanceDto>(currPage, pageSize, rows, lists);
	}

	@Override
	public String checktranNo(String tno) {
		ObjectNode node = Json.newObject();
		boolean suc = applyMapper.getBytransferNumber(tno).size()<=0;
		node.put("suc", suc);
		node.put("msg",suc?"付款流水号可用":"付款流水号重复");
		return node.toString();
	}

	@Override
	public Map<String, Object> backstageWelcome(String email) {
		Map<String, Object> result = Maps.newHashMap();
		Map<String, Object> param = Maps.newHashMap();
		param.put("applyType", "1");
		param.put("auditState", 0);
		result.put("trialCount", applyMapper.queryApplyCount(param));//充值初审
		param.clear();
		param.put("applyType", "1");
		param.put("auditOrreview", 1);
		param.put("reviewState", 4);
		result.put("reviewCount", applyMapper.queryApplyCount(param));//充值复审
		param.clear();
		param.put("applyType", "2");
		param.put("auditState", 0);
		result.put("withdraw", applyMapper.queryWithdrawCount(param));
		return result;
	}
}
