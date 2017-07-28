package mapper.product;

import java.util.List;

import dto.product.search.InventoryLockSearch;
import entity.product.InventoryLock;

public interface InventoryLockMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(InventoryLock record);

    int insertSelective(InventoryLock record);

    InventoryLock selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(InventoryLock record);

    int updateByPrimaryKey(InventoryLock record);

	/**
	 * 
	 * @author zbc
	 * @since 2017年4月20日 下午12:05:30
	 */
	List<InventoryLock> pageSearch(InventoryLockSearch search);

	/**
	 * 
	 * @author zbc
	 * @since 2017年4月20日 下午12:05:57
	 */
	Integer pageCount(InventoryLockSearch search);
}