package dto.contract.fee;

import java.util.List;
/**
 * 封装合同费用项结果
 * 
 * @author huangjc
 */
public class ContractFeeItemResult {
	private String contractNo;
	List<ContractFeeItemDto> feeItems;

	public ContractFeeItemResult(String contractNo, List<ContractFeeItemDto> feeItems) {
		super();
		this.contractNo = contractNo;
		this.feeItems = feeItems;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public List<ContractFeeItemDto> getFeeItems() {
		return feeItems;
	}

	public void setFeeItems(List<ContractFeeItemDto> feeItems) {
		this.feeItems = feeItems;
	}

}
