package dto.dismember;

import utils.dismember.DateUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by luwj on 2016/9/20.
 */
public class WithdrawBalanceDto implements Serializable {

    private static final long serialVersionUID = 7504222556772959472L;

    private Integer id;

    private String onlineApplyNo;//提现单号

    private Integer withdrawAccountId;//提现卡号id

    private String withdrawAccountNo;//提现卡号

    private String accountUser;//姓名

    private String accountUnit;//开户行或第三方

    private BigDecimal withdrawAmount;//提现金额

    private Date createDate;//申请时间

    private String createDateStr;

    private Date transferTime;//转帐时间

    private String transferTimeStr;

    private Date updateDate;//审核时间

    private String updateDateStr;

    private Integer auditState;//0:待审核,1:审核不通过,2:已转帐

    private String auditStateStr;

    private String transferNumber;//转帐流水号

    private BigDecimal transferAmount;//转帐金额

    private String auditReasons;//审核理由

    private String auditMark;

    private String applyType;//申请类型（1:充值、2:提现）

    private String distributorEmail;//分销商用户名

    private String accountProvince;//开户行所在省

    private String accountCity;//开户行所在市

    private String accountPC;//开户行所在省市，该属性用于导出Excel

    private String accountType;//提现账户类型（0:银行卡, 1:支付宝）
    
    private BigDecimal counterFee;
    
    private String nickName;//分销商昵称
    
    public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOnlineApplyNo() {
        return onlineApplyNo;
    }

    public void setOnlineApplyNo(String onlineApplyNo) {
        this.onlineApplyNo = onlineApplyNo;
    }

    public Integer getWithdrawAccountId() {
        return withdrawAccountId;
    }

    public void setWithdrawAccountId(Integer withdrawAccountId) {
        this.withdrawAccountId = withdrawAccountId;
    }

    public String getWithdrawAccountNo() {
        return withdrawAccountNo;
    }

    public void setWithdrawAccountNo(String withdrawAccountNo) {
        this.withdrawAccountNo = withdrawAccountNo;
    }

    public String getAccountUser() {
        return accountUser;
    }

    public void setAccountUser(String accountUser) {
        this.accountUser = accountUser;
    }

    public String getAccountUnit() {
        return accountUnit;
    }

    public void setAccountUnit(String accountUnit) {
        this.accountUnit = accountUnit;
    }

    public BigDecimal getWithdrawAmount() {
        return withdrawAmount;
    }

    public void setWithdrawAmount(BigDecimal withdrawAmount) {
        this.withdrawAmount = withdrawAmount;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getTransferTime() {
        return transferTime;
    }

    public void setTransferTime(Date transferTime) {
        this.transferTime = transferTime;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getAuditState() {
        return auditState;
    }

    public void setAuditState(Integer auditState) {
        this.auditState = auditState;
    }

    public String getTransferNumber() {
        return transferNumber;
    }

    public void setTransferNumber(String transferNumber) {
        this.transferNumber = transferNumber;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
    }

    public String getAuditReasons() {
        return auditReasons;
    }

    public void setAuditReasons(String auditReasons) {
        this.auditReasons = auditReasons;
    }

    public String getApplyType() {
        return applyType;
    }

    public void setApplyType(String applyType) {
        this.applyType = applyType;
    }

    public String getCreateDateStr() {
        return createDate == null?"": DateUtils.date2string(createDate, DateUtils.FORMAT_DATETIME);
    }

    public String getTransferTimeStr() {
        return transferTime == null?"": DateUtils.date2string(transferTime, DateUtils.FORMAT_DATETIME);
    }

    public String getUpdateDateStr() {
        return updateDate == null?"": DateUtils.date2string(updateDate, DateUtils.FORMAT_DATETIME);
    }

    public String getAuditStateStr(){
        String str = "";
        switch (auditState){
            case 0 : str = "处理中";
                break;
            case 1 : str = "处理失败";
                break;
            case 2 : str = "已完成";
                break;
        }
        return str;
    }

    public String getAuditMark() {
        return auditMark;
    }

    public void setAuditMark(String auditMark) {
        this.auditMark = auditMark;
    }

    public String getDistributorEmail() {
        return distributorEmail;
    }

    public void setDistributorEmail(String distributorEmail) {
        this.distributorEmail = distributorEmail;
    }

    public void setCreateDateStr(String createDateStr) {
        this.createDateStr = createDateStr;
    }

    public void setTransferTimeStr(String transferTimeStr) {
        this.transferTimeStr = transferTimeStr;
    }

    public void setUpdateDateStr(String updateDateStr) {
        this.updateDateStr = updateDateStr;
    }

    public void setAuditStateStr(String auditStateStr) {
        this.auditStateStr = auditStateStr;
    }

    public String getAccountProvince() {
        return accountProvince;
    }

    public void setAccountProvince(String accountProvince) {
        this.accountProvince = accountProvince;
    }

    public String getAccountCity() {
        return accountCity;
    }

    public void setAccountCity(String accountCity) {
        this.accountCity = accountCity;
    }

    public String getAccountPC() {
        return accountPC;
    }

    public void setAccountPC(String accountPC) {
        this.accountPC = accountPC;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

	public BigDecimal getCounterFee() {
		return counterFee;
	}

	public void setCounterFee(BigDecimal counterFee) {
		this.counterFee = counterFee;
	}
    
}
