package utils.discart;

public final class KeyUtil {
	/**
	 * 构建一个key，作为map的key
	 * @param sku
	 * @param warehouseId
	 * @return sku_warehouseId，例如：IF637_2024
	 */
	public static String getKey(String sku, int warehouseId) {
		return sku + "_" + warehouseId;
	}
}
