package service.timer.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import mapper.timer.OperateRecordMapper;
import mapper.timer.SaleBaseMapper;
import mapper.timer.SaleDetailMapper;
import mapper.timer.SaleMainMapper;

import org.apache.commons.collections.CollectionUtils;

import play.Logger;
import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import entity.timer.OperateRecord;
import entity.timer.SaleBase;
import entity.timer.SaleMain;
import events.timer.CsConfirmEvent;
import service.timer.IHttpService;
import service.timer.ISaleMainService;
import service.timer.ISaleService;
import util.timer.IDUtils;
import util.timer.JsonCaseUtil;
import util.timer.SaleOrderStatus;
import util.timer.StringUtils;


public class SaleService implements ISaleService {

	@Inject
	private SaleMainMapper saleMainMapper;
	@Inject
	private SaleBaseMapper saleBaseMapper;
	@Inject
	private SaleDetailMapper saleDetailMapper;
	@Inject
	private ISaleMainService saleMainService;
	@Inject
	private OperateRecordMapper operateRecordMapper;
	@Inject
	private IHttpService httpService;
	
	//处理中
	private static boolean IS_PROCESSING = false;
	
	@Override
	public void autoCsConfirm() {
		if(!IS_PROCESSING) {
			IS_PROCESSING = true;//处理中
			// 1、查询1小时前支并且状态为客服审核的订单
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.MINUTE, -60);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			List<SaleMain> sms = saleMainMapper.getAutoConfirmOrders(sdf.format(cal.getTime()));
			// 2、循环确认订单
			CsConfirmEvent csfirm = null;
			JsonNode purNode = null;
			String pno = "";
			if (CollectionUtils.isNotEmpty(sms)) {
				Logger.info("本次共【" + sms.size() + "】个订单需自动确认。");
				for (SaleMain saleMain : sms) {
					try {
						pno = saleMain.getPurchaseOrderNo();
						if(StringUtils.isNotBlankOrNull(pno)) {
							purNode = httpService.getPurchaseOrder(pno);
							if (purNode.get("status").asInt() != 1) {
								Logger.info("销售单【"+saleMain.getSalesOrderNo()+"】对应的缺货采购单【"+pno+"】未完成。");
								continue;
							}
						}
						Logger.info("开始自动审核订单：" + saleMain.getSalesOrderNo());
						csfirm = new CsConfirmEvent(saleMain.getSalesOrderNo(), true);
						cusAudit(Json.toJson(csfirm.getCsparam()).toString(), "system",true);
					} catch (Exception e) {
						Logger.error(saleMain.getSalesOrderNo() + "客服审核自动通过任务失败。" + e);
						continue;
					}
				}
			}
			IS_PROCESSING = false;//处理完
		} else {
			Logger.info("上次任务还在执行中，本次任务跳过。");
		}
	}
	
	/**
	 * 盈利订单返回false
	 * @param id
	 * @return
	 */
	private boolean checkProfit(Integer id) {
		Map<String, Object> amount = getAmount(id);
		if(amount == null) {
			return true;
		}
		if(null != amount.get("profit") && Double.valueOf(amount.get("profit").toString()) >= 0) {
			return false;
		}
		return true;
	}
	
	public Map<String, Object> getAmount(Integer sid) {
		Map<String,Object> map = saleDetailMapper.getAmount(sid);
		if(map !=null ){
			BigDecimal bbcpostage = map.get("bbcpostage") != null?new BigDecimal(map.get("bbcpostage")+""):BigDecimal.ZERO;
			BigDecimal platformamount =  new BigDecimal(map.get("platformamount")+"");
			BigDecimal arrvicetotal = new BigDecimal(map.get("arrvicetotal")+"");
			BigDecimal optfee = map.get("optfee") != null?new BigDecimal(map.get("optfee")+""):BigDecimal.ZERO;
			if("3".equals(map.get("distributortype").toString())){
				SaleBase base = saleBaseMapper.selectByOrderId(sid);	
				platformamount = new BigDecimal(base.getOrderActualAmount());
			}else{
				platformamount = platformamount.add(bbcpostage);
			}
			map.put("platformamount",platformamount.setScale(2, BigDecimal.ROUND_HALF_UP));
			//订单总成本
			BigDecimal totalcost =  arrvicetotal.add(bbcpostage).add(optfee);
			//利润值
			BigDecimal profit = platformamount.subtract(totalcost).setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal profitmargin = new BigDecimal(0);
			//利润率
			if(platformamount.compareTo(new BigDecimal(0)) > 0) {
				profitmargin = profit.divide(platformamount,5);
			}
			map.put("totalcost", totalcost.setScale(2, BigDecimal.ROUND_HALF_UP));
			map.put("profit", profit);
			map.put("profitmargin",profitmargin.setScale(2,BigDecimal.ROUND_HALF_UP));
		}else{
			map = Maps.newHashMap();
		}
		return map;
	}
	
	public Map<String, Object> cusAudit(String str,String admin,boolean isauto) {
		Map<String,Object> res = Maps.newHashMap();
		try {
			JsonNode node = Json.parse(str);
			String sno = node.get("sno").asText();
			SaleMain main = saleMainMapper.selectByOrderNo(sno);
			if(main != null){
				if(main.getStatus() == SaleOrderStatus.WAITING_AUDIT_BY_CS){
					OperateRecord autoConfirm = null;
					String msg = null;
					Integer warehouseId = main.getWarehouseId();
					if(node.get("csAudit").asBoolean()){
						msg = "客服确认";
						// add by xuse
						// 增加逻辑，内部订单、亏本订单、保税仓订单需要财务审核，其他订单不需要财务审核
						if (main.getDistributorType() == 3 // 内部分销商
								|| main.getDisMode() > 1 // 线下订单
								|| (warehouseId == 2029 || warehouseId == 2050)// 保税仓库
								|| checkProfit(main.getId()) // 亏本
						) {
							if(isauto) {
								Logger.info(main.getSalesOrderNo() + "订单亏本，需要手动审核。");
								return Maps.newHashMap();
							} else {
								// 待财务审核
								main.setStatus(SaleOrderStatus.WAITING_AUDIT_BY_FINANCE);
							}
						} else {
							autoConfirm = new OperateRecord();
							autoConfirm.setOrderId(main.getId());
							autoConfirm.setOperateType(2);
							autoConfirm.setResult(1);
							autoConfirm.setComment("订单符合审核条件，自动通过财务审核。");
							autoConfirm.setEmail("system");
							main.setStatus(SaleOrderStatus.WAITING_DELIVERY_SIX);
							//无需实际支付时审核通过，手动生成支付信息
							main.setPaymentNo(IDUtils.getPayNo());
							main.setPayDate(new Date());
							main.setPaymentType("system");
							main.setCurrency("CNY");
						}
						Logger.info(sno + "客服审核通过");
						//修改订单信息
						SaleBase base = saleBaseMapper.selectByOrderId(main.getId());
						base.setAddress(JsonCaseUtil.jsonToString(node.get("address")));
						base.setReceiver(JsonCaseUtil.jsonToString(node.get("receiver")));
						base.setTel(JsonCaseUtil.jsonToString(node.get("tel")));
						base.setPostCode(JsonCaseUtil.jsonToString(node.get("postCode")));
						base.setIdcard(JsonCaseUtil.jsonToString(node.get("idcard")));
						saleBaseMapper.updateByPrimaryKeySelective(base);
					}else{
						msg = "客服关闭";
						//客服关闭
						main.setStatus(SaleOrderStatus.AUDIT_NOT_PASSED);
					}
					boolean flag = saleMainService.updateSaleMainOrder(main);
					saleMainService.updateCouponsState(main);
					//判断是否已有审核通过的操作日志
					OperateRecord record = new OperateRecord();
					record.setOrderId(main.getId());
					record.setOperateType(10);
					Integer result = main.getStatus().equals(SaleOrderStatus.AUDIT_NOT_PASSED) ? 0 : 1;
					record.setResult(result);
					record.setComment(JsonCaseUtil.jsonToString(node.get("csRemark")));
					record.setEmail(admin);
					operateRecordMapper.insertSelective(record);
					if(null != autoConfirm) {
						Logger.info(main.getSalesOrderNo() + "系统自动通过财务审核状态。 ");
						operateRecordMapper.insertSelective(autoConfirm);
					}
					res.put("suc", flag);
					res.put("msg", msg+(flag?"成功":"失败"));
				}else{
					res.put("suc", false);
					res.put("msg", "该订单不是待客服确认状态,不能进行客服确认,请刷新页面!");
				}
			}else{
				res.put("suc", false);
				res.put("msg", "该订单不存在");
			}
		} catch (Exception e) {
			res.put("suc", false);
			res.put("msg", "客服审核异常");
			Logger.info("客服审核异常",e);
		}
		return res;
	}

	@Override
	public void autoConfirmReceipt() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void syncLogs(SaleMain main, String type) {
		// TODO Auto-generated method stub
		
	}

	
}
