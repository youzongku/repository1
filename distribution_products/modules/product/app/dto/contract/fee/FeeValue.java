package dto.contract.fee;
/**
 * 费用值
 * @author Administrator
 *
 */
public class FeeValue extends BaseContractFeeValue {
	/** 预估总费用 */
	private Double estimatedTotalCost = 0d;
	/** 预估总业绩 */
	private Double estimatedTotalPerformance = 0d;

	/** 实际总费用 */
	private Double realTotalCost = 0d;
	/** 实际总业绩 */
	private Double realTotalPerformance = 0d;

	public Double getEstimatedTotalCost() {
		return estimatedTotalCost;
	}

	public void setEstimatedTotalCost(Double estimatedTotalCost) {
		this.estimatedTotalCost = estimatedTotalCost;
	}

	public Double getEstimatedTotalPerformance() {
		return estimatedTotalPerformance;
	}

	public void setEstimatedTotalPerformance(Double estimatedTotalPerformance) {
		this.estimatedTotalPerformance = estimatedTotalPerformance;
	}

	public Double getRealTotalCost() {
		return realTotalCost;
	}

	public void setRealTotalCost(Double realTotalCost) {
		this.realTotalCost = realTotalCost;
	}

	public Double getRealTotalPerformance() {
		return realTotalPerformance;
	}

	public void setRealTotalPerformance(Double realTotalPerformance) {
		this.realTotalPerformance = realTotalPerformance;
	}

	@Override
	public String toString() {
		return "FeeValue [estimatedTotalCost=" + estimatedTotalCost + ", estimatedTotalPerformance="
				+ estimatedTotalPerformance + ", realTotalCost=" + realTotalCost + ", realTotalPerformance="
				+ realTotalPerformance + "]";
	}

}
