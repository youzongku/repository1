package dto.contract.fee;

/**
 * 费用率
 */
public class FeeRate extends BaseContractFeeValue {
	/** 费用率 */
	private Double feeRate;

	public Double getFeeRate() {
		return feeRate;
	}

	public void setFeeRate(Double feeRate) {
		this.feeRate = feeRate;
	}

	@Override
	public String toString() {
		return "FeeRate [feeRate=" + feeRate + "]";
	}

}
