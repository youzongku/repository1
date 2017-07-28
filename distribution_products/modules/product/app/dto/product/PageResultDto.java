package dto.product;

import java.util.List;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class PageResultDto<T> {
	@ApiModelProperty("页长度")
	private Integer pageSize;
	@ApiModelProperty("总页数")
	private Integer totalPage;
	@ApiModelProperty("当前页")
	private Integer currPage;
	@ApiModelProperty("总记录数")
	private Integer rows;
	@ApiModelProperty("行数据")
	private List<T> result;
	
	public PageResultDto(Integer pageSize,Integer rows, Integer currPage, List<T> result) {
		super();
		this.rows = rows;
		this.pageSize = pageSize;
		if(null != pageSize){
			this.totalPage = rows / pageSize + (rows % pageSize == 0 ? 0 : 1);			
		}
		this.currPage = currPage;
		this.result = result;
	}
	
	public Integer getRows() {
		return rows;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getTotalPage() {
		if(null != pageSize)
		totalPage = rows / pageSize + (rows % pageSize == 0 ? 0 : 1);
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

	public List<T> getResult() {
		return result;
	}

	public void setResult(List<T> result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "PageResultDto [pageSize=" + pageSize + ", totalPage="
				+ totalPage + ", currPage=" + currPage + ", rows=" + rows
				+ ", result=" + result + "]";
	}

}
