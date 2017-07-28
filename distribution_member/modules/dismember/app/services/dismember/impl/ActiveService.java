package services.dismember.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import constant.dismember.Constant;
import dto.dismember.PageResultDto;
import entity.dismember.DisActive;
import entity.dismember.DisCoupons;
import mapper.dismember.DisActiveMapper;
import mapper.dismember.DisCouponsMapper;
import play.Logger;
import services.dismember.IActiveService;
import utils.dismember.DateUtils;
import utils.dismember.IDUtils;

public class ActiveService implements IActiveService {
	
	@Inject
	private DisActiveMapper activeMapper;
	@Inject
	private DisCouponsMapper couponsMapper;
	
	@Override
	public Map<String, Object> saveActive(DisActive active) {
		Map<String, Object> res = new HashMap<String, Object>();
		active.setCreateDate(new Date());//添加时间
		try {
			active.setValidDateEnd(DateUtils.string2date(active.getValidDateEndtStr(), DateUtils.FORMAT_FULL_DATETIME));
			active.setValidDateStart(DateUtils.string2date(active.getValidDateStartStr(), DateUtils.FORMAT_FULL_DATETIME));
			boolean falg = activeMapper.insertSelective(active)>0;
			if(!falg){
				res.put("suc", false);
				res.put("info", "新增优惠活动失败");
				return res;
			}
			
			Integer activteId = active.getId();//获取活动id
			Integer length = active.getCouponsLenght();//优惠码长度
			Integer publishQty  = active.getPublishQty();//发型数量
			List<DisCoupons> list = new  ArrayList<DisCoupons>();
			for(int i = 0;i<publishQty;i++){
				DisCoupons coupons = new DisCoupons();
				coupons.setActiveId(activteId);
				coupons.setCouponsNo(IDUtils.getStringRandom(length));
				list.add(coupons);
			}
			couponsMapper.batchSaveCoupons(list);
			res.put("suc", true);
			res.put("info", activteId);
		} catch (ParseException e) {
			Logger.error("时间格式转换异常",e);
		}
		return res;
	}

	@Override
	public PageResultDto queryPageActive(JsonNode node) {
		execute();
		Map<String,Object> param = new  HashMap<String,Object>();
		param.put("status", node.has("status")?Integer.valueOf(node.get("status").asText()):null);
		param.put("createStartDate",node.has("createStartDate")?node.get("createStartDate").asText():null);
		param.put("createEndDate", node.has("createEndDate")?node.get("createEndDate").asText():null);
		param.put("seachSpan", node.has("seachSpan")?node.get("seachSpan").asText():null);
		param.put("pageSize", node.has("pageSize")?Integer.valueOf(node.get("pageSize").asInt()):null);
		param.put("currPage", node.has("currPage")?Integer.valueOf(node.get("currPage").asInt()):null);

		//排序
		param.put("sidx", node.has("sidx")?node.get("sidx").asText():null);
		param.put("sord", node.has("sord")?node.get("sord").asText():null);

		List<DisActive>  list = activeMapper.queryPageActive(param);
		for (DisActive disActive : list) {
			disActive.setState(Constant.ACTIVE_STATE_MAP.get(disActive.getStatus()));
		}
		int totalCount = activeMapper.queryTotalCount(param);
		//处理如果不传分页参数 则处理为10，前台不作为分页依据，而是单纯查数据
		Integer pageSize = param.get("pageSize")!=null?Integer.valueOf(param.get("pageSize").toString()):10;
		Integer currPage = param.get("currPage")!=null?Integer.valueOf(param.get("currPage").toString()):1;
		Integer totalPage = totalCount % pageSize > 0 ? totalCount / pageSize + 1 : totalCount / pageSize;
		PageResultDto page = new PageResultDto(pageSize, totalPage, currPage,list);
		page.setTotalCount(totalCount);
		return page;
	}

	@Override
	public PageResultDto queryPageCoupons(JsonNode main) {
		Map<String,Object> param = new  HashMap<String,Object>();
		param.put("activeId", main.has("activeId")?Integer.valueOf(main.get("activeId").asText()):null);
		param.put("status", main.has("status")?Integer.valueOf(main.get("status").asText()):null);
		param.put("pageSize", main.has("pageSize")?Integer.valueOf(main.get("pageSize").asInt()):null);
		param.put("currPage", main.has("currPage")?Integer.valueOf(main.get("currPage").asInt()):null);
		param.put("usedStartDate",main.has("usedStartDate")?main.get("usedStartDate").asText():null);
		param.put("usedEndDate", main.has("usedEndDate")?main.get("usedEndDate").asText():null);
		param.put("seachSpan", main.has("seachSpan")?main.get("seachSpan").asText():null);
		//处理如果不传分页参数 则处理为10，前台不作为分页依据，而是单纯查数据
		Integer pageSize = param.get("pageSize")!=null?Integer.valueOf(param.get("pageSize").toString()):10;
		Integer currPage = param.get("currPage")!=null?Integer.valueOf(param.get("currPage").toString()):1;
		List<DisCoupons>  list = couponsMapper.queryPageCoupons(param);
		for (DisCoupons disCoupons : list) {
			disCoupons.setState(Constant.COUPONS_STATE_MAP.get(disCoupons.getIstatus()));
			if(disCoupons.getOrderNo() != null) {
				String orderNo = disCoupons.getOrderNo().toUpperCase();
				if(orderNo.startsWith("XS") || orderNo.startsWith("MD")) {
					disCoupons.setOrderState(Constant.SALES_STATE_MAP.get(disCoupons.getOrderStatus()));
				} else {
					disCoupons.setOrderState(Constant.PURCHASE_STATE_MAP.get(disCoupons.getOrderStatus()));
				}
			} else {
				disCoupons.setOrderState("--");
			}
		}
		int totalCount = couponsMapper.queryTotalCount(param);
		Integer totalPage = totalCount % pageSize > 0 ? totalCount / pageSize + 1 : totalCount / pageSize;
		PageResultDto page = new PageResultDto(pageSize, totalPage, currPage,list);
		page.setTotalCount(totalCount);
		return page;
	}
	
