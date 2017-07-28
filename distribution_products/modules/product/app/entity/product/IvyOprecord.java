package entity.product;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * KA库存 库存释放日志实体
 * @author zbc
 * 2017年5月9日 下午3:20:49
 */
public class IvyOprecord implements Serializable{

	private static final long serialVersionUID = 678534836129998371L;

	/**
	 * 主键
	 */
	private Integer id;

	@ApiModelProperty("锁库id")
    private Integer lockId;

	@ApiModelProperty("操作人")
    private String opuser;

	@ApiModelProperty("操作时间")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date opdate;

	@ApiModelProperty("操作描述")
    private String comment;
	
	@ApiModelProperty("备注")
	private String remark;
	
    public IvyOprecord(Integer lockId, String opuser, Date opdate, String comment, String remark) {
		super();
		this.lockId = lockId;
		this.opuser = opuser;
		this.opdate = opdate;
		this.comment = comment;
		this.remark = remark;
	}
    
    public IvyOprecord(){
    	
    }

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLockId() {
        return lockId;
    }

    public void setLockId(Integer lockId) {
        this.lockId = lockId;
    }

    public String getOpuser() {
        return opuser;
    }

    public void setOpuser(String opuser) {
        this.opuser = opuser;
    }

    public Date getOpdate() {
        return opdate;
    }

    public void setOpdate(Date opdate) {
        this.opdate = opdate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}