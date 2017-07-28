package services.sales.impl;

import mapper.sales.SaleBaseMapper;

import java.util.Map;

import com.google.inject.Inject;

import entity.sales.SaleBase;
import services.sales.ISaleBaseService;

public class SaleBaseService implements ISaleBaseService {

	@Inject private SaleBaseMapper saleBaseMapper;

	/**
	 * 添加销售订单基本信息
	 */
	@Override
	public SaleBase saveSaleBaseOrder(SaleBase saleBase) {
		int result = saleBaseMapper.insertSelective(saleBase);
		if (result > 0) {
			return saleBase;
		}
		return null;
	}

	@Override
	public boolean updateSaleBaseOrder(SaleBase saleBase) {
		int result = saleBaseMapper.updateByPrimaryKey(saleBase);
		return result > 0;
	}

	@Override
	public SaleBase getSaleBaseByOrderId(Integer orderId) {
		return saleBaseMapper.selectByOrderId(orderId);
	}

	@Override
	public Map getOrderer(String salesOrderNo) {
		return saleBaseMapper.getOrderer(salesOrderNo);
	}

	@Override
	public SaleBase getSaleBaseByOrderNo(String salesOrderNo) {
		return saleBaseMapper.getSaleBaseByOrderNo(salesOrderNo);
	}

}
