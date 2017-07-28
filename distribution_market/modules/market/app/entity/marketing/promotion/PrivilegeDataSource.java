package entity.marketing.promotion;
/**
 * 优惠数据来源
 * @author huangjc
 * @date 2016年7月22日
 */
public class PrivilegeDataSource {
    private Integer id;
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

	@Override
	public String toString() {
		return "PrivilegeDataSource [id=" + id + ", isDelete=" + isDelete + "]";
	}
    
    
}