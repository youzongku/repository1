package entity.product;

import java.util.Date;
import java.util.Map;

/**
 * 商品类目品牌实体(用于设置价格系数)
 * @author zbc
 * 2016年8月16日 下午12:21:09
 */
public class ProductPriceCategoryBrand {
    private Integer id;

    /**
     * 类目id
     */
    private Integer categoryId;

    /**
     * 应用状态
     */
    private Boolean status;

    /**
     * 最后操作人
     */
    private String lastOperator;

    /**
     * 最后操作时间
     */
    private Date lastOperatorTime;

    /**
     * 类目名称
     */
    private String categoryName;

    /**
     * 品牌
     */
    private String brand;
    
	/**
	 * 系数集合
	 */
	private Map<String,Double> factorMap;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getLastOperator() {
        return lastOperator;
    }

    public void setLastOperator(String lastOperator) {
        this.lastOperator = lastOperator;
    }

    public Date getLastOperatorTime() {
        return lastOperatorTime;
    }

    public void setLastOperatorTime(Date lastOperatorTime) {
        this.lastOperatorTime = lastOperatorTime;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public Map<String, Double> getFactorMap() {
		return factorMap;
	}

	public void setFactorMap(Map<String, Double> factorMap) {
		this.factorMap = factorMap;
	}
}