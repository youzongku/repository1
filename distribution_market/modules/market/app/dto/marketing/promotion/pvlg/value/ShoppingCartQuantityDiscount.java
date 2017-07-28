package dto.marketing.promotion.pvlg.value;

/**
 * 购物车定额折扣
 * 
 * @author huangjc
 * @date 2016年7月29日
 */
public class ShoppingCartQuantityDiscount extends BasePvlgValue {
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
		return "ShoppingCartQuantityDiscount [num=" + num + "]";
	}

}
