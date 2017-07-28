package entity.contract;

import java.util.Date;

import util.product.DateUtils;

/**
 * 合同费用操作日志实体
 * @author zbc
 * 2017年3月27日 上午11:15:32
 */
public class ChargesOprecord {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 费用id
     */
    private Integer cid;

    /**
     * 操作人
     */
    private String opuser;

    /**
     * 操作时间
     */
    private Date opdate;

    /**
     * 操作描述
     */
    private String comment;
    
    public ChargesOprecord(){
    	
    }

    /**
	 * @param cid
	 * @param opuser
	 * @param comment
	 */
	public ChargesOprecord(Integer cid, String opuser, String comment) {
		super();
		this.cid = cid;
		this.opuser = opuser;
		this.comment = comment;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
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
    
    public String getOpdateStr() {
        return DateUtils.date2string(opdate, DateUtils.FORMAT_FULL_DATETIME);
    }
    
}