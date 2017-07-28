package services.dismember;

import java.io.File;
import java.util.List;
import java.util.Map;

import entity.dismember.DisBill;

/**
 * Created by LSL on 2016/1/5.
 */
public interface IDisBillService {

    /**
     * 根据邮件查询该账户的交易记录
     * @param string
     * @return
     */
	Map<String,Object> getPagedBills(String string,String email,List<String> accounts);

    /**
     * 查询交易记录，不分页
     * @param params
     * @return
     */
    List<DisBill> queryBills(Map<String, Object> params);

	Map<String, Object> createBill(DisBill bill);

	int save(DisBill bill);

	Map<String,Object> getBill(Integer id);

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月16日 上午9:54:43
	 */
	File export(Map<String, String[]> map,String[] header, String email, List<String> accounts);
	
}
