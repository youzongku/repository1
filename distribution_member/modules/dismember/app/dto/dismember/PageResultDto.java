package dto.dismember;

import java.util.List;

public class PageResultDto {
	
	private Integer pageSize;
	
	private Integer totalPage;
	
	private Integer currPage;
	
	private List<?> result;
	
	private Integer totalCount;
	
	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public PageResultDto(Integer pageSize, Integer totalPage, Integer currPage, List<?> result) {
		super();
		this.pageSize = pageSize;
		this.totalPage = totalPage;
		this.currPage = currPage;
		this.result = result;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}

	public Integer getCurrPage() {
		return currPage;
	}

	public void setCurrPage(Integer currPage) {
		this.currPage = currPage;
	}

	public List<?> getResult() {
		return result;
	}

	public void setResult(List<?> result) {
		this.result = result;
	}

}
