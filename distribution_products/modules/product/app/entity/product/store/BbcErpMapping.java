package entity.product.store;

/**
 * BBC属性与ERP属性映射表实体
 */
public class BbcErpMapping extends StoreBase {
    

	private static final long serialVersionUID = 4350098122516089820L;

	/**
     * 主键
     */
    private Integer id;

    /**
     * bbc属性表id
     */
    private Integer bbcAttrId;

    /**
     * erp属性表id
     */
    private Integer erpAttrId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBbcAttrId() {
        return bbcAttrId;
    }

    public void setBbcAttrId(Integer bbcAttrId) {
        this.bbcAttrId = bbcAttrId;
    }

    public Integer getErpAttrId() {
        return erpAttrId;
    }

    public void setErpAttrId(Integer erpAttrId) {
        this.erpAttrId = erpAttrId;
    }
}