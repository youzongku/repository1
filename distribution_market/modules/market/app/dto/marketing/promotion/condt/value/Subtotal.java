package dto.marketing.promotion.condt.value;

import dto.marketing.promotion.ConditionMatchResult;
import dto.marketing.promotion.OrderPromotionActivityDto;
import entity.marketing.promotion.ConditionInstanceExt;
import entity.marketing.promotion.ConditionJudgementType;

/**
 * 小计金额
 * 
 * @author huangjc
 * @date 2016年7月29日
 */
public class Subtotal extends BaseCondtValue {
	/*
	 * { "minPrice":1, "maxPrice":2 }
	 */
	// 当不是区间时，只有minPrice才有值
	private Double minPrice = 0.0;
	private Double maxPrice = 0.0;

	public Double getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(Double minPrice) {
		this.minPrice = minPrice;
	}

	public Double getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(Double maxPrice) {
		this.maxPrice = maxPrice;
	}

	@Override
	public String toString() {
		return "Subtotal [minPrice=" + minPrice + ", maxPrice=" + maxPrice
				+ "]";
	}

	@Override
	public ConditionMatchResult handle(String condtJgmntType, ConditionInstanceExt condtInstExt,
			OrderPromotionActivityDto dtoArg) {
		ConditionMatchResult dto = new ConditionMatchResult();
		dto.setMatched(false);
		double money = dtoArg.getMoney().doubleValue();
		int multiple;
		switch (condtJgmntType) {
		case ConditionJudgementType.JTYPE_GT:// 大于
			if (money > minPrice.doubleValue()) {
				multiple = (int) (money / minPrice.doubleValue());
				if (condtInstExt.isDoubleUp()) {
					dto.setMultiple(multiple);
				} else {
					dto.setMultiple(1);
				}
				dto.setMatched(true);
				return dto;
			}
			break;
		case ConditionJudgementType.JTYPE_LT:// 小于
			if (money < minPrice.doubleValue()) {
				dto.setMatched(true);
				return dto;
			}
			break;
		case ConditionJudgementType.JTYPE_GTEQ:// 大于等于
			if (money >= minPrice.doubleValue()) {
				multiple = (int) (money / minPrice.doubleValue());
				if (condtInstExt.isDoubleUp()) {
					dto.setMultiple(multiple);
				} else {
					dto.setMultiple(1);
				}
				dto.setMatched(true);
				return dto;
			}
			break;
		case ConditionJudgementType.JTYPE_LTEQ:// 小于等于
			if (money <= minPrice.doubleValue()) {
				dto.setMatched(true);
				return dto;
			}
			break;
		case ConditionJudgementType.JTYPE_YES:// 是
			if (minPrice.doubleValue() == money) {
				dto.setMatched(true);
				return dto;
			}
			break;
		case ConditionJudgementType.JTYPE_LTV:// 区间
			if (minPrice.doubleValue() <= money
					&& money < maxPrice.doubleValue()) {
				dto.setMatched(true);
				return dto;
			}
			break;
		case ConditionJudgementType.JTYPE_NO:// 非
			if (minPrice != money) {
				dto.setMatched(true);
				return dto;
			}
			break;
		}
		return dto;
	}

}
