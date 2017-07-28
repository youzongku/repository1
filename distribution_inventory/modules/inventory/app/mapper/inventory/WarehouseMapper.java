package mapper.inventory;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.inventory.Warehouse;

public interface WarehouseMapper extends BaseMapper<Warehouse> {

	List<Warehouse> queryWarehouse(@Param("wid")Integer wid, @Param("batchNo")String batchNo);

}