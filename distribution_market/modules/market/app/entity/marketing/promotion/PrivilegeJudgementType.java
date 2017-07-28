package entity.marketing.promotion;

/**
 * 优惠判断类型
 * 
 * @author huangjc
 * @date 2016年7月22日
 */
public class PrivilegeJudgementType {
	private Integer id;
	/** 优惠判断类型id */
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
		return "PrivilegeJudgementType [id=" + id + ", name=" + name
				+ ", jType=" + jType + ", isDelete=" + isDelete + "]";
	}

}