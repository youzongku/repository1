package dto;

public class QueryContractFeeParam {
	private Integer contractId;
	private Integer currPage;
	private Integer pageSize;
	private String optUser;
	
	public QueryContractFeeParam() {
	}
	
	public QueryContractFeeParam(Integer contractId, Integer currPage, Integer pageSize, String optUser) {
		super();
		this.contractId = contractId;
		this.currPage = currPage;
		this.pageSize = pageSize;
		this.optUser = optUser;
	}

	public Integer getContractId() {
		return contractId;
	}

	public void setContractId(Integer contractId) {
		this.contractId = contractId;
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

	public String getOptUser() {
		return optUser;
	}

	public void setOptUser(String optUser) {
		this.optUser = optUser;
	}

}
