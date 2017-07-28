package mapper.timer;

import java.util.List;
import java.util.Map;

import entity.timer.SaleDetail;
import entity.timer.SalesToB2cDetail;

public interface SaleDetailMapper extends BaseMapper<SaleDetail> {

	public List<SaleDetail> selectByOrderId(Integer orderId);

	public List<SalesToB2cDetail> getDetails(Integer id);

	public Map<String, Object> getAmount(Integer salesorderid);

}