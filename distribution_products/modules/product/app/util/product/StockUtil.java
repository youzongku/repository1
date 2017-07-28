package util.product;

/**
 * 库存数量的帮助类
 */
public final class StockUtil {
	private StockUtil(){}
	
	/**
	 * 修复库存数量
	 * @param stockNumber 库存数量
	 * @return 当stockNumber为null或小于0时，返回0；大于0就原值返回
	 */
	public static int fixStockNumber(Integer stockNumber) {
		if (stockNumber==null || stockNumber.intValue()<0) {
			return 0;
		}
		return stockNumber;
	}
}
