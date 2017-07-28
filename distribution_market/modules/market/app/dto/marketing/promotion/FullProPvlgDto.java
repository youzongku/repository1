package dto.marketing.promotion;


public class FullProPvlgDto {
	private Integer id;

	private String name;

	private String remark;
	/** 是否删除，默认false */
	private boolean isDelete = Boolean.FALSE;
	/** 优惠类型，手工配置 */
	private Integer pType;

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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public Integer getpType() {
		return pType;
	}

	public void setpType(Integer pType) {
		this.pType = pType;
	}

}
