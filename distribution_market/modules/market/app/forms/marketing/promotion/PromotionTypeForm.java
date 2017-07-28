package forms.marketing.promotion;
/**
 * 促销类型：新增、修改的form
 * @author huangjc
 * @date 2016年7月26日
 */
public class PromotionTypeForm {
	private Integer id;
	/** 名称 */
	private String name;
	/** 描述 */
	private String description;
	/** 属性，于条件的属性一致 */
	private Short attr;

	private String createUser;

	private String lastUpdateUser;
	/** 条件id */
	private String condtIds;
	/** 优惠id */
	private String pvlgIds;

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



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public Short getAttr() {
		return attr;
	}



	public void setAttr(Short attr) {
		this.attr = attr;
	}



	public String getCreateUser() {
		return createUser;
	}



	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}



	public String getLastUpdateUser() {
		return lastUpdateUser;
	}



	public void setLastUpdateUser(String lastUpdateUser) {
		this.lastUpdateUser = lastUpdateUser;
	}



	public String getCondtIds() {
		return condtIds;
	}



	public void setCondtIds(String condtIds) {
		this.condtIds = condtIds;
	}



	public String getPvlgIds() {
		return pvlgIds;
	}



	public void setPvlgIds(String pvlgIds) {
		this.pvlgIds = pvlgIds;
	}



	@Override
	public String toString() {
		return "PromotionTypeForm [id=" + id + ", name=" + name
				+ ", description=" + description + ", attr=" + attr
				+ ", createUser=" + createUser + ", lastUpdateUser="
				+ lastUpdateUser + ", condtIds=" + condtIds + ", pvlgIds="
				+ pvlgIds + "]";
	}

}
