package mapper.product_inventory;

import java.util.List;

import dto.product_inventory.ErpStockChangeQueryDto;
import entity.product_inventory.ProductInventoryBatchDetail;

public interface ProductInventoryBatchDetailMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProductInventoryBatchDetail record);

    int insertSelective(ProductInventoryBatchDetail record);

    ProductInventoryBatchDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProductInventoryBatchDetail record);

    int updateByPrimaryKey(ProductInventoryBatchDetail record);
    
    public ProductInventoryBatchDetail getBatchDetailByIdentifier(String identifier);

	List<ProductInventoryBatchDetail> getSyncRecordByParam(ErpStockChangeQueryDto queryParam);

	int getSyncRecordTotalCountByParam(ErpStockChangeQueryDto queryParam);

}