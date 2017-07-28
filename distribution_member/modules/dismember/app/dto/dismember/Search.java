package dto.dismember;

import java.io.Serializable;
import java.util.List;

import entity.dismember.AccountPeriodSlave;

/**
 * 翻页搜索公共类:个性化参数可由继承实体获得
 * @author zbc
 * 2017年2月20日 下午3:19:51
 */
public class Search extends AccountPeriodSlave implements Serializable{
	
	private static final long serialVersionUID = -384617374892085318L;

	/**
	 * 当前页 默认为 1
	 */
	private Integer currPage = 1;
	
	/**
	 * 页长度 默认为 10
	 */
	private Integer pageSize = 10;
	
	/**
	 * 分销渠道 
	 */
	private Integer disMode;
	
	/**
	 * 开始时间筛选
	 */
	private String std;
	
	/**
	 * 是否全选
	 */
	private Boolean isAll;
	
	/**
	 * 订单id集合
	 */
	private List<Integer> orderIds;
	
	
	/**
	 *  正序倒序 asc/desc
	 */
	private String sort;
	
	/**
	 * 排序字段
	 */
	private String filter;
	
	private Boolean isUnfinished;//是否未完结{0 未生效 1 可使用 2 待还款 3 已逾期 （账户冻结） 4 禁用中 （无法透支）}
	
	public Search() {
	}
	
	/**
	 * @param isAll
	 * @param search
	 */
	public Search(Boolean isAll, Integer id) {
		super();
		this.isAll = isAll;
		setId(id);
	}

	public Boolean getIsUnfinished() {
		return isUnfinished;
	}

	public void setIsUnfinished(Boolean isUnfinished) {
		this.isUnfinished = isUnfinished;
	}
	
	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public Boolean getIsAll() {
		return isAll;
	}

	public void setIsAll(Boolean isAll) {
		this.isAll = isAll;
		if(isAll != null &&isAll){
			setCurrPage(null);
			setPageSize(null);
			this.orderIds = null;
		}
	}

	public List<Integer> getOrderIds() {
		return orderIds;
	}

	public void setOrderIds(List<Integer> orderIds) {
		this.orderIds = orderIds;
	}

	public String getStd() {
		return std;
	}

	public void setStd(String std) {
		this.std = std;
	}

	/**
	 * 搜索内容
	 */
	private String search;
	
	public Integer getDisMode() {
		return disMode;
	}

	public void setDisMode(Integer disMode) {
		this.disMode = disMode;
	}

	public Integer getCurrPage() {
		return currPage;
	}

	public void setCurrPage(Integer currPage) {
		this.currPage = currPage;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}
	
}
