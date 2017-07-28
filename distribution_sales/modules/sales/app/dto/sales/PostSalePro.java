package dto.sales;

import java.io.Serializable;

import com.wordnik.swagger.annotations.ApiModel;

import entity.sales.SaleDetail;

/**
 * 商品信息内部类
 * @author zbc
 * 2017年4月13日 下午7:25:14
 */
@ApiModel
public class PostSalePro implements Serializable{
	private static final long serialVersionUID = 4706854775669571512L;
	private String sku;
	private Integer warehouseId;
	private String expirationDate;
	private Integer num;
	private Double finalSellingPrice;
	
	public PostSalePro(String sku, Integer warehouseId, String expirationDate, Integer num, Double finalSellingPrice) {
		super();
		this.sku = sku;
		this.warehouseId = warehouseId;
		this.expirationDate = expirationDate;
		this.num = num;
		this.finalSellingPrice = finalSellingPrice;
	}
	
	public PostSalePro(SaleDetail detail) {
		super();
		this.sku = detail.getSku();
		this.warehouseId = detail.getWarehouseId();
		this.expirationDate = detail.getExpirationDateStr();
		this.num = detail.getQty();
		this.finalSellingPrice = detail.getFinalSellingPrice();
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public Integer getWarehouseId() {
		return warehouseId;
	}
	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}
	public String getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public Double getFinalSellingPrice() {
		return finalSellingPrice;
	}
	public void setFinalSellingPrice(Double finalSellingPrice) {
		this.finalSellingPrice = finalSellingPrice;
	}
}