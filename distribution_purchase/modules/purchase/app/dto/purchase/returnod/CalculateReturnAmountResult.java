package dto.purchase.returnod;

/**
 * 计算到的退款相关数据
 * 
 * @author huangjc
 * @date 2017年2月20日
 */
public class CalculateReturnAmountResult {
	private Boolean suc = true;// 是否计算成功
	private String msg;// 提示信息

	private Integer daySpace;// 距离到期日期
	private Double coefficient;// 退货系数
	private Double returnAmount;// 退款金额

	/**
	 * 没有计算到退款金额
	 * @param msg
	 * @return
	 */
	public static CalculateReturnAmountResult newNoCalculateReturnAmountResult(String msg){
		return new CalculateReturnAmountResult(false, msg);		
	}
	
	private CalculateReturnAmountResult(Boolean suc, String msg) {
		this.suc = suc;
		this.msg = msg;
	}

	public CalculateReturnAmountResult(Integer daySpace, Double coefficient,
			Double returnAmount) {
		super();
		this.daySpace = daySpace;
		this.coefficient = coefficient;
		this.returnAmount = returnAmount;
	}

	@Override
	public String toString() {
		return "CalculateReturnAmountResult [suc=" + suc + ", msg=" + msg
				+ ", daySpace=" + daySpace + ", coefficient=" + coefficient
				+ ", returnAmount=" + returnAmount + "]";
	}

	public Boolean getSuc() {
		return suc;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Integer getDaySpace() {
		return daySpace;
	}

	public void setDaySpace(Integer daySpace) {
		this.daySpace = daySpace;
	}

	public Double getCoefficient() {
		return coefficient;
	}

	public void setCoefficient(Double coefficient) {
		this.coefficient = coefficient;
	}

	public Double getReturnAmount() {
		return returnAmount;
	}

	public void setReturnAmount(Double returnAmount) {
		this.returnAmount = returnAmount;
	}

}
