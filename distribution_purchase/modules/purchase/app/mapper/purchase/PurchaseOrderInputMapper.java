package mapper.purchase;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.purchase.PurchaseOrderInput;

public interface PurchaseOrderInputMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PurchaseOrderInput record);

    int insertSelective(PurchaseOrderInput record);

    PurchaseOrderInput selectByPrimaryKey(Integer id);
    
    List<PurchaseOrderInput> selectByInputType(@Param("inputType")Integer inputType,@Param("inputUser")String inputUser);

    List<PurchaseOrderInput> selectByInputType(Integer inputType);
    
    List<PurchaseOrderInput> selectByParam(PurchaseOrderInput record);
    
    int updateByPrimaryKeySelective(PurchaseOrderInput record);

    int updateByPrimaryKey(PurchaseOrderInput record);
    
    int deleteInput(@Param("inputId")Integer id);
    
    int deleteInputByParam(PurchaseOrderInput record);
}