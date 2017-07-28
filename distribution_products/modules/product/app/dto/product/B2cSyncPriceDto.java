package dto.product;

/**
 * b2c同步价格更新 实体
 * @author zbc
 * 2016年8月5日 上午10:09:55
 */
public class B2cSyncPriceDto {

	private String sku;

	private Integer warehouseId;

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}
}
