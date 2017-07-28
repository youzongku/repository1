package dto.product;

import java.io.Serializable;

import com.wordnik.swagger.annotations.ApiModel;

/**
 * 清货价实体
 * @author zbc
 * 2017年4月11日 上午11:56:24
 */
@ApiModel
public class ClearancePriceDto implements Serializable{

	private static final long serialVersionUID = 1532435013074696379L;

    private String sku;

    private String typeName;//产品名称
    
    private Double  clearanceRate;//清货率
    
    private Double clearancePrice;//清货价
    
	private String productTitle;//商品名称
	
	private String categoryName;//类目名称
	
	private Double arriveWarePrice;//  到仓价

	public Double getArriveWarePrice() {
		return arriveWarePrice;
	}

	public void setArriveWarePrice(Double arriveWarePrice) {
		this.arriveWarePrice = arriveWarePrice;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public Double getClearanceRate() {
		return clearanceRate;
	}

	public void setClearanceRate(Double clearanceRate) {
		this.clearanceRate = clearanceRate;
	}

	public Double getClearancePrice() {
		return clearancePrice;
	}

	public void setClearancePrice(Double clearancePrice) {
		this.clearancePrice = clearancePrice;
	}

	public String getProductTitle() {
		return productTitle;
	}

	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

}
