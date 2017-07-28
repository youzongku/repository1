package mapper.sales;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.sales.Receiver;

public interface ReceiverMapper extends BaseMapper<Receiver> {
	
  public Receiver selectByOrderId(Integer orderId);
  
  public List<Receiver> query(@Param("account")String account, @Param("searchText")String searchText);
  public List<Receiver> queryAllByAccount(@Param("account")String account);
}