package forms.order;

/**
 * 
 * 
 * @author ye_ziran
 * @since 2016年3月23日 下午2:11:53
 */
public class PurchaseSearchParamForm {
	
	private String email;
	private String pageSize;
	private String status;
	private String pageCount;
	private String seachFlag;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPageSize() {
		return pageSize;
	}
	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}
	public String getPageCount() {
		return pageCount;
	}
	public void setPageCount(String pageCount) {
		this.pageCount = pageCount;
	}
	public String getSeachFlag() {
		return seachFlag;
	}
	public void setSeachFlag(String seachFlag) {
		this.seachFlag = seachFlag;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
