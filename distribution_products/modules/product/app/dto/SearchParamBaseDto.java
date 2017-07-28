package dto;

public class SearchParamBaseDto {
	/**
	 * 页长
	 */
	private Integer pageSize;
	/**
	 * 页码
	 */
	private Integer pageNo;
	
	public Integer getPageSize() {
		
		return pageSize == null ? 10 : pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	
	public Integer getPageNo() {
		return pageNo == null ? 1 : pageNo;
	}
	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}
	
	
}
