package dto.discart;

import java.io.Serializable;

import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 采购单下单详情实体
 * 
 * @author zbc 2017年3月23日 下午2:43:55
 */
public class OrderDetail implements Serializable {

	private static final long serialVersionUID = -8167284236394154763L;

	private Integer itemId;
	@ApiModelProperty("商品名称")
	private String title;
	private Double realPrice;
	@ApiModelProperty("商品sku")
	private String sku;
	@ApiModelProperty("商品数量 ")
	private Integer qty;
	@ApiModelProperty("商品图片")
	private String publicImg;
	@ApiModelProperty("仓库id")
	private Integer warehouseId;
	@ApiModelProperty("仓库名称")
	private String warehouseName;
	@ApiModelProperty("类目id")
	private Integer categoryId;
	@ApiModelProperty("类目名称")
	private String categoryName;
	@ApiModelProperty("分销价")
	private Double price;
	@ApiModelProperty("市场零售价")
	private Double marketPrice;
	@ApiModelProperty("商品价格小计")
	private Double sumPrice;
	private String contractNo;
	private String interBarCode;
	@ApiModelProperty("是否赠品")
	private Boolean isgift;
	private Double clearancePrice;
	
	public OrderDetail() {

	}

	/**
	 */
	public OrderDetail(DisCartDto dto) {
		super();
		this.itemId = dto.getItemId();
		this.title = dto.getTitle();
		this.realPrice = dto.getDisPrice();
		this.sku = dto.getSku();
		this.qty = dto.getQty();
		this.publicImg = dto.getImage();
		this.warehouseId = dto.getWarehouseId();
		this.warehouseName = dto.getStorageName();
		this.categoryId = dto.getCategoryId();
		this.categoryName = dto.getCategoryName();
		this.price = dto.getDisPrice();
		this.marketPrice = dto.getMarketPrice();
		this.sumPrice = dto.getSumprice();
		this.contractNo = dto.getContractNo();
		this.interBarCode = dto.getInterBarCode();
		this.clearancePrice = dto.getClearancePrice();
	}

	public Double getClearancePrice() {
		return clearancePrice;
	}

	public void setClearancePrice(Double clearancePrice) {
		this.clearancePrice = clearancePrice;
	}

	public String getInterBarCode() {
		return interBarCode;
	}

	public void setInterBarCode(String interBarCode) {
		this.interBarCode = interBarCode;
	}

	public Boolean getIsgift() {
		return isgift;
	}

	public void setIsgift(Boolean isgift) {
		this.isgift = isgift;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Double getRealPrice() {
		return realPrice;
	}

	public void setRealPrice(Double realPrice) {
		this.realPrice = realPrice;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}

	public String getPublicImg() {
		return publicImg;
	}

	public void setPublicImg(String publicImg) {
		this.publicImg = publicImg;
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

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
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

	public Double getSumPrice() {
		return sumPrice;
	}

	public void setSumPrice(Double sumPrice) {
		this.sumPrice = sumPrice;
	}
}
