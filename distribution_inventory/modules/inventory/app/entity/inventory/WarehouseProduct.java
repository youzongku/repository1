package entity.inventory;

import java.util.Date;

public class WarehouseProduct {
    private Integer id;

    private String csku;

    private Integer status;

    private Integer stock;

    private Double weight;

    private Double zeroStockPrice;

    private Double postageFreePrice;

    private Double postagePrice;

    private Double marketPrice;

    private Date createDate;

    private String productName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCsku() {
        return csku;
    }

    public void setCsku(String csku) {
        this.csku = csku;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getZeroStockPrice() {
        return zeroStockPrice;
    }

    public void setZeroStockPrice(Double zeroStockPrice) {
        this.zeroStockPrice = zeroStockPrice;
    }

    public Double getPostageFreePrice() {
        return postageFreePrice;
    }

    public void setPostageFreePrice(Double postageFreePrice) {
        this.postageFreePrice = postageFreePrice;
    }

    public Double getPostagePrice() {
        return postagePrice;
    }

    public void setPostagePrice(Double postagePrice) {
        this.postagePrice = postagePrice;
    }

    public Double getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(Double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}