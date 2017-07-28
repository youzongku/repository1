package entity.dismember;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 后台账号操作日志实体
 * @author zbc
 * 2017年4月28日 下午3:00:06
 */
public class AdminOperateRecord implements Serializable{

	private static final long serialVersionUID = -5718984929241453793L;

	/**
     * 主键
     */
    private Integer id;

    @ApiModelProperty("操作人")
    private String operator;

    @ApiModelProperty("操作时间")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date operateTime;

    @ApiModelProperty("操作描述")
    private String opdesc;

    /**
     * 后台账号id
     */
    private Integer adminId;

    
    public AdminOperateRecord(){
    	
    }
    /**
	 * @param id
	 * @param operator
	 * @param operateTime
	 * @param opdesc
	 * @param adminId
	 */
	public AdminOperateRecord(String operator, String opdesc, Integer adminId) {
		this.operator = operator;
		this.opdesc = opdesc;
		this.adminId = adminId;
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

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }
}