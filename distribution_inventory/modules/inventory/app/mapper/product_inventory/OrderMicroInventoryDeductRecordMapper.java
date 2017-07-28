package mapper.product_inventory;

import entity.product_inventory.OrderMicroInventoryDeductRecord;
import entity.product_inventory.ProductInventoryDetail;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface OrderMicroInventoryDeductRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderMicroInventoryDeductRecord record);

    int insertSelective(OrderMicroInventoryDeductRecord record);

    OrderMicroInventoryDeductRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderMicroInventoryDeductRecord record);

    int updateByPrimaryKey(OrderMicroInventoryDeductRecord record);

    List<OrderMicroInventoryDeductRecord> listByOrderNo(String orderNo);

	List<OrderMicroInventoryDeductRecord> selectByOrderNoAndSku(@Param("list")List<String> orderNoLists, @Param("sku")String sku,
			@Param("expirationDate")Date expirationDate);

	List<OrderMicroInventoryDeductRecord> selectRecordBySkuAndExpirationDate(ProductInventoryDetail paramDetail);
}