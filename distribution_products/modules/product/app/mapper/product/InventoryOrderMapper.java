package mapper.product;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.product.InventoryOrder;

public interface InventoryOrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(InventoryOrder record);

    int insertSelective(InventoryOrder record);

    InventoryOrder selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(InventoryOrder record);

    int updateByPrimaryKey(InventoryOrder record);
    
    List<InventoryOrder> selectByOrderNo(@Param("orderNo")String orderNo);
}