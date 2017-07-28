package mapper.timer;

import java.util.List;

import entity.timer.ProductInventoryDetail;


public interface ProductInventoryDetailMapper {

    int insertSelective(ProductInventoryDetail record);

    ProductInventoryDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProductInventoryDetail record);

	ProductInventoryDetail selectByParam(ProductInventoryDetail param);

	List<ProductInventoryDetail> list(ProductInventoryDetail inventoryDetailParam);
}