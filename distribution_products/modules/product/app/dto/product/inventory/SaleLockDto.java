package dto.product.inventory;

import java.io.Serializable;
import java.util.List;

/**
 * 销售发货微仓出库实体
 * @author zbc
 * 2017年4月24日 上午9:03:15
 */
public class SaleLockDto implements Serializable {

	private static final long serialVersionUID = -1927068034466188022L;
	
	/**
	 * 发货单单号
	 */
	private  String orderNo;
	
	/**
	 * 仓库ID
	 */
	private Integer warehouseId;
	
	/**
	 * 仓库名称
	 */
	private String warehouseName;
	
	/**
	 * 分销商账号
	 */
	private String account;
	
	/**
	 * 是否只锁云仓
	 */
	private Boolean  lockCloud;
	
	/**
	 * 指定发 某个采购单 微仓入库商品
	 */
	private String purchaseNo;
	
	/**
	 * 发货单详情
	 */
	private List<SaleLockDetailDto> pros;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Boolean getLockCloud() {
		return lockCloud;
	}

	public void setLockCloud(Boolean lockCloud) {
		this.lockCloud = lockCloud;
	}

	public String getPurchaseNo() {
		return purchaseNo;
	}

	public void setPurchaseNo(String purchaseNo) {
		this.purchaseNo = purchaseNo;
	}

	public List<SaleLockDetailDto> getPros() {
		return pros;
	}

	public void setPros(List<SaleLockDetailDto> pros) {
		this.pros = pros;
	}

}

