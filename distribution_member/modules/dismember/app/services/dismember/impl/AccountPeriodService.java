package services.dismember.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;

import constant.dismember.Constant;
import dto.dismember.ApBillDto;
import dto.dismember.ApReminderSettingParam;
import dto.dismember.ResultDto;
import dto.dismember.Search;
import entity.ap.ApOrderDetail;
import entity.dismember.AccountPeriodMaster;
import entity.dismember.AccountPeriodSlave;
import entity.dismember.ApBill;
import entity.dismember.ApBillOrderMapping;
import entity.dismember.ApOptRecord;
import entity.dismember.ApReminderSetting;
import entity.dismember.ApReminderSettingLog;
import entity.dismember.DisAccount;
import entity.dismember.OrderByAp;
import events.dismember.DefaultAccountPeriodReminderEvent;
import mapper.ap.ApOrderDetailMapper;
import mapper.dismember.AccountPeriodMasterMapper;
import mapper.dismember.AccountPeriodSlaveMapper;
import mapper.dismember.ApBillMapper;
import mapper.dismember.ApBillOrderMappingMapper;
import mapper.dismember.ApOptRecordMapper;
import mapper.dismember.ApReminderSettingLogMapper;
import mapper.dismember.ApReminderSettingMapper;
import mapper.dismember.DisAccountMapper;
import mapper.dismember.OrderByApMapper;
import play.Logger;
import play.libs.Json;
import services.base.utils.JsonFormatUtils;
import services.dismember.IAccountPeriodService;
import services.dismember.IHttpService;
import utils.dismember.DateUtils;
import utils.dismember.JsonCaseUtil;
import vo.dismember.Page;

/**
 * 账期服务接口实现类
 * @author zbc
 * 2017年2月17日 下午6:09:38
 */
public class AccountPeriodService implements IAccountPeriodService {
	
	@Inject
	private LoginService loginService;
	@Inject
	private AccountPeriodMasterMapper accountPeriodMasterMapper;
	@Inject
	private AccountPeriodSlaveMapper accountPeriodSlaveMapper;
	@Inject
	private ApOptRecordMapper recordMapper;
	@Inject
	private DisAccountMapper accountMapper;
	@Inject
	private OrderByApMapper orderByApMapper;
	@Inject
	private ApBillMapper billMapper;
	@Inject
	private ApBillOrderMappingMapper billOrderMappingMapper;
	@Inject
	private ApReminderSettingMapper reminderSettingMapper;
	@Inject
	private ApReminderSettingLogMapper reminderSettingLogMapper;
	@Inject
	private EventBus ebus;
	@Inject
	private IHttpService httpService;
	@Inject
	private ApOrderDetailMapper apOrderDetailMapper;
	
	private static String CREATE_USER = "system";
	
	@Override
	public ApReminderSetting getReminderSetting(String account) {
		return reminderSettingMapper.selectByAccount(account);
	}
	
	@Override
	public Map<String,Object> setReminderSetting(ApReminderSettingParam rsParam){
		Map<String, Object> result = Maps.newHashMap();
		// 开启情况下，天数判断
		if (rsParam.getEnable() && rsParam.getDaysAgo() < 1) {
			result.put("suc", false);
			result.put("msg", "正在开启账期短信提醒功能，天数不能小于1天");
			return result;
		}
		
		// 查询是否存在配置
		ApReminderSetting setting = reminderSettingMapper.selectByAccount(rsParam.getAccount());
		if (setting==null) {
			// 查询操作
			// 插入一条新的配置
			ApReminderSetting newRS = new ApReminderSetting(rsParam.getAccount(), rsParam.getEnable(), rsParam.getDaysAgo(), rsParam.getOptUser());
			boolean suc = reminderSettingMapper.insertSelective(newRS)==1;
			
			addReminderSettingLog(newRS,suc);
			
			Logger.info("新增账期短信提醒，结果：{}",suc);
			result.put("suc", suc);
			result.put("msg", suc?"设置成功":"设置失败");
			return result;
		}
		
		// 刚好一条记录，就更新它
		ApReminderSetting updateSetting = new ApReminderSetting();
		updateSetting.setId(setting.getId());
		updateSetting.setEnable(rsParam.getEnable());
		if (rsParam.getEnable()) {// 启用的才更新天数
			updateSetting.setDaysAgo(rsParam.getDaysAgo());
		}
		updateSetting.setLastUpdateUser(rsParam.getOptUser());
		Logger.info("更新账期提醒参数 in service：{}",updateSetting);
		boolean suc = reminderSettingMapper.updateByPrimaryKeySelective(updateSetting)==1;
		
		addReminderSettingLog(updateSetting,suc);
		
		Logger.info("更新账期短信提醒，结果：{}",suc);
		result.put("suc", suc);
		result.put("msg", suc?"设置成功":"设置失败");
		return result;
	}
	
	private void addReminderSettingLog(ApReminderSetting rs, boolean suc){
		String createUser = rs.getLastUpdateUser();
		if (StringUtils.isEmpty(createUser)) {
			createUser = rs.getCreateUser();
		}
		ApReminderSettingLog log = new ApReminderSettingLog(rs.getId(), rs.getEnable(), rs.getDaysAgo(), createUser,
				suc);
		reminderSettingLogMapper.insert(log);
	}

