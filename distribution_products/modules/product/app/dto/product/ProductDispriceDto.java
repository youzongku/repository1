package dto.product;

import java.math.BigDecimal;

import play.Logger;
import entity.product.ProductDisprice;

/**
 * @author zbc
 * 2016年7月28日 下午4:19:22
 */
public class ProductDispriceDto extends ProductDisprice {
	
	private String productTitle;//商品名称
	
	private Integer categoryId;//类目id
	
	private String categoryName;//类目名称
	
	private String warehoseName;//仓库名称
	
	private String brand;//品牌

	private Integer cloudStock;//云仓有效库存
	
	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getWarehoseName() {
		return warehoseName;
	}

	public void setWarehoseName(String warehoseName) {
		this.warehoseName = warehoseName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getProductTitle() {
		return productTitle;
	}

	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}
	
	/**
	 * 计算到仓价
	 * @param qty 数量
	 * @return
	 */
	public BigDecimal calculateArriveWarePrice(int qty, boolean useCostIfAbsent){
		if(getArriveWarePrice()==null || getArriveWarePrice().intValue()==0){
			if (useCostIfAbsent && getCost()!=null) {// 允许使用裸采价来计算
				return new BigDecimal(getCost()).multiply(new BigDecimal(qty));
			}
			Logger.info("仓库{}-{}到仓价为0",getWarehoseName(),getSku());
			return new BigDecimal(0);
		}
		return new BigDecimal(getArriveWarePrice()).multiply(new BigDecimal(qty));
	}

	public Integer getCloudStock() {
		return cloudStock;
	}

	public void setCloudStock(Integer cloudStock) {
		this.cloudStock = cloudStock;
	}

	@Override
	public String toString() {
		return "ProductDispriceDto{" +
				"productTitle='" + productTitle + '\'' +
				", categoryId=" + categoryId +
				", categoryName='" + categoryName + '\'' +
				", warehoseName='" + warehoseName + '\'' +
				", brand='" + brand + '\'' +
				", cloudStock=" + cloudStock +
				'}';
	}
}
