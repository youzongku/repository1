package entity.dismember;

import java.io.Serializable;

/**
 * 店铺平台(淘宝、天猫、京东、亚马逊......)表实体
 */
public class ShopPlatform implements Serializable {

    private static final long serialVersionUID = -410167640838450712L;

    private Integer id;

    private String shopPlatform;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShopPlatform() {
        return shopPlatform;
    }

    public void setShopPlatform(String shopPlatform) {
        this.shopPlatform = shopPlatform;
    }
}