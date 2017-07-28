package entity.dismember;

import java.util.Date;

public class AccountOperationRecord {
    private Integer id;//主键id

    private String operator;//账户操作者

    private Date operateTime;//操作日期

    private String opdesc;//操作描述

    private Integer accountId;//账户id
    
    private String operateTimeDesc;//操作时间字符串
    
    private String reduceMark;//备注

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getOpdesc() {
        return opdesc;
    }

    public void setOpdesc(String opdesc) {
        this.opdesc = opdesc;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

	public String getOperateTimeDesc() {
		return operateTimeDesc;
	}

	public void setOperateTimeDesc(String operateTimeDesc) {
		this.operateTimeDesc = operateTimeDesc;
	}

	public String getReduceMark() {
		return reduceMark;
	}

	public void setReduceMark(String reduceMark) {
		this.reduceMark = reduceMark;
	}
}