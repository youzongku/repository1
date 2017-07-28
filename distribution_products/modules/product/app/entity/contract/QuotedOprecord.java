package entity.contract;

import java.util.Date;

public class QuotedOprecord {
    private Integer id;

    private Integer qid;

    private String opuser;

    private Date opdate;

    private String comment;
    
    private String opdateStr;
    
    public QuotedOprecord(){
    	
    }
    public QuotedOprecord(Integer qid, String opuser, String comment,Date opdate) {
		super();
		this.qid = qid;
		this.opuser = opuser;
		this.comment = comment;
		this.opdate = opdate;
	}

	public String getOpdateStr() {
		return opdateStr;
	}

	public void setOpdateStr(String opdateStr) {
		this.opdateStr = opdateStr;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQid() {
        return qid;
    }

    public void setQid(Integer qid) {
        this.qid = qid;
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