package mapper.timer;

import entity.timer.ProductInventoryBatchDetail;

public interface ProductInventoryBatchDetailMapper {

    int insertSelective(ProductInventoryBatchDetail record);
    
    public ProductInventoryBatchDetail getBatchDetailByIdentifier(String identifier);

}