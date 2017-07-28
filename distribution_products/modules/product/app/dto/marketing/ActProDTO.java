package dto.marketing;

import dto.product.ProductLite;

/**
 * Created by LSL on 2016/7/6.
 */
public class ActProDTO extends ProductLite {

    private Integer id;

    private Boolean limitedPurchase;

    private Integer limitedPnum;

    private Double specialPrice;

    private String predisNumber;

    private String predisUnit;

    private Double predisProfitRate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getLimitedPurchase() {
        return limitedPurchase;
    }

    public void setLimitedPurchase(Boolean limitedPurchase) {
        this.limitedPurchase = limitedPurchase;
    }

    public Integer getLimitedPnum() {
        return limitedPnum;
    }

    public void setLimitedPnum(Integer limitedPnum) {
        this.limitedPnum = limitedPnum;
    }

    public Double getSpecialPrice() {
        return specialPrice;
    }

    public void setSpecialPrice(Double specialPrice) {
        this.specialPrice = specialPrice;
    }

    public String getPredisNumber() {
        return predisNumber;
    }

    public void setPredisNumber(String predisNumber) {
        this.predisNumber = predisNumber;
    }

    public String getPredisUnit() {
        return predisUnit;
    }

    public void setPredisUnit(String predisUnit) {
        this.predisUnit = predisUnit;
    }

    public Double getPredisProfitRate() {
        return predisProfitRate;
    }

    public void setPredisProfitRate(Double predisProfitRate) {
        this.predisProfitRate = predisProfitRate;
    }
}
