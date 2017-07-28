package service.timer;

/**
 * 账期服务接口
 * @author zbc
 * 2017年2月17日 下午6:07:31
 */
public interface IAccountPeriodService {

	/**
	 * 新增账期
	 * @author zbc
	 * @since 2017年2月17日 下午6:22:35
	 */
	String dealAccountPeriod();

	/**
	 * 
	 * @author zbc
	 * @since 2017年4月1日 下午2:31:05
	 */
	Boolean generBill(String string, String createUser);

	/**
	 * 
	 * @author zbc
	 * @since 2017年4月1日 下午2:38:18
	 */
	boolean delBill(Integer id);

	/**
	 * 
	 * @author zbc
	 * @since 2017年4月1日 下午2:43:29
	 */
	boolean nextSlave(String string, String creater);
	
}
