package entity.marketing.promotion;
/**
 * 优惠与优惠判断类型关系
 * @author huangjc
 * @date 2016年7月22日
 */
public class RelPrivilegeJudgementType {
    private Integer id;
    /** 优惠id */
    private Integer pvlgId;
    /** 优惠判断类型id */
    private Integer pvlgJgmntTypeId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPvlgId() {
        return pvlgId;
    }

    public void setPvlgId(Integer pvlgId) {
        this.pvlgId = pvlgId;
    }

    public Integer getPvlgJgmntTypeId() {
        return pvlgJgmntTypeId;
    }

    public void setPvlgJgmntTypeId(Integer pvlgJgmntTypeId) {
        this.pvlgJgmntTypeId = pvlgJgmntTypeId;
    }

	@Override
	public String toString() {
		return "RelPrivilegeJudgementType [id=" + id + ", pvlgId=" + pvlgId
				+ ", pvlgJgmntTypeId=" + pvlgJgmntTypeId + "]";
	}
}