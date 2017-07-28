package entity.product;

import java.util.Date;

/**
 * 商品价格系数实体
 * @author zbc
 * 2016年8月16日 下午12:21:39
 */
public class ProductPriceFactor {
    private Integer id;

    /**
     * 价格种类
     */
    private String kind;

    /**
     * 系数
     */
    private Double factor;

    /**
     * 类目品牌表id
     */
    private Integer categoryBrandId;
    
    private Integer priceId;
    
    private Double profit;//利润值（最后一次设置）
    
    private Date createDate;//创建时间
    
    private Date updateDate;//更新时间
    
    public Double getProfit() {
		return profit;
	}

	public void setProfit(Double profit) {
		this.profit = profit;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Integer getPriceId() {
		return priceId;
	}

	public void setPriceId(Integer priceId) {
		this.priceId = priceId;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Double getFactor() {
        return factor;
    }

    public void setFactor(Double factor) {
        this.factor = factor;
    }

    public Integer getCategoryBrandId() {
        return categoryBrandId;
    }

    public void setCategoryBrandId(Integer categoryBrandId) {
        this.categoryBrandId = categoryBrandId;
    }
}