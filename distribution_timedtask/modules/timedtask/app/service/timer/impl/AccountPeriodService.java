package service.timer.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;

import entity.timer.AccountPeriodMaster;
import entity.timer.AccountPeriodSlave;
import entity.timer.ApBill;
import entity.timer.ApBillOrderMapping;
import entity.timer.ApOptRecord;
import entity.timer.Constant;
import entity.timer.DisAccount;
import entity.timer.OrderByAp;
import entity.timer.Search;
import mapper.timer.AccountPeriodMasterMapper;
import mapper.timer.AccountPeriodSlaveMapper;
import mapper.timer.ApBillMapper;
import mapper.timer.ApBillOrderMappingMapper;
import mapper.timer.ApOptRecordMapper;
import mapper.timer.DisAccountMapper;
import mapper.timer.OrderByApMapper;
import play.Logger;
import play.libs.Json;
import service.timer.IAccountPeriodService;
import services.base.utils.JsonFormatUtils;
import util.timer.DateUtils;
import util.timer.JsonCaseUtil;

/**
 * 账期服务接口实现类
 * @author zbc
 * 2017年2月17日 下午6:09:38
 */
public class AccountPeriodService implements IAccountPeriodService {
	
	@Inject
	private AccountPeriodSlaveMapper  accountPeriodSlaveMapper;
	
	@Inject
	private DisAccountMapper accountMapper;

	@Inject
	private OrderByApMapper orderMapper;
	
	@Inject
	private ApBillMapper billMapper;
	
	@Inject
	private ApBillOrderMappingMapper billOrderMappingMapper;
	
	@Inject
	private AccountPeriodMasterMapper accountPeriodMasterMapper;
	
	@Inject
	private ApOptRecordMapper recordMapper;

	private static String CREATE_USER = "system";

