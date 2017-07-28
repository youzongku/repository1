package dto.marketing.promotion;

import java.util.List;

import entity.marketing.promotion.ConditionJudgementType;

public class FullProCondtDto {
	private Integer id;
	private Integer cType;
	/** 名称 */
	private String name;
	/** 属性 */
	private Short attr;
	/** 备注 */
	private String remark;
	/** 是否删除，默认false */
	private boolean isDelete = Boolean.FALSE;

	private boolean hasExtCondt = Boolean.FALSE;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getcType() {
		return cType;
	}

	public void setcType(Integer cType) {
		this.cType = cType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Short getAttr() {
		return attr;
	}

	public void setAttr(Short attr) {
		this.attr = attr;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public boolean isHasExtCondt() {
		return hasExtCondt;
	}

	public void setHasExtCondt(boolean hasExtCondt) {
		this.hasExtCondt = hasExtCondt;
	}

	private List<ConditionJudgementType> condtJgmntTypeList;

	public List<ConditionJudgementType> getCondtJgmntTypeList() {
		return condtJgmntTypeList;
	}

	public void setCondtJgmntTypeList(
			List<ConditionJudgementType> condtJgmntTypeList) {
		this.condtJgmntTypeList = condtJgmntTypeList;
	}

}
