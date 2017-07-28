package entity.marketing.promotion;
/**
 * 条件与条件判断类型关系
 * @author huangjc
 * @date 2016年7月22日
 */
public class RelConditionJudgementType {
    private Integer id;
    /** 条件判断类型id */
    private Integer condtJgmntTypeId;
    /** 条件id */
    private Integer condtId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCondtJgmntTypeId() {
        return condtJgmntTypeId;
    }

    public void setCondtJgmntTypeId(Integer condtJgmntTypeId) {
        this.condtJgmntTypeId = condtJgmntTypeId;
    }

    public Integer getCondtId() {
        return condtId;
    }

    public void setCondtId(Integer condtId) {
        this.condtId = condtId;
    }

	@Override
	public String toString() {
		return "RelConditionJudgementType [id=" + id + ", condtJgmntTypeId="
				+ condtJgmntTypeId + ", condtId=" + condtId + "]";
	}
}