	/**
	 * 状态变更自动任务
	 * 1,账期 开始时间次日      未生效 -> 可使用
	 * 2,账期 过合同账期          可使用 -> 待还款  如果未使用订单，则自动变为已完结
	 * 3,账期过了红线账期       待还款 -> 已逾期
	 * 4,账期过了红线账期      禁用中 -> 已逾期
	 * 5,账期已逾期如果订单为0   逾期 ->完结  
	 * 如果到了合同账期，自动生成账单和开启下一账期
	 *               
	 */
	@Override
	public String dealAccountPeriod() {
		try {
			//1
			dealApByState(Constant.AP_INVALID);
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
		List<AccountPeriodSlave> list = accountPeriodSlaveMapper.getNeedHandleApByState(state);
		if (list.size() > 0) {
			Integer nextState = state + 1;
			Date updateDate = new Date();
			list.forEach(e -> {
				if (e.getOrderQty() == 0) {
					e.setState(state >= Constant.AP_AVAILABLE ? Constant.AP_HAS_BEEN_FINISHED : nextState);
				} else {
					switch (state) {
					case Constant.AP_DISABLE_THE:
						e.setState(Constant.AP_OVERDUE);
						break;
					case Constant.AP_OVERDUE:
						e.setState(Constant.AP_OVERDUE);
						break;
					case Constant.AP_FOR_REFUND:
						if (e.getRedLineDate().before(new Date())) {
							e.setState(Constant.AP_OVERDUE);
						}
						break;
					default:
						e.setState(nextState);
						break;
					}
				}
				// 如果状态没有变更 则不做更新操作
				if (state != e.getState()) {
					e.setUpdateDate(updateDate);
					if (accountPeriodSlaveMapper.updateByPrimaryKeySelective(e) > 0) {
						Logger.info("========[{}]账期更新状态为:[{}]======", e.getAccount(),
								Constant.ACCOUNT_PERIOD_STATU_MAP.get(e.getState()));
						// 如果账期逾期，账户冻结
						if (e.getState() == Constant.AP_OVERDUE) {
							DisAccount account = accountMapper.getDisAccountByEmail(e.getAccount());
							account.setFrozen(true);
							accountMapper.updateByPrimaryKeySelective(account);
						} else if (e.getState() == Constant.AP_FOR_REFUND) {
							Search search = new Search(true, e.getId());
							// 如果状态为待还款，自动生成账单，开启下一期
							List<OrderByAp> orders = orderMapper.pageSearch(search);
							// 校验有没有逾期的
							if (orders.size() > 0){
								Logger.info("[{}]自动生成账单结果:[{}]",e.getAccount(),
										generBill("{\"id\":" +e.getId() + "," + "\"isAll\":" + true+"}", CREATE_USER));
							}
							if (validAutoNext(e.getMasterId())) {
								int days = DateUtils.daysOfTwo(e.getPerformanceStartTime(), e.getPerformanceEndTime());
								String performanceEndTime = DateUtils.date2string(
										DateUtils.dateAddDays(e.getPerformanceEndTime(), days + 1),
										DateUtils.FORMAT_DATE_PAGE);
								Logger.info("[{}]自动开启下一账期结果:[{}]",e.getAccount(),
										nextSlave("{\"id\":" + e.getId() + "," + "\"redLineDays\":" + e.getRedLineDays()
								+ "," + "\"performanceEndTime\":\"" + performanceEndTime + "\"}", CREATE_USER));
							}
						}
					}
				}
			});
		}
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
	
	@Override
	public Boolean generBill(String string,String createUser) {
		try {
			Search search = JsonFormatUtils.jsonToBean(string, Search.class);
			AccountPeriodSlave slave = accountPeriodSlaveMapper.selectByPrimaryKey(search.getId());
			if(!(slave.getState() == Constant.AP_FOR_REFUND ||
					slave.getState() == Constant.AP_DISABLE_THE||
						slave.getState() == Constant.AP_OVERDUE)){
				return false;
			}
			//重新生成账单，要删除旧的账单
			if(slave.getHasBill()){
				delBill(slave.getId());
			}
			List<OrderByAp> orders = orderMapper.pageSearch(search);
			if((search.getIsAll()!= null&&search.getIsAll())||(search.getOrderIds() != null&& search.getOrderIds().size()>0)){
				//账单金额
				BigDecimal totalAmount = new BigDecimal(orders.stream().mapToDouble(e->e.getPayAmount().doubleValue()).sum()).setScale(2, BigDecimal.ROUND_HALF_UP);
					//生成账单
				ApBill bill = new ApBill();
				bill.setAccount(slave.getAccount());
				bill.setApId(slave.getId());
				bill.setArearAmount(totalAmount);
				bill.setTotalAmount(totalAmount);
				bill.setRechargeLeft(slave.getRechargeLeft());
				bill.setCreateUser(createUser);
				billMapper.insertSelective(bill);
				orders.forEach(e->{
					e.setIsChoice(OrderByAp.HAVE_BEEN_CHOICE);	
				});
				//更新订单为已选
				orderMapper.batchUpdate(orders);
				//维护  账单 订单关系
				List<ApBillOrderMapping> mappings = Lists.transform(orders, e->{
					return new ApBillOrderMapping(e.getId(),bill.getId());
				});
				billOrderMappingMapper.batchInsert(mappings);
				createRecord(createUser, ApOptRecord.GENERATED_BILLS, "生成本期应结款项", slave.getId(), null);
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			Logger.info("生成账单异常"+e);
			return false;
		}
	}
	private void createRecord(String operator, Integer operateType, String operateDesc, 
			Integer slaveId,Integer masterId) {
		recordMapper.insertSelective(new ApOptRecord(operator, operateType, operateDesc, slaveId, masterId));
	}
	
	@Override
	public boolean nextSlave(String string,String creater) {
		String info =  null;
		try {
			JsonNode json = Json.parse(string);
			Integer id = JsonCaseUtil.jsonToInteger(json.get("id"));
			AccountPeriodSlave slave = accountPeriodSlaveMapper.selectByPrimaryKey(id);
			if(slave != null){
				DisAccount  account = accountMapper.getDisAccountByEmail(slave.getAccount());
				if(account.isFrozen()){
					info = "账户被冻结，无法开启下一期";
				}else if(slave.getState() != Constant.AP_FOR_REFUND &&
						slave.getState() != Constant.AP_HAS_BEEN_FINISHED &&
						slave.getState() != Constant.AP_OVERDUE ){
					info = "当前状态不能开启下一期";
				}else if(!slave.getHasNext()){
					Date createDate = new Date();
					//更新 标识 已开启下一期 防止多次开启
					slave.setHasNext(true);
					accountPeriodSlaveMapper.updateByPrimaryKeySelective(slave);
					AccountPeriodMaster master = accountPeriodMasterMapper.selectByPrimaryKey(slave.getMasterId());
					//当前账期没有未核销订单更新为已完结
					if(slave.getOrderQty() == 0){
						slave.setState(Constant.AP_HAS_BEEN_FINISHED);
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
					next.setState(Constant.AP_INVALID);//未生效
					next.setContractPeriodDate(//合同账期
							getDateWithType(next.getPerformanceStartTime(),
									master.getPeriodType(), master.getPeriodLength()));
					//固定月结 月底结算
					if(master.getPeriodType() == AccountPeriodMaster.PERIOD_TYPE_MONTH_STATEMENT){
						next.setPerformanceEndTime(DateUtils.dateAddMonths(next.getPerformanceStartTime(), 1));
					// 目前自然月类型业绩周期结束时间必须是合同账期，不然会出现周期混乱
					}else if(master.getPeriodType() == AccountPeriodMaster.PERIOD_TYPE_MONTH){
						next.setPerformanceEndTime(next.getContractPeriodDate());	
					}else{
						next.setPerformanceEndTime(//业绩周期结束时间 不大于合同账期
								JsonCaseUtil.jsonStrToDate(json.get("performanceEndTime"),DateUtils.FORMAT_DATE_PAGE ));
					}
					if(next.getPerformanceEndTime().after(next.getContractPeriodDate())){
						next.setPerformanceEndTime(next.getContractPeriodDate());
					}
					next.setRedLineDate(//红线时间
							DateUtils.dateAddDays(next.getContractPeriodDate(), next.getRedLineDays()));	
					//标识有上一期
					next.setHasPrev(true);
					// 如果业绩周期开始时间大于当前，立即生效
					if(next.getPerformanceStartTime().before(new Date())){
						next.setState(Constant.AP_AVAILABLE);;
					}
					accountPeriodSlaveMapper.insertSelective(next);
					//开启下一账期日志
					createRecord(creater, ApOptRecord.OPEN_THE_NEXT_ISSUE, "开启下一账期",slave.getId(), null);
					//创建账期日志
					createRecord(creater, ApOptRecord.CREATE, "创建一条子账期", next.getId(), null);
					return true;
				}else{
					info = "下一期已经开启，请不要重复操作";
				}
			}else{
				info = "账期明细不存在";	
			}
		} catch (Exception e) {
			info = "开启下一账期异常";
			Logger.info(info+e);
		}
		return false;
	}
	
	/**
	 * 根据周期类型长度获取日期
	 * @param startDate    开始时间
	 * @param periodType   周期类型 日，月
	 * @param length       周期长度
	 * @author zbc
	 * @since 2017年2月25日 下午3:27:43
	 */
	private static Date  getDateWithType(Date startDate,int periodType,Integer length){
		switch (periodType) {
		case AccountPeriodMaster.PERIOD_TYPE_DATE:
			return DateUtils.dateAddDays(startDate, length);
		case AccountPeriodMaster.PERIOD_TYPE_MONTH:
			return DateUtils.dateAddMonths(startDate, length);
		case AccountPeriodMaster.PERIOD_TYPE_MONTH_STATEMENT:
			return DateUtils.dateAddDays(startDate, length);
		default:
			break;
		}
		return null;
	}
	@Override
	public boolean delBill(Integer id) {
		try {
			ApBill bill = billMapper.selectByApId(id);
			if(bill != null){
				List<OrderByAp> orders = orderMapper.selectByBillId(bill.getId());
				if(orders.size()>0){
					orders.forEach(e->{
						e.setIsChoice(OrderByAp.HAVE_NOT_CHOICE);	
					});
					//更新订单为已选
					orderMapper.batchUpdate(orders);
				}
				billOrderMappingMapper.deleteByBillId(bill.getId());
				billMapper.deleteByPrimaryKey(bill.getId());	
			}
			return true;
		} catch (Exception e) {
			Logger.info("应结款项失败"+e);
			return false;
		}
	}
}
