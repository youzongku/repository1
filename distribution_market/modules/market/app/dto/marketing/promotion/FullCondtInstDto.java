package dto.marketing.promotion;

import java.util.List;

import entity.marketing.promotion.ConditionInstanceExt;
import entity.marketing.promotion.ConditionJudgementType;

/**
 * 封装一个条件实例及判断类型
 * 
 * @author huangjc
 * @date 2016年7月29日
 */
public class FullCondtInstDto {
	private Integer id;
	/** 活动实例id */
	private Integer actInstId;
	/** 条件id */
	private Integer condtId;
	/** 条件的类型 */
	private Integer cType;
	/** 名称 */
	private String name;
	/** 属性 */
	private Short attr;
	/** 条件判断类型id */
	private Integer condtJgmntTypeId;
	/** 条件判断类型名称 */
	private String condtJgmntName;
	/** 条件判断类型 */
	private String condtJgmntType;
	/** 条件判断类型的值 */
	private String condtJgmntValue;

	private String field4;

	private String field5;
	
	/** 是否删除，默认false */
	private boolean isDelete = Boolean.FALSE;

	private Short isSetV;

	/** 优先级别 */
	private Short priority = 1;

	private boolean hasExtCondt = Boolean.FALSE;

	private FullPvlgInstDto fullPvlgInstDto;

	// 条件的判断类型
	private List<ConditionJudgementType> condtJgmntTypeList;

	// 额外的指定指定商品属性或指定购物车属性
	private ConditionInstanceExt condtInstExt;
	
	/**
	 * 父条件实例id，有值的话，说明此条件实例是一个可阶梯条件下的子条件实例
	 */
	private Integer parentId;
	
	/** 可阶梯条件实例的子条件实例 */
	private List<FullCondtInstDto> subCondtInstDtoList;

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public List<FullCondtInstDto> getSubCondtInstDtoList() {
		return subCondtInstDtoList;
	}

	public void setSubCondtInstDtoList(List<FullCondtInstDto> subCondtInstDtoList) {
		this.subCondtInstDtoList = subCondtInstDtoList;
	}

	public Short getIsSetV() {
		return isSetV;
	}

	public void setIsSetV(Short isSetV) {
		this.isSetV = isSetV;
	}

	public Short getPriority() {
		return priority;
	}

	public void setPriority(Short priority) {
		this.priority = priority;
	}

	public boolean isHasExtCondt() {
		return hasExtCondt;
	}

	public void setHasExtCondt(boolean hasExtCondt) {
		this.hasExtCondt = hasExtCondt;
	}

	public ConditionInstanceExt getCondtInstExt() {
		return condtInstExt;
	}

	public void setCondtInstExt(ConditionInstanceExt condtInstExt) {
		this.condtInstExt = condtInstExt;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getActInstId() {
		return actInstId;
	}

	public void setActInstId(Integer actInstId) {
		this.actInstId = actInstId;
	}

	public Integer getCondtId() {
		return condtId;
	}

	public void setCondtId(Integer condtId) {
		this.condtId = condtId;
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

	public Integer getCondtJgmntTypeId() {
		return condtJgmntTypeId;
	}

	public void setCondtJgmntTypeId(Integer condtJgmntTypeId) {
		this.condtJgmntTypeId = condtJgmntTypeId;
	}

	public String getCondtJgmntName() {
		return condtJgmntName;
	}

	public void setCondtJgmntName(String condtJgmntName) {
		this.condtJgmntName = condtJgmntName;
	}

	public String getCondtJgmntType() {
		return condtJgmntType;
	}

	public void setCondtJgmntType(String condtJgmntType) {
		this.condtJgmntType = condtJgmntType;
	}

	public String getCondtJgmntValue() {
		return condtJgmntValue;
	}

	public void setCondtJgmntValue(String condtJgmntValue) {
		this.condtJgmntValue = condtJgmntValue;
	}

	public String getField4() {
		return field4;
	}

	public void setField4(String field4) {
		this.field4 = field4;
	}

	public String getField5() {
		return field5;
	}

	public void setField5(String field5) {
		this.field5 = field5;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public List<ConditionJudgementType> getCondtJgmntTypeList() {
		return condtJgmntTypeList;
	}

	public void setCondtJgmntTypeList(
			List<ConditionJudgementType> condtJgmntTypeList) {
		this.condtJgmntTypeList = condtJgmntTypeList;
	}

	public FullPvlgInstDto getFullPvlgInstDto() {
		return fullPvlgInstDto;
	}

	public void setFullPvlgInstDto(FullPvlgInstDto fullPvlgInstDto) {
		this.fullPvlgInstDto = fullPvlgInstDto;
	}

	@Override
	public String toString() {
		return "FullCondtInstDto [id=" + id + ", actInstId=" + actInstId
				+ ", condtId=" + condtId + ", cType=" + cType + ", name="
				+ name + ", attr=" + attr + ", condtJgmntTypeId="
				+ condtJgmntTypeId + ", condtJgmntName=" + condtJgmntName
				+ ", condtJgmntType=" + condtJgmntType + ", condtJgmntValue="
				+ condtJgmntValue + ", field4=" + field4 + ", field5=" + field5
				+ ", isDelete=" + isDelete + ", isSetV=" + isSetV
				+ ", priority=" + priority + ", hasExtCondt=" + hasExtCondt
				+ ", fullPvlgInstDto=" + fullPvlgInstDto
				+ ", condtJgmntTypeList=" + condtJgmntTypeList
				+ ", condtInstExt=" + condtInstExt + ", subCondtInstDtoList="
				+ subCondtInstDtoList + "]";
	}

}
