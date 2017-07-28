package util.marketing.promotion;

public enum PromotionAttrEnum {

	GOODS(1, "商品属性"), SHOPPING_CART(2, "购物车属性"), USER(3, "用户属性");

	private int value;
	private String name;

	private PromotionAttrEnum(int value, String name) {
		this.value = value;
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public String getName() {
		return name;
	}
}
