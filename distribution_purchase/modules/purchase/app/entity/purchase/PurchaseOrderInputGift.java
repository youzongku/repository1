package entity.purchase;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import dto.purchase.ExpirationDateDto;
import utils.purchase.DateUtils;

/**
 * 录入采购单 赠品实体
 * 
 * @author zbc 2016年8月30日 下午4:18:08
 */
public class PurchaseOrderInputGift {
	public static final int UNIT_TYPE_SINGLE = 1;
	public static final int UNIT_TYPE_BOX = 2;

	private Integer id;
	private Integer inputId;// 录入表id
	private Integer serialNumber;// 序列号
	private String sku;// 商品编码
	private String title;// 商品名称
	private String interBarCode;// 国际条码
	private Integer carton;// 箱规
	private Integer unitType;// 单位类型（1 为单个商品，2 为整箱商品）
	private Integer qty;// 数量
	// 增加属性,导入进来的产品没有指明仓库，所以一下几个字段通过查询另外的接口添加
	private List<Map<String, Object>> warehouseNameId;
	private Integer warehouseId;
	// private String proIds;// 正价商品id
	private String imageUrl;
	private String warehouseName;
	private Double price; // 分销价
	private Integer stock;// 云仓库存
	private Integer batchNumber;// 起批量
	private String expirationDate;// 到期日期
	private Boolean needExpirationDate;// 到期日期
	Map<String, Object> productDetails;// 商品详情
	private Integer expirationDays;// 保质期
	/** 赠品所有到期日期以及对应的云仓库存 */
	private Set<ExpirationDateDto> expirationDateDtoSet = new HashSet<ExpirationDateDto>();

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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getInputId() {
		return inputId;
	}

	public void setInputId(Integer inputId) {
		this.inputId = inputId;
	}

	public Integer getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(Integer serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
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

	public Integer getCarton() {
		return carton;
	}

	public void setCarton(Integer carton) {
		this.carton = carton;
	}

	public Integer getUnitType() {
		return unitType;
	}

	public void setUnitType(Integer unitType) {
		this.unitType = unitType;
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}

	public List<Map<String, Object>> getWarehouseNameId() {
		return warehouseNameId;
	}

	public void setWarehouseNameId(List<Map<String, Object>> warehouseNameId) {
		this.warehouseNameId = warehouseNameId;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public Integer getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(Integer batchNumber) {
		this.batchNumber = batchNumber;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Boolean getNeedExpirationDate() {
		return needExpirationDate;
	}

	public void setNeedExpirationDate(Boolean needExpirationDate) {
		this.needExpirationDate = needExpirationDate;
	}

	public Map<String, Object> getProductDetails() {
		return productDetails;
	}

	public void setProductDetails(Map<String, Object> productDetails) {
		this.productDetails = productDetails;
	}

	public Set<ExpirationDateDto> getExpirationDateDtoSet() {
		return expirationDateDtoSet;
	}

	public void setExpirationDateDtoSet(Set<ExpirationDateDto> expirationDateDtoSet) {
		this.expirationDateDtoSet = expirationDateDtoSet;
	}

	@Override
	public String toString() {
		return "PurchaseOrderInputGift [id=" + id + ", inputId=" + inputId + ", serialNumber=" + serialNumber + ", sku="
				+ sku + ", title=" + title + ", interBarCode=" + interBarCode + ", carton=" + carton + ", unitType="
				+ unitType + ", qty=" + qty + ", warehouseNameId=" + warehouseNameId + ", warehouseId=" + warehouseId
				+ ", imageUrl=" + imageUrl + ", warehouseName=" + warehouseName + ", price=" + price + ", stock="
				+ stock + ", batchNumber=" + batchNumber + ", expirationDate=" + expirationDate
				+ ", needExpirationDate=" + needExpirationDate + ", productDetails=" + productDetails
				+ ", expirationDays=" + expirationDays + ", expirationDateDtoSet=" + expirationDateDtoSet + "]";
	}

}