package entity.product.store;

/**
 * 属性集与属性关联属性可选值表实体（用于分类属性可选值属于哪些属性集）
 */
public class SetAttrMultivalueMapping extends StoreBase {
    

	private static final long serialVersionUID = 8398918627370084957L;

	/**
     * 主键
     */
    private Integer id;

    /**
     * 属性集与属性映射表id
     */
    private Integer setAttrId;

    /**
     * 可选项值id
     */
    private String valueId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSetAttrId() {
        return setAttrId;
    }

    public void setSetAttrId(Integer setAttrId) {
        this.setAttrId = setAttrId;
    }

    public String getValueId() {
        return valueId;
    }

    public void setValueId(String valueId) {
        this.valueId = valueId;
    }
}