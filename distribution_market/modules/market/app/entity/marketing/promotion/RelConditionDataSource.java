package entity.marketing.promotion;
/**
 * 条件与条件数据来源关系
 * @author huangjc
 * @date 2016年7月22日
 */
public class RelConditionDataSource {
    private Integer id;
    /** 条件数据来源id */
    private Integer dsId;
    /** 条件id */
    private Integer condtId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDsId() {
        return dsId;
    }

    public void setDsId(Integer dsId) {
        this.dsId = dsId;
    }

    public Integer getCondtId() {
        return condtId;
    }

    public void setCondtId(Integer condtId) {
        this.condtId = condtId;
    }

	@Override
	public String toString() {
		return "RelConditionDataSource [id=" + id + ", dsId=" + dsId
				+ ", condtId=" + condtId + "]";
	}
}