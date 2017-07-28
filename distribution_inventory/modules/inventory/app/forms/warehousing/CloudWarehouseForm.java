package forms.warehousing;

import entity.inventory.Warehouse;

/**
 * 云仓搜索带分页
 * @author mjx
 *
 */
public class CloudWarehouseForm extends Warehouse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Integer pageNo;
	public Integer pageSize;
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
	
	
}
