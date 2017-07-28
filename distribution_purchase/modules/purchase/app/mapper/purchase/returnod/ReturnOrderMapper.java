package mapper.purchase.returnod;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import dto.purchase.returnod.ReturnOrderInfo;
import entity.purchase.returnod.ReturnOrder;

public interface ReturnOrderMapper {
	List<ReturnOrder> selectByParams(Map<String,Object> params);
	Integer selectCountByParams(Map<String,Object> params);
	
    int deleteByPrimaryKey(@Param("id")Integer id, @Param("status")Integer status);

    int insert(ReturnOrder record);

    int insertSelective(ReturnOrder record);

    ReturnOrder selectByPrimaryKey(Integer id);
    
    /**
     * 根据采购单单号查询
     * @param purchaseOrderNoList 采购单单号集合
     * @param status 退货单状态
     * @return
     */
    List<ReturnOrderInfo> selectReturnOrderInfo(
    		@Param("purchaseOrderNo")String purchaseOrderNo, @Param("status")Integer status);

    int updateByPrimaryKeySelective(ReturnOrder record);

    int batchUpdateByPrimaryKeySelective(List<ReturnOrder> records);

    ReturnOrder selectByReturnOrderNo(@Param("returnOrderNo")String returnOrderNo);
    List<ReturnOrder> selectByReturnOrderNoList(List<String> returnOrderNos);

}