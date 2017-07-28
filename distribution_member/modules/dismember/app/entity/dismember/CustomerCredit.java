package entity.dismember;

import java.util.Date;

/**
 * 分销商类型 模式 额度配置实体类
 * @author zbc
 * 2016年10月12日 下午3:34:39
 */
public class CustomerCredit {
	
    private Integer id;

    /**
     * 分销商模式
     */
    private Integer customerMode;

    /**
     * 分销商类型
     */
    private Integer customerType;

    /**
     * 是否有永久额度
     */
    private Boolean hasLongCredit;

    /**
     * 是否有临时额度
     */
    private Boolean hasShortCredit;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 更新时间
     */
    private Date updateDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCustomerMode() {
        return customerMode;
    }

    public void setCustomerMode(Integer customerMode) {
        this.customerMode = customerMode;
    }

    public Integer getCustomerType() {
        return customerType;
    }

    public void setCustomerType(Integer customerType) {
        this.customerType = customerType;
    }

    public Boolean getHasLongCredit() {
        return hasLongCredit;
    }

    public void setHasLongCredit(Boolean hasLongCredit) {
        this.hasLongCredit = hasLongCredit;
    }

    public Boolean getHasShortCredit() {
        return hasShortCredit;
    }

    public void setHasShortCredit(Boolean hasShortCredit) {
        this.hasShortCredit = hasShortCredit;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}