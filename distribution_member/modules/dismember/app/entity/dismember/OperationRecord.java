package entity.dismember;

import java.util.Date;

public class OperationRecord {
	
    private Integer id;//id

    private String operator;//操作者

    private Date opdate;//操作时间

    private String opdesc;//操作描述，主要记录申请的状态变化

    private Integer applyId;//申请Id
    
    private String opDateStr;//操作时间字符串
    
    public String getOpDateStr() {
		return opDateStr;
	}

	public void setOpDateStr(String opDateStr) {
		this.opDateStr = opDateStr;
	}

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

    public Date getOpdate() {
        return opdate;
    }

    public void setOpdate(Date opdate) {
        this.opdate = opdate;
    }

    public String getOpdesc() {
        return opdesc;
    }

    public void setOpdesc(String opdesc) {
        this.opdesc = opdesc;
    }

    public Integer getApplyId() {
        return applyId;
    }

    public void setApplyId(Integer applyId) {
        this.applyId = applyId;
    }
}