	/**
	 *第一个生成的账期，隔日生效
	 * 账期已完结可以开启下一个账期，也可以新增一条账期
	 * 账期逾期标准：<em style="color:red" > 过红线账期不   核销</em> 逾期要冻结账户
	 * 核销只能核销一次，未选流到下一期,核销完才能开启下一个账期，该账期完结
	 * <br>
	 * <br> <b>校验规则：</b>
	 * <ol>
	 * <li>查询是否已经存在账期
	 * <ul>
	 * <li>是否禁用：<em style="color:red">是?可以新建:(本接口不支持)只能在原有的账期基础上开启下一期</em>
	 * <li><em style="color:red">是：可以新建</em>
	 * 
	 */
	@Override
	public ResultDto<?> addAccountPeriod(String string) {
		try {
			JsonNode json = Json.parse(string);
			JsonNode master = json.get("master");
			JsonNode slave = json.get("slave");
			String account = JsonCaseUtil.jsonToString(master.get("account"));
			DisAccount  disAccount = accountMapper.getDisAccountByEmail(account);
			if (disAccount != null && disAccount.isFrozen()) {
				return new ResultDto<>(false, "该用户账户冻结，无法开启账期");
			}
			
			// 要加校验 判断该分销商是否有正在使用的账期
			if (hasAccountPeriodWorking(account)) {
				return new ResultDto<>(false, "存在未完结账期，不能进行添加操作");
			}
			
			String createUser = loginService.getLoginContext(2).getEmail();
			//保存父表信息
			AccountPeriodMaster apMaster = new AccountPeriodMaster();
			apMaster.setAccount(account);// 分销商用户
			apMaster.setTotalLimit(JsonCaseUtil.jsonToBigDecimal(master.get("totalLimit")));// 账期额度
			apMaster.setContractNo(JsonCaseUtil.jsonToString(master.get("contractNo")));// 合同号
			apMaster.setCreateUser(createUser);// 创建人
			apMaster.setDutyOfficer(JsonCaseUtil.jsonToString(master.get("dutyOfficer")));// 责任人
			apMaster.setPeriodType(JsonCaseUtil.jsonToInteger(master.get("periodType")));// 周期类型
			apMaster.setPeriodLength(JsonCaseUtil.jsonToInteger(master.get("periodLength")));// 周期长度
			apMaster.setOaAuditCode(JsonCaseUtil.jsonToString(master.get("oaAuditCode")));
			accountPeriodMasterMapper.insertSelective(apMaster);
			
			//插入新增日志
			createRecord(createUser, ApOptRecord.CREATE,"创建账期", null, apMaster.getId());
			
			//创建第一条账期记录
			AccountPeriodSlave apSlave = new AccountPeriodSlave();
			apSlave.setTotalLimit(apMaster.getTotalLimit());//额度
			apSlave.setMasterId(apMaster.getId());//主表id
			apSlave.setCreateUser(createUser);//创建人
			apSlave.setStartTime(JsonCaseUtil.jsonStrToDate(slave.get("startTime"), DateUtils.FORMAT_DATE_PAGE));
			//目前开始时间就是业绩周期开始时间
			apSlave.setPerformanceStartTime(apSlave.getStartTime());//业绩周期开始时间
			apSlave.setRedLineDays(JsonCaseUtil.jsonToInteger(slave.get("redLineDays")));//红线时间
			apSlave.setState(Constant.AP_NOT_START);//未生效
			//合同账期
			apSlave.setContractPeriodDate(getDateWithType(apSlave.getPerformanceStartTime(), apMaster.getPeriodType(),
					apMaster.getPeriodLength()));
			//月结是按照自然月为结算周期 
			//所以前台传业绩周期结束时间是无效的
			if (apMaster.getPeriodType() == AccountPeriodMaster.PERIOD_TYPE_MONTH_STATEMENT) {
				// 月底结算
				apSlave.setPerformanceEndTime(DateUtils.dateAddMonths(apSlave.getPerformanceStartTime(), 1));
				// 固定月结 业绩周期结束时间 开始算 合同账期
				apSlave.setContractPeriodDate(getDateWithType(apSlave.getPerformanceEndTime(), apMaster.getPeriodType(),
						apMaster.getPeriodLength()));
			}
			// 目前自然月类型业绩周期结束时间必须是合同账期，不然会出现周期混乱
			else if (apMaster.getPeriodType() == AccountPeriodMaster.PERIOD_TYPE_MONTH){
				apSlave.setPerformanceEndTime(apSlave.getContractPeriodDate());
			} else {
				apSlave.setPerformanceEndTime(
						JsonCaseUtil.jsonStrToDate(slave.get("performanceEndTime"),DateUtils.FORMAT_DATE_PAGE ));
			}
			// 业绩周期结束时间 不大于合同账期
			if (apSlave.getPerformanceEndTime().after(apSlave.getContractPeriodDate())) {
				apSlave.setPerformanceEndTime(apSlave.getContractPeriodDate());
			}
			// 红线时间
			apSlave.setRedLineDate(DateUtils.dateAddDays(apSlave.getContractPeriodDate(), apSlave.getRedLineDays()));
			if (apSlave.getPerformanceStartTime().before(new Date())) {
				apSlave.setState(Constant.AP_AVAILABLE);
			}
			accountPeriodSlaveMapper.insertSelective(apSlave);
			// 插入新增日志
			createRecord(createUser, ApOptRecord.CREATE,"创建一条子账期", apSlave.getId(), null);
			
			// 默认开启账期提醒，时间为3天，异步的
			ebus.post(new DefaultAccountPeriodReminderEvent(apMaster.getAccount(), apMaster.getCreateUser()));
			
			return new ResultDto<>(true, "新增账期成功");
		} catch (Exception e) {
			Logger.info("新增账期异常"+e);
			return new  ResultDto<>(false, "新增账期异常");
		}
	}
	
	/**
	 * 根据周期类型长度获取日期
	 * @param startDate    开始时间
	 * @param periodType   周期类型 日，月
	 * @param length       周期长度
	 * @author zbc
	 * @since 2017年2月25日 下午3:27:43
	 */
	private static Date getDateWithType(Date startDate,int periodType,Integer length){
		switch (periodType) {
		case AccountPeriodMaster.PERIOD_TYPE_DATE:
			return DateUtils.dateAddDays(startDate, length);
		case AccountPeriodMaster.PERIOD_TYPE_MONTH:
			return DateUtils.dateAddMonths(startDate, length);
		case AccountPeriodMaster.PERIOD_TYPE_MONTH_STATEMENT:
			return DateUtils.dateAddDays(startDate, length);
		default:
			return null;
		}
	}
	
