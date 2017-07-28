package services.purchase.returnod.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import dto.purchase.returnod.CalculateReturnAmountResult;
import dto.purchase.returnod.ReturnOrderDto;
import entity.purchase.returnod.ReturnAmountCoefficient;
import entity.purchase.returnod.ReturnOrder;
import entity.purchase.returnod.ReturnOrderDetail;
import entity.purchase.returnod.ReturnOrderLog;
import forms.purchase.Page;
import forms.purchase.returnod.AuditReturnOrderParams;
import forms.purchase.returnod.ReturnLockParams;
import mapper.purchase.returnod.ReturnAmountCoefficientMapper;
import mapper.purchase.returnod.ReturnOrderDetailMapper;
import mapper.purchase.returnod.ReturnOrderLogMapper;
import mapper.purchase.returnod.ReturnOrderMapper;
import play.Logger;
import services.purchase.IHttpService;
import services.purchase.ISequenceService;
import services.purchase.returnod.IReturnOrderService;
import utils.purchase.DateUtils;
import utils.purchase.PriceFormatUtil;
import utils.purchase.ReturnOrderStatus;
import utils.purchase.StringUtils;
/**
 * 微仓退货service实现
 *
 * @author huangjc
 * @since 2017年3月10日
 */
public class ReturnOrderService implements IReturnOrderService {
	@Inject	private ReturnOrderMapper returnOrderMapper;
	@Inject	private ReturnOrderLogMapper returnOrderLogMapper;
	@Inject	private ReturnOrderDetailMapper returnOrderDetailMapper;
	@Inject	private ReturnAmountCoefficientMapper returnAmountCoefficientMapper;
	@Inject private ISequenceService sequenceService;
	@Inject	private IHttpService httpService;
	
	@Override
	public Double getTotalUserExpectReturnAmount4MatchedConditions(JsonNode paramsNode) {
		Map<String, Object> params = getParams(null, paramsNode);
		Logger.info("getTotalReturnAmount4MatchedConditions查询退货单的参数：{}", params);
		// 不用分页
		params.remove("currPage");
		params.remove("pageSize");
		// 待审核的才要
		params.put("status", ReturnOrderStatus.AUDIT_WAITING);
		
		List<ReturnOrder> returnOrders = returnOrderMapper.selectByParams(params);
		if (CollectionUtils.isEmpty(returnOrders)) {
			return 0.00;
		}
		
		BigDecimal total = returnOrders.stream()
				.filter(ro -> ro.getUserExpectTotalReturnAmount()!=null)
				.map(ro -> new BigDecimal(ro.getUserExpectTotalReturnAmount()))
			.reduce(new BigDecimal(0.00), (x, y)->x.add(y));
		return PriceFormatUtil.toFix2(total);
	}
	
	@Override
	public CalculateReturnAmountResult calculateExpectReturnAmount(ReturnOrderDetail detailParams){
		return getReturnAmount(detailParams);
	}

	private boolean noReturnAmountCoefficient(ReturnAmountCoefficient returnAmountCoefficient){
		return (returnAmountCoefficient==null ||
				StringUtils.isBlankOrNull(returnAmountCoefficient.getCoefficientValue()));
	}

	/**
	 * 获取退款金额
	 * @param detail
	 * @return
	 */
	private CalculateReturnAmountResult getReturnAmount(ReturnOrderDetail detail){
		ReturnAmountCoefficient returnAmountCoefficient = returnAmountCoefficientMapper.selectBySkuWarehouseId(
				detail.getSku(), detail.getWarehouseId());
		Logger.info("查询商品退款系数：{}",returnAmountCoefficient);
		Date now = new Date();
		Date expirationDate = DateUtils.parse(detail.getExpirationDate(), DateUtils.FORMAT_DATE_PAGE);
		int daySpace = DateUtils.getDateSpace(now,expirationDate);
		Logger.info("{}，{}===两个时间相差天数：{}",now,expirationDate,daySpace);
		// 没有退款系数，就不计算了
		if(noReturnAmountCoefficient(returnAmountCoefficient)){
			CalculateReturnAmountResult noCalculateReturnAmountResult = CalculateReturnAmountResult.newNoCalculateReturnAmountResult("没有设置退款系数");
			noCalculateReturnAmountResult.setDaySpace(daySpace);
			return noCalculateReturnAmountResult;
		}

		Logger.info("退款系数为：{}",returnAmountCoefficient.getCoefficientValue());
		Double coefficient = returnAmountCoefficient.getCoefficient(daySpace);
		Logger.info("最终获取到的退货系数是：{}",coefficient);
		if(coefficient==null){
			throw new RuntimeException("获取到的退货系数为空");
		}

		if(coefficient<0 || coefficient>1){
			Logger.info("退款系数的范围应为[0~1]，当前退款系数为："+coefficient);
			throw new RuntimeException("退款系数的范围应为[0~1]，当前退款系数为："+coefficient);
		}
		Double subTotalReturnAmount = detail.calculateSubTotalReturnAmount(coefficient);
		return new CalculateReturnAmountResult(daySpace,coefficient,subTotalReturnAmount);
	}

