package event;
/**
 * 计算合同费用事件
 * 
 * @author huangjc
 */
public class CalContractFeeEvent {
	
	private Integer feeItemId;
	
	private Boolean isInputRealFeeValue;
	
	/**
	 * @param feeItemId 合同费用项id
	 * @param isInputRealFeeValue 是否是录入实际费用
	 */
	public CalContractFeeEvent(Integer feeItemId, Boolean isInputRealFeeValue) {
		super();
		this.feeItemId = feeItemId;
		this.isInputRealFeeValue = isInputRealFeeValue;
	}

	public Integer getFeeItemId() {
		return feeItemId;
	}

	public void setFeeItemId(Integer feeItemId) {
		this.feeItemId = feeItemId;
	}

	public Boolean isInputRealFeeValue() {
		return isInputRealFeeValue;
	}

	public void setInputRealFeeValue(Boolean isInputRealFeeValue) {
		this.isInputRealFeeValue = isInputRealFeeValue;
	}

}
