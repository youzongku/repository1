package mapper.ap;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.ap.ApOrderDetail;

public interface ApOrderDetailMapper {

	int insert(ApOrderDetail record);

	int insertBatch(List<ApOrderDetail> records);

	ApOrderDetail selectByPrimaryKey(Integer id);

	List<ApOrderDetail> selectByApOrderId(@Param("apOrderId") Integer apOrderId);

	List<ApOrderDetail> selectByOrderNo(@Param("orderNo") String orderNo);

}