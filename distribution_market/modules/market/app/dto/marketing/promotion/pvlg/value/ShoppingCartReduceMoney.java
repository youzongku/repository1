package dto.marketing.promotion.pvlg.value;

/**
 * 满减金额
 * 
 * @author huangjc
 * @date 2016年7月29日
 */
public class ShoppingCartReduceMoney extends BasePvlgValue {
	// {"moneyReduce":12.5}
	private double moneyReduce;

	public double getMoneyReduce() {
		return moneyReduce;
	}

	public void setMoneyReduce(double moneyReduce) {
		this.moneyReduce = moneyReduce;
	}

	@Override
	public String toString() {
		return "ReduceMoney [moneyReduce=" + moneyReduce + "]";
	}

}
