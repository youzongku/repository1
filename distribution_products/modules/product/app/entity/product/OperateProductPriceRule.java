package entity.product;

import java.util.Date;

import util.product.DateUtils;

/**
 * @author zbc
 *
 * 2016年7月28日 上午11:38:07
 */
public class OperateProductPriceRule {
	
    private Integer id;

    private String priceClassification;//价格种类

    private Integer priceClassificationId;//价格种类id

    private Boolean status;//启用状态

    private String statusDesc;//状态描述

    private String operate;//操作人

    private Double factor;//更改系数

    private Date operateTime;//操作时间
    
    private Integer categoryId;//类目id
    
    private String categoryName;//类目名称
    
    private String brand;//品牌
    
    private Integer categoryBrandId;//类目 品牌 表id
    
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

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public Integer getCategoryBrandId() {
		return categoryBrandId;
	}

	public void setCategoryBrandId(Integer categoryBrandId) {
		this.categoryBrandId = categoryBrandId;
	}

	public String getOperateTimeStr(){
    	return DateUtils.date2string(operateTime, DateUtils.FORMAT_FULL_DATETIME);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPriceClassification() {
        return priceClassification;
    }

    public void setPriceClassification(String priceClassification) {
        this.priceClassification = priceClassification;
    }

    public Integer getPriceClassificationId() {
        return priceClassificationId;
    }

    public void setPriceClassificationId(Integer priceClassificationId) {
        this.priceClassificationId = priceClassificationId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    public Double getFactor() {
        return factor;
    }

    public void setFactor(Double factor) {
        this.factor = factor;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }
}