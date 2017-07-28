package entity.marketing.promotion;

import java.util.Date;

public class ActivityInformationLog {
    private Integer id;
    //用户名
    private String userName;
    //单号
    private String orderNumber;
    //价格
    private Double purchasePrice;
    //活动ID
    private Integer proActId;
    //是否支付成功
    private Boolean isPayment;
    //创建时间
    private Date createTime;
    //优惠实例ID
    private String pvlgInstId;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Integer getProActId() {
        return proActId;
    }

    public void setProActId(Integer proActId) {
        this.proActId = proActId;
    }

    public Boolean getIsPayment() {
        return isPayment;
    }

    public void setIsPayment(Boolean isPayment) {
        this.isPayment = isPayment;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

	public String getPvlgInstId() {
		return pvlgInstId;
	}

	public void setPvlgInstId(String pvlgInstId) {
		this.pvlgInstId = pvlgInstId;
	}
    
}