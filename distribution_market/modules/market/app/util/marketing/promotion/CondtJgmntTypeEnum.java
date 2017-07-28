package util.marketing.promotion;
/**
 * 条件判断类型
 * @author huangjc
 * @date 2016年7月29日
 */
public enum CondtJgmntTypeEnum {
	GT("大于", "gt"), LT("小于", "lt"), GTEQ("大于等于", "gteq"), LTEQ("小于等于", "lteq"), Y(
			"是", "y"), N("非", "n"), LTV("区间", "ltv");
	private String name;
	private String jType;

	private CondtJgmntTypeEnum(String name, String jType) {
		this.name = name;
		this.jType = jType;
	}

	public String getName() {
		return name;
	}

	public String getjType() {
		return jType;
	}

}
