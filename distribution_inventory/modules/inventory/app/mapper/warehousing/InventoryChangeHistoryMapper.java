package mapper.warehousing;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import dto.warehousing.InventoryChangeHistoryDto;
import dto.warehousing.WarehouseChangeRecordDto;
import entity.warehousing.InventoryChangeHistory;
import forms.warehousing.InventoryChangeHistoryForm;

public interface InventoryChangeHistoryMapper {
	/**
	 * 新增
	 * 
	 * @param ih
	 * @return
	 */
	int insert(InventoryChangeHistory changeHistory);

	int insertSelective(InventoryChangeHistory changeHistory);
	
	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	int delete(Integer id);

	/**
	 * 查询
	 * 
	 * @param dto
	 * @return
	 */
	List<InventoryChangeHistory> selectByParamDto(@Param("paramDto") InventoryChangeHistoryDto dto);

	/**
	 * 修改（更新）
	 * 
	 * @param listingID
	 * @return
	 */
	int update(InventoryChangeHistory changeHistory);

	List<InventoryChangeHistoryForm> queryByParam(@Param("param") WarehouseChangeRecordDto dto);

	int getTotal(@Param("param") WarehouseChangeRecordDto dto);
	
	/**
	 * 获取指定SKU，仓库的出仓记录总数
	 */
	Integer getStockOutTotalQty(String sku,int warehouseId);

	/**
	 * 查询
	 *
	 * @param dto
	 * @return
	 */
	List<InventoryChangeHistory> selectByParam(@Param("paramDto") InventoryChangeHistoryDto dto);

	List<InventoryChangeHistory> query(@Param("warehouseId")Integer warehouseId, @Param("sku") String sku, @Param("account") String account);
}