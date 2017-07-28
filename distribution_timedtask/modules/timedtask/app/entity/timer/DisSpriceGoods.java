package entity.timer;

import java.io.Serializable;
import java.util.Date;

public class DisSpriceGoods implements Serializable {

    private static final long serialVersionUID = -4525580622903263167L;

    private Integer id;

    private Integer activityId;

    private String sku;

    private Integer warehouseId;

    private Boolean limitedPurchase;

    private Integer limitedPnum;

    private Double specialPrice;

    private String predisNumber;

    private String predisUnit;

    private Double predisProfitRate;

    private String createUser;

    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
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

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}