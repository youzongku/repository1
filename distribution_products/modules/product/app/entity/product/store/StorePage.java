package entity.product.store;

import java.io.Serializable;
import java.util.List;

/**
 * @author zbc 2016年12月5日 下午2:37:13
 */
public class StorePage implements Serializable{

	private static final long serialVersionUID = 8620900574154437641L;

	private Integer pageSize;

	private Integer totalPage;

	private Integer currPage;

	private Integer rows;

	private List<?> result;

	public StorePage(Integer pageSize, Integer rows, Integer currPage, List<?> result) {
		super();
		this.rows = rows;
		this.pageSize = pageSize;
		if (null != pageSize) {
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
		if (null != pageSize)
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

	public void setResult(List<?> result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "PageResultDto [pageSize=" + pageSize + ", totalPage=" + totalPage + ", currPage=" + currPage + ", rows="
				+ rows + ", result=" + result + "]";
	}
}
