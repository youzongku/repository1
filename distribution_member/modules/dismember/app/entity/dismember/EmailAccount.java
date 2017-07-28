package entity.dismember;

import java.util.Date;

public class EmailAccount {
	private Integer iid;// 主键

	private Integer iwebsiteid;// 站点编号

	private String ctype;// 邮件类型名称

	private String csmtphostname;// 邮件服务器地址

	private Integer iserverport;// 邮件服务器端口

	private String cusername;// 帐号名称

	private String cemail;// 邮箱帐号

	private String cpassword;// 发送邮箱密码

	private String ccreateuser;// 创建人

	private Date dcreatedate;// 创建时间

	public Integer getIid() {
		return iid;
	}

	public void setIid(Integer iid) {
		this.iid = iid;
	}

	public Integer getIwebsiteid() {
		return iwebsiteid;
	}

	public void setIwebsiteid(Integer iwebsiteid) {
		this.iwebsiteid = iwebsiteid;
	}

	public String getCtype() {
		return ctype;
	}

	public void setCtype(String ctype) {
		this.ctype = ctype;
	}

	public String getCsmtphostname() {
		return csmtphostname;
	}

	public void setCsmtphostname(String csmtphostname) {
		this.csmtphostname = csmtphostname;
	}

	public Integer getIserverport() {
		return iserverport;
	}

	public void setIserverport(Integer iserverport) {
		this.iserverport = iserverport;
	}

	public String getCusername() {
		return cusername;
	}

	public void setCusername(String cusername) {
		this.cusername = cusername;
	}

	public String getCemail() {
		return cemail;
	}

	public void setCemail(String cemail) {
		this.cemail = cemail;
	}

	public String getCpassword() {
		return cpassword;
	}

	public void setCpassword(String cpassword) {
		this.cpassword = cpassword;
	}

	public String getCcreateuser() {
		return ccreateuser;
	}

	public void setCcreateuser(String ccreateuser) {
		this.ccreateuser = ccreateuser;
	}

	public Date getDcreatedate() {
		return dcreatedate;
	}

	public void setDcreatedate(Date dcreatedate) {
		this.dcreatedate = dcreatedate;
	}

	@Override
	public String toString() {
		return "EmailAccount [iid=" + iid + ", iwebsiteid=" + iwebsiteid + ", ctype=" + ctype + ", csmtphostname="
				+ csmtphostname + ", iserverport=" + iserverport + ", cusername=" + cusername + ", cemail=" + cemail
				+ ", cpassword=" + cpassword + ", ccreateuser=" + ccreateuser + ", dcreatedate=" + dcreatedate + "]";
	}

}