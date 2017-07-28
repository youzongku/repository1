package mapper.dismember;

import entity.dismember.ShopCategory;

import java.util.List;

public interface ShopCategoryMapper extends BaseMapper<ShopCategory> {

    /**
     * 查询所有店铺种类
     * @return
     */
    List<ShopCategory> getAllShopCategorys();

}