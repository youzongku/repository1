package entity.marketing.promotion;


/**
 * 促销条件
 * 
 * @author huangjc
 * @date 2016年7月22日
 */
public class PromotionCondition {
	private Integer id;
	private Integer cType;
	/** 名称 */
	private String name;
	/** 属性 */
	private Short attr;
	/** 备注 */
	private String remark;
	/** 是否删除，默认false */
	private boolean isDelete = Boolean.FALSE;

	private boolean hasExtCondt = Boolean.FALSE;

	public boolean isHasExtCondt() {
		return hasExtCondt;
	}

	public void setHasExtCondt(boolean hasExtCondt) {
		this.hasExtCondt = hasExtCondt;
	}

	public static final int CTYPE_PRODUCT_CATEGORY = 1;
	public static final int CTYPE_SPECIFY_PRODUCT = 2;
	public static final int CTYPE_SPECIFY_WAREHOUSE = 3;
	public static final int CTYPE_PRODUCT_TYPE = 4;
	public static final int CTYPE_SUBTOTAL = 5;
	public static final int CTYPE_PRODUCT_TOTAL_COUNT = 6;
	public static final int CTYPE_PRODUCT_TOTAL_WEIGHT = 7;
	public static final int CTYPE_SHIPPING_REGION = 8;
	public static final int CTYPE_COMMON_DISTRIBUTOR = 9;
	public static final int CTYPE_CO_DISTRIBUTOR = 10;
	public static final int CTYPE_INTER_DISTRIBUTOR = 11;
	public static final int CTYPE_NEW_USER = 12;
	public static final int CTYPE_FIRST_SHOPPING = 13;
	public static final int CTYPE_NO_SHOPPING_IN_THREE_MONTH = 14;

	public Integer getcType() {
		return cType;
	}

	public void setcType(Integer cType) {
		this.cType = cType;
	}

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

	public Short getAttr() {
		return attr;
	}

	public void setAttr(Short attr) {
		this.attr = attr;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "PromotionCondition [id=" + id + ", cType=" + cType + ", name="
				+ name + ", attr=" + attr + ", remark=" + remark
				+ ", isDelete=" + isDelete + ", hasExtCondt=" + hasExtCondt
				+ "]";
	}

}