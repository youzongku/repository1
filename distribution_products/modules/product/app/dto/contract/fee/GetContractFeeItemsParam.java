package dto.contract.fee;

import java.time.LocalDateTime;
import java.util.List;

import dto.contract.fee.GetContractFeeItemsParam.ContractParam;

/**
 * 获取合同费用项参数
 * 
 * @author huangjc
 */
public class GetContractFeeItemsParam {
	/*
	 {
	 	"contracts":[
	 		{
	 			"pros":[{"sku":"IF942-1","warehouseId":2024}],
				"contractNo":"HT2017050915114300000256"
			}
		],
		"payDate":"2017-05-16 10:20:46"
	}
	 */
	
	private List<ContractParam> contracts;
	private LocalDateTime payDate;
	
	public GetContractFeeItemsParam(List<ContractParam> contracts, LocalDateTime payDate) {
		this.contracts = contracts;
		this.payDate = payDate;
	}

	public List<ContractParam> getContracts() {
		return contracts;
	}

	public void setContracts(List<ContractParam> contracts) {
		this.contracts = contracts;
	}

	public LocalDateTime getPayDate() {
		return payDate;
	}

	public void setPayDate(LocalDateTime payDate) {
		this.payDate = payDate;
	}

	public static class SkuWarehouseId {
		private String sku;
		private int warehouseId;
		public String getSku() {
			return sku;
		}
		public void setSku(String sku) {
			this.sku = sku;
		}
		public int getWarehouseId() {
			return warehouseId;
		}
		public void setWarehouseId(int warehouseId) {
			this.warehouseId = warehouseId;
		}
		public SkuWarehouseId(String sku, int warehouseId) {
			super();
			this.sku = sku;
			this.warehouseId = warehouseId;
		}
		@Override
		public String toString() {
			return "SkuWarehouseId [sku=" + sku + ", warehouseId=" + warehouseId + "]";
		}
	}

	public static class ContractParam {
		private String contractNo;
		private List<SkuWarehouseId> pros;
		public ContractParam(String contractNo, List<SkuWarehouseId> pros) {
			super();
			this.contractNo = contractNo;
			this.pros = pros;
		}

		public String getContractNo() {
			return contractNo;
		}

		public void setContractNo(String contractNo) {
			this.contractNo = contractNo;
		}

		public List<SkuWarehouseId> getPros() {
			return pros;
		}

		public void setPros(List<SkuWarehouseId> pros) {
			this.pros = pros;
		}
	}
}
