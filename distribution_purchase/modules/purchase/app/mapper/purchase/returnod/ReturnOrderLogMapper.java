package mapper.purchase.returnod;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.purchase.returnod.ReturnOrderLog;

public interface ReturnOrderLogMapper {
	int insert(ReturnOrderLog record);

	int batchInsert(List<ReturnOrderLog> records);

	List<ReturnOrderLog> selectByReturnOrderNo(@Param("returnOrderNo")String returnOrderNo);

	List<ReturnOrderLog> selectByReturnOrderNoList(@Param("returnOrderNoList")List<String> returnOrderNoList);
}
