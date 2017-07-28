package mapper.product_inventory;

import java.util.List;

import dto.product_inventory.ProductMicroInventoryInRecordDto;
import entity.product_inventory.ProductInventoryDetail;
import entity.product_inventory.ProductMicroInventoryInRecord;
import org.apache.ibatis.annotations.Param;

public interface ProductMicroInventoryInRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProductMicroInventoryInRecord record);

    int insertSelective(ProductMicroInventoryInRecord record);

    ProductMicroInventoryInRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProductMicroInventoryInRecord record);

    int updateByPrimaryKey(ProductMicroInventoryInRecord record);

	List<ProductMicroInventoryInRecord> selectMicroInRecordListByParam(
			ProductMicroInventoryInRecord micrInventoryInRecordParam);

	List<ProductMicroInventoryInRecord> selectMicroInRecordListForDeduct(
			ProductMicroInventoryInRecord microInRecordParam);

    List<ProductMicroInventoryInRecord> listMicroInRecordList(
            @Param("paramDto") ProductMicroInventoryInRecordDto productMicroInventoryInRecordDto);

    /**
     * 查询满足条件的采购记录用来更新剩余量
     * @param microInRecordParam
     * @return
     */
	ProductMicroInventoryInRecord selectForUpdateResidueNum(ProductMicroInventoryInRecord microInRecordParam);

	/**
	 * 查询剩余量大于0的入库记录用来释放
	 * @param microInRecordParam
	 * @return
	 */
	List<ProductMicroInventoryInRecord> selectMicroInRecordListForRelease(
			ProductMicroInventoryInRecord microInRecordParam);

	/**
	 * 历史订单数据关闭查询该订单该sku是否有入库记录
	 * @param inRecordParam
	 * @return
	 */
	ProductMicroInventoryInRecord selectInRecordForHistoryOrderClosed(ProductMicroInventoryInRecord inRecordParam);

	/**
	 * 获取指定sku，仓库id，过期日期商的品囤货信息
	 * @param inRecord
	 * @return
	 */
	List<ProductMicroInventoryInRecord> getMicroInventoryStockpile(ProductMicroInventoryInRecord inRecord);

	/**
	 * 指定sku，仓库id商品的囤货数量
	 * @param warehouseId
	 * @param sku
	 * @return
	 */
	int getTotalMicroInventoryStockpile(@Param("warehouseId")Integer warehouseId, @Param("sku")String sku);

	int getTotalMicroInventoryStockpileByExpiration(ProductInventoryDetail paramDetail);

}