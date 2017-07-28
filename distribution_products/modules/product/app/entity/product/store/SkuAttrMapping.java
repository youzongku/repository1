package entity.product.store;

/**
 * SKU与属性映射表实体
 */
public class SkuAttrMapping extends StoreBase {
     

	private static final long serialVersionUID = 5974044636506126090L;

	/**
     * 主键 uuid
     */
    private String id;

    /**
     * sku编号
     */
    private String sku;

    /**
     * 属性id
     */
    private Integer attrId;

    /**
     * 属性key
     */
    private String attrKey;

    /**
     * 属性类型（文本、下拉、单选、多选）
     */
    private String attrType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getAttrId() {
        return attrId;
    }

    public void setAttrId(Integer attrId) {
        this.attrId = attrId;
    }

    public String getAttrKey() {
        return attrKey;
    }

    public void setAttrKey(String attrKey) {
        this.attrKey = attrKey;
    }

    public String getAttrType() {
        return attrType;
    }

    public void setAttrType(String attrType) {
        this.attrType = attrType;
    }
}