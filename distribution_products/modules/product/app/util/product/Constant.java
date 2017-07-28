package util.product;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author zbc
 * 2016年7月30日 下午3:13:33
 */
public class Constant {
	
	//修改价格类型 与描述
	public static Map<String, String> PRICE_TYPE_MAP = Maps.newHashMap();
	
	public static Map<Integer, String> DISTRIBUTIONTYPE = Maps.newHashMap();
	
	public static Map<Integer, String> DISTRIBUTIONMODE = Maps.newHashMap();
	
	public static List<String> FILES_TYPE = Lists.newArrayList();
	
	public static Map<Integer,String> CONTRACT_COST_STATUS = Maps.newHashMap();

	static {
		PRICE_TYPE_MAP.put("floorPrice", "市场最低价");
		PRICE_TYPE_MAP.put("supermarketPrice", "KA经销价格");
		PRICE_TYPE_MAP.put("electricityPrices", "Bbc价格");
		PRICE_TYPE_MAP.put("distributorPrice", "经销商价格");
		PRICE_TYPE_MAP.put("proposalRetailPrice", "零售价");
		PRICE_TYPE_MAP.put("disCompanyCost", "营销成本价");
		PRICE_TYPE_MAP.put("ftzPrice", "自贸区经销价格");
		PRICE_TYPE_MAP.put("marketInterventionPrice", "市场干预供货价");
		PRICE_TYPE_MAP.put("vipPrice", "VIP价格");
		
		DISTRIBUTIONTYPE.put(1, "普通分销商");
		DISTRIBUTIONTYPE.put(2, "合营分销商");
		DISTRIBUTIONTYPE.put(3, "内部分销商");
		
		DISTRIBUTIONMODE.put(1, "电商");
		DISTRIBUTIONMODE.put(2, "经销商");
		DISTRIBUTIONMODE.put(3, "KA直营");
		DISTRIBUTIONMODE.put(4, "进口专营");
		DISTRIBUTIONMODE.put(5, "VIP");
		
		FILES_TYPE.add(".jpg");
		FILES_TYPE.add(".png");
		FILES_TYPE.add(".gif");
		FILES_TYPE.add(".pdf");
		FILES_TYPE.add(".xls");
		FILES_TYPE.add(".xlsx");
		FILES_TYPE.add(".txt");
		FILES_TYPE.add(".doc");
		FILES_TYPE.add(".docx");
		
		CONTRACT_COST_STATUS.put(1, "未开始");
		CONTRACT_COST_STATUS.put(2, "已开始");
		CONTRACT_COST_STATUS.put(3, "已结束");
	}

}
