package entity.dismember;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员邮箱激活实体
 * Created by luwj on 2015/11/24.
 */
public class DisEmailVerify implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer iid;

    private String cemail;//会员邮箱

    private Boolean bisending;//是否已发送

    private String cmark;//今天是否发送邮箱认证标记

    private String cactivationcode;//邮箱激活码

    private Integer idaynumber;//每天发送邮箱次数(最多为3次)

    private Date dvaliddate;//邮件激活有效时间(3天)

    private Date dsenddate;//邮件发送日期

    private Date dcreatedate;//创建时间
    
    private Integer sendType;//邮件发送类型
    
    private String sendParams;//邮件参数

    public Integer getSendType() {
		return sendType;
	}

	public void setSendType(Integer sentType) {
		this.sendType = sentType;
	}

	public Integer getIid() {
        return iid;
    }

    public void setIid(Integer iid) {
        this.iid = iid;
    }

    public String getCemail() {
        return cemail;
    }

    public void setCemail(String cemail) {
        this.cemail = cemail;
    }

    public Boolean getBisending() {
        return bisending;
    }

    public void setBisending(Boolean bisending) {
        this.bisending = bisending;
    }

    public String getCmark() {
        return cmark;
    }

    public void setCmark(String cmark) {
        this.cmark = cmark;
    }

    public String getCactivationcode() {
        return cactivationcode;
    }

    public void setCactivationcode(String cactivationcode) {
        this.cactivationcode = cactivationcode;
    }

    public Integer getIdaynumber() {
        return idaynumber;
    }

    public void setIdaynumber(Integer idaynumber) {
        this.idaynumber = idaynumber;
    }

    public Date getDvaliddate() {
        return dvaliddate;
    }

    public void setDvaliddate(Date dvaliddate) {
        this.dvaliddate = dvaliddate;
    }

    public Date getDsenddate() {
        return dsenddate;
    }

    public void setDsenddate(Date dsenddate) {
        this.dsenddate = dsenddate;
    }

    public Date getDcreatedate() {
        return dcreatedate;
    }

    public void setDcreatedate(Date dcreatedate) {
        this.dcreatedate = dcreatedate;
    }

	public String getSendParams() {
		return sendParams;
	}

	public void setSendParams(String sendParams) {
		this.sendParams = sendParams;
	}
    
}