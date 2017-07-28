package dto.sales;

import java.io.Serializable;
import java.math.BigDecimal;

public class FeeValue implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7718817055169417330L;
	/**
	 * 费用率
	 */
	private BigDecimal feeRate;
	/**
	 * 预估费用
	 */
	private BigDecimal estimatedTotalCost;
	/**
	 * 预估业绩
	 */
	private BigDecimal estimatedTotalPerformance;
	/**
	 * 实际费用
	 */
	private BigDecimal realTotalCost;
	/**
	 * 实际业绩
	 */
	private BigDecimal realTotalPerformance;
	
	public FeeValue() {
		super();
	}
	public BigDecimal getFeeRate() {
		return feeRate;
	}
	public void setFeeRate(BigDecimal feeRate) {
		this.feeRate = feeRate;
	}
	public BigDecimal getEstimatedTotalCost() {
		return estimatedTotalCost;
	}
	public void setEstimatedTotalCost(BigDecimal estimatedTotalCost) {
		this.estimatedTotalCost = estimatedTotalCost;
	}
	public BigDecimal getEstimatedTotalPerformance() {
		return estimatedTotalPerformance;
	}
	public void setEstimatedTotalPerformance(BigDecimal estimatedTotalPerformance) {
		this.estimatedTotalPerformance = estimatedTotalPerformance;
	}
	public BigDecimal getRealTotalCost() {
		return realTotalCost;
	}
	public void setRealTotalCost(BigDecimal realTotalCost) {
		this.realTotalCost = realTotalCost;
	}
	public BigDecimal getRealTotalPerformance() {
		return realTotalPerformance;
	}
	public void setRealTotalPerformance(BigDecimal realTotalPerformance) {
		this.realTotalPerformance = realTotalPerformance;
	}
	
	/**
	 * 实际费用率
	 * @author zbc
	 * @since 2017年5月12日 下午5:28:57
	 * @return
	 */
	public BigDecimal getRealRate() {
		if(realTotalCost != null && 
				realTotalCost.compareTo(BigDecimal.ZERO) !=0 && 
					realTotalPerformance != null && 
							realTotalPerformance.compareTo(BigDecimal.ZERO) !=0){
			return realTotalCost.divide(realTotalPerformance,10,BigDecimal.ROUND_HALF_UP);
		}
		return null;
	}
	
	/**
	 * 预估费用率
	 * @author zbc
	 * @since 2017年5月12日 下午5:29:04
	 * @return
	 */
	public BigDecimal getEstimatedRate() {
		if(estimatedTotalCost != null && 
				estimatedTotalCost.compareTo(BigDecimal.ZERO) != 0 && 
				estimatedTotalPerformance != null &&
					estimatedTotalPerformance.compareTo(BigDecimal.ZERO) != 0) {
			return estimatedTotalCost.divide(estimatedTotalPerformance,10,BigDecimal.ROUND_HALF_UP);
		}
		return null;
	}
	
}
