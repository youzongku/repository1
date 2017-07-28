package mapper.purchase.returnod;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.purchase.returnod.ReturnAmountCoefficientLog;
/**
 * 设置商品退款系数的日志记录
 * @author huangjc
 * @date 2017年2月14日
 */
public interface ReturnAmountCoefficientLogMapper {
    int insert(ReturnAmountCoefficientLog log);
    int batchInsert(List<ReturnAmountCoefficientLog> logs);
    List<ReturnAmountCoefficientLog> selectByCoefficientId(@Param("coefficientId")Integer coefficientId);
    List<ReturnAmountCoefficientLog> selectBySkuAndWarehouseId(@Param("sku")String sku, @Param("warehouseId")Integer warehouseId);
}