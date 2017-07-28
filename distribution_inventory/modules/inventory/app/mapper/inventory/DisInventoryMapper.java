package mapper.inventory;

import java.util.List;

import dto.inventory.FirstInFirstOutDisIvyInfo;
import entity.inventory.DisInventory;

public interface DisInventoryMapper extends BaseMapper<DisInventory> {
    /**
     * 获取指定微仓仓库指定商品的库存
     * @param sku
     * @param warehouseid
     * @return
     */
    Integer getStockInWarehouse(String email,String sku,int warehouseid);
    
    /**
     * 更新指定微仓仓库指定商品的库存
     * @param email
     * @param sku
     * @param warehouseid
     * @param qty 变化的数量，正数是添加，负数是减少
     * @return
     */
    int updateStockInWarehouse(String email,String sku,int warehouseid,int qty);
    
    /**
     * 获取指定分销商，指定商品的微仓平均采购价
     * @param email
     * @param sku
     * @return
     */
    float getAvgPurchasePrice(String email, String sku);
    
    /**
     * 按先进先出的原则，获取指定分销商，指定产品，指定库存的库存信息
     * @param email
     * @param sku
     * @param warehouseid
     * @return
     */
    List<FirstInFirstOutDisIvyInfo> getFirstInFirstOutDisIvyInfo(String email,String sku,int warehouseid);
    
    /**
     * 直接扣除微仓中某个记录的库存（用于先进先出库存扣除的情况）
     * @param id
     * @param qty
     * @return
     */
    int updateStockInDisIvyById(int id,int qty);
    
}