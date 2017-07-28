package entity.marketing.promotion;
/**
 * 优惠与优惠数据来源关系
 * @author huangjc
 * @date 2016年7月22日
 */
public class RelPrivilegeDataSource {
    private Integer id;
    /** 优惠id */
    private Integer pvlgId;
    /** 优惠数据来源id */
    private Integer dsId;

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

    public Integer getDsId() {
        return dsId;
    }

    public void setDsId(Integer dsId) {
        this.dsId = dsId;
    }

	@Override
	public String toString() {
		return "RelPrivilegeDataSource [id=" + id + ", pvlgId=" + pvlgId
				+ ", dsId=" + dsId + "]";
	}
}