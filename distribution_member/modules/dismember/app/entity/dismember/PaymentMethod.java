package entity.dismember;

import java.util.Date;

public class PaymentMethod {
    private Integer id;

    /**
     * 支付描述
     */
    private String name;

    /**
     *  支付类型code:
     *  余额支付：balance、
     *  支付宝支付：zhifubao、
     *  微信支付：weixin、
     *  易极付：easy、
     *  易极付-微信扫码：easy-wx、
     *  现金支付：cash、
     *  线下转账：cash-online
     */
    private String key;

    private Date createTime;

    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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