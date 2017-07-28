package component.elasticsearch;

import java.text.DecimalFormat;

/**
 * 商品info
 * 
 * @author ye_ziran
 * @since 2017年1月10日 下午4:40:16
 */
public class ProductInfoDoc {
	DecimalFormat df = new DecimalFormat("#.00");

	@MappingType(type = "integer")
	private Integer iid;
	@MappingType(type = "string", index = "not_analyzed")
	private String csku;
	/**
	 * 商品标题；
	 * 商品标题取自t_product_translate，直接去ilanguageid=1的标题
	 */
	@MappingType(type = "string", index = "analyzed")
	private String ctitle;
	@MappingType(type = "string", index = "not_analyzed")
	private String clistingId;
	@MappingType(type = "string", index = "not_analyzed")
	private String cparentsku;//父编号
	@MappingType(type = "string", index = "not_analyzed")
	private String imageUrl; // 图片地址
	@MappingType(type = "integer")
	private Integer istatus;
	@MappingType(type = "string")
	private String brand;//商品品牌
	@MappingType(type = "double")
	private Double fweight;// 重量
	@MappingType(type = "integer")
	private Integer iqty;
	@MappingType(type = "boolean")
	private Boolean bvisible;//是否可见
	@MappingType(type = "string")
	private String interBarCode;//国际条码
	@MappingType(type = "string")
	private String packageType;//包装种类
	@MappingType(type = "string")
	private String reShippingType;//零售发货方式；零售情况下，当sku的仓库没有发货方式时，取商品的发货代码
	@MappingType(type = "boolean")
	private Boolean bbcvisible;
	@MappingType(type = "boolean")
	private Boolean ordinaryMsiteVisible;
	@MappingType(type = "boolean")
	private Boolean brandMsiteVisible;
	@MappingType(type = "double")
	private Double originalPrice; // 通淘价（fprice）
	@MappingType(type = "double")
	private Double localPrice; // 市场价 local_ref_price
	/**
	 * 所属的一级类目；当需要类目树的时候，需要重新维护es的type；
	 * 需要将t_category_base和t_product_category_mapper表的关系维护进es
	 */
	@MappingType(type = "integer")
	private Integer categoryId;// 
	@MappingType(type = "string", index = "not_analyzed")
	private String cname;// 所属类目名称

	public String getCparentsku() {
		return cparentsku;
	}

	public void setCparentsku(String cparentsku) {
		this.cparentsku = cparentsku;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public Integer getIqty() {
		return iqty;
	}

	public void setIqty(Integer iqty) {
		this.iqty = iqty;
	}

	public Boolean getBvisible() {
		return bvisible;
	}

	public void setBvisible(Boolean bvisible) {
		this.bvisible = bvisible;
	}

	public String getInterBarCode() {
		return interBarCode;
	}

	public void setInterBarCode(String interBarCode) {
		this.interBarCode = interBarCode;
	}

	public String getPackageType() {
		return packageType;
	}

	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}

	public String getReShippingType() {
		return reShippingType;
	}

	public void setReShippingType(String reShippingType) {
		this.reShippingType = reShippingType;
	}

	public Boolean getBbcvisible() {
		return bbcvisible;
	}

	public void setBbcvisible(Boolean bbcvisible) {
		this.bbcvisible = bbcvisible;
	}

	public Boolean getOrdinaryMsiteVisible() {
		return ordinaryMsiteVisible;
	}

	public void setOrdinaryMsiteVisible(Boolean ordinaryMsiteVisible) {
		this.ordinaryMsiteVisible = ordinaryMsiteVisible;
	}

	public Boolean getBrandMsiteVisible() {
		return brandMsiteVisible;
	}

	public void setBrandMsiteVisible(Boolean brandMsiteVisible) {
		this.brandMsiteVisible = brandMsiteVisible;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public void setOriginalPrice(Double originalPrice) {
		this.originalPrice = originalPrice;
	}

	public void setLocalPrice(Double localPrice) {
		this.localPrice = localPrice;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public Integer getIid() {
		return iid;
	}

	public void setIid(Integer iid) {
		this.iid = iid;
	}

	public String getCtitle() {
		return ctitle;
	}

	public void setCtitle(String ctitle) {
		this.ctitle = ctitle;
	}

	public String getCsku() {
		return csku;
	}

	public void setCsku(String csku) {
		this.csku = csku;
	}

	public String getClistingId() {
		return clistingId;
	}

	public void setClistingId(String clistingId) {
		this.clistingId = clistingId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public double getOriginalPrice() {
		return originalPrice;
	}

	public void setOriginalPrice(double originalPrice) {
		this.originalPrice = Double.parseDouble(df.format(originalPrice));
	}

	public double getLocalPrice() {
		return localPrice;
	}

	public void setLocalPrice(double localPrice) {
		this.localPrice = Double.parseDouble(df.format(localPrice));
	}

	public Integer getIstatus() {
		return istatus;
	}

	public void setIstatus(Integer istatus) {
		this.istatus = istatus;
	}

	public Double getFweight() {
		return fweight;
	}

	public void setFweight(Double fweight) {
		this.fweight = fweight;
	}

}