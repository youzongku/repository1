package mapper.product;

import java.util.List;

import entity.product.GoodsInventory;
import entity.product.Warehouse;

public interface WarehouseMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Warehouse record);

    int insertSelective(Warehouse record);

    Warehouse selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Warehouse record);

    int updateByPrimaryKey(Warehouse record);
    
    List<Warehouse> selectAll();

	List<GoodsInventory> getGoodsInventorys(String sku);

	List<Warehouse> getAvailableWarehouse();
}