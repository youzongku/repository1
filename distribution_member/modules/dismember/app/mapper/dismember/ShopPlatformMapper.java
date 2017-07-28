package mapper.dismember;

import entity.dismember.ShopPlatform;

import java.util.List;

public interface ShopPlatformMapper extends BaseMapper<ShopPlatform> {

    /**
     * 查询所有店铺平台
     * @return
     */
    List<ShopPlatform> getAllShopPlatforms();

}