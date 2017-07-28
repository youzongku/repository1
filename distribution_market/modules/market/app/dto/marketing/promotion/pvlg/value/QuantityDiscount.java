package dto.marketing.promotion.pvlg.value;

/**
 * 折扣
 * 
 * @author huangjc
 * @date 2016年7月29日
 */
public class QuantityDiscount extends BasePvlgValue {
	// {"num":11}，单位是百分比
	private double num;

	public double getNum() {
		return num;
	}

	public void setNum(double num) {
		this.num = num;
	}

	@Override
	public String toString() {
		return "QuantityDiscount [num=" + num + "]";
	}

}
