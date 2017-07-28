package entity.timer;

import java.io.Serializable;

/**
 * 账期：账单订单关系实体
 * @author zbc
 * 2017年2月17日 下午12:14:48
 */
public class ApBillOrderMapping implements Serializable{

	private static final long serialVersionUID = 9172439835184234689L;

	/**
     * 主键
     */
    private Integer id;

    /**
     * 订单id
     */
    private Integer orderId;

    /**
     * 账单id
     */
    private Integer billId;

    /**
	 * @param orderId
	 * @param billId
	 */
	public ApBillOrderMapping(Integer orderId, Integer billId) {
		super();
		this.orderId = orderId;
		this.billId = billId;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getBillId() {
        return billId;
    }

    public void setBillId(Integer billId) {
        this.billId = billId;
    }
}