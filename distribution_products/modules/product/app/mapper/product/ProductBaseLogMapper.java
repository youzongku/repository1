package mapper.product;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.product.ProductBaseLog;

public interface ProductBaseLogMapper {
    int batchInsert(@Param("logList")List<ProductBaseLog> logList);

    int insert(ProductBaseLog record);

    List<ProductBaseLog> selectBySku(@Param("sku")String sku);
}