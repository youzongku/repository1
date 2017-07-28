package entity.timer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import services.base.utils.DateFormatUtils;

/**
 * 使用账期订单 实体
 * @author zbc
 * 2017年2月17日 下午2:10:17
 */
public class OrderByAp implements Serializable {

	private static final long serialVersionUID = 3892425082771584274L;

	/**
	 * 已选标识
	 */
	public static final int HAVE_BEEN_CHOICE = 1;
	
	/**
	 * 未选标识
	 */
	public static final int HAVE_NOT_CHOICE = 0; 
	/**
     *主键 
     */
    private Integer id;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;
    /**
     * 使用账期金额
     */
    private BigDecimal payAmount;

    /**
     * 支付时间
     */
    private Date payDate;

    /**
     * 是否已选 0/1
     */
    private Integer isChoice;

    /**
     * 账期id
     */
    private Integer apId;

    /**
     * 还款时间
     */
    private Date refundDate;

    /**
     * 分销商账号
     */
    private String account;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 订单号
     */
    private String orderNo;
    
    public OrderByAp(){
    	
    }

	/**
	 * @param orderAmount 订单金额
	 * @param payAmount   支付金额
	 * @param payDate     支付时间
	 * @param apId        账期id
	 * @param account     分销商账号
	 * @param orderNo     订单号
	 */
	public OrderByAp(BigDecimal orderAmount, BigDecimal payAmount, Date payDate,Integer apId,
			String account, String orderNo) {
		super();
		this.orderAmount = orderAmount;
		this.payAmount = payAmount;
		this.payDate = payDate;
		this.apId = apId;
		this.account = account;
		this.orderNo = orderNo;
	}

	public BigDecimal getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(BigDecimal orderAmount) {
		this.orderAmount = orderAmount;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public Integer getIsChoice() {
        return isChoice;
    }

    public void setIsChoice(Integer isChoice) {
        this.isChoice = isChoice;
    }

    public Integer getApId() {
		return apId;
	}

	public void setApId(Integer apId) {
		this.apId = apId;
	}

	public Date getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(Date refundDate) {
        this.refundDate = refundDate;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
    
    public String getPayDateStr(){
    	return DateFormatUtils.getStrFromYYYYMMDDHHMMSS(payDate);
    }
    
}