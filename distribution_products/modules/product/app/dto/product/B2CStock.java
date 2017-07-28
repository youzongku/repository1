package dto.product;

import java.util.List;

public class B2CStock {
	
	private List<GoodsInventoryListDto> list;
	private Integer pageNo;
	private Integer pageSize;
	private Integer totalCount;
	private Integer totalPages;
	public List<GoodsInventoryListDto> getList() {
		return list;
	}
	public void setList(List<GoodsInventoryListDto> list) {
		this.list = list;
	}
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
	public Integer getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}
	public Integer getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}
	
	

}
