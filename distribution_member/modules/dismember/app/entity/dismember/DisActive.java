package entity.dismember;

import java.math.BigDecimal;
import java.util.Date;


public class DisActive {
	
    /**
     * 主键
     */
    private Integer id;
    /**
     * 优惠码名称
     */
    private String couponsName;
    /**
     * 优惠码面额
     */
    private BigDecimal couponsCost;
    /**
     * 发型数量
     */
    private Integer publishQty;
    /**
     * 状态(0 可使用 1 已过期)
     */
    private Integer status;
    /**
     * 添加时间
     */
    private Date createDate;
    /**
     * 创建人
     */
    private String creater;
    /**
     * 门槛金额
     */
    private BigDecimal thresholdPrice;
    /**
     * 有效时间开始
     */
    private Date validDateStart;
    /**
     * 有效时间结束
     */
    private Date validDateEnd;
    /**
     * 优惠码长度
     */
    private Integer couponsLenght;
    
    private String createDateStr;
    
	private String validDateStartStr;
    
    private String validDateEndtStr;
    
    /**
     * 用于页面展示状态
     */
    private String state;
    
    public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
    
    public String getCreateDateStr() {
		return createDateStr;
	}

	public void setCreateDateStr(String createDateStr) {
		this.createDateStr = createDateStr;
	}
    public String getValidDateStartStr() {
		return validDateStartStr;
	}

	public void setValidDateStartStr(String validDateStartStr) {
		this.validDateStartStr = validDateStartStr;
	}

	public String getValidDateEndtStr() {
		return validDateEndtStr;
	}

	public void setValidDateEndtStr(String validDateEndtStr) {
		this.validDateEndtStr = validDateEndtStr;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCouponsName() {
        return couponsName;
    }

    public void setCouponsName(String couponsName) {
        this.couponsName = couponsName;
    }

    public BigDecimal getCouponsCost() {
        return couponsCost;
    }

    public void setCouponsCost(BigDecimal couponsCost) {
        this.couponsCost = couponsCost;
    }

    public Integer getPublishQty() {
        return publishQty;
    }

    public void setPublishQty(Integer publishQty) {
        this.publishQty = publishQty;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public BigDecimal getThresholdPrice() {
        return thresholdPrice;
    }

    public void setThresholdPrice(BigDecimal thresholdPrice) {
        this.thresholdPrice = thresholdPrice;
    }

    public Date getValidDateStart() {
        return validDateStart;
    }

    public void setValidDateStart(Date validDateStart) {

			this.validDateStart = validDateStart;
		
    }

    public Date getValidDateEnd() {
        return validDateEnd;
    }

    public void setValidDateEnd(Date validDateEnd) {
        this.validDateEnd = validDateEnd;
    }

    public Integer getCouponsLenght() {
        return couponsLenght;
    }

    public void setCouponsLenght(Integer couponsLenght) {
        this.couponsLenght = couponsLenght;
    }
}