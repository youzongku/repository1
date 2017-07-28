package dto.product_inventory;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProductMicroDeductDetail implements Serializable{
    
	private static final long serialVersionUID = -201782409378784380L;

	private String account;

    private String sku;
    
    private String imgUrl;
    
    private String title;

    private Integer qty;

    private String purchasePrice;//采购价
    
    private String capfee;//均摊价
    
    private String arriveWarePrice;//到仓价

    private Integer warehouseId;

    private String warehouseName;
    
    private String saleOrderNo;//销售单号
    
    private String purchaseNo;//采购单号

    private String expirationDate;//过期时间

    private short isGift;
    /**
     * 合同号
     */
    private String contractNo;
    
    /**
     * 清货价
     */
    private Double clearancePrice;
    
    
	@Override
	public String toString() {
		return "ProductMicroDeductDetail [account=" + account + ", sku=" + sku + ", imgUrl=" + imgUrl + ", title="
				+ title + ", qty=" + qty + ", purchasePrice=" + purchasePrice + ", capfee=" + capfee
				+ ", arriveWarePrice=" + arriveWarePrice + ", warehouseId=" + warehouseId + ", warehouseName="
				+ warehouseName + ", saleOrderNo=" + saleOrderNo + ", purchaseNo=" + purchaseNo + ", expirationDate="
				+ expirationDate + ", isGift=" + isGift + "]";
	}
	
	public Double getClearancePrice() {
		return clearancePrice;
	}

	public void setClearancePrice(Double clearancePrice) {
		this.clearancePrice = clearancePrice;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}

	public String getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(String purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public String getCapfee() {
		return capfee;
	}

	public void setCapfee(String capfee) {
		this.capfee = capfee;
	}

	public String getArriveWarePrice() {
		return arriveWarePrice;
	}

	public void setArriveWarePrice(String arriveWarePrice) {
		this.arriveWarePrice = arriveWarePrice;
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

	public String getSaleOrderNo() {
		return saleOrderNo;
	}

	public void setSaleOrderNo(String saleOrderNo) {
		this.saleOrderNo = saleOrderNo;
	}
	
	public String getPurchaseNo() {
		return purchaseNo;
	}

	public void setPurchaseNo(String purchaseNo) {
		this.purchaseNo = purchaseNo;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String startTime = sdf.format(expirationDate);
		this.expirationDate = startTime;
	}

	public short getIsGift() {
		return isGift;
	}

	public void setIsGift(short isGift) {
		this.isGift = isGift;
	}
}
