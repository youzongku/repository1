package entity.dismember;

import java.io.Serializable;
//b2b全局变量实体类
public class CommonField implements Serializable {
	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;
	private Integer id;//主键id
	private String name;//变量名称
	private String value;//变量值
	private Boolean isAble;//是否可用（默认为true表示可用）
	private String describe;//描述

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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Boolean getIsAble() {
		return isAble;
	}

	public void setIsAble(Boolean isAble) {
		this.isAble = isAble;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}
}
