package util.product;

import org.apache.commons.lang3.StringUtils;

public class KeyUtil {
	/**
	 * 使用2个字段构建一个key
	 * @param sku 不能为空
	 * @param warehouseId 仓库id，不能为空
	 * @return sku_warehouseId
	 */
	public static String getKey(String sku, Integer warehouseId){
		return getKey(sku, warehouseId, null);
	}
	
	/**
	 * 使用三个字段构建一个key，expirationDate可有可无
	 * @param sku 不能为空
	 * @param warehouseId 仓库id，不能为空
	 * @param expirationDate 到期日期，可以为空，为空时，返回值为sku_warehouseId
	 * @return sku_warehouseId[_expirationDate]
	 */
	public static String getKey(String sku, Integer warehouseId, String expirationDate){
		StringBuilder sb = new StringBuilder();
		if (StringUtils.isEmpty(sku) || warehouseId==null) {
			throw new RuntimeException("sku or warehouseId can not be null or empty");
		}
		sb.append(sku).append("_").append(warehouseId.toString());
		if (StringUtils.isNotEmpty(expirationDate)) {
			sb.append("_").append(warehouseId);
		}
		return sb.toString();
	}
}
