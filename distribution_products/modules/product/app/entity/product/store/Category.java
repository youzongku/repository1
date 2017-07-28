package entity.product.store;

import java.util.Date;

/**
 * 商品分类表实体
 */
public class Category extends StoreBase {
    

	private static final long serialVersionUID = 3287757448727521605L;

	/**
     * 主键 
     */
    private Integer id;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 分类描述
     */
    private String categoryDesc;

    /**
     * 父类目id
     */
    private Integer parentId;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 父类目id
     */
    private Integer categoryId;

    /**
     * 分类级别
     */
    private Integer level;

    /**
     * 位置排序
     */
    private Integer position;

    /**
     * 前台是否展示
     */
    private Boolean isShow;
    /**
     * 是否导航显示
     */
    private Boolean isNavigation;

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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDesc() {
        return categoryDesc;
    }

    public void setCategoryDesc(String categoryDesc) {
        this.categoryDesc = categoryDesc;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Boolean getIsShow() {
        return isShow;
    }

    public void setIsShow(Boolean isShow) {
        this.isShow = isShow;
    }

    public Boolean getIsNavigation() {
        return isNavigation;
    }

    public void setIsNavigation(Boolean isNavigation) {
        this.isNavigation = isNavigation;
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