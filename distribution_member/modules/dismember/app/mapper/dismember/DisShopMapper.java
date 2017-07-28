package mapper.dismember;

import entity.dismember.DisShop;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author luwj
 *
 */
public interface DisShopMapper extends BaseMapper<DisShop> {
	
	List<DisShop> selectByIds(List<Integer> ids);
	
	/**
	 * 批量设置店铺扣点
	 * @param disShops
	 * @return
	 */
	int batchUpdateDeductionPointsById(List<DisShop> disShops);

    /**
     * 根据条件获取店铺信息记录数
     * @param map
     * @return
     */
    int getCountByCondition(Map<String, Object> map);

    /**
     * 根据条件获取店铺信息
     * @param map
     * @return
     */
    List<DisShop> getDisShopsByCondition(Map<String, Object> map);
    
    int deleteByParentid(Integer parentId);

}