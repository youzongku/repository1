package dto.dismember;

import java.util.List;

import entity.dismember.AccountPeriodSlave;
import entity.dismember.ApBill;
import entity.dismember.OrderByAp;

/**
 * @author zbc
 * 2017年3月5日 上午10:39:09
 */
public class ApBillDto extends ApBill {
	
	private static final long serialVersionUID = 897451057228400L;

	private AccountPeriodSlave slave;
	
	private List<OrderByAp> orders;

	public ApBillDto(){
		
	}
	/**
	 * @param slave
	 * @param orders
	 */
	public ApBillDto(AccountPeriodSlave slave, List<OrderByAp> orders,ApBill bill) {
		super();
		this.slave = slave;
		this.orders = orders;
		setAccount(bill.getAccount());
		setApId(bill.getApId());
		setArearAmount(bill.getArearAmount());
		setId(bill.getId());
		setRechargeLeft(bill.getRechargeLeft());
		setTotalAmount(bill.getTotalAmount());
		setVerificationDate(bill.getVerificationDate());
		setVerificationUser(bill.getVerificationUser());
		setCreateDate(bill.getCreateDate());
		setCreateUser(bill.getCreateUser());
		setIsChargeOff(bill.getIsChargeOff());
	}

	public AccountPeriodSlave getSlave() {
		return slave;
	}

	public void setSlave(AccountPeriodSlave slave) {
		this.slave = slave;
	}

	public List<OrderByAp> getOrders() {
		return orders;
	}

	public void setOrders(List<OrderByAp> orders) {
		this.orders = orders;
	}
	
}
