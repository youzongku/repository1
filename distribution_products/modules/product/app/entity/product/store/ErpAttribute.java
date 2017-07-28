package entity.product.store;

import java.util.Date;

/**
 * ERP属性表实体
 */
public class ErpAttribute extends StoreBase {

	private static final long serialVersionUID = -6348942327565549065L;

	/**
     * 主键
     */
    private Integer id;

    /**
     * ERP传入Json Key
     */
    private String erpAttrKey;

    /**
     * erp属性名称
     */
    private String erpAttrName;

    /**
     * ERP属性描述
     */
    private String erpAttrDesc;

    /**
     * ERP属性展现类型（下拉、文本、、、、）
     */
    private String erpAttrType;

    /**
     * Json数据key对应的value数据类型
     */
    private Integer typeId;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getErpAttrKey() {
        return erpAttrKey;
    }

    public void setErpAttrKey(String erpAttrKey) {
        this.erpAttrKey = erpAttrKey;
    }

    public String getErpAttrName() {
        return erpAttrName;
    }

    public void setErpAttrName(String erpAttrName) {
        this.erpAttrName = erpAttrName;
    }

    public String getErpAttrDesc() {
        return erpAttrDesc;
    }

    public void setErpAttrDesc(String erpAttrDesc) {
        this.erpAttrDesc = erpAttrDesc;
    }

    public String getErpAttrType() {
        return erpAttrType;
    }

    public void setErpAttrType(String erpAttrType) {
        this.erpAttrType = erpAttrType;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}