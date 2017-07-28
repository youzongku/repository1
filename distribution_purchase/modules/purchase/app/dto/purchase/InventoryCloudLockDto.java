package dto.purchase;

import java.io.Serializable;
import java.util.List;


/**
 * 云仓锁库实体类
 * @author zbc
 * 2017年4月21日 下午2:58:55
 */
public class InventoryCloudLockDto implements Serializable {

	private static final long serialVersionUID = 7760762434798025574L;

	/**
	 * 单号
	 */
	private String orderNo;
	
	/**
	 * 分销商账号
	 */
	private String account;

	/**
	 * 昵称
	 */
	private String accountName;

	/**
	 * 发货单号（缺货采购单）
	 */
	private String saleOrderNo;
	
	/**
	 * 采购单详情
	 */
	private List<CloudLockPro> pros;
	
	/**
	 * 赠品详情
	 */
	private List<CloudLockPro> change;

	
	/**
	 * @param orderNo
	 * @param account
	 * @param accountName
	 * @param saleOrderNo
	 * @param pros
	 * @param change
	 */
	public InventoryCloudLockDto(String orderNo, String account, String accountName,
			List<CloudLockPro> pros, List<CloudLockPro> change) {
		super();
		this.orderNo = orderNo;
		this.account = account;
		this.accountName = accountName;
		this.pros = pros;
		this.change = change;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

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

	public String getSaleOrderNo() {
		return saleOrderNo;
	}

	public void setSaleOrderNo(String saleOrderNo) {
		this.saleOrderNo = saleOrderNo;
	}

	public List<CloudLockPro> getPros() {
		return pros;
	}

	public void setPros(List<CloudLockPro> pros) {
		this.pros = pros;
	}

	public List<CloudLockPro> getChange() {
		return change;
	}

	public void setChange(List<CloudLockPro> change) {
		this.change = change;
	}

}



