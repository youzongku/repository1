package util.sales;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * @author xu_shengen
 *
 */
public class Constant {
	
	//营销单导出列头 财务
	public static Map<String, String> EXPORT_MARKET_ORDER_MAP_FIN = Maps.newHashMap();
	//营销单导出列头 财务
	public static Map<String, String> EXPORT_MARKET_ORDER_MAP_CS = Maps.newHashMap();
	//后台销售发货订单导出列头
	public static Map<String, String> EXPORT_SALE_ORDER_MAP = Maps.newLinkedHashMap();
	//后台营销单导出列头
	public static Map<String, String> EXPORT_MO_ORDER_MAP = Maps.newHashMap();
	//京东订单状态
	public static Map<String,String> JD_ORDER_STATE  = Maps.newHashMap();
	//客户订单状态-前端
	public static Map<Integer,String> SALES_ORDER_STATE_FRONT = Maps.newHashMap();
	//客户订单状态-后台
	public static Map<Integer,String> SALES_ORDER_STATE_MANAGER = Maps.newHashMap();
	//有赞订单状态       
	public static Map<String,String> YZ_ORDER_STATE = Maps.newHashMap();
	//物流导出列头
	public static Map<String,String> EXOPRT_LOGISTICS_INFO = Maps.newHashMap();
	//分销商类型
	public static Map<Integer,String> DIS_TYPE_AMP = Maps.newHashMap();
	
	//拼多多物流对应关系
	public static Map<String,String> PDD_LOGISTICS = Maps.newHashMap();
	
	//有赞物流对应关系
	public static Map<String,String> YZ_LOGISTICS = Maps.newHashMap();
	
	//销售模块key
	public static String SALES_KEY = "sales_order";
	public static String CREATE = "create";
	public static String UPDATE = "update";
	public static String DELETE = "delete";
	/********************************* 发货单合同费用常量 start *******************************************************/
	
	public static Map<Integer, String> DISTRIBUTIONTYPE = Maps.newHashMap();
	
	public static Map<Integer, String> DISTRIBUTIONMODE = Maps.newHashMap();
	
	public static Map<String,String> CONTRACT_FEE_MAP = Maps.newHashMap();

	public static final String CONTRACT_QUOTED = "quoted";
	
	public static final String CONTRACT_FEE_RATE = "rate";
	
	public static final String CONTRACT_ESTIMATED_COST = "es_cost";
	
	public static final String CONTRACT_ESTIMATED_COST_TOTAL = "es_cost_total";
	
	public static final String CONTRACT_ORDER_ESTIMATED_COST_TOTAL = "od_es_cost_total";
	
	public static final String CONTRACT_ACTUAL_COST = "ac_cost";
	
	public static final String CONTRACT_ACTUAL_COST_TOTAL = "ac_cost_total";
	
	public static final String CONTRACT_ORDER_ACTUAL_COST_TOTAL = "od_ac_cost_total";
	
	/********************************* 发货单合同费用常量   end  *******************************************************/
	/******************************  发货单发票常量 start ***********************************************************/
	
	public static final int INVOICE_TYPE_PERSON = 1;
	
	public static final int INVOICE_TYPE_COMPANY = 2;
	
