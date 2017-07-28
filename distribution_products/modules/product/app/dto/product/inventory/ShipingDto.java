package dto.product.inventory;

import java.util.Date;

/**
 * @author longhuashen
 * @since 2016/12/20
 *
 * 锁库或成功发货逻辑
 */
public class ShipingDto {

    private String account;

    private String sku;

    private String productTitle;

    private String imgUrl;

    private Integer qty;

    private Double purchasePrice;//采购价

    private Double capfee;//均摊价

    private Integer warehouseId;

    private String warehouseName;

    private String expirationDate;//过期时间

    private Double arriveWarePrice;//到仓价

    /**
     * 采购单号
     */
    private String purchaseNo;

    /**
     * 是否赠品
     */
    private Short isGift;
    
    /**
     * add by zbc 合同号 
     */
    private String contractNo;
    
    /**
     * 清货价
     */
    private Double clearancePrice;
    
    
    
    public ShipingDto(){
    	
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

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Double getCapfee() {
        return capfee;
    }

    public void setCapfee(Double capfee) {
        this.capfee = capfee;
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

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getPurchaseNo() {
        return purchaseNo;
    }

    public void setPurchaseNo(String purchaseNo) {
        this.purchaseNo = purchaseNo;
    }

    public Short getIsGift() {
        return isGift;
    }

    public void setIsGift(Short isGift) {
        this.isGift = isGift;
    }

    public Double getArriveWarePrice() {
        return arriveWarePrice;
    }

    public void setArriveWarePrice(Double arriveWarePrice) {
        this.arriveWarePrice = arriveWarePrice;
    }
}
