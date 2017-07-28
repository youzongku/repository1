package entity.dismember;

import java.util.Date;

/**
 * 分销商类型实体类
 * @author zbc
 * 2016年10月12日 下午3:38:23
 */
/**
 * @author zbc
 * 2016年10月12日 下午3:39:18
 */
public class CustomerType {
    /**
     * 分销商类型id
     */
    private Integer id;

    /**
     * 分销商类型名称
     */
    private String customerName;

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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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