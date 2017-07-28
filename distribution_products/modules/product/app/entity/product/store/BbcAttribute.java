package entity.product.store;

import java.util.Date;

/**
 * BBC属性表实体
 */
public class BbcAttribute extends StoreBase {
     
	public BbcAttribute(){
		
	}
	
	private static final long serialVersionUID = 5566327617231641887L;

	/**
     * 主键
     */
    private Integer id;

    /**
     * 属性名称
     */
    private String attrName;

    /**
     * 属性key，实体类属性名称
     */
    private String attrKey;

    /**
     * 属性描述
     */
    private String attrDesc;

    /**
     * 属性状态
     */
    private Integer status;

    /**
     * 属性类型（文本、下拉、单选、多选）
     */
    private String attrType;

    /**
     * 字段类型
     */
    private Integer typeId;

    /**
     * 是否可为空
     */
    private Boolean isNull;

    /**
     * 是否展示
     */
    private Boolean isShow;

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

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public String getAttrKey() {
        return attrKey;
    }

    public void setAttrKey(String attrKey) {
        this.attrKey = attrKey;
    }

    public String getAttrDesc() {
        return attrDesc;
    }

    public void setAttrDesc(String attrDesc) {
        this.attrDesc = attrDesc;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAttrType() {
        return attrType;
    }

    public void setAttrType(String attrType) {
        this.attrType = attrType;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Boolean getIsNull() {
        return isNull;
    }

    public void setIsNull(Boolean isNull) {
        this.isNull = isNull;
    }

    public Boolean getIsShow() {
        return isShow;
    }

    public void setIsShow(Boolean isShow) {
        this.isShow = isShow;
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