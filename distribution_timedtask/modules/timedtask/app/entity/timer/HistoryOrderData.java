package entity.timer;

import java.io.Serializable;
import java.util.List;

/**
 * 2017-1-27号上线前还原逻辑实体（临时）
 * 库存还原实体
 * @author zbc
 * 2017年1月21日 下午4:12:34
 */
@SuppressWarnings("serial")
public class HistoryOrderData implements Serializable {
	private String account;
	private String accountName;
	private List<HistoryDateOrderDetail> orderList;
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public List<HistoryDateOrderDetail> getOrderList() {
		return orderList;
	}
	public void setOrderList(List<HistoryDateOrderDetail> orderList) {
		this.orderList = orderList;
	}
	@Override
	public String toString() {
		return "HistoryOrderData [account=" + account + ", accountName=" + accountName + ", orderList=" + orderList
				+ "]";
	}
	
	
}
