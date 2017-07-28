package dto.marketing;

import java.util.List;

/**
 * 查询营销单的条件
 * 
 * @author huangjc
 * @date 2016年12月26日
 */
public class QueryMarketingOrderParams {
	private Integer pageSize = 10;
	private Integer currPage = 1;
	private String startDate;
	private String endDate;
	private Integer distributorType;
	private Integer status;
	private String searchText;
	private String email;
	
	private String filter;
	private String sort;
	/**
	 * 业务员所关联的分销商
	 */
	private List<String> relatedMembers;

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public List<String> getRelatedMembers() {
		return relatedMembers;
	}

	public void setRelatedMembers(List<String> relatedMembers) {
		this.relatedMembers = relatedMembers;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public Integer getDistributorType() {
		return distributorType;
	}

	public void setDistributorType(Integer distributorType) {
		this.distributorType = distributorType;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getCurrPage() {
		return currPage;
	}

	public void setCurrPage(Integer currPage) {
		this.currPage = currPage;
	}

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	@Override
	public String toString() {
		return "QueryMarketingOrderParams [pageSize=" + pageSize
				+ ", currPage=" + currPage + ", startDate=" + startDate
				+ ", endDate=" + endDate + ", distributorType="
				+ distributorType + ", status=" + status + ", searchText="
				+ searchText + ", email=" + email + ", relatedMembers="
				+ relatedMembers + "]";
	}

}
