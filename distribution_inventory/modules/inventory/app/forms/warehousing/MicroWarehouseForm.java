package forms.warehousing;

import entity.warehousing.MicroWarehouse;

/**
 * 微仓查询表单
 * @author ouyangyaxiong
 * @date 2016年3月7日
 */
public class MicroWarehouseForm extends MicroWarehouse{
	private int pageSize;
	private int pageNo;
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	@Override
	public String toString() {
		return "MicroWarehouseForm [pageSize=" + pageSize + ", pageNo=" + pageNo  + "]";
	}

}
