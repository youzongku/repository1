package entity.product.store;

/**
 * 属性集与属性映射实体
 */
public class AttributeSetMapping extends StoreBase {
     

	private static final long serialVersionUID = -7245389126249889955L;
	/**
     * 主键
     */
    private Integer id;
    /**
     * 属性集id
     */
    private Integer setId;

    /**
     * 属性id
     */
    private Integer attributeId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSetId() {
        return setId;
    }

    public void setSetId(Integer setId) {
        this.setId = setId;
    }

    public Integer getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Integer attributeId) {
        this.attributeId = attributeId;
    }
}