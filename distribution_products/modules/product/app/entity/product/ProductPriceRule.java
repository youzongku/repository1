package entity.product;

import java.util.Date;

import util.product.DateUtils;

/**
 * @author zbc
 *
 * 2016年7月28日 上午11:40:51
 */
public class ProductPriceRule {
    private Integer id;

    private String cRule;//设置价格系数 -- 价格计算公式

    private Boolean status;//应用状态

    private String lastOperator;//最后操作人

    private Date lastOperatorTime;//最后操作时间

    private Double factor;//计算系数

    private Double defaultFactor;//默认系数 

    private String priceClassification;//价格种类 如：floorPrice 等 对应实体类字段

    private String priceClassificationDesc;//价格种类描述  如：最低价
    
    private String fieldName;//变量反射的字段名
    
    private String profitRule;//设置利润 -- 计算公式
    
    public String getProfitRule() {
		return profitRule;
	}

	public void setProfitRule(String profitRule) {
		this.profitRule = profitRule;
	}

	public String getLastOperatorTimeStr() {
    	if(lastOperatorTime != null){
    		return DateUtils.date2string(lastOperatorTime, DateUtils.FORMAT_FULL_DATETIME);
    	}
    	return null;
	}
    
    public String getStatusStr() {
		return status?"应用中":"未应用";
	}
    
    public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getcRule() {
        return cRule;
    }

    public void setcRule(String cRule) {
        this.cRule = cRule;
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

    public Double getFactor() {
        return factor;
    }

    public void setFactor(Double factor) {
        this.factor = factor;
    }

    public Double getDefaultFactor() {
        return defaultFactor;
    }

    public void setDefaultFactor(Double defaultFactor) {
        this.defaultFactor = defaultFactor;
    }

    public String getPriceClassification() {
        return priceClassification;
    }

    public void setPriceClassification(String priceClassification) {
        this.priceClassification = priceClassification;
    }

    public String getPriceClassificationDesc() {
        return priceClassificationDesc;
    }

    public void setPriceClassificationDesc(String priceClassificationDesc) {
        this.priceClassificationDesc = priceClassificationDesc;
    }

}