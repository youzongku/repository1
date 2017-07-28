package entity.product.store;

import java.util.Date;

/**
 * 分类与属性集映射实体，
 * 多对多
 */
public class CategorySetMapping extends StoreBase {
    

	private static final long serialVersionUID = -3155688028493152368L;

	/**
     * 实体
     */
    private Integer id;

    /**
     * 属性集id
     */
    private Integer setId;

    /**
     * 商品分类表主键
     */
    private Integer cid;

    /**
     * 商品分类id（erp一致）
     */
    private Integer categoryId;

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

    public Integer getSetId() {
        return setId;
    }

    public void setSetId(Integer setId) {
        this.setId = setId;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
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