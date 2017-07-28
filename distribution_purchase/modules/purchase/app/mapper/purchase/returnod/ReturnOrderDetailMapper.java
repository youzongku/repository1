package mapper.purchase.returnod;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.purchase.returnod.ReturnOrderDetail;

public interface ReturnOrderDetailMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ReturnOrderDetail record);

    int insertSelective(ReturnOrderDetail record);

    ReturnOrderDetail selectByPrimaryKey(Integer id);

    /**
     * 根据退货单号批量查询
     * @param roIds
     * @return
     */
    List<ReturnOrderDetail> selectByRoIdList(@Param("roIds")List<Integer> roIds);

	List<ReturnOrderDetail> selectByPurchaseOrderNo(@Param("purchaseOrderNo")String purchaseOrderNo);
}