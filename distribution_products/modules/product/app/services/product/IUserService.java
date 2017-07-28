package services.product;


public interface IUserService {

	/**
	 * 获取分销商账号
	 * 
	 * @author zbc
	 * @since 2016年10月17日 下午5:51:15
	 */
	String getDisAccount();

	/**
	 * 获取后台用户账号
	 * 
	 * @author zbc
	 * @since 2016年10月17日 下午5:51:57
	 */
	String getAdminAccount();

	/**
	 * 获取业务员关联的分销商账号
	 * 
	 * @author zbc
	 * @since 2016年10月17日 下午7:16:12
	 */
	String getAccounts();

	/**
	 * 获取分销商
	 * @author zbc
	 * @since 2016年10月17日 下午7:22:45
	 */
	String getDismember();
	
}
