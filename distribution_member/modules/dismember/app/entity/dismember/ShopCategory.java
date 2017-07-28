package entity.dismember;

import java.io.Serializable;

/**
 * 店铺类型(B2B/B2C/C2C/O2O......)表实体
 */
public class ShopCategory implements Serializable {

    private static final long serialVersionUID = -2745004118565663615L;

    private Integer id;

    private String shopCategory;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShopCategory() {
        return shopCategory;
    }

    public void setShopCategory(String shopCategory) {
        this.shopCategory = shopCategory;
    }
}