package dto.dismember;

import java.io.Serializable;

public class PasswordResetForm implements Serializable {
	private static final long serialVersionUID = 1L;
	String email; //邮箱
	String cid; //用户id
	String passwd; //密码
	String confirm_password; //确认密码

	@Override
	public String toString() {
		return "PasswordResetUpdateForm [email=" + email + ", cid=" + cid
				+ ", passwd=" + passwd + ", confirm_password="
				+ confirm_password + "]";
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getConfirm_password() {
		return confirm_password;
	}

	public void setConfirm_password(String confirm_password) {
		this.confirm_password = confirm_password;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

}
