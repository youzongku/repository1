package entity.product.store;

/**
 * SKU实体表实体
 */
public class SkuEntity extends StoreBase {

	private static final long serialVersionUID = 3069817091317620517L;

	/**
     * 主键
     */
    private Integer id;

    /**
     * 数据值表id（uuid）
     */
    private String contentId;

    /**
     * multiselect为true时，取content_id，去其他表取值
     */
    private Boolean multiselect;

    /**
     * 属性类型（文本:text、下拉:select、单选:radio、多选:checkbox）
     * 
     */
    private String attrType;

    /**
     * 属性id（uuid）
     */
    private String attrId;

    /**
     * 属性key
     */
    private String attrKey;

    /**
     * 数据类型
     */
    private String dataType;
    
    /**
     * 属性值
     */
    private String attrValue;
    
    public String getAttrValue() {
		return attrValue;
	}

	public void setAttrValue(String attrValue) {
		this.attrValue = attrValue;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public Boolean getMultiselect() {
        return multiselect;
    }

    public void setMultiselect(Boolean multiselect) {
        this.multiselect = multiselect;
    }

    public String getAttrType() {
        return attrType;
    }

    public void setAttrType(String attrType) {
        this.attrType = attrType;
    }

    public String getAttrId() {
        return attrId;
    }

    public void setAttrId(String attrId) {
        this.attrId = attrId;
    }

    public String getAttrKey() {
        return attrKey;
    }

    public void setAttrKey(String attrKey) {
        this.attrKey = attrKey;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}