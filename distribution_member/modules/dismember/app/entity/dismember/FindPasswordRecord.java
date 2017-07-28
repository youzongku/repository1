package entity.dismember;

import java.io.Serializable;
import java.util.Date;

public class FindPasswordRecord implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Integer id;//主键id
	private String email; // 邮箱地址
	private String key;// 秘钥
	private Integer timeout;// 过期期限（小时）
	private String decode;// 解码字符串
	private Date createTime;// 记录创建时间

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public String getDecode() {
		return decode;
	}

	public void setDecode(String decode) {
		this.decode = decode;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}
