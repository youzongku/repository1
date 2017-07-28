package dto.product_inventory;

/**
 * 仓库库存查询结果
 * 
 * @author ye_ziran
 * @since 2017年1月6日 下午5:41:35
 */
public class ProductInventoryEnquiryResult {
	
	private Integer id;
	
	private String csku;

    private Integer warehouseId;
    
    private String warehouseName;

    private Integer stockNum;
    
    private String disAccount;
    
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCsku() {
		return csku;
	}
	public void setCsku(String csku) {
		this.csku = csku;
	}
	public Integer getWarehouseId() {
		return warehouseId;
	}
	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}
	public Integer getStockNum() {
		return stockNum;
	}
	public void setStockNum(Integer stockNum) {
		this.stockNum = stockNum;
	}
	public String getDisAccount() {
		return disAccount;
	}
	public void setDisAccount(String disAccount) {
		this.disAccount = disAccount;
	}
	public String getWarehouseName() {
		return warehouseName;
	}
	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	@Override
	public String toString() {
		return "ProductInventoryEnquiryResult [id=" + id + ", csku=" + csku + ", warehouseId=" + warehouseId
				+ ", warehouseName=" + warehouseName + ", stockNum=" + stockNum + ", disAccount=" + disAccount + "]";
	}
}