	@Override
	public Map<String,Object> applyReturnOrder(ReturnOrder ro, ReturnOrderDetail detailParams){

		// 获取退款系数
		ReturnOrderDetail detail = new ReturnOrderDetail();
		BeanUtils.copyProperties(detailParams, detail);
		CalculateReturnAmountResult calculateReturnAmountResult = null;
		try {
			calculateReturnAmountResult = getReturnAmount(detail);
		} catch (Exception e) {
			e.printStackTrace();
			return applyErrorResult(null);
		}

		// 判断是否有退款系数
		ro.setReturnOrderNo(sequenceService.getReturnOrderNo());// 退货单号
		ro.setTotalReturnAmount(calculateReturnAmountResult.getReturnAmount());
		// TODO 如果用户填了金额，那么actualTotalReturnAmount的值就跟用户填的金额一致，否则就是跟系统计算的金额一致
		Double actualTotalReturnAmount = ro.getUserExpectTotalReturnAmount() == null ? ro
				.getTotalReturnAmount() : ro.getUserExpectTotalReturnAmount();
		ro.setActualTotalReturnAmount(actualTotalReturnAmount);
		ro.setStatus(ReturnOrderStatus.AUDIT_WAITING);
		ro.setCreateUser(ro.getEmail());
		try {
			ro.setSalesman(httpService.custaccount(ro.getEmail()).get("account").textValue());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 先把微仓的锁定，直到审核通过返回到云仓或审核不通过，锁定释放
		// 1、锁微仓
		ReturnLockParams params = new ReturnLockParams();
		params.setReturnOrderNo(ro.getReturnOrderNo());
		params.setPurchaseOrderNo(detail.getPurchaseOrderNo());
		params.setSku(detail.getSku());
		params.setWarehouseId(detail.getWarehouseId());
		params.setReturnQty(detail.getReturnQty());
		params.setAccount(ro.getEmail());
		params.setInRecordId(detail.getInRecordId());
		JsonNode lockResult = null;
		try {
			lockResult = httpService.returnLock(params);
		} catch (Exception e) {
			e.printStackTrace();
			return applyErrorResult(null);
		}
		if(lockFailed(lockResult)){
			String lockFailedMsg = lockResult==null?"":lockResult.get("msg").asText();
			// 微仓锁库失败
			return applyErrorResult(lockFailedMsg);
		}

		Logger.info("退货单主数据：{}",ro);
		// 2、保存退货单数据
		returnOrderMapper.insert(ro);

		// 3、保存退货单详情数据
		detail.setDaySpace(calculateReturnAmountResult.getDaySpace());// 间隔时间
		detail.setCoefficient(calculateReturnAmountResult.getCoefficient());// 退款系数
		detail.setReturnOrderId(ro.getId());
		detail.setReturnOrderNo(ro.getReturnOrderNo());
		Logger.info("退货单详情：{}",detail);
		returnOrderDetailMapper.insert(detail);

		// 记录日志
		batchAddReturnOrderLog(Lists.newArrayList(ro));

		Map<String,Object> result = Maps.newHashMap();
		result.put("suc", true);
		result.put("msg", "申请成功");
		return result;
	}
	
	/**
	 * 申请退货失败结果
	 * @param msg 提示信息，可以为空
	 * @return
	 */
	private Map<String,Object> applyErrorResult(String msg){
		Map<String,Object> result = Maps.newHashMap();
		result.put("suc", false);
		result.put("msg", StringUtils.isBlankOrNull(msg)?"申请退货失败":msg);
		return result;
	}
	
	/**
	 * 锁库失败
	 * @param lockResult
	 * @return
	 */
	private boolean lockFailed(JsonNode lockResult){
		return lockResult==null || lockResult.get("result").asInt()==1;
	}

	@Override
	public Map<String,Object> cancelReturnOrderApplication(String email, String returnOrderNo) {
		Map<String, Object> result = Maps.newHashMap();
		ReturnOrder returnOrder = returnOrderMapper.selectByReturnOrderNo(returnOrderNo);
		// 此退货单不是此email申请的
		if(returnOrder==null || StringUtils.isNotBlankOrNull(email) && !email.equals(returnOrder.getEmail())){
			result.put("suc", false);
			result.put("msg", "不存在退货单号为["+returnOrderNo+"]此退货单");
			return result;
		}

		// 检查状态是否可以取消操作
//		if(returnOrder.getStatus()==ReturnOrderStatus.AUDIT_WAITING){
//			result.put("suc", false);
//			result.put("msg", "此退货单不能进行取消申请操作");
//			return result;
//		}

		// 微仓库存解锁
		JsonNode releaseReturnLockNode = null;
		try {
			releaseReturnLockNode = httpService.releaseReturnLock(Lists.newArrayList(returnOrderNo));
		} catch (IOException e) {
			e.printStackTrace();
			return cancelErrorResult();
		}
		// 释放锁失败
		if(releaseReturnLockFailed(releaseReturnLockNode)){
			return cancelErrorResult();
		}

		// 更新退货单记录状态
		ReturnOrder updateReturnOrderParam = new ReturnOrder();
		updateReturnOrderParam.setStatus(ReturnOrderStatus.CANCELED);
		updateReturnOrderParam.setLastUpdateUser(email);
		updateReturnOrderParam.setId(returnOrder.getId());
		updateReturnOrderParam.setReturnOrderNo(returnOrder.getReturnOrderNo());
		int count = returnOrderMapper.updateByPrimaryKeySelective(updateReturnOrderParam);
		if(count==1){
			Logger.info("成功更新退货单记录状态{}",returnOrder.getReturnOrderNo());
			// 记录日志
			batchAddReturnOrderLog(Lists.newArrayList(returnOrder));
			result.put("suc", true);
			result.put("msg", "取消申请操作成功");
			return result;
		}
		Logger.info("失败更新退货单记录状态{}",returnOrder.getReturnOrderNo());
		return cancelErrorResult();
	}
	
	private Map<String, Object> cancelErrorResult(){
		Map<String, Object> result = Maps.newHashMap();
		result.put("suc", false);
		result.put("msg", "取消申请操作失败");
		return result;
	}
	
	private boolean releaseReturnLockFailed(JsonNode releaseReturnLockNode){
		return releaseReturnLockNode==null || releaseReturnLockNode.get("result").asInt()==1;
	}

	@Override
	public Page<ReturnOrderDto> getReturnOrdersByPage(String email, JsonNode paramsNode) {
		Map<String, Object> params = getParams(email, paramsNode);
		Logger.info("getReturnOrdersByPage查询退货单的参数：{}", params);
		return queryReturnOrderDtoPage(params);
	}

	@Override
	public Page<ReturnOrderDto> get2BeAuditedReturnOrders(String email, JsonNode paramsNode) {
		Map<String, Object> params = getParams(email, paramsNode);
		params.put("status", ReturnOrderStatus.AUDIT_WAITING);
		Logger.info("get2BeAuditedReturnOrders查询退货单的参数：{}", params);
		return queryReturnOrderDtoPage(params);
	}

	private Page<ReturnOrderDto> queryReturnOrderDtoPage(Map<String, Object> params){
		List<ReturnOrder> returnOrders = returnOrderMapper.selectByParams(params);

		List<ReturnOrderDto> roDtoList = Lists.newArrayList();
		Integer totalCount = 0;
		if(returnOrders!=null && returnOrders.size()>0){
			totalCount = returnOrderMapper.selectCountByParams(params);

			// 批量查询退货单详情
			List<Integer> roIds = Lists.transform(returnOrders, ro->ro.getId());
			List<ReturnOrderDetail> allDetails = returnOrderDetailMapper.selectByRoIdList(roIds);
			Map<Integer, List<ReturnOrderDetail>> detailsByRoId = allDetails.stream().collect(Collectors.groupingBy(ReturnOrderDetail::getReturnOrderId));

			// 批量查询退货单日志
			List<String> returnOrderNoList = Lists.transform(returnOrders, ro->ro.getReturnOrderNo());
			List<ReturnOrderLog> allLogs = returnOrderLogMapper.selectByReturnOrderNoList(returnOrderNoList);
			Map<String, List<ReturnOrderLog>> logsByReturnOrderNo = allLogs.stream().collect(Collectors.groupingBy(ReturnOrderLog::getReturnOrderNo));

			// 对象转换
			ReturnOrderDto roDto;
			for(ReturnOrder ro : returnOrders){
				roDto = new ReturnOrderDto();
				BeanUtils.copyProperties(ro, roDto);
				roDto.setDetails(detailsByRoId.get(ro.getId()));
				roDto.setLogs(logsByReturnOrderNo.get(ro.getReturnOrderNo()));
				roDtoList.add(roDto);
			}
		}

		Page<ReturnOrderDto> page = new Page<ReturnOrderDto>((Integer)params.get("currPage"), (Integer)params.get("pageSize"), totalCount, roDtoList);
		return page;
	}

	private Map<String, Object> getParams(String email, JsonNode paramsNode){
		int currPage = paramsNode.has("currPage")?paramsNode.get("currPage").asInt():1;
		int pageSize = paramsNode.has("pageSize")?paramsNode.get("pageSize").asInt():10;

		Map<String, Object> params = Maps.newHashMap();
		params.put("email", email);
		params.put("returnOrderNo", paramsNode.has("returnOrderNo")?paramsNode.get("returnOrderNo").asText():null);
		// 时间范围
		if(paramsNode.has("dateScope")) {
			// 3,6,12,18
			int dateScope = paramsNode.get("dateScope").asInt();
			LocalDate endApplicationDate = LocalDate.now();
			LocalDate startApplicationDate = endApplicationDate.minusMonths(dateScope);
			params.put("startApplicationDate", startApplicationDate.format(DateTimeFormatter.ofPattern(DateUtils.FORMAT_DATE_PAGE)));
			params.put("endApplicationDate", endApplicationDate.format(DateTimeFormatter.ofPattern(DateUtils.FORMAT_DATE_PAGE)));
		}

		params.put("currPage", currPage);
		params.put("pageSize", pageSize);
		if(paramsNode.has("startApplicationDate")){
			params.put("startApplicationDate", paramsNode.get("startApplicationDate").asText());
		}
		if(paramsNode.has("endApplicationDate")){
			params.put("endApplicationDate", paramsNode.get("endApplicationDate").asText());
		}
		params.put("status", paramsNode.has("status")?paramsNode.get("status").asInt():null);
		params.put("searchText", paramsNode.has("searchText")?paramsNode.get("searchText").asText():null);
		// 排序参数
		params.put("sort", paramsNode.has("sort")?paramsNode.get("sort").asText():null);
		params.put("filter", paramsNode.has("filter")?paramsNode.get("filter").asText():null);

		return params;
	}

	@Override
	public Map<String, Object> batchAudit(AuditReturnOrderParams params){
		List<String> returnOrderNoList = params.getReturnOrderNoList();
		if(!hasReturnOrderNos(returnOrderNoList)){
			return auditErrorResult("请选择要审核的退货单");
		}

		// 查询退货单
		List<ReturnOrder> returnOrderList = returnOrderMapper.selectByReturnOrderNoList(returnOrderNoList);
		if (returnOrderList.size()==0) {
			return auditErrorResult("不存在退货单号为[" + String.join(",", returnOrderNoList) + "]的退货单");
		}
		// 实际存在的退货单比传进来的退货单号数量小，说明有些退货单号是伪造的
		if(returnOrderList.size()<returnOrderNoList.size()){
			List<String> realReturnOrderNoList = returnOrderList.stream().map(ReturnOrder::getReturnOrderNo).collect(Collectors.toList());
			List<String> fakeReturnOrderNoList = Lists.newArrayList(returnOrderNoList);
			fakeReturnOrderNoList.removeAll(realReturnOrderNoList);
			return auditErrorResult("不存在退货单号为[" + String.join(",", fakeReturnOrderNoList) + "]的退货单");
		}

		// 检查订单状态是否合理
		List<ReturnOrder> returnOrdersNot2BeAudited = returnOrderList.stream().filter(ro->ro.getStatus()!=ReturnOrderStatus.AUDIT_WAITING).collect(Collectors.toList());
		if(returnOrdersNot2BeAudited.size()>0){
			List<String> returnOrderNosNot2BeAudited = Lists.transform(returnOrdersNot2BeAudited, ro->ro.getReturnOrderNo());
			return auditErrorResult("[" + String.join(",", returnOrderNosNot2BeAudited) + "]退货单不处于待审核状态，请检查");
		}

		List<ReturnOrder> toBeUpdateList = Lists.newArrayList();
		Integer updateStatus = params.getPassed()!=0?ReturnOrderStatus.AUDIT_PASSED:ReturnOrderStatus.AUDIT_NOT_PASSED;
		for(ReturnOrder ro : returnOrderList){
			ReturnOrder updateRo = new ReturnOrder();
			updateRo.setId(ro.getId());
			updateRo.setEmail(ro.getEmail());// 这里设置email，为后续使用方便
			updateRo.setReturnOrderNo(ro.getReturnOrderNo());
			updateRo.setStatus(updateStatus);
			if (isAuditPassed(updateRo)) {// 审核通过才有退款（用户填的金额）
				updateRo.setActualTotalReturnAmount(ro.getUserExpectTotalReturnAmount());
			}
			updateRo.setAuditRemarks(params.getAuditRemarks());
			updateRo.setLastUpdateUser(params.getAuditUser());
			toBeUpdateList.add(updateRo);
		}
		Logger.info("批量更新退货单：{}",toBeUpdateList);
		returnOrderMapper.batchUpdateByPrimaryKeySelective(toBeUpdateList);

		// 记录日志
		batchAddReturnOrderLog(toBeUpdateList);

		// 审核不通过，因为所有单的最终审核状态一样，判断一个即可
		if (!isAuditPassed(toBeUpdateList.get(0))) {
			Logger.info("退货单批量审核不通过");
			return auditNotPassed(Lists.transform(toBeUpdateList, ro->ro.getReturnOrderNo()));
		}

		Logger.info("退货单批量审核通过");
		return auditPassed(toBeUpdateList);
	}

	@Override
	public Map<String, Object> audit(AuditReturnOrderParams params) {
		if(!hasReturnOrderNos(params.getReturnOrderNoList())){
			return auditErrorResult("请选择要审核的退货单");
		}
		String returnOrderNo = params.getReturnOrderNoList().get(0);
		ReturnOrder returnOrder = returnOrderMapper.selectByReturnOrderNo(returnOrderNo);
		if(returnOrder==null){
			return auditErrorResult("不存在退货单号为["+returnOrderNo+"]的退货单");
		}
		// 看能不能进行审核操作
		if(returnOrder.getStatus()!=ReturnOrderStatus.AUDIT_WAITING){
			return auditErrorResult("此退货单不处于待审核状态");
		}

		ReturnOrder updateRo = new ReturnOrder();
		updateRo.setId(returnOrder.getId());
		updateRo.setEmail(returnOrder.getEmail());// 这里设置email，为后续使用方便
		updateRo.setReturnOrderNo(returnOrder.getReturnOrderNo());
		updateRo.setStatus(params.getPassed()!=0?ReturnOrderStatus.AUDIT_PASSED:ReturnOrderStatus.AUDIT_NOT_PASSED);
		if (isAuditPassed(updateRo)) {// 审核通过才有退款（用户填的金额）
			updateRo.setActualTotalReturnAmount(params.getActualTotalReturnAmount());
		}
		updateRo.setAuditRemarks(params.getAuditRemarks());
		updateRo.setLastUpdateUser(params.getAuditUser());
		Logger.info("更新退货单{}数据：{}",returnOrder.getReturnOrderNo(),updateRo);
		int count = returnOrderMapper.updateByPrimaryKeySelective(updateRo);

		if(count!=1){
			Logger.info("更新退货单{}状态失败",returnOrder.getReturnOrderNo());
			return auditErrorResult();
		}

		// 记录日志
		batchAddReturnOrderLog(Lists.newArrayList(updateRo));

		// 审核不通过
		if(!isAuditPassed(updateRo)){
			Logger.info("退货单{}审核不通过",returnOrder.getReturnOrderNo());
			return auditNotPassed(Lists.newArrayList(updateRo.getReturnOrderNo()));
		}

		Logger.info("退货单{}审核通过",returnOrder.getReturnOrderNo());
		return auditPassed(Lists.newArrayList(updateRo));
	}

	/**
	 * 审核通过-退回云仓&退款
	 * @param params
	 * @param returnOrder
	 * @return
	 */
	private Map<String, Object> auditPassed(List<ReturnOrder> returnOrderList){
		try {
			// 审核通过，要把钱退回，微仓的货物退回到云仓
			List<String> returnOrderNoList = Lists.transform(returnOrderList, ro->ro.getReturnOrderNo());
			JsonNode return2CloudNode = httpService.returnToCloudInventory(returnOrderNoList);
			if (return2CloudNode == null || return2CloudNode.get("result").asInt() == 1){
				Logger.info("微仓退货-审核通过：还回云仓失败==={}", return2CloudNode);
				return auditErrorResult();
			}
			// 循环退款
			for (ReturnOrder returnOrder : returnOrderList) {
				// 退回到云仓成功，把钱退回到用户的余额里
				String email = returnOrder.getEmail();// 退回到哪个账户里
				Double returnMoney = returnOrder.getActualTotalReturnAmount();// 退的钱
				// 检查参数是否足以进行退款操作
				Logger.info("退款参数，email={}，returnMoney={}，returnOrderNo={}", email, returnMoney, returnOrder.getReturnOrderNo());
				if (StringUtils.isNotBlankOrNull(email) && returnMoney != null && returnMoney > 0) {
					JsonNode refundResultNode = httpService.refund2Balance4ReturnOrder(email, returnOrder.getReturnOrderNo(), returnMoney);
					if (refundResultNode == null || refundResultNode.get("code").asInt() != 4) {// 退款失败
						Logger.info("微仓退货-审核通过：退货单{}退款失败", returnOrder.getReturnOrderNo());
						return auditErrorResult();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			Logger.info("微仓退货-审核通过，还回到云仓&退款失败");
			return auditErrorResult();
		}
		return auditSuccessResult();
	}
	
	/**
	 * 审核不通过-释放微仓锁
	 * @param params
	 * @return
	 */
	private Map<String, Object> auditNotPassed(List<String> returnOrderNoList){
		JsonNode releaseReturnLockNode = null;
		try {
			// 审核不通过，释放微仓锁
			releaseReturnLockNode = httpService.releaseReturnLock(returnOrderNoList);
		} catch (IOException e) {
			e.printStackTrace();
			Logger.info("微仓退货-审核不通过，释放锁失败");
			return auditErrorResult();
		}
		// 释放锁失败
		if (releaseReturnLockNode == null || releaseReturnLockNode.get("result").asInt() == 1) {
			Logger.info("微仓退货-审核不通过，释放锁失败", releaseReturnLockNode);
			return auditErrorResult();
		}

		// 释放锁成功
		return auditSuccessResult();
	}

	private boolean hasReturnOrderNos(List<String> list){
		return list!=null && list.size()>0;
	}

	/**
	 * 判断是否审核通过
	 * @param ro
	 * @return
	 */
	private boolean isAuditPassed(ReturnOrder ro){
		if(ro.getStatus()==null)
			return false;
		return ro.getStatus() == ReturnOrderStatus.AUDIT_PASSED;
	}

	private Map<String,Object> auditErrorResult(){
		return auditErrorResult(null);
	}
	
	private Map<String,Object> auditErrorResult(String msg){
		Map<String, Object> result = Maps.newHashMap();
		result.put("suc", false);
		result.put("msg", StringUtils.isNotBlankOrNull(msg)?msg:"审核失败");
		return result;
	}
	
	private Map<String,Object> auditSuccessResult(){
		Map<String, Object> result = Maps.newHashMap();
		result.put("suc", true);
		result.put("msg", "审核成功");
		return result;
	}

	/**
	 * 记录日志
	 * @param ros
	 */
	private void batchAddReturnOrderLog(List<ReturnOrder> ros){
		if(ros!=null && ros.size()>0){
			List<ReturnOrderLog> logs = Lists.transform(ros, ro->{
				String auditUser = ro.getLastUpdateUser();
				if(StringUtils.isBlankOrNull(auditUser)){
					auditUser = ro.getCreateUser();
				}
				return new ReturnOrderLog(ro.getReturnOrderNo(),ro.getStatus(),auditUser,ro.getAuditRemarks());
			});
			Logger.info("审核退货单日志：{}",logs);
			returnOrderLogMapper.batchInsert(logs);
		}
	}

	@Override
	public ReturnOrderDto getReturnOrder(String returnOrderNo) {
		ReturnOrder ro = returnOrderMapper.selectByReturnOrderNo(returnOrderNo);
		List<ReturnOrderDetail> details = returnOrderDetailMapper.selectByRoIdList(Arrays.asList(ro.getId()));
		List<ReturnOrderLog> logs = returnOrderLogMapper.selectByReturnOrderNo(returnOrderNo);

		ReturnOrderDto roDto = new ReturnOrderDto();
		BeanUtils.copyProperties(ro, roDto);
		roDto.setDetails(details);
		roDto.setLogs(logs);

		return roDto;
	}

}
