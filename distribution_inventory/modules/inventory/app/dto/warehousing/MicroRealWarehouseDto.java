package dto.warehousing;

/**
 * 分销商微仓商品所属的仓库
 * @author zbc
 * 2016年8月10日 下午5:34:53
 */
public class MicroRealWarehouseDto {
	
	public Integer warehouseId;//仓库名称
	
	public String warehouseName;//仓库id

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}
	
	

}