	@Override
	public List<DisCoupons> queryCoupons(Map<String,Object>param){
		List<DisCoupons>  list = couponsMapper.queryPageCoupons(param);
		for (DisCoupons disCoupons : list) {
			disCoupons.setState(Constant.COUPONS_STATE_MAP.get(disCoupons.getIstatus()));
			if(disCoupons.getOrderNo() != null) {
				String orderNo = disCoupons.getOrderNo().toUpperCase();
				if(orderNo.startsWith("XS") || orderNo.startsWith("MD")) {
					disCoupons.setOrderState(Constant.SALES_STATE_MAP.get(disCoupons.getOrderStatus()));
				} else {
					disCoupons.setOrderState(Constant.PURCHASE_STATE_MAP.get(disCoupons.getOrderStatus()));
				}
			}
		}
		return list;
	};
	
	@Override
	public Map<String, Object> updateCoupons(DisCoupons coupons) {
		Map<String, Object> res = new HashMap<String,Object>();
		boolean flag = couponsMapper.updateCoupons(coupons)>0;
		String info = flag ? "优惠码数据更新成功！" : "优惠码数据更新失败！";
		res.put("suc", flag);
		res.put("info", info);
		return res;
	}

	@Override
	public Map<String, Object> getCouponsInfo(String couponsNo, Double orderAmount) {
		Map<String, Object> res = new HashMap<String,Object>();
		DisCoupons coupons = couponsMapper.getCoupons(couponsNo);
		if(coupons == null){
			res.put("suc", false);
			res.put("info", "该优惠码不存在");
			return res;
		}
		DisActive active = activeMapper.selectByPrimaryKey(coupons.getActiveId());
		if(coupons.getIstatus() != 0){
			res.put("suc", false);
			res.put("info", "该优惠码"+ Constant.COUPONS_STATE_MAP.get(coupons.getIstatus()));
			return res;
		}
		
		boolean canUse = active.getStatus() == 0;
		//活动状态不可使用
		if (!canUse) {
			res.put("suc", false);
			res.put("info", "活动已过期");
			return res;
		}
		
		Date now = new Date();
		//活动状态可使用
		if(now.before(active.getValidDateStart())||now.after(active.getValidDateEnd())){
			res.put("suc", false);
			res.put("info", "优惠活动还没到或者已过期");
			return res;
		}
		
		//获取成功判断门槛金额
		if(orderAmount >= active.getThresholdPrice().doubleValue()){
			res.put("suc", true);
			res.put("coupons", coupons);
			res.put("active", active);
			return res;
		}
		
		res.put("suc", false);
		res.put("info", "未达到优惠门槛:"+ active.getThresholdPrice().doubleValue());
		return res;
	}

	@Override
	public void execute() {
		Date now = new Date();
		Map<String, Object> param = Maps.newHashMap();
		param.put("nowDate", now);
		param.put("status",2);
		List<DisActive> actives = activeMapper.queryInnerTimeActive(param);
		DisActive active = null;
		int rows = 0;
		if(actives != null && actives.size() > 0) {
			for (DisActive disActive : actives) {
				Logger.info("更新正常活动状态：" + disActive.getCouponsName());
				active = new DisActive();
				active.setId(disActive.getId());
				active.setStatus(0);
				activeMapper.updateByPrimaryKeySelective(active);
				rows = couponsMapper.updateState(active);
				Logger.info("更新正常优惠码状态：" + rows);
			}
		}
		param.put("status",0);//
		List<DisActive> actives2 = activeMapper.queryOutTimeActive(param);
		if(actives2 != null && actives2.size() > 0) {
			for (DisActive disActive : actives2) {
				Logger.info("更新过期活动状态：" + disActive.getCouponsName());
				active = new DisActive();
				active.setId(disActive.getId());
				active.setStatus(1);
				activeMapper.updateByPrimaryKeySelective(active);
				active.setStatus(4);
				rows = couponsMapper.updateState(active);
				Logger.info("更新过期优惠码状态：" + rows);
			}
		}
	}

	@Override
	public DisActive getActive(Integer activeId) {
		return activeMapper.selectByPrimaryKey(activeId);
	}
}
