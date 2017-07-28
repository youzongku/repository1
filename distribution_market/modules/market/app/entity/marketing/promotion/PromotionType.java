package entity.marketing.promotion;

import java.util.Date;

/**
 * 促销类型（包含多个条件&优惠）见RelPromotionTypePrivilegeCondition
 * 
 * @author huangjc
 * @date 2016年7月22日
 */
public class PromotionType {
	private Integer id;
	/** 名称 */
	private String name;
	/** 描述 */
	private String description;
	/** 属性，于条件的属性一致 */
	private Short attr;

	private String createUser;

	private String lastUpdateUser;

	private Date createTime;

	private Date lastUpdateTime;
	/** 是否删除，默认false */
	private boolean isDelete = Boolean.FALSE;
	/** 是否被应用，true已应用，false未被应用,默认false */
	private boolean used = Boolean.FALSE;

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Short getAttr() {
		return attr;
	}

	public void setAttr(Short attr) {
		this.attr = attr;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getLastUpdateUser() {
		return lastUpdateUser;
	}

	public void setLastUpdateUser(String lastUpdateUser) {
		this.lastUpdateUser = lastUpdateUser;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	@Override
	public String toString() {
		return "PromotionType [id=" + id + ", name=" + name + ", description="
				+ description + ", attr=" + attr + ", createUser=" + createUser
				+ ", lastUpdateUser=" + lastUpdateUser + ", createTime="
				+ createTime + ", lastUpdateTime=" + lastUpdateTime
				+ ", isDelete=" + isDelete + ", used=" + used
				+ "]";
	}

}