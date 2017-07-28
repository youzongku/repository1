package entity.marketing.promotion;

import dto.marketing.promotion.ConditionMatchResult;
import dto.marketing.promotion.OrderPromotionActivityDto;
import dto.marketing.promotion.condt.value.BaseCondtValue;

/**
 * 条件实例
 * 
 * @author huangjc
 * @date 2016年7月22日
 */
public class ConditionInstance {
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
	
	/**
	 * 父条件实例id，有值的话，说明此条件实例是一个可阶梯条件下的子条件实例
	 */
	private Integer parentId;

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public boolean isHasExtCondt() {
		return hasExtCondt;
	}

	public void setHasExtCondt(boolean hasExtCondt) {
		this.hasExtCondt = hasExtCondt;
	}
	
	public Short getPriority() {
		return priority;
	}

	public void setPriority(Short priority) {
		this.priority = priority;
	}

	public Short getIsSetV() {
		return isSetV;
	}

	public void setIsSetV(Short isSetV) {
		this.isSetV = isSetV;
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

	@Override
	public String toString() {
		return "ConditionInstance [id=" + id + ", actInstId=" + actInstId
				+ ", condtId=" + condtId + ", cType=" + cType + ", name="
				+ name + ", attr=" + attr + ", condtJgmntTypeId="
				+ condtJgmntTypeId + ", condtJgmntName=" + condtJgmntName
				+ ", condtJgmntType=" + condtJgmntType + ", condtJgmntValue="
				+ condtJgmntValue + ", field4=" + field4 + ", field5=" + field5
				+ ", isDelete=" + isDelete + ", isSetV=" + isSetV
				+ ", priority=" + priority + ", hasExtCondt=" + hasExtCondt
				+ ", parentId=" + parentId + "]";
	}

	/**
	 * 检查传入的参数是否符合此条件
	 * @param dtoArg 传入的参数
	 * @return 符合true，不符合false
	 */
	public ConditionMatchResult handle(BaseCondtValue condtValue, ConditionInstanceExt condtInstExt,OrderPromotionActivityDto dtoArg){
		return condtValue.handle(this.condtJgmntType,condtInstExt, dtoArg);
	}

}