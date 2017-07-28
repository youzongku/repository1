package dto.dismember;

import java.util.List;

import entity.dismember.PaymentCondition;

public class PaymentMethodDto extends PaymentCondition {

	private static final long serialVersionUID = 2385419164255322202L;
	
	public List<Integer> methodids;

	public List<Integer> getMethodids() {
		return methodids;
	}

	public void setMethodids(List<Integer> methodids) {
		this.methodids = methodids;
	}
	
}
