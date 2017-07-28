package dto.sales;

import java.io.Serializable;
import java.util.List;

/**
 * 合同费用返回值结果
 * @author zbc
 * 2017年5月12日 下午12:14:43
 */
public class ContractFeeTypeResult implements Serializable {

	private static final long serialVersionUID = -1573042094945776980L;
	
	private String contractNo;
	
	private List<FeeItem> feeItems;
	
	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public List<FeeItem> getFeeItems() {
		return feeItems;
	}

	public void setFeeItems(List<FeeItem> feeItems) {
		this.feeItems = feeItems;
	}
}

