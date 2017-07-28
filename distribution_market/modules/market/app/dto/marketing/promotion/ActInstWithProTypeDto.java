package dto.marketing.promotion;

/**
 * 活动实例基本信息及所属模板信息
 * 
 * @author huangjc
 * @date 2016年10月17日
 */
public class ActInstWithProTypeDto {
	private Integer actInstId;
	private Integer proTypeId;
	/** 名称 */
	private String name;
	/** 描述 */
	private String description;
	/** 属性，于条件的属性一致 */
	private Short attr;

	public Integer getActInstId() {
		return actInstId;
	}

	public void setActInstId(Integer actInstId) {
		this.actInstId = actInstId;
	}

	public Integer getProTypeId() {
		return proTypeId;
	}

	public void setProTypeId(Integer proTypeId) {
		this.proTypeId = proTypeId;
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

	@Override
	public String toString() {
		return "ActInstWithProTypeDto [actInstId=" + actInstId + ", proTypeId="
				+ proTypeId + ", name=" + name + ", description=" + description
				+ ", attr=" + attr + "]";
	}

}
