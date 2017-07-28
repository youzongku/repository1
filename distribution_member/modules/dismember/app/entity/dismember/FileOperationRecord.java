package entity.dismember;

import java.util.Date;

/**
 * 注册申请成为经销商文件修改操作记录实体类
 * @author Administrator
 *
 */
public class FileOperationRecord {
    private Integer id;

    private String operator;

    private Date operateTime;

    private String opdesc;

    private Integer applyId;
    
    private String operateTimeDesc;

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

    public Integer getApplyId() {
        return applyId;
    }

    public void setApplyId(Integer applyId) {
        this.applyId = applyId;
    }

	public String getOperateTimeDesc() {
		return operateTimeDesc;
	}

	public void setOperateTimeDesc(String operateTimeDesc) {
		this.operateTimeDesc = operateTimeDesc;
	}
}