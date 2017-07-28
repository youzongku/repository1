package mapper.dismember;

import entity.dismember.ShopSite;

public interface ShopSiteMapper extends BaseMapper<ShopSite> {

    ShopSite selectByCondition(ShopSite shopSite);
    
    int updateByEmail(ShopSite shopSite);
}