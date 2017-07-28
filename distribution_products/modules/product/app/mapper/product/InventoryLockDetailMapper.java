package mapper.product;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import dto.product.InvetoryLockNumDto;
import dto.product.inventory.InventoryLockStock;
import dto.product.search.InventoryLockDeSearch;
import entity.product.InventoryLockDetail;

public interface InventoryLockDetailMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(InventoryLockDetail record);

    int insertSelective(InventoryLockDetail record);

    InventoryLockDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(InventoryLockDetail record);

    int updateByPrimaryKey(InventoryLockDetail record);
    
    List<InventoryLockDetail> pageSearch(InventoryLockDeSearch dto);
    
    Integer pageCount(InventoryLockDeSearch dto);
    
    List<InvetoryLockNumDto> querySubstock(@Param("param")Map<String,Object> map);
    
    List<InvetoryLockNumDto> querySubstockByExpirationDate(@Param("param")Map<String,Object> map);
    
    List<InventoryLockDetail>  getDetail(String account);
    
    int autoChange();
    
    InventoryLockStock getLockStock(@Param("sku")String sku,@Param("wareId")Integer wareId);
    
    List<InventoryLockDetail> getDetailByLockId(Integer lockId);
}