package services.sales.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

import dto.JsonResult;
import entity.sales.SaleInvoice;
import entity.sales.SaleMain;
import entity.sales.hb.SalesHBDelivery;
import mapper.sales.SaleInvoiceMapper;
import mapper.sales.SaleMainMapper;
import mapper.sales.hb.SalesHBDeliveryMapper;
import services.sales.ISaleInvoiceService;
import util.sales.Constant;
import util.sales.JsonCaseUtil;
import util.sales.StringUtils;

/**
 * @author zbc
 * 2017年6月22日 下午3:17:31
 */
public class SaleInvoiceService implements ISaleInvoiceService {
	
	@Inject private SaleInvoiceMapper invoiceMapper;
	@Inject private SaleMainMapper mainMapper;
	@Inject private SalesHBDeliveryMapper hbDeliveryMapper;
	
	@Override
	public JsonResult<SaleInvoice> checkVaildInvoice(JsonNode json){
		Boolean  isNeedInvoice = JsonCaseUtil.jsonToBoolean(json.get("isNeedInvoice"));
		SaleInvoice invoice = null;
		if(isNeedInvoice != null && isNeedInvoice){
			Integer invoiceType = JsonCaseUtil.jsonToInteger(json.get("invoiceType"));
			String  invoiceTitle = JsonCaseUtil.jsonToString(json.get("invoiceTitle"));
			if(invoiceType == null){
				return JsonResult.<SaleInvoice>newIns().result(false).msg("请选择发票类型");
			}
			if(Lists.newArrayList(Constant.INVOICE_TYPE_PERSON,Constant.INVOICE_TYPE_COMPANY).indexOf(invoiceType) == -1){
				return JsonResult.<SaleInvoice>newIns().result(false).msg("发票类型不存在");
			}
			if(invoiceType == Constant.INVOICE_TYPE_COMPANY && (StringUtils.isBlankOrNull(invoiceTitle)|| invoiceTitle.length()> 100)){
				return JsonResult.<SaleInvoice>newIns().result(false).msg("公司发票 发票抬头不能为空并且长度不能大于100");
			}
			invoice = new SaleInvoice(invoiceType, invoiceTitle);
		}
		return JsonResult.<SaleInvoice>newIns().result(true).data(invoice);
	}

	@Override
	public void save(SaleMain sm, String createUser, SaleInvoice invoice) {
		if(invoice != null){
			invoice.setSalesOrderNo(sm.getSalesOrderNo());
			invoice.setCreateUser(createUser);
			invoiceMapper.insertSelective(invoice);
			SaleMain main = new SaleMain(); 
			main.setId(sm.getId());
			main.setIsNeedInvoice(true);
			//更新为需要开发票
			mainMapper.updateByPrimaryKeySelective(main);
		}
	}

	@Override
	public void save(SalesHBDelivery delivery, String createUser, SaleInvoice invoice) {
		if(invoice != null){
			invoice.setSalesOrderNo(delivery.getSalesHbNo());
			invoice.setCreateUser(createUser);
			invoiceMapper.insertSelective(invoice);
			SalesHBDelivery _delivery = new SalesHBDelivery(); 
			_delivery.setId(delivery.getId());
			_delivery.setIsNeedInvoice(true);
			//更新为需要开发票
			hbDeliveryMapper.updateById(_delivery);
		}
	}

	@Override
	public JsonResult<?> getInvoice(String so) {
		SaleInvoice invoice = invoiceMapper.selectByOrderNo(so);
		return JsonResult.newIns().result(true).data(invoice);
	}
}
