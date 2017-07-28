package dto.product_inventory;

import java.util.Date;

/**
 * @author longhuashen
 * @since 2017/3/7
 */
public class ProductMicroAndCloudLockDto {

    /**
     * sku
     */
    private String sku;

    /**
     * 需要的微仓数量
     */
    private Integer lockMicroStock;

    /**
     * 需要的云仓数量
     */
    private Integer lockCloudStock;

    /**
     * 过期日期
     */
    private Date expiredDate;

    /**
     * 产品名称
     */
    private String productTitle;

    /**
     * 图片地址
     */
    private String imgUrl;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getLockMicroStock() {
        return lockMicroStock;
    }

    public void setLockMicroStock(Integer lockMicroStock) {
        this.lockMicroStock = lockMicroStock;
    }

    public Integer getLockCloudStock() {
        return lockCloudStock;
    }

    public void setLockCloudStock(Integer lockCloudStock) {
        this.lockCloudStock = lockCloudStock;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return "ProductMicroAndCloudLockDto{" +
                "sku='" + sku + '\'' +
                ", lockMicroStock=" + lockMicroStock +
                ", lockCloudStock=" + lockCloudStock +
                ", expiredDate=" + expiredDate +
                ", productTitle='" + productTitle + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }
}
