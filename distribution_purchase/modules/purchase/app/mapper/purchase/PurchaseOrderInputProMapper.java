package mapper.purchase;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.purchase.PurchaseOrderInputPro;

public interface PurchaseOrderInputProMapper {
	
	int updateChecked(@Param("inputId")int inputId, @Param("proIdList")List<Integer> proIdList,@Param("checked")boolean checked);
	
    int deleteByPrimaryKey(Integer id); 
    int deleteByInputId(@Param("inputId")int inputId); 
    int deleteByIdList(List<Integer> idList); 

    int insert(PurchaseOrderInputPro record);
    int insertBatch(List<PurchaseOrderInputPro> records);

    int insertSelective(PurchaseOrderInputPro record);

    PurchaseOrderInputPro selectByPrimaryKey(Integer id);
    
    List<PurchaseOrderInputPro> selectByInputId(Integer inputId);

    List<PurchaseOrderInputPro> selectByChecked(@Param("inputId")Integer inputId,@Param("checked")boolean checked);
    
    List<PurchaseOrderInputPro> selectByIdList(List<Integer> idList);

    int updateByPrimaryKeySelective(PurchaseOrderInputPro record);

    int updateByPrimaryKey(PurchaseOrderInputPro record);
    
    int updateProQty(@Param("id")int id,@Param("qty")int qty);

    int batchUpdateWare(List<PurchaseOrderInputPro> pro);
    
    int batchUpdateProQtyAndNeedExpirationDate(List<PurchaseOrderInputPro> proList);
}