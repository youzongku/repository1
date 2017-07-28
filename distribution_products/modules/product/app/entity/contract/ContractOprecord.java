package entity.contract;

import java.util.Date;

public class ContractOprecord {
    private Integer id;

    private String cno;

    private String opuser;

    private Date opdate;
    
    private String opdateStr;

    private String comment;

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

    public String getCno() {
		return cno;
	}

	public void setCno(String cno) {
		this.cno = cno;
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