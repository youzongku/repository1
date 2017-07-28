package entity.discart;

import java.io.Serializable;
import java.util.Date;

/**
 * 分销商购物车表实体
 */
public class DisCart implements Serializable {

	private static final long serialVersionUID = 4601107739967845651L;

	private Integer id; // 主键id

	private String email;// 邮箱

	private Integer useable;// 是否可用（1：可用，0：不可用）

	private Date createTime;// 创建时间

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

	public Integer getUseable() {
		return useable;
	}

	public void setUseable(Integer useable) {
		this.useable = useable;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}