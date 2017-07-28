package mapper.product;

import java.util.List;

import dto.product.search.InventoryLockDeSearch;
import entity.product.IvyOptDetail;

public interface IvyOptDetailMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(IvyOptDetail record);

    int insertSelective(IvyOptDetail record);

    IvyOptDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(IvyOptDetail record);

    int updateByPrimaryKey(IvyOptDetail record);

	Integer pageCount(InventoryLockDeSearch search);

	List<IvyOptDetail> pageSearch(InventoryLockDeSearch search);
}