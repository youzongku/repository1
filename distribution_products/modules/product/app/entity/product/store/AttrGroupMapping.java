package entity.product.store;

/**
 * 属性与分组映射实体
 */
public class AttrGroupMapping extends StoreBase {
    

	private static final long serialVersionUID = 6035552822147751444L;

	/**
     * 主键
     */
    private Integer id;

    /**
     * 分组id
     */
    private Integer groupId;

    /**
     * 属性id
     */
    private Integer attrId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getAttrId() {
        return attrId;
    }

    public void setAttrId(Integer attrId) {
        this.attrId = attrId;
    }
}