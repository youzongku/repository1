package dto.marketing.promotion.condt.value;
/**
 * 封装更新条件实例的对象
 * @author huangjc
 * @date 2016年8月1日
 */
public class FinalCondtValue {
	private Short isSetV;
	private int condtInstId;// 条件实例id
	private String jType;// 条件判断类型
	private String jgmntName;
	private int jdmntTypeId;// 条件判断类型id
	private String jsonValue;

	public String getJgmntName() {
		return jgmntName;
	}

	public void setJgmntName(String jgmntName) {
		this.jgmntName = jgmntName;
	}

	public Short getIsSetV() {
		return isSetV;
	}

	public void setIsSetV(Short isSetV) {
		this.isSetV = isSetV;
	}

	public int getCondtInstId() {
		return condtInstId;
	}

	public void setCondtInstId(int condtInstId) {
		this.condtInstId = condtInstId;
	}

	public String getjType() {
		return jType;
	}

	public void setjType(String jType) {
		this.jType = jType;
	}

	public int getJdmntTypeId() {
		return jdmntTypeId;
	}

	public void setJdmntTypeId(int jdmntTypeId) {
		this.jdmntTypeId = jdmntTypeId;
	}

	public String getJsonValue() {
		return jsonValue;
	}

	public void setJsonValue(String jsonValue) {
		this.jsonValue = jsonValue;
	}

	@Override
	public String toString() {
		return "FinalCondtValue [condtInstId=" + condtInstId + ", jType="
				+ jType + ", jdmntTypeId=" + jdmntTypeId + ", jsonValue="
				+ jsonValue + "]";
	}

}
