package mapper.purchase.returnod;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.purchase.returnod.ReturnAmountCoefficient;

public interface ReturnAmountCoefficientMapper {
	int insert(ReturnAmountCoefficient record);
	int batchInsert(List<ReturnAmountCoefficient> records);
	ReturnAmountCoefficient selectBySkuWarehouseId(@Param("sku") String sku,@Param("warehouseId") Integer warehouseId);
	int updateByConditions(ReturnAmountCoefficient record);
	int batchUpdateByConditions(List<ReturnAmountCoefficient> records);
}