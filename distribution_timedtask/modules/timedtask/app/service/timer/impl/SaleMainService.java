package service.timer.impl;

import java.util.Map;

import mapper.timer.SaleMainMapper;


import play.Logger;
import play.libs.Json;

import service.timer.ISaleMainService;
import service.timer.ISaleService;
import util.timer.Constant;
import util.timer.HttpUtil;
import util.timer.StringUtils;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

import entity.timer.SaleMain;


public class SaleMainService implements ISaleMainService {

	@Inject
	SaleMainMapper saleMainMapper;
	
	@Inject
	private ISaleService saleService;
	
	@Override
	public boolean updateSaleMainOrder(SaleMain saleMain) {
		saleService.syncLogs(saleMain, Constant.UPDATE);
		return saleMainMapper.updateByPrimaryKeySelective(saleMain)>0;
	}
	
	@Override
	public void updateCouponsState(SaleMain sm) {
		Map<String, String> sale = saleMainMapper.getSalesById(sm.getId());
		if (null != sale && StringUtils.isNotBlankOrNull(sale.get("couponscode"))) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("orderStatus", sm.getStatus());// 订单状态
			params.put("couponsNo", sale.get("couponscode"));// 优惠码
			if(sm.getStatus() == 5 || sm.getStatus() == 100) {
				params.put("istatus", 3);
			}
			String response = HttpUtil.post(Json.toJson(params).toString(),
					HttpUtil.B2BBASEURL + "/member/updateCoupons");
			Logger.info("updateCouponsState : " + response);
		}
	}

}
