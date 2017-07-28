package forms.marketing.promotion;

/**
 * 促销类型查询表单数据
 * 
 * @author huangjc
 * @date 2016年7月25日
 */
public class PromotionTypeSearchForm extends BaseForm {

	private String proTypeName;

	private String createDate;// yyyy-DD-mm

	private Integer attr;// 促销属性

	private Boolean used;

	public Boolean isUsed() {
		return used;
	}

	public void setUsed(Boolean used) {
		this.used = used;
	}

	public String getProTypeName() {
		return proTypeName;
	}

	public void setProTypeName(String proTypeName) {
		this.proTypeName = proTypeName;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public Integer getAttr() {
		return attr;
	}

	public void setAttr(Integer attr) {
		this.attr = attr;
	}

	@Override
	public String toString() {
		return "PromotionTypeSearchForm [proTypeName=" + proTypeName
				+ ", createDate=" + createDate + ", attr=" + attr + ", used="
				+ used + "]";
	}

}
