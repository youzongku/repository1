package entity.product.store;

/**
 * 字段属性类型实体类
 */
public class AttributeType extends StoreBase {
    

	private static final long serialVersionUID = -3392566227853030629L;

	/**
     * 主键
     */
    private Integer id;

    /**
     * 属性类型（String、Double、Date、Integer）
     */
    private String typeName;

    /**
     * 属性类型描述
     */
    private String typeDesc;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeDesc() {
        return typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }
}