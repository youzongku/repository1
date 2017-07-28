package util.sales;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;

import entity.platform.order.template.TaoBaoOrder;
import entity.platform.order.template.TaoBaoOrderGoods;

public class TitleUtils {

	public static String[] getTBOrderHeader() {
		String tbo = "订单编号, 买家会员名, 买家支付宝账号, 支付交易号, 买家应付货款, 买家应付邮费, 买家支付积分, 总金额, 返点积分, 买家实际支付金额, 买家实际支付积分, 订单状态, 买家留言, 收货人姓名, 收货地址 , 运送方式, 联系电话 , 联系手机, 订单创建时间, 订单付款时间 , 宝贝标题 , 宝贝种类 , 物流单号 , 物流公司, 订单备注, 宝贝总数量, 店铺Id, 店铺名称, 订单关闭原因, 卖家服务费, 买家服务费, 发票抬头, 是否手机订单, 分阶段订单信息, 定金排名, 修改后的sku, 修改后的收货地址, 异常信息, 天猫卡券抵扣, 集分宝抵扣, 是否是O2O交易, 买家姓名, 买家身份证号, 买家手机号";
		String[] split = tbo.split(",");
		return split;
	}

	public static String[] getTBGoodHeader() {
		String tbg = "订单编号, 标题, 价格, 购买数量, 外部系统编号, 商品属性, 套餐信息, 备注, 订单状态, 商家编码";
		String[] split = tbg.split(",");
		return split;
	}


	public static Map<String, String> tbOrderTitleToEntity() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("买家会员名", "buyerAccount");
		map.put("收货人姓名", "receiverName");
		// map.put("收货人身份证号", "receiverCardNumber");
		map.put("联系手机", "receiverPhone");
		map.put("修改后的收货地址", "address");
		map.put("店铺名称", "shopName");
		map.put("订单编号", "orderNo");
		map.put("总金额", "orderTotal");
		map.put("订单状态", "orderStatus");
		map.put("订单创建时间", "paymentDate");
		map.put("联系电话", "receiverTelephone");
		map.put("收货地址", "receiverAddress");
		// map.put("支付交易号", "paymentNo");
		// map.put("支付人电话", "paymentPhone");
		// map.put("支付人姓名", "paymentName");
		// map.put("支付人身份证号", "paymentCardNumber");
		map.put("订单备注", "sellerRemark");
		map.put("买家留言", "buyerMessage");
		map.put("发票抬头", "invoiceInfo");
		// map.put("邮政编码", "postCode");
		map.put("买家应付邮费", "logisticsCost");
		return map;
	}

	public static Map<String, String> tbOrderGoodsTitleToEntity() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("订单编号", "orderNo");
		map.put("标题", "goodsTitle");
		map.put("购买数量", "amount");
		map.put("外部系统编号", "sku");
		map.put("价格", "price");
		return map;
	}

	public static TaoBaoOrder setOrderField(TaoBaoOrder order, String field, String value) {
		if ("buyerAccount".equals(field)) {
			order.setBuyerAccount(value);
		}
		if ("receiverName".equals(field)) {
			order.setReceiverName(value);
		}
		/*if ("receiverCardNumber".equals(field)) {
			order.setReceiverCardNumber(value);[^\\w]|_
		}*/
		if ("receiverPhone".equals(field)) {
			order.setReceiverPhone(StringUtils.isBlank(value) ? null : value.replace(".00", "").replaceAll("[^\\w]|_", ""));
		}
		//添加联系电话
		if ("receiverTelephone".equals(field)) {
			order.setReceiverTelephone(StringUtils.isBlank(value) ? null : value.replace(".00", "").replaceAll("'", "").replaceAll("'", ""));
		}
		if ("address".equals(field) && !Strings.isNullOrEmpty(value)) {
			order.setAddress(value);
		}
		//如果修改后的收货地址为空，则取收货地址
		if ("receiverAddress".equals(field) && Strings.isNullOrEmpty(order.getAddress())) {
			order.setAddress(value);
		}
		if ("shopName".equals(field)) {
			order.setShopName(value);
		}
		if ("orderNo".equals(field)) {
			order.setOrderNo(StringUtils.isBlank(value) ? null : value.replace(".00", "").replaceAll("[^\\w]|_", ""));
		}
		if ("orderTotal".equals(field)) {
			order.setOrderTotal(StringUtils.isBlank(value) ? null : Double.parseDouble(value));
		}
		if ("orderStatus".equals(field)) {
			order.setOrderStatus(value);
		}
		if ("paymentDate".equals(field)) {
			if (!Strings.isNullOrEmpty(value)){
				order.setPaymentDateStr(value);
			}
		}
		/*
		 * if("paymentNo".equals(field)){order.setPaymentNo(value);}
		 * if("paymentPhone".equals(field)){order.setPaymentPhone(value);}
		 * if("paymentName".equals(field)){order.setPaymentName(value);}
		 * if("paymentCardNumber".equals(field)){order.setPaymentCardNumber(
		 * value);}
		 */
		if ("sellerRemark".equals(field)) {
			order.setSellerRemark(value);
		}
		if ("buyerMessage".equals(field)) {
			order.setBuyerMessage(value);
		}
		if ("invoiceInfo".equals(field)) {
			order.setInvoiceInfo(value);
		}
		/* if("postCode".equals(field)){order.setPostCode(value);} */
		if ("logisticsCost".equals(field)) {
			order.setLogisticsCost(StringUtils.isBlank(value) ? null : Double.parseDouble(value));
		}
		return order;
	}

	public static TaoBaoOrderGoods setGoodsField(TaoBaoOrderGoods goods, String field, String value) {
		if ("orderNo".equals(field)) {
			goods.setOrderNo(StringUtils.isBlank(value) ? null : value.replace(".00", "").replaceAll("[^\\w]|_", ""));
		}
		if ("goodsTitle".equals(field)) {
			goods.setGoodsTitle(value);
		}
		if ("amount".equals(field)) {
			goods.setAmount(StringUtils.isBlank(value) ? null : Integer.parseInt(value));
		}
		if ("sku".equals(field)) {
			goods.setSku(value);
		}
		if ("price".equals(field)) {
			goods.setPrice(StringUtils.isBlank(value) ? null : Double.parseDouble(value));
		}
		return goods;
	}
}
