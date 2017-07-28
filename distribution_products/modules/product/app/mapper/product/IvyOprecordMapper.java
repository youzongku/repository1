package mapper.product;

import java.util.List;

import dto.product.search.InventoryLockDeSearch;
import entity.product.IvyOprecord;

public interface IvyOprecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(IvyOprecord record);

    int insertSelective(IvyOprecord record);

    IvyOprecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(IvyOprecord record);

    int updateByPrimaryKey(IvyOprecord record);
    
    List<IvyOprecord> pageSearch(InventoryLockDeSearch search);
    
    Integer pageCount(InventoryLockDeSearch search);
}