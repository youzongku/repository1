package dto.warehousing;

import java.io.Serializable;

@SuppressWarnings("serial")
public class WarehouseChangeRecordDto implements Serializable {

	private Integer pageNo;// 页码
	private Integer pageSize;// 每页条数
	private String key;// sku或者商品名称
	private int warehouseType;// 仓库类型（0 全部即不分微、云仓 1 微仓 2云仓实际是总仓）
	private Integer warehouseId;// 仓库id

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getWarehouseType() {
		return warehouseType;
	}

	public void setWarehouseType(int warehouseType) {
		this.warehouseType = warehouseType;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

}
