package util.sales;

import java.util.Map;

import com.google.common.collect.Maps;

public final class SalesStatusUtil {
	private static Map<String,String> STATUS_MAP = Maps.newHashMap();
	static{
		STATUS_MAP.put("Payment_Pending","1");//1：待采购
		STATUS_MAP.put("On_Hold","3");//3：待客服审核
		STATUS_MAP.put("Audit_Not_Passed","4");//4：审核不通过
		STATUS_MAP.put("Order_Cancelled","5");//5：已取消
		STATUS_MAP.put("Audit_Passed","6");//6: 审核通过
		STATUS_MAP.put("Dispatched","9");//9：待收货
		STATUS_MAP.put("Completed","10");//10：已收货
		STATUS_MAP.put("Shipping_Fee","103");//103: 待支付运费
		STATUS_MAP.put("Order_Processing","104");//104：处理中
	}
	
	public static boolean contains(String status){
		return STATUS_MAP.containsKey(status);
	}
	
	public static String get(String status){
		if(contains(status)){
			return STATUS_MAP.get(status);
		}
		return "";
	}
	
	public static String getKey(String value){
		for(Map.Entry<String, String> entry : STATUS_MAP.entrySet()){
			if(value.equals(entry.getValue())){
				return entry.getKey();
			}
		}
		return null;
	} 
}