	/**
	 * 是否有正在使用的账期
	 * @author zbc
	 * @since 2017年2月20日 下午12:23:31
	 */
	private boolean hasAccountPeriodWorking(String account) {
		List<AccountPeriodSlave> list = accountPeriodSlaveMapper.getAccountPeriods(account);
		if (CollectionUtils.isEmpty(list)) {
			return false;
		}
		
		for (AccountPeriodSlave el : list) {
			if (el.getState() != Constant.AP_FINISHED) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 账期未生效才可以修改
	 * @author zbc
	 * @since 2017年2月20日 下午12:23:31
	 */
	private boolean validUpDateCheck(Integer id) {
		List<AccountPeriodSlave> list = accountPeriodSlaveMapper.getAccountPeriodsByMasterId(id);
		if (CollectionUtils.isEmpty(list)) {
			return true;
		}
		
		for (AccountPeriodSlave el : list) {
			if (el.getState() != Constant.AP_NOT_START) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ResultDto<AccountPeriodMaster> readMasterOne(Integer id) {
		return  new ResultDto<>(100,"",accountPeriodMasterMapper.selectByPrimaryKey(id));
	}

	/**
	 * 重做，分页查询账期父记录
	 * 分页查询
	 */
	@Override
	public ResultDto<Page<AccountPeriodMaster>> readMasterList(String string) {
		try {
			Search search = JsonFormatUtils.jsonToBean(string, Search.class);
			List<AccountPeriodMaster> list = accountPeriodMasterMapper.pageSearch(search);
			Integer rows = accountPeriodMasterMapper.pageCount(search);
			return new ResultDto<>(100,"分页查询成功",
					new Page<AccountPeriodMaster>(search.getCurrPage(), search.getPageSize(), rows,list));
		} catch (Exception e) {
			Logger.info("分页查询异常"+e);
			return new ResultDto<>(false, "分页查询异常，请检查参数");
		}
	}
	
	/**
	 * 重做，分页查询账期记录
	 * 分页查询
	 */
	@Override
	public ResultDto<Page<AccountPeriodSlave>> readSlaveList(String string) {
		try {
			Search dto = JsonFormatUtils.jsonToBean(string, Search.class);
			List<AccountPeriodSlave> list = accountPeriodSlaveMapper.pageSearch(dto);
			Integer rows = accountPeriodSlaveMapper.pageCount(dto);
			return new ResultDto<>(100,"分页查询成功",
					new Page<AccountPeriodSlave>(dto.getCurrPage(), dto.getPageSize(), rows,list));
		} catch (Exception e) {
			Logger.info("分页查询异常"+e);
			return new ResultDto<>(false, "分页查询异常，请检查参数");
		}
	}


	/**
	 * 状态变更自动任务<br>
	 * 1、账期 开始时间次日      未生效 -> 可使用<br>
	 * 2、账期 过合同账期          可使用 -> 待还款  如果未使用订单，则自动变为已完结<br>
	 * 3、账期过了红线账期       待还款 -> 已逾期<br>
	 * 4、账期过了红线账期      禁用中 -> 已逾期<br>
	 * 5、账期已逾期如果订单为0   逾期 ->完结  <br>
	 * 如果到了合同账期，自动生成账单和开启下一账期<br>
	 * TODO 修改为 业绩周期结束生成账单
 	 * 
	 *               
	 */
	@Override
	public String dealAccountPeriod() {
		try {
			// TODO 业绩周期结束的时候可以自动生成账单，账单订单时业绩周期内的
			//获取所有可使用的账期
			List<AccountPeriodSlave> list = accountPeriodSlaveMapper.getNeedHandleApByState(Constant.AP_AVAILABLE,null);
			list.forEach(ele->{
				if(!ele.getHasBill()&&new Date().after(DateUtils.dateAddDays(ele.getPerformanceEndTime(), 1))){
					Search search = new Search(true, ele.getId());
					// 如果状态为待还款，自动生成账单，开启下一期
					List<OrderByAp> orders = orderByApMapper.pageSearchAuto(search);
					// 校验有没有逾期的
					if (orders.size() > 0){
						List<Integer> orderIds =  Lists.transform(orders, e->e.getId());
						Map<String,Object> map = Maps.newHashMap();
						map.put("id", ele.getId());
						map.put("orderIds", orderIds);
						map.put("isAll", true);
						ResultDto<ApBillDto> generBillResult = generBill(Json.toJson(map).toString(), CREATE_USER, true);
						Logger.info("[{}]自动生成账单结果:[{}]",ele.getAccount(),generBillResult);
					}
				}
			});
			//1
			dealApByState(Constant.AP_NOT_START);
			//2
			dealApByState(Constant.AP_AVAILABLE);
			//3
			dealApByState(Constant.AP_FOR_REFUND);
			//4
			dealApByState(Constant.AP_DISABLE_THE);
			//5
			dealApByState(Constant.AP_OVERDUE);
			return "SUCCESS";
		} catch (Exception e) {
			Logger.info("批量更新订单状态异常"+e);
			return " FAILURE";
		}
	}

	/**
	 * 
	 * @author zbc
	 * @since 2017年2月23日 下午5:20:10
	 */
	private void dealApByState(Integer state) {
		List<AccountPeriodSlave> list = accountPeriodSlaveMapper.getNeedHandleApByState(state,new Date());
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		
		Integer nextState = state + 1;
		Date updateDate = new Date();
		list.forEach(apSlave -> {
			if (apSlave.getOrderQty() == 0) {
				apSlave.setState(state >= Constant.AP_AVAILABLE ? Constant.AP_FINISHED : nextState);
			} else {
				switch (state) {
				case Constant.AP_DISABLE_THE:
					apSlave.setState(Constant.AP_OVERDUE);
					break;
				case Constant.AP_OVERDUE:
					apSlave.setState(Constant.AP_OVERDUE);
					break;
				case Constant.AP_FOR_REFUND:
					if (apSlave.getRedLineDate().before(new Date())) {
						apSlave.setState(Constant.AP_OVERDUE);
					}
					break;
				default:
					apSlave.setState(nextState);
					break;
				}
			}
			// 如果状态没有变更 则不做更新操作
			if (state != apSlave.getState()) {
				apSlave.setUpdateDate(updateDate);
				if (accountPeriodSlaveMapper.updateByPrimaryKeySelective(apSlave) > 0) {
					Logger.info("========[{}]账期更新状态为:[{}]======", apSlave.getAccount(),
							Constant.ACCOUNT_PERIOD_STATU_MAP.get(apSlave.getState()));
					// 如果账期逾期，账户冻结
					if (apSlave.getState() == Constant.AP_OVERDUE) {
						DisAccount account = accountMapper.getDisAccountByEmail(apSlave.getAccount());
						account.setFrozen(true);
						accountMapper.updateByPrimaryKeySelective(account);
					}
				}
			}
			if (!apSlave.getHasNext() && apSlave.getState() == Constant.AP_FOR_REFUND) {
			/*	Search search = new Search(true, apSlave.getId());
				// 如果状态为待还款，自动生成账单，开启下一期
				List<OrderByAp> orders = orderMapper.pageSearchAuto(search);
				// 校验有没有逾期的
				if (orders.size() > 0){
					ObjectNode newObject = Json.newObject()
							.put("id", apSlave.getId()).put("isAll", true);
					ResultDto<ApBillDto> generBillResult = generBill(newObject.toString(), CREATE_USER, true);
					Logger.info("[{}]自动生成账单结果:[{}]",apSlave.getAccount(),generBillResult);
				}*/
				if (validAutoNext(apSlave.getMasterId())) {
					int days = DateUtils.daysInterval(apSlave.getPerformanceStartTime(), apSlave.getPerformanceEndTime());
					String performanceEndTime = DateUtils.date2string(
							DateUtils.dateAddDays(apSlave.getPerformanceEndTime(), days + 1),
							DateUtils.FORMAT_DATE_PAGE);
					ObjectNode newObject = Json.newObject()
							.put("id", apSlave.getId())
							.put("redLineDays", apSlave.getRedLineDays())
							.put("performanceEndTime", performanceEndTime);
					ResultDto<?> nextSlaveResult = nextSlave(newObject.toString(), CREATE_USER);
					Logger.info("[{}]自动开启下一账期结果:[{}]",apSlave.getAccount(), nextSlaveResult);
				}
			}
		});
	}

	
	/**
	 * 校验是否可以自动开启下一张期
	 * 如果有已逾期账期，不能开启下一账期
	 * @author zbc
	 * @since 2017年3月31日 下午2:47:20
	 */
	private boolean validAutoNext(Integer masterId){
		List<AccountPeriodSlave> slaves = accountPeriodSlaveMapper.getAccountPeriodsByMasterId(masterId);
		for(AccountPeriodSlave slave:slaves){
			if(slave.getState() == Constant.AP_OVERDUE){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @author zbc
	 * @since 2017年2月27日 上午9:17:42
	 */
	@Override
	public ResultDto<?> updateMaster(String string) {
		try {
			JsonNode json = Json.parse(string);
			Integer id = JsonCaseUtil.jsonToInteger(json.get("id"));
			AccountPeriodMaster master = accountPeriodMasterMapper.selectByPrimaryKey(id);
			if (master == null) {
				return new ResultDto<>(false,"账期不存在");
			}
			
			if (!validUpDateCheck(master.getId())) {
				return new ResultDto<>(false,"已生效账期不可修改");
			}
			
			String operator = loginService.getLoginContext(2).getEmail();
			master.setTotalLimit(JsonCaseUtil.jsonToBigDecimal(json.get("totalLimit")));
			master.setOaAuditCode(JsonCaseUtil.jsonToString(json.get("oaAuditCode")));
			master.setContractNo(JsonCaseUtil.jsonToString(json.get("contractNo")));
			master.setDutyOfficer(JsonCaseUtil.jsonToString(json.get("dutyOfficer")));
			master.setPeriodLength(JsonCaseUtil.jsonToInteger(json.get("periodLength")));
			master.setPeriodType(JsonCaseUtil.jsonToInteger(json.get("periodType")));
			master.setUpdateDate(new Date());
			accountPeriodMasterMapper.updateByPrimaryKey(master);
			String operateDesc = "<div><span>整个账期额度:</span><em>"+master.getTotalLimit()+"</em></div>"+
						  "<div><span>责任人：</span><em>"+master.getDutyOfficer()+"</em></div>"+
						  "<div><span>OA审批单号:</span><em>"+ master.getOaAuditCode()+"</em></div>"+
						  "<div><span>账期合同编号:</span><em>"+master.getContractNo()+"</em></div>"+
						  "<div><span>账期周期类型:</span><em>"+master.getPeriodDesc()+"</em></div>";
			//插入日志
			createRecord(operator, ApOptRecord.UPDATE,operateDesc, null, master.getId());
			
			AccountPeriodSlave slave = accountPeriodSlaveMapper.getAccountPeriodsByMasterId(id).get(0);;
			Date contractPeriodDate = getDateWithType(slave.getPerformanceStartTime(), master.getPeriodType(),master.getPeriodLength());
			slave.setContractPeriodDate(contractPeriodDate);
			if (master.getPeriodType() == AccountPeriodMaster.PERIOD_TYPE_MONTH_STATEMENT) {
				slave.setPerformanceEndTime(DateUtils.dateAddMonths(slave.getPerformanceStartTime(), 1));
				//固定月结是，合同账期从业绩周期结束时间开始
				slave.setContractPeriodDate(//合同账期
						getDateWithType(slave.getPerformanceEndTime(),
								master.getPeriodType(), master.getPeriodLength()));
			// 目前自然月类型业绩周期结束时间必须是合同账期，不然会出现周期混乱
			} else if (master.getPeriodType() == AccountPeriodMaster.PERIOD_TYPE_MONTH) {
				slave.setPerformanceEndTime(slave.getContractPeriodDate());
			}
			slave.setRedLineDate(DateUtils.dateAddDays(slave.getContractPeriodDate(), slave.getRedLineDays()));
			operateDesc = "<div><span>合同账期：</span><em>"+ slave.getContractPeriodDateStr()+"</em></div>";
			slave.setUpdateDate(new Date());
			//如果业绩结束时间大于合同时间，则更新为合同时间
			if (slave.getPerformanceEndTime().after(contractPeriodDate)) {
				slave.setPerformanceEndTime(contractPeriodDate);
				operateDesc += "<div><span>业绩周期结束时间:</span><em>"+slave.getPerformanceEndTimeStr()+"</em></div>";
			}
			accountPeriodSlaveMapper.updateByPrimaryKeySelective(slave);
			createRecord(operator, ApOptRecord.UPDATE,operateDesc, slave.getId(), null);
			return new ResultDto<>(true, "修改账期成功");
		} catch (Exception e) {
			Logger.info("修改账期异常"+e);
			return new ResultDto<>(false,"修改账期异常");
		}
	}

	private void createRecord(String operator, Integer operateType, String operateDesc, 
			Integer slaveId,Integer masterId) {
		recordMapper.insertSelective(new ApOptRecord(operator, operateType, operateDesc, slaveId, masterId));
	}
	
	/**
	 * {
	    	"id":2,
	    	"startTime": "2017-02-25", 
		    "redLineDays": 7, 
		    "performanceEndTime": "2017-02-27"
	    } 
	 */
	@Override
	public ResultDto<?> updateSlave(String string) {
		try {
			JsonNode json = Json.parse(string);
			Integer id = JsonCaseUtil.jsonToInteger(json.get("id"));
			AccountPeriodSlave slave = accountPeriodSlaveMapper.selectByPrimaryKey(id);
			if (slave == null) {
				return new ResultDto<>(false,"账期不存在");
			}
			
			boolean permitted = slave.getState() == Constant.AP_NOT_START;
			if(!permitted){
				return new ResultDto<>(false,"已生效账期不可修改");
			}
			
			AccountPeriodMaster master = accountPeriodMasterMapper.selectByPrimaryKey(slave.getMasterId());
			String operator = loginService.getLoginContext(2).getEmail();
			slave.setStartTime(JsonCaseUtil.jsonStrToDate(json.get("startTime"), DateUtils.FORMAT_DATE_PAGE));
			slave.setPerformanceStartTime(slave.getStartTime());
			slave.setContractPeriodDate(getDateWithType(slave.getPerformanceStartTime(), master.getPeriodType(), master.getPeriodLength()));
			if (master.getPeriodType() == AccountPeriodMaster.PERIOD_TYPE_MONTH_STATEMENT) {
				slave.setPerformanceEndTime(DateUtils.dateAddMonths(slave.getPerformanceStartTime(), 1));
				//固定月结是，合同账期从业绩周期结束时间开始
				slave.setContractPeriodDate(//合同账期
						getDateWithType(slave.getPerformanceEndTime(),
								master.getPeriodType(), master.getPeriodLength()));
			} 
			// 目前自然月类型业绩周期结束时间必须是合同账期，不然会出现周期混乱
			else if (master.getPeriodType() == AccountPeriodMaster.PERIOD_TYPE_MONTH) {
				slave.setPerformanceEndTime(slave.getContractPeriodDate());	
			} else {
				slave.setPerformanceEndTime(//业绩周期结束时间 不大于合同账期
						JsonCaseUtil.jsonStrToDate(json.get("performanceEndTime"),DateUtils.FORMAT_DATE_PAGE ));
			}
			slave.setRedLineDays(JsonCaseUtil.jsonToInteger(json.get("redLineDays")));
			slave.setRedLineDate(//红线时间
					DateUtils.dateAddDays(slave.getContractPeriodDate(), slave.getRedLineDays()));
			slave.setUpdateDate(new Date());
			String operateDesc =
					"<div><span>本账期开始时间:</span><em>"+slave.getStartTimeStr()+"</em></div>"+
					"<div><span>合同账期:</span><em>"+slave.getContractPeriodDateStr()+"</em></div>"+
					"<div><span>红线账期:</span><em>每周期后"+slave.getRedLineDays()+"天</em></div>"+
					"<div><span>业绩周期开始时间:</span><em>"+slave.getPerformanceStartTimeStr()+"</em></div>"+
					"<div><span>业绩周期结束时间:</span><em>"+slave.getPerformanceEndTimeStr()+"</em></div>";
			accountPeriodSlaveMapper.updateByPrimaryKeySelective(slave);
			createRecord(operator, ApOptRecord.UPDATE, operateDesc, slave.getId(), null);
			return new ResultDto<>(true,"更新账期成功");
		} catch (Exception e) {
			Logger.info("修改账期异常==>{}",e);
			return new ResultDto<>(false,"修改账期异常");
		}
	}

	@Override
	public List<ApOptRecord> readMasterRecord(Integer masterId) {
		return recordMapper.selectByMasterId(masterId);
	}

	@Override
	public List<ApOptRecord> readSlaveRecord(Integer slaveId) {
		return recordMapper.selectBySlaveId(slaveId);
	}

	/**
	 *
	 *{
		  "id": 2, 
		  "redLineDays": 8, 
		  "performanceEndTime": "2017-02-27"
		}
	 */
	@Override
	public ResultDto<?> nextSlave(String string,String creater) {
		try {
			JsonNode json = Json.parse(string);
			Integer id = JsonCaseUtil.jsonToInteger(json.get("id"));
			AccountPeriodSlave slave = accountPeriodSlaveMapper.selectByPrimaryKey(id);
			if (slave == null) {
				return new ResultDto<>(false, "账期明细不存在");
			}

			DisAccount account = accountMapper.getDisAccountByEmail(slave.getAccount());
			if (account.isFrozen()) {
				return new ResultDto<>(false, "账户被冻结，无法开启下一期");
			}
			
			if (slave.getState() != Constant.AP_FOR_REFUND && slave.getState() != Constant.AP_FINISHED
					&& slave.getState() != Constant.AP_OVERDUE) {
				return new ResultDto<>(false, "当前状态不能开启下一期");
			}

			if (slave.getHasNext()) {
				return new ResultDto<>(false, "下一期已经开启，请不要重复操作");
			}
			
			Date createDate = new Date();
			//更新 标识 已开启下一期 防止多次开启
			slave.setHasNext(true);
			accountPeriodSlaveMapper.updateByPrimaryKeySelective(slave);
			AccountPeriodMaster master = accountPeriodMasterMapper.selectByPrimaryKey(slave.getMasterId());
			//当前账期没有未核销订单更新为已完结
			if (slave.getOrderQty() == 0) {
				slave.setState(Constant.AP_FINISHED);
			}
			slave.setUpdateDate(createDate);
			accountPeriodSlaveMapper.updateByPrimaryKeySelective(slave);
			//新增账期
			AccountPeriodSlave next = new AccountPeriodSlave();
			next.setStartTime(DateUtils.dateAddDays(slave.getPerformanceEndTime(),1));
			next.setPerformanceStartTime(next.getStartTime());
			next.setTotalLimit(master.getTotalLimit());//额度
			next.setMasterId(master.getId());//主表id
			next.setCreateUser(creater);//创建人
			next.setCreateDate(createDate);
			next.setRedLineDays(JsonCaseUtil.jsonToInteger(json.get("redLineDays")));//红线时间
			next.setState(Constant.AP_NOT_START);//未生效
			//合同账期
			next.setContractPeriodDate(
					getDateWithType(next.getPerformanceStartTime(), master.getPeriodType(), master.getPeriodLength()));
			//固定月结 月底结算
			if (master.getPeriodType() == AccountPeriodMaster.PERIOD_TYPE_MONTH_STATEMENT) {
				next.setPerformanceEndTime(DateUtils.dateAddMonths(next.getPerformanceStartTime(), 1));
				// 合同账期
				// 固定月结是，合同账期从业绩周期结束时间开始
				next.setContractPeriodDate(getDateWithType(next.getPerformanceEndTime(), master.getPeriodType(),
						master.getPeriodLength()));
				
			} 
			// 目前自然月类型业绩周期结束时间必须是合同账期，不然会出现周期混乱
			else if (master.getPeriodType() == AccountPeriodMaster.PERIOD_TYPE_MONTH) {
				next.setPerformanceEndTime(next.getContractPeriodDate());
			} else {
				next.setPerformanceEndTime(// 业绩周期结束时间 不大于合同账期
						JsonCaseUtil.jsonStrToDate(json.get("performanceEndTime"), DateUtils.FORMAT_DATE_PAGE));
			}
			if (next.getPerformanceEndTime().after(next.getContractPeriodDate())) {
				next.setPerformanceEndTime(next.getContractPeriodDate());
			}
			next.setRedLineDate(// 红线时间
					DateUtils.dateAddDays(next.getContractPeriodDate(), next.getRedLineDays()));
			// 标识有上一期
			next.setHasPrev(true);
			// 如果业绩周期开始时间大于当前，立即生效
			if (next.getPerformanceStartTime().before(new Date())) {
				next.setState(Constant.AP_AVAILABLE);
			}
			accountPeriodSlaveMapper.insertSelective(next);
			//开启下一账期日志
			createRecord(creater, ApOptRecord.OPEN_THE_NEXT_ISSUE, "开启下一账期",slave.getId(), null);
			//创建账期日志
			createRecord(creater, ApOptRecord.CREATE, "创建一条子账期", next.getId(), null);
			return new ResultDto<>(true, "开启下一账期成功");
		} catch (Exception e) {
			Logger.info("开启下一账期异常==>{}",e);
			return new ResultDto<>(false, "开启下一账期异常");
		}
	}

	@Override
	public AccountPeriodMaster getAccountPeriod(String email) {
		return accountPeriodMasterMapper.getValidAp(email);
	}

	@Override
	public List<String> getStartDate(Integer id) {
		return accountPeriodSlaveMapper.getStartDate(id);
	}

	@Override
	public ResultDto<AccountPeriodSlave> readSlaveOne(Integer id) {
		return new ResultDto<>(100,null,accountPeriodSlaveMapper.selectByPrimaryKey(id));
	}

	@Override
	public ResultDto<AccountPeriodSlave> nextSlave(Integer id) {
		AccountPeriodSlave slave = accountPeriodSlaveMapper.selectByPrimaryKey(id);
		AccountPeriodSlave next = new AccountPeriodSlave();
		Date startDate = slave.getPerformanceEndTime();
		next.setStartTime(DateUtils.dateAddDays(startDate,1));
		next.setMasterId(slave.getMasterId());
		next.setPerformanceStartTime(next.getStartTime());
		next.setContractPeriodDate(getDateWithType(next.getPerformanceStartTime(), slave.getPeriodType(),slave.getPeriodLength()));
		return new ResultDto<>(100,null,next);
	}

	@Override
	public ResultDto<?> disabled(Integer id) {
		AccountPeriodSlave slave = accountPeriodSlaveMapper.selectByPrimaryKey(id);
		if(slave != null && slave.getState() == Constant.AP_AVAILABLE){
			//如果没有使用订单，直接变为完结
			int state = slave.getOrderQty() != null && slave.getOrderQty() == 0 ? Constant.AP_FINISHED
					: Constant.AP_DISABLE_THE;
			slave.setState(state);
			slave.setUpdateDate(new Date());
			if(accountPeriodSlaveMapper.updateByPrimaryKeySelective(slave)>0){
				createRecord(loginService.getLoginContext(Constant.LOGIN_FROM_MARK_BACK).getEmail(), ApOptRecord.FORBIDDEN, "禁用当前账期", slave.getId(), null);
				return new ResultDto<>(true,"禁用当前账期操作成功");
			}
		}
		return new ResultDto<>(false,"禁用当前账期操作失败");
	}

	@Override
	public ResultDto<Page<OrderByAp>> readOrderList(String string) {
		try {
			Search search = JsonFormatUtils.jsonToBean(string, Search.class);
			List<OrderByAp> orders = orderByApMapper.pageSearch(search);
			Integer rows = orderByApMapper.pageCount(search);
			return new ResultDto<>(100,"分页查询成功",
					new Page<OrderByAp>(search.getCurrPage(), search.getPageSize(), rows,orders));
		} catch (Exception e) {
			Logger.info("分页查询异常"+e);
			return new ResultDto<>(false, "分页查询异常");
		}
	}

	@Override
	public ResultDto<ApBillDto> generBill(String string,String createUser,boolean isAuto) {
		try {
			Search search = JsonFormatUtils.jsonToBean(string, Search.class);
			AccountPeriodSlave slave = accountPeriodSlaveMapper.selectByPrimaryKey(search.getId());
			if(new Date().before(DateUtils.dateAddDays(slave.getPerformanceStartTime(), 1))){
				return new ResultDto<>(false, "业绩周期结束才可以生成账单");
			}
			if (!(slave.getState() == Constant.AP_AVAILABLE||slave.getState() == Constant.AP_FOR_REFUND || slave.getState() == Constant.AP_DISABLE_THE
					|| slave.getState() == Constant.AP_OVERDUE)) {
				return new ResultDto<>(false, "当前状态不能进行该操作");
			}
			
			//重新生成账单，要删除旧的账单
			if (slave.getHasBill()) {
				delBill(slave.getId());
			}
			
			boolean permitted = (search.getIsAll() != null && search.getIsAll())
					|| (search.getOrderIds() != null && search.getOrderIds().size() > 0);
			if (!permitted) {
				return new ResultDto<>(false, "请勾选订单");
			}
			
			//判断是否是自动生成账单
			List<OrderByAp> orders = isAuto ? orderByApMapper.pageSearchAuto(search) : orderByApMapper.pageSearch(search);
			//账单金额
			BigDecimal totalAmount = new BigDecimal(orders.stream().mapToDouble(e->e.getPayAmount().doubleValue()).sum()).setScale(2, BigDecimal.ROUND_HALF_UP);
			//生成账单
			ApBill bill = new ApBill(slave, totalAmount, totalAmount, createUser);
			billMapper.insertSelective(bill);
			orders.forEach(e->{
				e.setIsChoice(OrderByAp.HAVE_BEEN_CHOICE);	
			});
			//更新订单为已选
			orderByApMapper.batchUpdate(orders);
			//维护  账单 订单关系
			List<ApBillOrderMapping> mappings = Lists.transform(orders, e->{
				return new ApBillOrderMapping(e.getId(),bill.getId());
			});
			billOrderMappingMapper.batchInsert(mappings);
			createRecord(createUser, ApOptRecord.GENERATED_BILLS, "生成本期应结款项", slave.getId(), null);
			return new ResultDto<>(100,"生成账单成功",new ApBillDto(slave,orders,bill));
		} catch (Exception e) {
			Logger.info("生成账单异常:{}",e);
			return new ResultDto<>(false, "生成账单异常");
		}
	}

	@Override
	public ResultDto<ApBillDto> readBill(Integer id) {
		AccountPeriodSlave slave = accountPeriodSlaveMapper.selectByPrimaryKey(id);
		if (slave==null) {
			return new ResultDto<>(false ,"账期不存在");
		}
		ApBill bill = billMapper.selectByApId(slave.getId());
		if (bill==null) {
			return new ResultDto<>(false ,"账单不存在");
		}
		
		if(!bill.getIsChargeOff()){
			bill.setRechargeLeft(slave.getRechargeLeft());
		}
		List<OrderByAp> orders = orderByApMapper.selectByBillId(bill.getId());
		return new ResultDto<>(100 ,"查询账单成功",new ApBillDto(slave,orders,bill));
	}

	@Override
	public ResultDto<?> delBill(Integer id) {
		try {
			ApBill bill = billMapper.selectByApId(id);
			if(bill != null){
				List<OrderByAp> orders = orderByApMapper.selectByBillId(bill.getId());
				if (orders.size() > 0) {
					orders.forEach(e -> {
						e.setIsChoice(OrderByAp.HAVE_NOT_CHOICE);
					});
					// 更新订单为已选
					orderByApMapper.batchUpdate(orders);
				}
				billOrderMappingMapper.deleteByBillId(bill.getId());
				billMapper.deleteByPrimaryKey(bill.getId());	
			}
			return new ResultDto<>(true, "删除应结款项成功");
		} catch (Exception e) {
			Logger.info("应结款项失败"+e);
			return new ResultDto<>(false, "删除应结款项失败");
		}
	}

	@Override
	public ResultDto<?> chargeOff(String jsonStr,String operator) {
		try {
			JsonNode json = Json.parse(jsonStr);
			Integer id =  JsonCaseUtil.jsonToInteger(json.get("id"));
			Boolean isForce = JsonCaseUtil.jsonToBoolean(json, "isForce");
			String remark = JsonCaseUtil.jsonToString(json.get("remark"));
			AccountPeriodSlave slave = accountPeriodSlaveMapper.selectByPrimaryKey(id);
			if (slave == null) {
				return new ResultDto<>(false, "账期不存在");
			}
			if (!slave.getHasBill()) {
				return new ResultDto<>(false, "未生成应结款项");
			}
			if (!(slave.getState() == Constant.AP_FOR_REFUND || slave.getState() == Constant.AP_DISABLE_THE
					|| slave.getState() == Constant.AP_OVERDUE)) {
				return new ResultDto<>(false, "该状态不能核销");
			}
			
			AccountPeriodMaster master = accountPeriodMasterMapper.selectByPrimaryKey(slave.getMasterId());
			ApBill bill = billMapper.selectByApId(slave.getId());
			if(bill.getIsChargeOff()){
				return new ResultDto<>(false, "该账期已经被核销");
			}
			BigDecimal rechargeLeft = master.getRechargeLeft();
			BigDecimal totalAmount = bill.getTotalAmount();
			//校验是否强制核销
			if(isForce != null && isForce){
				if(remark == null || remark.length()> 50 ){
					return new ResultDto<>(false, "核销原因不能为空并且长度不能超过50");
				}
			}else{
				if(totalAmount.compareTo(rechargeLeft)>0){
					return new ResultDto<>(false,"应结款项大于总已还金额");
				}
			}
			String msg = "核销账期";
			Date upDate = new Date();
			//更新账单数据
			bill.setRechargeLeft(rechargeLeft);
			bill.setVerificationUser(operator);
			bill.setIsChargeOff(true);
			bill.setVerificationDate(upDate);
			billMapper.updateByPrimaryKeySelective(bill);
			slave = accountPeriodSlaveMapper.selectByPrimaryKey(id);
			slave.setUpdateDate(upDate);
			slave.setIsChargeOff(true);
			//如果全部订单已经被核销，直接变为已完结，若否，需要开启一下账期才能算完结
			if(slave.getOrderQty()!=null &&slave.getOrderQty() == 0){
				slave.setState(Constant.AP_FINISHED);
			}
			accountPeriodSlaveMapper.updateByPrimaryKeySelective(slave);
			//更新总已还金额
			master.setRechargeLeft(rechargeLeft.subtract(totalAmount).setScale(2, BigDecimal.ROUND_HALF_UP));
			accountPeriodMasterMapper.updateByPrimaryKeySelective(master);
			createRecord(operator, ApOptRecord.VERIFICATION, msg, slave.getId(),null);
			return new ResultDto<>(true,"核销成功");
		} catch (Exception e) {
			return new ResultDto<>(false, "核销异常");
		}
	}

	@Override
	public AccountPeriodSlave getCurAp(String email) {
		return accountPeriodSlaveMapper.getCurAccountPeriod(email);
	}

	@Override
	public ResultDto<?> adjust(String string, String admin) {
		try {
			JsonNode json = Json.parse(string);
			Integer id = JsonCaseUtil.jsonToInteger(json.get("id"));
			AccountPeriodMaster apm = accountPeriodMasterMapper.selectByPrimaryKey(id);
			if(apm == null){
				return new ResultDto<>(false, "该账期存在");
			}
			BigDecimal totalLimit = JsonCaseUtil.jsonToBigDecimal(json.get("totalLimit"));
			String oaAuditCode = JsonCaseUtil.jsonToString(json.get("oaAuditCode"));
			String contractNo = JsonCaseUtil.jsonToString(json.get("contractNo"));
			String remarks = JsonCaseUtil.jsonToString(json.get("remarks"));
			if(totalLimit.compareTo(apm.getTotalLimit()) < 0){
				return new ResultDto<>(false, "账期额度必须大于等于现有额度");
			}
			if(remarks.length() > 100 ){
				return new ResultDto<>(false, "备注信息长度必须到1-100之间");
			}
			apm.setTotalLimit(totalLimit);
			apm.setContractNo(contractNo);
			apm.setOaAuditCode(oaAuditCode);
			apm.setUpdateDate(new Date());
			if(accountPeriodMasterMapper.updateByPrimaryKeySelective(apm)>0){
				String operateDesc = "<div><span>整个账期额度:</span><em>"+totalLimit+"</em></div>"+
						  "<div><span>OA审批单号:</span><em>"+ oaAuditCode+"</em></div>"+
						  "<div><span>账期合同编号:</span><em>"+contractNo+"</em></div>"+
						  "<div><span>修改备注:</span><em>"+remarks+"</em></div>";
				createRecord(admin, ApOptRecord.UPDATE, operateDesc, null,id);
				//批量更新子账期信息
				accountPeriodSlaveMapper.getAccountPeriodsByMasterId(id).forEach(s->{
					s.setTotalLimit(totalLimit);
					accountPeriodSlaveMapper.updateByPrimaryKeySelective(s);
				});
			}
			return new ResultDto<>(true, "修改额度成功");
		} catch (Exception e) {
			Logger.info("调整额度异常:{}",e);
			return new ResultDto<>(false, "调整额度异常");
		}
 	}
	
	@Override
	public ResultDto<?> brushApOrderDetail() {
		List<OrderByAp> all = orderByApMapper.selectAll();
		if (CollectionUtils.isEmpty(all)) {
			return ResultDto.newIns().suc(false).msg("没有订单");
		}
		
		ImmutableMap<String, OrderByAp> orderNo2Order = Maps.uniqueIndex(all, e->e.getOrderNo());
		
		int totalCount = 0;
		try {
			List<ApOrderDetail> apOrderDetailList = Lists.newArrayListWithCapacity(50);
			for (Map.Entry<String, OrderByAp> entry : orderNo2Order.entrySet()) {
				String orderNo = entry.getKey();
				OrderByAp orderByAp = entry.getValue();
				
				// 同步过的不管
				if (CollectionUtils.isNotEmpty(apOrderDetailMapper.selectByOrderNo(orderNo))) {
					continue;
				}
				
				
				if (orderNo.startsWith("CG")) {
					apOrderDetailList.addAll(getPurchaseOrderDetails(orderByAp.getId(), orderNo));
				} else if (orderNo.startsWith("XS")) {
					apOrderDetailList.addAll(getSalesOrderDetails(orderByAp.getId(), orderNo));
				} else  if (orderNo.startsWith("HBXS")) {
					apOrderDetailList.addAll(getCombinedSalesOrderProsDetails(orderByAp.getId(), orderNo));
				} 
				
				// 批量插入
				if (apOrderDetailList.size()>100) {
					int line = apOrderDetailMapper.insertBatch(apOrderDetailList);
					Logger.info("手动同步账期支付的订单的详情：分批保存{}条详情，保存订单的详情{}", apOrderDetailList.size(), line > 0 ? "成功" : "失败");
					apOrderDetailList.clear();
					totalCount += line;
				}
			}
			
			// 保存详情
			if (CollectionUtils.isNotEmpty(apOrderDetailList)) {
				int line = apOrderDetailMapper.insertBatch(apOrderDetailList);
				totalCount += line;
				Logger.info("手动同步账期支付的订单的详情：最后保存{}条详情，保存订单的详情{}", apOrderDetailList.size(), line > 0 ? "成功" : "失败");	
			}
			
			return ResultDto.newIns().suc(true).msg("同步成功，共同步" + totalCount + "条记录");
		} catch (Exception e) {
			Logger.info("手动同步账期支付的订单的详情：异常：{}",e);
			return ResultDto.newIns().suc(false).msg("同步异常");
		}
	}
	
	private List<ApOrderDetail> getPurchaseOrderDetails(Integer apOrderId, String purchaseOrderNo){
		List<ApOrderDetail> apOrderDetailList = Lists.newArrayList();
		JsonNode purchaseOrderNode = null;
		try {
			purchaseOrderNode = httpService.getPurchaseOrder(purchaseOrderNo);
		} catch (IOException e) {
			Logger.info("手动同步账期支付的订单的详情，获取采购单[{}]异常，{}", purchaseOrderNo, e);
		}
		if (purchaseOrderNode==null) {
			Logger.info("手动同步账期支付的订单的详情，获取采购单[{}]失败，停止备份",purchaseOrderNo);
			return apOrderDetailList;
		}
		
		for (Iterator<JsonNode> it = purchaseOrderNode.get("details").iterator(); it.hasNext(); ) {
			JsonNode next = it.next();
			String sku = next.get("sku").asText();
			String productName = next.get("productName").asText();
			int qty = next.get("qty").asInt();
			int warehouseId = next.get("warehouseId").asInt();
			String warehouseName = next.get("warehouseName").asText();
			apOrderDetailList.add(new ApOrderDetail(apOrderId, purchaseOrderNo, sku, productName, qty, warehouseId, warehouseName));
		}
		return apOrderDetailList;
	}
	
	private List<ApOrderDetail> getSalesOrderDetails(Integer apOrderId, String salesOrderNo){
		List<ApOrderDetail> apOrderDetailList = Lists.newArrayList();
		JsonNode salesOrderNode = null;
		try {
			salesOrderNode = httpService.getSalesOrder(salesOrderNo);
		} catch (IOException e) {
			Logger.info("手动同步账期支付的订单的详情，获取发货单[{}]异常，{}", salesOrderNo, e);
		}
		if (salesOrderNode==null || !salesOrderNode.get("suc").asBoolean()) {
			Logger.info("手动同步账期支付的订单的详情，获取发货单[{}]失败，停止备份",salesOrderNo);
			return apOrderDetailList;
		}
		
		for (Iterator<JsonNode> it = salesOrderNode.get("details").iterator(); it.hasNext(); ) {
			JsonNode next = it.next();
			String sku = next.get("sku").asText();
			String productName = next.get("productName").asText();
			int qty = next.get("qty").asInt();
			int warehouseId = next.get("warehouseId").asInt();
			String warehouseName = next.get("warehouseName").asText();
			apOrderDetailList.add(new ApOrderDetail(apOrderId, salesOrderNo, sku, productName, qty, warehouseId, warehouseName));
		}
		return apOrderDetailList;
	}
	
	private List<ApOrderDetail> getCombinedSalesOrderProsDetails(Integer apOrderId, String hbNo){
		List<ApOrderDetail> apOrderDetailList = Lists.newArrayList();
		JsonNode salesOrderNode = null;
		try {
			salesOrderNode = httpService.getCombinedSalesOrder(hbNo);
		} catch (IOException e) {
			Logger.info("账期支付订单，获取合并发货单商品详情[{}]异常，{}", hbNo, e);
		}
		if (salesOrderNode==null || !salesOrderNode.get("result").asBoolean()) {
			Logger.info("异步备份账期支付的订单，获取合并发货单商品详情[{}]失败，停止备份",hbNo);
			return apOrderDetailList;
		}
		
		for (Iterator<JsonNode> it = salesOrderNode.get("data").iterator(); it.hasNext(); ) {
			JsonNode next = it.next();
			String sku = next.get("sku").asText();
			String productName = next.get("productName").asText();
			int qty = next.get("qty").asInt();
			int warehouseId = next.get("warehouseId").asInt();
			String warehouseName = next.get("warehouseName").asText();
			String salesOrderNo = JsonCaseUtil.jsonToString(next.get("salesOrderNo"));
			ApOrderDetail apOrderDetail = new ApOrderDetail(apOrderId, hbNo, sku, productName, qty, warehouseId, warehouseName);
			apOrderDetail.setSalesOrderNo(salesOrderNo);
			apOrderDetailList.add(apOrderDetail);
		}
		return apOrderDetailList;
	}
}
