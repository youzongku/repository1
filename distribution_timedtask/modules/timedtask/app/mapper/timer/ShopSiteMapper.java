package mapper.timer;

import entity.timer.ShopSite;


public interface ShopSiteMapper extends BaseMapper<ShopSite> {

    ShopSite selectByCondition(ShopSite shopSite);
    
    int updateByEmail(ShopSite shopSite);
}