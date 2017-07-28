package entity.timer;

import java.math.BigDecimal;
import java.util.Date;

import util.timer.DateUtils;


/**
 * @author Administrator
 *
 */
public class DisCoupons {
	
    /**
     * 主键
     */
    private Integer id;
    /**
     * 优惠活动id
     */
    private Integer activeId;
    /**
     * 优惠码编号
     */
    private String couponsNo;
    /**
     * 使用状态
     */
    private Integer istatus;
    /**
     * 使用人
     */
    private String user;
    /**
     * 使用时间
     */
    private Date usageTime;
    /**
     * 订单编号
     */
    private String orderNo;
    /**
     * 订单状态
     */
    private Integer orderStatus;
    /**
     * 订单金额
     */
    private BigDecimal orderAmount;
    /**
     * 实际支付金额
     */
    private BigDecimal actuallyPaid;
    /**
     * 面额  由优惠活动表维护
     */
    private BigDecimal couponsCost;
    
    private String usageTimeStr;
    
    /**
     * 用于页面展示状态
     */
    private String state;
    
    private String orderState;
    
    public String getOrderState() {
		return orderState;
	}

	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getUsageTimeStr() {
    	if(this.usageTime != null){
    		return DateUtils.date2string(usageTime, DateUtils.FORMAT_FULL_DATETIME);
    	}
		return usageTimeStr;
	}

	public void setUsageTimeStr(String usageTimeStr) {
		this.usageTimeStr = usageTimeStr ;
	}

	public BigDecimal getCouponsCost() {
		return couponsCost;
	}

	public void setCouponsCost(BigDecimal couponsCost) {
		this.couponsCost = couponsCost;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getActiveId() {
        return activeId;
    }

    public void setActiveId(Integer activeId) {
        this.activeId = activeId;
    }

    public String getCouponsNo() {
        return couponsNo;
    }

    public void setCouponsNo(String couponsNo) {
        this.couponsNo = couponsNo;
    }

    public Integer getIstatus() {
        return istatus;
    }

    public void setIstatus(Integer istatus) {
        this.istatus = istatus;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getUsageTime() {
        return usageTime;
    }

    public void setUsageTime(Date usageTime) {
        this.usageTime = usageTime;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public BigDecimal getActuallyPaid() {
        return actuallyPaid;
    }

    public void setActuallyPaid(BigDecimal actuallyPaid) {
        this.actuallyPaid = actuallyPaid;
    }

	@Override
	public String toString() {
		return "DisCoupons [id=" + id + ", activeId=" + activeId + ", couponsNo=" + couponsNo + ", istatus=" + istatus
				+ ", user=" + user + ", usageTime=" + usageTime + ", orderNo=" + orderNo + ", orderStatus="
				+ orderStatus + ", orderAmount=" + orderAmount + ", actuallyPaid=" + actuallyPaid + ", couponsCost="
				+ couponsCost + ", usageTimeStr=" + usageTimeStr + "]";
	}
    
}