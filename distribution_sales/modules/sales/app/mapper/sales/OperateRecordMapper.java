package mapper.sales;

import java.util.List;
import java.util.Map;

import entity.sales.OperateRecord;

public interface OperateRecordMapper{

	List<OperateRecord> select(Map<String, Object> map);
	List<OperateRecord> selectByOrderIdList(List<Integer> orderIdList);
	
    int insert(OperateRecord record);

    int updateByOrderId(OperateRecord record);

	int deleteByOrderId(Integer orderId);

    List<OperateRecord> findOperateRecordByCondition(OperateRecord record);
    
    int insertSelective(OperateRecord record);
}
