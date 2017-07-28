package mapper.sales;

import entity.sales.PayWarehouse;
import org.apache.ibatis.annotations.Param;

public interface PayWarehouseMapper {

    /**
     * 通过仓库id查询
     * @param warehouseId
     * @return
     */
    PayWarehouse getRecord(@Param("warehouseId")String warehouseId);
}