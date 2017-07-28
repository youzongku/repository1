package util.marketing.promotion;

import java.util.List;

import play.Logger;
/**
 * 封装分页数据
 * @author huangjc
 * @date 2016年7月25日
 */
public class PageInfo<T> {
	/**
	 * 页记录数
	 */
	private Integer pageSize;
	/**
	 * 总页数
	 */
	private Integer totalPage;
	/**
	 * 当前页码
	 */
	private Integer currPage;
	/**
	 * 总记录数
	 */
	private Integer rows;
	/**
	 * 记录列表
	 */
	private List<T> result;
	
	public PageInfo(Integer pageSize,Integer rows, Integer currPage, List<T> result) {
		super();
		this.rows = rows;
		this.pageSize = pageSize;
		Logger.info("pageSize="+pageSize);
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

	public List<?> getResult() {
		return result;
	}

	public void setResult(List<T> result) {
		this.result = result;
	}

}
