package dto.marketing.promotion;

import java.util.Date;
import java.util.List;

/**
 * 封装促销类型的所有数据：基本信息、条件、优惠
 * 
 * @author huangjc
 * @date 2016年7月25日
 */
public class FullProTypeDto {
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

	/**
	 * 条件list
	 */
	private List<FullProCondtDto> fullProCondtDtoList;
	/**
	 * 优惠list
	 */
	private List<FullProPvlgDto> fullProPvlgDtoList;

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

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public List<FullProCondtDto> getFullProCondtDtoList() {
		return fullProCondtDtoList;
	}

	public void setFullProCondtDtoList(List<FullProCondtDto> fullProCondtDtoList) {
		this.fullProCondtDtoList = fullProCondtDtoList;
	}

	public List<FullProPvlgDto> getFullProPvlgDtoList() {
		return fullProPvlgDtoList;
	}

	public void setFullProPvlgDtoList(List<FullProPvlgDto> fullProPvlgDtoList) {
		this.fullProPvlgDtoList = fullProPvlgDtoList;
	}

}
