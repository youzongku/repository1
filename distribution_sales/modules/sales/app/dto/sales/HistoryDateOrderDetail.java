package dto.sales;

import java.io.Serializable;

import entity.sales.SaleDetail;

/**
 *  2017-1-27号上线前还原逻辑实体（临时）
 * @author zbc
 * 2017年1月21日 下午4:13:55
 */
public class HistoryDateOrderDetail implements Serializable {
	private static final long serialVersionUID = 7015193788985836078L;

	private String sku;
    
    private String imgUrl;
    
    private String productTitle;

    private Integer qty;

    private Double purchasePrice;
    
    private Double capfee;
    
    private Double arriveWarePrice;

    private Integer warehouseId;

    private String warehouseName;

    private Integer isGift;
    
    private String orderNo;

	public HistoryDateOrderDetail(SaleDetail sd) {
		this.sku = sd.getSku();
		this.imgUrl = sd.getProductImg();
		this.productTitle = sd.getProductName();
		this.qty = sd.getQty();
		this.purchasePrice = sd.getPurchasePrice();
		this.capfee = sd.getCapFee();
		this.arriveWarePrice = sd.getArriveWarePrice();
		this.warehouseId = sd.getWarehouseId();
		this.warehouseName = sd.getWarehouseName();
		this.isGift = sd.getIsgift() != null &&sd.getIsgift()?1:0;
		this.orderNo = sd.getSalesOrderNo();
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

	public String getProductTitle() {
		return productTitle;
	}

	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
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

	public Double getArriveWarePrice() {
		return arriveWarePrice;
	}

	public void setArriveWarePrice(Double arriveWarePrice) {
		this.arriveWarePrice = arriveWarePrice;
	}


	public Integer getIsGift() {
		return isGift;
	}

	public void setIsGift(Integer isGift) {
		this.isGift = isGift;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	@Override
	public String toString() {
		return "HistoryDateOrderDetail [sku=" + sku + ", imgUrl=" + imgUrl + ", productTitle=" + productTitle + ", qty="
				+ qty + ", purchasePrice=" + purchasePrice + ", capfee=" + capfee + ", arriveWarePrice="
				+ arriveWarePrice + ", warehouseId=" + warehouseId + ", warehouseName=" + warehouseName + ", isGift="
				+ isGift + ", orderNo=" + orderNo + "]";
	}
    
    
}
