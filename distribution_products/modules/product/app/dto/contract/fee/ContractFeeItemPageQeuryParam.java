package dto.contract.fee;

/**
 * 封装合同费用项分页查询的参数
 * 
 * @author huangjc
 */
public class ContractFeeItemPageQeuryParam {
	private String contractNo;
	private Integer currPage;
	private Integer pageSize;

	public ContractFeeItemPageQeuryParam() {
	}

	public ContractFeeItemPageQeuryParam(String contractNo, Integer currPage, Integer pageSize) {
		super();
		this.contractNo = contractNo;
		this.currPage = currPage;
		this.pageSize = pageSize;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
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

}
