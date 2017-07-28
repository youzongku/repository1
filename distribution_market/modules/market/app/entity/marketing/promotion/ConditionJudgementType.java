package entity.marketing.promotion;

/**
 * 条件判断类型
 * 
 * @author huangjc
 * @date 2016年7月22日
 */
public class ConditionJudgementType {
	/** 大于 */
	public static final String JTYPE_GT = "gt";
	/** 小于 */
	public static final String JTYPE_LT = "lt";
	/** 大于等于 */
	public static final String JTYPE_GTEQ = "gteq";
	/** 小于等于 */
	public static final String JTYPE_LTEQ = "lteq";
	/** 是 */
	public static final String JTYPE_YES = "y";
	/** 区间 */
	public static final String JTYPE_LTV = "ltv";
	/** 非 */
	public static final String JTYPE_NO = "n";

	private Integer id;
	/** 名称 */
	private String name;

	private String jType;
	/** 是否删除，默认false */
	private boolean isDelete = Boolean.FALSE;

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

	public String getjType() {
		return jType;
	}

	public void setjType(String jType) {
		this.jType = jType;
	}

	@Override
	public String toString() {
		return "ConditionJudgementType [id=" + id + ", name=" + name
				+ ", jType=" + jType + ", isDelete=" + isDelete + "]";
	}

}