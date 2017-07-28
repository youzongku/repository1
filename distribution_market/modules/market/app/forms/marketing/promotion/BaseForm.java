package forms.marketing.promotion;
/**
 * 保存基础的分页所需数据
 * @author huangjc
 * @date 2016年7月26日
 */
public abstract class BaseForm {
	// 当前页
	private Integer curr;
	// 页记录数
	private Integer pageSize;
	// 起始
	private Integer offset;

	private String sidx;//jqgrid排序字段
	private String sord;//jqgird排序字段

	public Integer getOffset() {
		if (curr == null || curr < 1) {
			curr = 1;
		}
		if (pageSize == null || pageSize < 1) {
			pageSize = 10;
		}
		int offset = (curr-1) * pageSize;
		return offset;
	}

	public Integer getCurr() {
		return curr;
	}

	public void setCurr(Integer curr) {
		this.curr = curr;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getSidx() {
		return sidx;
	}

	public void setSidx(String sidx) {
		this.sidx = sidx;
	}

	public String getSord() {
		return sord;
	}

	public void setSord(String sord) {
		this.sord = sord;
	}
}
