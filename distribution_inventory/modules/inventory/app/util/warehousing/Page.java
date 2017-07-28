package util.warehousing;

import java.io.Serializable;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * page工具类
 * 
 * @param <T>
 * @author ye_ziran
 * @since 2015年11月4日 下午2:21:36
 */
public class Page<T> implements Serializable {
	private static final long serialVersionUID = -6287141621150731167L;
	final private List<T> list;//当前页的数据
	final private int total;//总记录数
	final private int page;//页码
	final private int recordPerPage;//页长

	public Page(List<T> list, int total, int page, int recordPerPage) {
		super();
		this.list = list;
		this.total = total;
		this.page = page;
		this.recordPerPage = recordPerPage;
	}

	public List<T> getList() {
		return this.list;
	}

	public int getTotalCount() {
		return total;
	}

	public int getPageNo() {
		return page;
	}

	public int getPageSize() {
		return recordPerPage;
	}

	public int getTotalPages() {
		return getTotalCount() / getPageSize()
				+ ((getTotalCount() % getPageSize() > 0) ? 1 : 0);
	}

	public <S> Page<S> map(Function<T, S> func) {
		return new Page<S>(Lists.transform(list, func), total, page,
				recordPerPage);
	}

	public <S> Page<S> batchMap(Function<List<T>, List<S>> func) {
		return new Page<S>(func.apply(list), total, page, recordPerPage);
	}

	@Override
	public String toString() {
		return "Page [list=" + list + ", total=" + total + ", page=" + page
				+ ", recordPerPage=" + recordPerPage + "]";
	}

}
