package entity.dismember;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 账期金额变化表实体
 * @author zbc
 * 2017年2月17日 下午12:16:51
 */
public class ApChange implements Serializable{

	private static final long serialVersionUID = -7132801523303823732L;
	
	public static final int PAY = 0; //支付
	
	public static final int REFUND = 1; //

	/**
     * 主键
     */
    private Integer id;

    /**
     * 变化金额(+/-)
     */
    private BigDecimal changeAmount;

    /**
     * 账期总额
     */
    private BigDecimal totalAmount;

    /**
     * 描述:扣款 ，还款
     */
    private String describe;

    /**
     * 变化时间
     */
    private Date changeDate;

    /**
     * 分销商账号
     */
    private String account;

    /**
     * 账期id
     */
    private Integer apId;
    
    /**
     * 操作类型（0、扣款 ,1、还款）
     */
    private Integer type;
    
    public ApChange(){
    	
    }
    
    /**
	 * @param changeAmount 变化金额
	 * @param totalAmount  账期总额度
	 * @param changeDate   变化时间
	 * @param account      分销商账号
	 * @param apId         账期id
	 */
	public ApChange(BigDecimal changeAmount, BigDecimal totalAmount,Date changeDate, String account,
			Integer apId,Integer type) {
		super();
		this.changeAmount = changeAmount;
		this.totalAmount = totalAmount;
		this.describe = (type == PAY?"支付":"充值");
		this.changeDate = changeDate;
		this.account = account;
		this.apId = apId;
		this.type = type;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(BigDecimal changeAmount) {
        this.changeAmount = changeAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Integer getApId() {
        return apId;
    }

    public void setApId(Integer apId) {
        this.apId = apId;
    }
}