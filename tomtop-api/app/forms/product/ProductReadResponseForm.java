package forms.product;

import java.io.Serializable;

/**
 * @author ye_ziran
 * @since 2016/4/7 12:13
 */
public class ProductReadResponseForm implements Serializable{
    private String ctitle;
    private String csku;
    private String imageUrl; // 图片地址
    private String cname;// 所属类目名称
    private Integer categoryId;//所属类目ID
    private Integer stock = 0;//云仓库存
    private Integer warehouseId;// 仓库ID
    private Integer istatus;
    private Double fweight;//重量
    private double originalPrice; // 通淘价（fprice）
    private Double disPrice;//分销价
    private String productEnterprise;//生产厂家
    private String componentContent;//成分含量
    private Integer expirationDays;//保质期
    private String packageType;//包装种类
    private String originCountry;//原产地
    private String plugType;//规格
    private String interBarCode;//国际条码
    private String brand;//品牌

    public String getCtitle() {
        return ctitle;
    }

    public void setCtitle(String ctitle) {
        this.ctitle = ctitle;
    }

    public String getCsku() {
        return csku;
    }

    public void setCsku(String csku) {
        this.csku = csku;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Integer getIstatus() {
        return istatus;
    }

    public void setIstatus(Integer istatus) {
        this.istatus = istatus;
    }

    public Double getFweight() {
        return fweight;
    }

    public void setFweight(Double fweight) {
        this.fweight = fweight;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Double getDisPrice() {
        return disPrice;
    }

    public void setDisPrice(Double disPrice) {
        this.disPrice = disPrice;
    }

    public String getProductEnterprise() {
        return productEnterprise;
    }

    public void setProductEnterprise(String productEnterprise) {
        this.productEnterprise = productEnterprise;
    }

    public String getComponentContent() {
        return componentContent;
    }

    public void setComponentContent(String componentContent) {
        this.componentContent = componentContent;
    }

    public Integer getExpirationDays() {
        return expirationDays;
    }

    public void setExpirationDays(Integer expirationDays) {
        this.expirationDays = expirationDays;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public String getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(String originCountry) {
        this.originCountry = originCountry;
    }

    public String getPlugType() {
        return plugType;
    }

    public void setPlugType(String plugType) {
        this.plugType = plugType;
    }

    public String getInterBarCode() {
        return interBarCode;
    }

    public void setInterBarCode(String interBarCode) {
        this.interBarCode = interBarCode;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
