package component.elasticsearch;

import java.util.List;

/**
 * 封装分页数据
 * 
 * @author huangjc
 * @date 2016年11月18日
 * @param <T>
 */
public class Page<T> {

	// 当前页
	int currPage;
	// 页记录数
	int pageSize;
	//　总记录数
	long totalCount;
	// 数据
	List<T> datas;

	public Page(int currPage, int pageSize, long totalCount, List<T> datas) {
		super();
		this.currPage = currPage;
		this.pageSize = pageSize;
		this.totalCount = totalCount;
		this.datas = datas;
	}

	public int getCurrPage() {
		return currPage;
	}

	public void setCurrPage(int currPage) {
		this.currPage = currPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public List<T> getDatas() {
		return datas;
	}

	public void setDatas(List<T> datas) {
		this.datas = datas;
	}
}
