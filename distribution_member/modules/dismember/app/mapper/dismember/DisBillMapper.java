package mapper.dismember;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import entity.dismember.DisBill;

public interface DisBillMapper extends BaseMapper<DisBill> {

	List<DisBill> queryBills(@Param("param")Map<String, Object> params);

	Integer queryBillsTotal(@Param("param")Map<String, Object> params);
	
	List<DisBill> queryBill(DisBill bill);
	
	int insertSelective(DisBill record) ;
}