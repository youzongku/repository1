package dto.product_inventory;

import java.util.Date;

import entity.product_inventory.ProductMicroInventoryInRecord;

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

    private Float purchasePrice;//采购价

    private Float capfee;//均摊价

    private Integer warehouseId;

    private String warehouseName;

    private Date expirationDate;//过期时间

    private Float arriveWarePrice;//到仓价

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
    /**
     * add by zbc 
     * @param microInRecord
     * @param qty
     */
    public ShipingDto(ProductMicroInventoryInRecord microInRecord,Integer qty){
    	this.account = microInRecord.getAccount();
    	this.sku = microInRecord.getSku();
    	this.productTitle = microInRecord.getProductTitle();
    	this.imgUrl = microInRecord.getImgUrl();
    	this.qty = qty;
    	this.purchasePrice = microInRecord.getPurchasePrice();
    	this.capfee = microInRecord.getCapfee();
    	this.warehouseId = microInRecord.getWarehouseId();
    	this.warehouseName = microInRecord.getWarehouseName();
    	this.expirationDate = microInRecord.getExpirationDate();
    	this.purchaseNo = microInRecord.getOrderNo();
    	this.isGift = microInRecord.getIsGift();
    	this.arriveWarePrice = microInRecord.getArriveWarePrice();
    	this.contractNo = microInRecord.getContractNo();
    	this.clearancePrice = microInRecord.getClearancePrice();
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

    public Float getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Float purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Float getCapfee() {
        return capfee;
    }

    public void setCapfee(Float capfee) {
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

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
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

    public Float getArriveWarePrice() {
        return arriveWarePrice;
    }

    public void setArriveWarePrice(Float arriveWarePrice) {
        this.arriveWarePrice = arriveWarePrice;
    }
}
