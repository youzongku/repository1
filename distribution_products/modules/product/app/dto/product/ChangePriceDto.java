package dto.product;

/**
 * @author longhuashen
 * @since 2017/4/17
 */
public class ChangePriceDto {

    private String key;

    private Double price;

    private String remark;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "ChangePriceDto{" +
                "key='" + key + '\'' +
                ", price=" + price +
                ", remark='" + remark + '\'' +
                '}';
    }
}