	/******************************  发货单发票常量   end  ***********************************************************/
	static {
		
		CONTRACT_FEE_MAP.put(CONTRACT_QUOTED, "报价（元）");
		CONTRACT_FEE_MAP.put(CONTRACT_ESTIMATED_COST,"预估费用（元）");
		CONTRACT_FEE_MAP.put(CONTRACT_ESTIMATED_COST_TOTAL, "合同内订单预估费用合计（元）");
		CONTRACT_FEE_MAP.put(CONTRACT_ORDER_ESTIMATED_COST_TOTAL, "整笔订单预估费用合计（元）");
		CONTRACT_FEE_MAP.put(CONTRACT_ACTUAL_COST, "实际费用（元）");
		CONTRACT_FEE_MAP.put(CONTRACT_ACTUAL_COST_TOTAL, "合同内订单实际费用合计（元）");
		CONTRACT_FEE_MAP.put(CONTRACT_ORDER_ACTUAL_COST_TOTAL, "整笔订单实际费用合计（元）");
		
		
		
		/*"序列", "下单日期", "录入人", "订单编号（YX）",
		"样品SKU", "发货单号（XS）", "SKU","中文品名",
		"数量", "到仓价","到仓价小计","SKU数量小计", "备注（业务备注的内容）"*/
		EXPORT_MARKET_ORDER_MAP_FIN.put("seq", "序列");
		EXPORT_MARKET_ORDER_MAP_FIN.put("createDateStr", "下单日期");
		EXPORT_MARKET_ORDER_MAP_FIN.put("createUser", "录入人");
		EXPORT_MARKET_ORDER_MAP_FIN.put("marketingOrderNo", "订单编号（YX）");
		EXPORT_MARKET_ORDER_MAP_FIN.put("salesOrderNo", "发货单号（XS）");
		EXPORT_MARKET_ORDER_MAP_FIN.put("sku", "SKU");
		EXPORT_MARKET_ORDER_MAP_FIN.put("productName", "中文品名");
		EXPORT_MARKET_ORDER_MAP_FIN.put("qty", "数量");
		EXPORT_MARKET_ORDER_MAP_FIN.put("arriveWarePrice", "到仓价");
		EXPORT_MARKET_ORDER_MAP_FIN.put("arriveTotal", "到仓价小计");
		EXPORT_MARKET_ORDER_MAP_FIN.put("totalQty", "SKU数量小计");
		EXPORT_MARKET_ORDER_MAP_FIN.put("businessRemark", "备注（业务备注的内容）");
		
		DIS_TYPE_AMP.put(1, "普通分销商");
		DIS_TYPE_AMP.put(2, "合营分销商");
		DIS_TYPE_AMP.put(3, "内部分销商");
		
		YZ_ORDER_STATE.put("TRADE_NO_CREATE_PAY ", "没有创建支付交易");
		YZ_ORDER_STATE.put("WAIT_BUYER_PAY", "等待买家付款");
		YZ_ORDER_STATE.put("WAIT_PAY_RETURN", "等待支付确认");
		YZ_ORDER_STATE.put("WAIT_GROUP", "等待成团");
		YZ_ORDER_STATE.put("WAIT_SELLER_SEND_GOODS", "等待卖家发货");
		YZ_ORDER_STATE.put("WAIT_BUYER_CONFIRM_GOODS ", "等待买家确认收货");
		YZ_ORDER_STATE.put("TRADE_BUYER_SIGNED", "买家已签收");
		YZ_ORDER_STATE.put("TRADE_CLOSED", "付款以后用户退款成功，交易自动关闭");
		YZ_ORDER_STATE.put("TRADE_CLOSED_BY_USER", "付款以前，卖家或买家主动关闭交易");
		
		// 发货单
		EXPORT_SALE_ORDER_MAP.put("email","客户账号");
		EXPORT_SALE_ORDER_MAP.put("nickName","客户名称");
		EXPORT_SALE_ORDER_MAP.put("saleOrderNO","订单编号");
		EXPORT_SALE_ORDER_MAP.put("erpOrderNo","ERP订单号");
		EXPORT_SALE_ORDER_MAP.put("orderDateStr","下单时间");
		EXPORT_SALE_ORDER_MAP.put("status","订单状态");
//		EXPORT_SALE_ORDER_MAP.put("disAccount","分销商账号");
		EXPORT_SALE_ORDER_MAP.put("erp","业务员");
		EXPORT_SALE_ORDER_MAP.put("distributionModelStr","分销渠道");
		EXPORT_SALE_ORDER_MAP.put("distributorType","分销商类型");
		EXPORT_SALE_ORDER_MAP.put("warehouseName","发货仓库");
		EXPORT_SALE_ORDER_MAP.put("shopName","店铺名称");
		EXPORT_SALE_ORDER_MAP.put("platformOrderNo","平台单号");
		EXPORT_SALE_ORDER_MAP.put("tradeNo","交易号");
		EXPORT_SALE_ORDER_MAP.put("receiver","收货人姓名");
		EXPORT_SALE_ORDER_MAP.put("tel","收货人电话");
		EXPORT_SALE_ORDER_MAP.put("receiverIDcard","收货人身份证号");
		EXPORT_SALE_ORDER_MAP.put("address","收货人地址");
		EXPORT_SALE_ORDER_MAP.put("platformAmount","毛收入");
		EXPORT_SALE_ORDER_MAP.put("sku","商品编号");
		EXPORT_SALE_ORDER_MAP.put("productName","商品名称");
		EXPORT_SALE_ORDER_MAP.put("qty","商品QTY");
		EXPORT_SALE_ORDER_MAP.put("expirationDate","到期日期");
		EXPORT_SALE_ORDER_MAP.put("capfee","均摊价");
		EXPORT_SALE_ORDER_MAP.put("finalSellingPrice","真实售价");
		EXPORT_SALE_ORDER_MAP.put("orderActualAmount","店铺实收金额");
		EXPORT_SALE_ORDER_MAP.put("orderActualPayAmount","BBC付款金额");
		EXPORT_SALE_ORDER_MAP.put("bbcPostage","分销平台运费");
		EXPORT_SALE_ORDER_MAP.put("remark","订单备注");
		
//		EXPORT_SALE_ORDER_MAP.put("buyerId","Buyer ID");
//		EXPORT_SALE_ORDER_MAP.put("cost","裸采购价");
//		EXPORT_SALE_ORDER_MAP.put("payAccount","付款账户");暂时没有舍去
		
		// 营销单
		EXPORT_MO_ORDER_MAP.put("marketingOrderNo", "订单编号");
		EXPORT_MO_ORDER_MAP.put("createDateStr", "下单时间");
		EXPORT_MO_ORDER_MAP.put("salesOrderNo", "发货单号");
		EXPORT_MO_ORDER_MAP.put("statusMsg", "订单状态");
		EXPORT_MO_ORDER_MAP.put("email", "分销商");
		EXPORT_MO_ORDER_MAP.put("nickName", "分销商名称");
		EXPORT_MO_ORDER_MAP.put("distributorTypeStr", "分销商类型");
		EXPORT_MO_ORDER_MAP.put("createUser", "录入人");
		EXPORT_MO_ORDER_MAP.put("salesman", "业务员");
		EXPORT_MO_ORDER_MAP.put("businessRemark", "业务备注");
		
		//京东订单状态集合
		JD_ORDER_STATE.put("WAIT_SELLER_STOCK_OUT","等待出库");
		JD_ORDER_STATE.put("SEND_TO_DISTRIBUTION_CENER","发往配送中心");
		JD_ORDER_STATE.put("DISTRIBUTION_CENTER_RECEIVED","发往配送中心");
		JD_ORDER_STATE.put("WAIT_GOODS_RECEIVE_CONFIRM","等待确认收货");
		JD_ORDER_STATE.put("RECEIPTS_CONFIRM","收款确认（服务完成）");
		JD_ORDER_STATE.put("WAIT_SELLER_DELIVERY","等待发货");
		JD_ORDER_STATE.put("FINISHED_L","完成");
		JD_ORDER_STATE.put("TRADE_CANCELED","取消");
		JD_ORDER_STATE.put("LOCKED","已锁定");
		JD_ORDER_STATE.put("PAUSE","暂停");
		
		//发货单状态
		/*
		 * 1：待采购，
		 * 2：待确认，
		 * 3：待客服审核，
		 * 4：审核不通过，
		 * 5：已取消,
		 * 6:审核通过,
		 * 7:待发货，
		 * 8：发货失败，
		 * 9：待收货，
		 * 10：已收货，
		 * 100：售后待审核，
		 * 101：售后审核通过，
		 * 102：售后审核不通过 ,
		 * 103:待支付运费,
		 * 104,处理中
		 * 105,已发货
		 * 106,已完成,
		 * 107,订单挂起（存疑的状态）,
		 * 108,已退款
		 */
		// 前台待付款，后台分为待付款、待付运费
		SALES_ORDER_STATE_FRONT.put(1,"待付款");
		SALES_ORDER_STATE_FRONT.put(103,"待付款");
		SALES_ORDER_STATE_MANAGER.put(1,"待付款");
		SALES_ORDER_STATE_MANAGER.put(103,"待付款");
		// 待确认，前台后台一样
		SALES_ORDER_STATE_FRONT.put(2, "待确认");
		SALES_ORDER_STATE_MANAGER.put(2, "待用户确认");
		
		// TODO 待完善
		SALES_ORDER_STATE_FRONT.put(3,"待审核");
		SALES_ORDER_STATE_FRONT.put(11,"待审核");
		SALES_ORDER_STATE_FRONT.put(12,"待审核");
		SALES_ORDER_STATE_MANAGER.put(3,"待客服确认");
		SALES_ORDER_STATE_MANAGER.put(11,"待财务确认");
		SALES_ORDER_STATE_MANAGER.put(12,"待二次支付");
		
		SALES_ORDER_STATE_FRONT.put(6,"待发货");
		SALES_ORDER_STATE_FRONT.put(7,"待发货");
		SALES_ORDER_STATE_FRONT.put(13,"待发货");
		SALES_ORDER_STATE_FRONT.put(104,"待发货");
		SALES_ORDER_STATE_MANAGER.put(6,"待发货");
		SALES_ORDER_STATE_MANAGER.put(7,"待发货");
		SALES_ORDER_STATE_MANAGER.put(13,"待发货（数据传输中）");// 已推送hk -> 待发货（数据传输中）
		SALES_ORDER_STATE_MANAGER.put(104,"待发货（erp）");// 已推送erp -> 待发货（erp）
		
		SALES_ORDER_STATE_FRONT.put(9,"待收货");
		SALES_ORDER_STATE_MANAGER.put(9,"待收货");
		
		// 已完成，前台后台一样
		SALES_ORDER_STATE_FRONT.put(106,"已完成");
		SALES_ORDER_STATE_MANAGER.put(106,"已完成");
		
		SALES_ORDER_STATE_FRONT.put(4,"审核不通过");// 审核不通过
		SALES_ORDER_STATE_FRONT.put(5,"已关闭");// 客户关闭
//		SALES_ORDER_STATE_FRONT.put(14,"已关闭");// 客服关闭，其实就是客服审核不通过
		SALES_ORDER_STATE_FRONT.put(20, "已关闭");// erp关闭
		SALES_ORDER_STATE_MANAGER.put(4,"已关闭");
		SALES_ORDER_STATE_MANAGER.put(5,"已关闭");
//		SALES_ORDER_STATE_MANAGER.put(14,"客服关闭");
		SALES_ORDER_STATE_MANAGER.put(20, "已关闭");
		

		// TODO 待完善
		SALES_ORDER_STATE_FRONT.put(10,"已完成");
		SALES_ORDER_STATE_MANAGER.put(10,"已完成");
		
		//物流导出列头
		EXOPRT_LOGISTICS_INFO.put("platformOrderNo", "订单号");
		EXOPRT_LOGISTICS_INFO.put("logisticsMode", "物流公司（中文）");
		EXOPRT_LOGISTICS_INFO.put("trackingNumber", "物流单号");
		EXOPRT_LOGISTICS_INFO.put("saleOrderNO", "BBC订单编号");
		EXOPRT_LOGISTICS_INFO.put("buyerId", "客户昵称");
		EXOPRT_LOGISTICS_INFO.put("receiver", "收件人姓名");
		EXOPRT_LOGISTICS_INFO.put("tel", "收件人手机号");
		
		PDD_LOGISTICS.put("JAN", "208");
		PDD_LOGISTICS.put("JS", "44");
		PDD_LOGISTICS.put("JY", "121");
		PDD_LOGISTICS.put("JYT", "85");
		PDD_LOGISTICS.put("JE", "132");//pdd 邮政小包
		
		YZ_LOGISTICS.put("JE", "8");//邮政小包
		YZ_LOGISTICS.put("JS", "42");//顺丰快递
		YZ_LOGISTICS.put("JYT", "2");//圆通快递
		YZ_LOGISTICS.put("JAN", "128");//安能物流
		YZ_LOGISTICS.put("JBS", "6");//百世汇通
		YZ_LOGISTICS.put("JY", "4");//韵达快递
		YZ_LOGISTICS.put("JZ", "3");//中通速递
		
		DISTRIBUTIONTYPE.put(1, "普通分销商");
		DISTRIBUTIONTYPE.put(2, "合营分销商");
		DISTRIBUTIONTYPE.put(3, "内部分销商");
		
		DISTRIBUTIONMODE.put(1, "电商");
		DISTRIBUTIONMODE.put(2, "经销商");
		DISTRIBUTIONMODE.put(3, "KA直营");
		DISTRIBUTIONMODE.put(4, "进口专营");
		DISTRIBUTIONMODE.put(5, "VIP");
	}
}
