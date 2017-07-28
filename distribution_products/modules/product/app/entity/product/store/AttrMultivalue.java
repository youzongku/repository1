package entity.product.store;

/**
 * 属性字段可选值表实体
 */
public class AttrMultivalue extends StoreBase {
     

	private static final long serialVersionUID = -4898962935308856198L;

	/**
     * 主键
     */
    private String id;

    /**
     * 属性id
     */
    private Integer attrId;

    /**
     * 内容
     */
    private String contentText;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getAttrId() {
        return attrId;
    }

    public void setAttrId(Integer attrId) {
        this.attrId = attrId;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }
}