package dto.product;

import java.io.Serializable;

/**
 * 产品信息dto
 * <p>
 * 适用于页面的列表展示<br>
 * 比较关键的概念:<br>
 * 
 * <ul>
 * <li>basePrice小于costPrice的时候，getbasePrice()返回costPrice
 * </ul>
 * 
 * @author ye_ziran
 * @since 2015年9月23日 下午4:54:46
 */
public class ProductListInfoDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;//产品id
	private String sku;//产品编码
	private String title;//产品标题
	private String listingId;//广告编号
	private	String imgUrl;//图片url
	private String linkUrl;//产品跳转url
	private Integer catId;//品类id
	private String catTitle;//品类标题
	private Double basePrice;//基本价格,对应t_product_base的fprice
	private Double costPrice;//成本价格,对应t_product_base的fcostprice
	private Double salePrice;//销售价格,对应t_product_base的saleprice
	private Double localRefPrice;//国内参考价,对应t_product_base的saleprice
	private Double foreignRefPrice;//国外参考价,对应t_product_base的saleprice
	private Double freeShippingPrice;//包邮价,对应t_product_base的saleprice
	private Double discount;//折扣
	private Double discountPrice;//折扣价
	private String shortDesc;	//短描述
	private Integer istatus;//状态(在售、停售、下架)
	
	public Integer getIstatus() {
		return istatus;
	}
	public void setIstatus(Integer istatus) {
		this.istatus = istatus;
	}
	//add by luwj 
	private Integer sales;//产品销量

	private Integer qty = 0;//商品库存
	
	public Integer getQty() {
		return qty;
	}
	public void setQty(Integer qty) {
		this.qty = qty;
	}
	public String getShortDesc() {
		return shortDesc;
	}
	public void setShortDesc(String shortDesc) {
		this.shortDesc = shortDesc;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public String getListingId() {
		return listingId;
	}
	public void setListingId(String listingId) {
		this.listingId = listingId;
	}
	/**
	 * 销售价低于成本价时，显示成本价
	 * 
	 * @return
	 * @author ye_ziran
	 * @since 2015年10月29日 下午5:57:28
	 */
	public Double getBasePrice() {
		return basePrice>costPrice?basePrice:costPrice;
	}
	public void setBasePrice(Double basePrice) {
		this.basePrice = basePrice;
	}
	public Double getSalePrice() {
		return salePrice;
	}
	public void setSalePrice(Double salePrice) {
		this.salePrice = salePrice;
	}
	public Double getLocalRefPrice() {
		return localRefPrice;
	}
	public void setLocalRefPrice(Double localRefPrice) {
		this.localRefPrice = localRefPrice;
	}
	public Double getForeignRefPrice() {
		return foreignRefPrice;
	}
	public void setForeignRefPrice(Double foreignRefPrice) {
		this.foreignRefPrice = foreignRefPrice;
	}
	public Double getFreeShippingPrice() {
		return freeShippingPrice;
	}
	public void setFreeShippingPrice(Double freeShippingPrice) {
		this.freeShippingPrice = freeShippingPrice;
	}
	public Double getDiscount() {
		return discount;
	}
	public void setDiscount(Double discount) {
		this.discount = discount;
	}
	public Double getDiscountPrice() {
		return discountPrice;
	}
	public void setDiscountPrice(Double discountPrice) {
		this.discountPrice = discountPrice;
	}
	public Integer getCatId() {
		return catId;
	}
	public void setCatId(Integer catId) {
		this.catId = catId;
	}
	public String getCatTitle() {
		return catTitle;
	}
	public void setCatTitle(String catTitle) {
		this.catTitle = catTitle;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getLinkUrl() {
		return linkUrl;
	}
	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}
	public Double getCostPrice() {
		return costPrice;
	}
	public void setCostPrice(Double costPrice) {
		this.costPrice = costPrice;
	}
	public Integer getSales() {
		return sales;
	}
	public void setSales(Integer sales) {
		this.sales = sales;
	}
}
