package dto.sales;

import java.io.Serializable;

import com.wordnik.swagger.annotations.ApiModelProperty;

import dto.JqGridBaseSearch;

/**
 * @author zbc
 * 2017年5月15日 下午7:15:32
 */
public class ContractFeeSearch extends JqGridBaseSearch implements Serializable {

	private static final long serialVersionUID = 3416788174741771212L;
	
	@ApiModelProperty("合同号")
	private String contractNo;

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

}
