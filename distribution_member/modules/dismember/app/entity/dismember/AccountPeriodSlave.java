package entity.dismember;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import constant.dismember.Constant;
import services.base.utils.DateFormatUtils;

/**
 * 账期从表实体
 * 
 * @author zbc 2017年2月24日 下午6:06:21
 */
public class AccountPeriodSlave implements Serializable {

	private static final long serialVersionUID = -5609976038537065671L;

	/**
	 * 主键
	 */
	private Integer id;

	/**
	 * 主表id
	 */
	private Integer masterId;

	/**
	 * 额度
	 */
	private BigDecimal totalLimit;

	/**
	 * 开始时间
	 */
	private Date startTime;

	/**
	 * 合同账期时间，应还款时间
	 */
	private Date contractPeriodDate;

	/**
	 * 红线时间，最迟还款时间
	 */
	private Date redLineDate;

	/**
	 * 红线时间长度
	 */
	private Integer redLineDays;

	/**
	 * 账期状态 0 未生效 1 可使用 2 待还款 3 已逾期 （账户冻结） 4 禁用中 （无法透支） 5 已完结
	 */
	private Integer state;

	/**
	 * 业绩周期开始时间(开始时间+1天)，账期开始时间（下一账期开始时间为上一账期的业绩周期结束时间）
	 * <p>
	 * 业绩周期开始时间默认与startTime开始时间一致
	 */
	private Date performanceStartTime;

	/**
	 * 业绩周期结束时间
	 */
	private Date performanceEndTime;

	/**
	 * 创建时间
	 */
	private Date createDate;

	/**
	 * 创建建人
	 */
	private String createUser;

	/**
	 * 更新时间
	 */
	private Date updateDate;

	/**
	 * 分销商账号
	 */
	private String account;

	/**
	 * 昵称
	 */
	private String nickName;

	/**
	 * 周期类型
	 */
	private Integer periodLength;

	/**
	 * 周期长度
	 */
	private Integer periodType;

	/**
	 * 是否已开启下一账期
	 */
	private Boolean hasNext;

	/**
	 * 需要结算的订单数量
	 */
	private Integer orderQty;

	/**
	 * 业务员
	 */
	private String saleMan;

	/**
	 * 责任人
	 */
	private String dutyOfficer;

	/**
	 * 总已还金额
	 */
	private BigDecimal rechargeLeft;

	/**
	 * 是否有账单标识
	 */
	private Boolean hasBill;

	/**
	 * 是否核销
	 */
	private Boolean isChargeOff;

	/**
	 * 已用额度，本期应还款金额
	 */
	private BigDecimal usedLimit;

	/**
	 * 是否有上一期
	 */
	private Boolean hasPrev;

	/**
	 * 应还金额
	 */
	private BigDecimal arearAmount;
	

	public BigDecimal getArearAmount() {
		return arearAmount;
	}

	public void setArearAmount(BigDecimal arearAmount) {
		this.arearAmount = arearAmount;
	}

	public Boolean getHasPrev() {
		return hasPrev;
	}

	public void setHasPrev(Boolean hasPrev) {
		this.hasPrev = hasPrev;
	}

	public BigDecimal getUsedLimit() {
		return usedLimit;
	}

	public void setUsedLimit(BigDecimal usedLimit) {
		this.usedLimit = usedLimit;
	}

	public Boolean getIsChargeOff() {
		return isChargeOff;
	}

	public void setIsChargeOff(Boolean isChargeOff) {
		this.isChargeOff = isChargeOff;
	}

	public Boolean getHasBill() {
		return hasBill;
	}

	public void setHasBill(Boolean hasBill) {
		this.hasBill = hasBill;
	}

	public BigDecimal getRechargeLeft() {
		return rechargeLeft;
	}

	public void setRechargeLeft(BigDecimal rechargeLeft) {
		this.rechargeLeft = rechargeLeft;
	}

	public String getDutyOfficer() {
		return dutyOfficer;
	}

	public void setDutyOfficer(String dutyOfficer) {
		this.dutyOfficer = dutyOfficer;
	}

	public String getSaleMan() {
		return saleMan;
	}

	public void setSaleMan(String saleMan) {
		this.saleMan = saleMan;
	}

	public Integer getOrderQty() {
		return orderQty;
	}

	public void setOrderQty(Integer orderQty) {
		this.orderQty = orderQty;
	}

	public Boolean getHasNext() {
		return hasNext;
	}

	public void setHasNext(Boolean hasNext) {
		this.hasNext = hasNext;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public Integer getPeriodLength() {
		return periodLength;
	}

	public void setPeriodLength(Integer periodLength) {
		this.periodLength = periodLength;
	}

	public Integer getPeriodType() {
		return periodType;
	}

	public void setPeriodType(Integer periodType) {
		this.periodType = periodType;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getMasterId() {
		return masterId;
	}

	public void setMasterId(Integer masterId) {
		this.masterId = masterId;
	}

	public BigDecimal getTotalLimit() {
		return totalLimit;
	}

	public void setTotalLimit(BigDecimal totalLimit) {
		this.totalLimit = totalLimit;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getContractPeriodDate() {
		return contractPeriodDate;
	}

	public void setContractPeriodDate(Date contractPeriodDate) {
		this.contractPeriodDate = contractPeriodDate;
	}

	public Date getRedLineDate() {
		return redLineDate;
	}

	public void setRedLineDate(Date redLineDate) {
		this.redLineDate = redLineDate;
	}

	public Integer getRedLineDays() {
		return redLineDays;
	}

	public void setRedLineDays(Integer redLineDays) {
		this.redLineDays = redLineDays;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Date getPerformanceStartTime() {
		return performanceStartTime;
	}

	public void setPerformanceStartTime(Date performanceStartTime) {
		this.performanceStartTime = performanceStartTime;
	}

	public Date getPerformanceEndTime() {
		return performanceEndTime;
	}

	public void setPerformanceEndTime(Date performanceEndTime) {
		this.performanceEndTime = performanceEndTime;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getStartTimeStr() {
		return formartDate(startTime);
	}

	public String getContractPeriodDateStr() {
		return formartDate(contractPeriodDate);
	}

	public String getRedLineDateStr() {
		return formartDate(redLineDate);
	}

	public String getPerformanceStartTimeStr() {
		return formartDate(performanceStartTime);
	}

	public String getPerformanceEndTimeStr() {
		return formartDate(performanceEndTime);
	}

	public String getCreateDateStr() {
		return formartDate(createDate);
	}

	public String getUpdateDateStr() {
		return formartDate(updateDate);
	}

	private static String formartDate(Date date) {
		return date != null ? DateFormatUtils.getDateTimeYYYYMMDD(date) : null;
	}

	public String getStateStr() {
		return Constant.ACCOUNT_PERIOD_STATU_MAP.get(state);
	}

	/**
	 * 待还款额度
	 */
	public BigDecimal getLeftLimit() {
		return usedLimit != null ? totalLimit.subtract(usedLimit).setScale(2, BigDecimal.ROUND_HALF_UP) : null;
	}

	public String getPeriodDesc() {
		if (this.periodLength != null && this.periodType != null) {
			switch (periodType) {
			case AccountPeriodMaster.PERIOD_TYPE_DATE:
				return this.periodLength + "天";
			case AccountPeriodMaster.PERIOD_TYPE_MONTH:
				return this.periodLength + "个自然月";
			case AccountPeriodMaster.PERIOD_TYPE_MONTH_STATEMENT:
				return "月结" + this.periodLength + "天";
			default:
				break;
			}
		}
		return null;
	}

}