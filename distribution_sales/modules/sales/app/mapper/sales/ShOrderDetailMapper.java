package mapper.sales;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.sales.ShOrderDetail;

public interface ShOrderDetailMapper {
    int insert(ShOrderDetail record);

    int insertSelective(ShOrderDetail record);

    List<ShOrderDetail> getShOrderDetailListByShOrderId(Integer id);
    List<ShOrderDetail> getShOrderDetailListByShOrderIdList(@Param("shOrderIdList")List<Integer> shOrderIdList);
    
    List<ShOrderDetail> selectByPurchaseOrderNo(@Param("purchaseOrderNo")String purchaseOrderNo);
}