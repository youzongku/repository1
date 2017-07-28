package dto.product.inventory;

import java.io.Serializable;

/**
 * 销售发货 微仓库存实体类
 * @author zbc
 * 2016年12月27日 上午11:58:38
 */
public class SaleLockDetailDto implements Serializable {

	private static final long serialVersionUID = 4587490588321736581L;
	
	/**
	 * 商品编码
	 */
	private String sku;
	
	/**
	 * 到期日期
	 */
	private String expirationDate;
	
	/**
	 * 数量
	 */
	private Integer qty;
	
	/**
	 * 商品标题
	 */
	private String productTitle;

	/**
	 * 图片链接
	 */
	private String imgUrl;
	
	public SaleLockDetailDto() {
	}

	/**
	 * @param SaleLockDetailDto 
	 * @param qty
	 */
	public SaleLockDetailDto(SaleLockDetailDto pro, Integer qty) {
		super();
		this.sku = pro.getSku();
		this.expirationDate = pro.getExpirationDate();
		this.qty = qty;
		this.productTitle = pro.getImgUrl();
		this.imgUrl = pro.getImgUrl();
	}
	
	public SaleLockDetailDto(SaleLockDetailDto pro, Integer qty,String expirationDate) {
		super();
		this.sku = pro.getSku();
		this.expirationDate = expirationDate;
		this.qty = qty;
		this.productTitle = pro.getImgUrl();
		this.imgUrl = pro.getImgUrl();
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
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
	
}
