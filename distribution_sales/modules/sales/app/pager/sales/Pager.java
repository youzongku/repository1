package pager.sales;

import java.util.List;

public class Pager<T> {
	// 记录
	private List<T> datas;
	// 当前页
	private int currPage;
	// 页记录数
	private int pageSize;
	// 总页数
	private int totalPage;
	// 总记录数
	private int totalCount;

	public Pager() {
		super();
	}
	
	public Pager(List<T> datas, int currPage, int pageSize, int totalCount) {
		this.datas = datas;
		this.currPage = currPage;
		this.pageSize = pageSize;
		this.totalCount = totalCount;
		this.totalPage = totalCount%pageSize==0 ? totalCount/pageSize : totalCount/pageSize + 1;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public List<T> getDatas() {
		return datas;
	}

	public void setDatas(List<T> datas) {
		this.datas = datas;
	}

	public int getCurrPage() {
		return currPage;
	}

	public void setCurrPage(int currPage) {
		this.currPage = currPage;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	@Override
	public String toString() {
		return "Pager [datas=" + datas + ", currPage=" + currPage
				+ ", pageSize=" + pageSize + ", totalPage=" + totalPage
				+ ", totalCount=" + totalCount + "]";
	}

}
