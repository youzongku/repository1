package dto.sales;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;

import util.sales.DateUtils;

/**
 * 发货单录入选择的商品
 * 
 * @author huangjc
 * @since 2017年3月6日
 */
public class SelectedProduct {
	private String sku;
	private int batchNumber;
	private String title;
	private String interBarCode;
	private String warehouseName;
	private Integer warehouseId;
	private int stock = 0;// 云仓库存
	private int subStock = 0;// 云仓到期日期对应的云仓库存
	private int microStock = 0;// 微仓库存
	private int subMicroStock = 0;// 微仓到期日期对应的微仓库存
	private int qty;
	private Double price;
	private Double marketPrice;
	private String imgUrl;
	private String expirationDate;// 到期日期
	private Integer expirationDays;// 保质期
	private Boolean isgift;// 是否是赠品

	public Integer getExpirationExponent() {
		if (StringUtils.isBlank(expirationDate) || expirationDays == null || expirationDays <= 0) {
			return null;
		}

		try {
			int daysInterval = DateUtils.daysInterval(LocalDate.now(),
					DateUtils.toLocalDate(DateUtils.string2date(expirationDate, DateUtils.FORMAT_DATE_PAGE)));
			if (daysInterval <= 0) {
				return 0;
			}
			int expirationExponent = new BigDecimal(daysInterval)
					.divide(new BigDecimal(expirationDays), 2, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(100))
					.intValue();
			return expirationExponent;
		} catch (ParseException e) {
			return null;
		}
	}

	public Integer getExpirationDays() {
		return expirationDays;
	}

	public void setExpirationDays(Integer expirationDays) {
		this.expirationDays = expirationDays;
	}

	public Boolean getIsgift() {
		return isgift;
	}

	public void setIsgift(Boolean isgift) {
		this.isgift = isgift;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public int getSubStock() {
		return subStock;
	}

	public void setSubStock(int subStock) {
		this.subStock = subStock;
	}

	public int getSubMicroStock() {
		return subMicroStock;
	}

	public void setSubMicroStock(int subMicroStock) {
		this.subMicroStock = subMicroStock;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public int getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(int batchNumber) {
		this.batchNumber = batchNumber;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getInterBarCode() {
		return interBarCode;
	}

	public void setInterBarCode(String interBarCode) {
		this.interBarCode = interBarCode;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public int getMicroStock() {
		return microStock;
	}

	public void setMicroStock(int microStock) {
		this.microStock = microStock;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(Double marketPrice) {
		this.marketPrice = marketPrice;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	@Override
	public String toString() {
		return "SelectedProduct [sku=" + sku + ", batchNumber=" + batchNumber + ", title=" + title + ", interBarCode="
				+ interBarCode + ", warehouseName=" + warehouseName + ", warehouseId=" + warehouseId + ", stock="
				+ stock + ", subStock=" + subStock + ", microStock=" + microStock + ", subMicroStock=" + subMicroStock
				+ ", qty=" + qty + ", price=" + price + ", marketPrice=" + marketPrice + ", imgUrl=" + imgUrl
				+ ", expirationDate=" + expirationDate + ", expirationDays=" + expirationDays + ", isgift=" + isgift
				+ "]";
	}

}
