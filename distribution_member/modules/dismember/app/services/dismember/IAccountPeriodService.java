package services.dismember;

import java.util.List;
import java.util.Map;

import dto.dismember.ApBillDto;
import dto.dismember.ApReminderSettingParam;
import dto.dismember.ResultDto;
import entity.dismember.AccountPeriodMaster;
import entity.dismember.AccountPeriodSlave;
import entity.dismember.ApOptRecord;
import entity.dismember.ApReminderSetting;
import entity.dismember.OrderByAp;
import vo.dismember.Page;

/**
 * 账期服务接口
 * @author zbc
 * 2017年2月17日 下午6:07:31
 */
public interface IAccountPeriodService {
	
	/**
	 * 刷数据使用-获取账期支付的订单的详情
	 * @return 
	 */
	ResultDto<?> brushApOrderDetail();
	
	/**
	 * 设置账期短信提醒设置
	 * @param id 设置id
	 * @param enable 是否开启短信提醒
	 * @param daysAgo 天数
	 * @return
	 */
	Map<String,Object> setReminderSetting(ApReminderSettingParam rsParam);
	
	/**
	 * 获取账期短信提醒设置
	 * @param account 分销商
	 * @return
	 */
	ApReminderSetting getReminderSetting(String account);

	/**
	 * 新增账期
	 * @author zbc
	 * @since 2017年2月17日 下午6:22:35
	 */
	ResultDto<?> addAccountPeriod(String string);
	
	/**
	 * 次日生效操作
	 * @author zbc
	 * @since 2016年11月20日 下午6:30:41
	 */
	String dealAccountPeriod();


	/**
	 * 更新账期信息
	 * @author zbc
	 * @since 2017年2月26日 上午11:34:02
	 */
	ResultDto<?> updateMaster(String string);


	/**
	 * 更新子账期信息
	 * @author zbc
	 * @since 2017年2月26日 上午11:34:07
	 */
	ResultDto<?> updateSlave(String string);


	/**
	 * 获取账期信息
	 * @author zbc
	 * @since 2017年2月26日 下午12:11:36
	 */
	ResultDto<AccountPeriodMaster> readMasterOne(Integer id);

	/**
	 * 分页查询账期信息
	 * @author zbc
	 * @since 2017年2月26日 下午12:11:42
	 */
	ResultDto<Page<AccountPeriodMaster>> readMasterList(String string);

	/**
	 * 分页查询子账期
	 * @author zbc
	 * @since 2017年2月26日 下午3:57:51
	 */
	ResultDto<Page<AccountPeriodSlave>> readSlaveList(String string);

	/**
	 * 
	 * @author zbc
	 * @since 2017年2月27日 下午12:27:20
	 */
	List<ApOptRecord> readMasterRecord(Integer id);
	
	/**
	 * 
	 * @author zbc
	 * @since 2017年2月27日 下午12:27:20
	 */
	List<ApOptRecord> readSlaveRecord(Integer id);


	/**
	 * 
	 * @author zbc
	 * @since 2017年2月27日 下午6:29:20
	 */
	ResultDto<?> nextSlave(String string,String creater);


	/**
	 * 
	 * @author zbc
	 * @since 2017年2月28日 下午3:25:44
	 */
	AccountPeriodMaster getAccountPeriod(String email);


	/**
	 * 
	 * @author zbc
	 * @since 2017年3月1日 下午5:53:22
	 */
	List<String> getStartDate(Integer id);


	/**
	 * 查询子账期
	 * @author zbc
	 * @since 2017年3月3日 下午2:51:05
	 */
	ResultDto<AccountPeriodSlave> readSlaveOne(Integer id);

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月3日 下午5:52:24
	 */
	ResultDto<AccountPeriodSlave> nextSlave(Integer id);

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月4日 上午10:04:38
	 */
	ResultDto<?> disabled(Integer id);

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月4日 下午3:52:58
	 */
	ResultDto<Page<OrderByAp>> readOrderList(String string);

	/**
	 * 账单生成
	 * @author zbc
	 * @since 2017年3月5日 上午10:36:13
	 */
	ResultDto<ApBillDto> generBill(String string,String createUser,boolean isAuto);

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月6日 下午2:44:39
	 */
	ResultDto<ApBillDto> readBill(Integer id);

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月6日 下午3:58:44
	 */
	ResultDto<?> delBill(Integer id);

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月6日 下午5:38:12
	 */
	ResultDto<?> chargeOff(String jsonStr,String operator);

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月7日 上午11:57:43
	 */
	AccountPeriodSlave getCurAp(String email);

	/**
	 * @author zbc
	 * @since 2017年6月8日 上午11:17:50
	 * @param string
	 * @param admin
	 * @return
	 */
	 ResultDto<?> adjust(String string, String admin);

}
