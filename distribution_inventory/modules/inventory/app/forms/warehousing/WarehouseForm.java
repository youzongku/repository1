package forms.warehousing;

import entity.warehousing.Warehouse;

/**
 * 真实仓搜索条件Form
 * 
 * @author ouyangyaxiong
 * @date 2016年3月11日
 */
public class WarehouseForm extends Warehouse {
	private static final long serialVersionUID = 1L;
	
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
}
