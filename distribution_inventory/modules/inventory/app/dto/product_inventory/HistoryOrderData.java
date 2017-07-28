package dto.product_inventory;

import java.util.List;

public class HistoryOrderData {
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
