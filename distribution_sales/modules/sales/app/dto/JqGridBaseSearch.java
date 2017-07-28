package dto;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * @author zbc
 * 2017年4月19日 下午5:34:50
 */
@Api("JqGrid分页查询公共父类")
public class JqGridBaseSearch {
	@ApiModelProperty("当前页码")
	private Integer page;
	@ApiModelProperty("页长")
	private Integer rows;
	@ApiModelProperty("模糊搜索")
	private String search;
	@ApiModelProperty("排序字段")
	private String sidx;
	@ApiModelProperty("排序规则")
	private String sord;
	private Boolean _search;
	private Long nd;
	
	public Boolean get_search() {
		return _search;
	}
	public void set_search(Boolean _search) {
		this._search = _search;
	}
	public Long getNd() {
		return nd;
	}
	public void setNd(Long nd) {
		this.nd = nd;
	}
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	public Integer getRows() {
		return rows;
	}
	public void setRows(Integer rows) {
		this.rows = rows;
	}
	public String getSearch() {
		return search;
	}
	public void setSearch(String search) {
		this.search = search;
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
