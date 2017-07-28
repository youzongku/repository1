package constant.purchase;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * @author xu_shengen
 *
 */
public class Constant {

	// 申请状态
	public static Map<String, String> PURCHASE_TABLE_MAP = Maps.newHashMap();
	// 后台导出报价单
	public static Map<String, String> EXPORT_QUOTATION_MAP = Maps.newHashMap();

	public static String PURCHASE_KEY = "purchase_order";

	// 操作类型常量
	public final static String CRE = "create";
	public final static String DEL = "delelte";
	public final static String UPD = "update";

	static {
		EXPORT_QUOTATION_MAP.put("cname", "商品分类");
		EXPORT_QUOTATION_MAP.put("brand", "商品品牌");
		EXPORT_QUOTATION_MAP.put("csku", "SKU");
		EXPORT_QUOTATION_MAP.put("interBarCode", "国际条码");
		EXPORT_QUOTATION_MAP.put("ctitle", "商品名称");
		EXPORT_QUOTATION_MAP.put("disPrice", "分销价");
		EXPORT_QUOTATION_MAP.put("localPrice", "市场价");
		EXPORT_QUOTATION_MAP.put("qty", "采购数量");
		EXPORT_QUOTATION_MAP.put("batchNumber", "起批量");
		EXPORT_QUOTATION_MAP.put("packageType", "包装种类");
		EXPORT_QUOTATION_MAP.put("originCountry", "原产地");
		EXPORT_QUOTATION_MAP.put("plugType", "规格");
		EXPORT_QUOTATION_MAP.put("warehouseName", "所属仓");
		EXPORT_QUOTATION_MAP.put("productEnterprise", "生产厂家");
		EXPORT_QUOTATION_MAP.put("componentContent", "成分含量");
		EXPORT_QUOTATION_MAP.put("expirationDays", "保质期(月)");
		EXPORT_QUOTATION_MAP.put("stock", "库存");
		EXPORT_QUOTATION_MAP.put("packQty", "箱规");

		PURCHASE_TABLE_MAP.put("purchaseOrderNo", "订单编号");
		PURCHASE_TABLE_MAP.put("statusMes", "订单状态");
		PURCHASE_TABLE_MAP.put("sorderDate", "下单时间");
		PURCHASE_TABLE_MAP.put("spayDate", "支付/取消时间");
		PURCHASE_TABLE_MAP.put("email", "下单分销商");
		PURCHASE_TABLE_MAP.put("purchaseTotalAmount", "订单金额");
	}
}
