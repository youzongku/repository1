package dto.marketing.promotion.condt.value;

import dto.marketing.promotion.ConditionMatchResult;
import dto.marketing.promotion.OrderPromotionActivityDto;
import entity.marketing.promotion.ConditionInstanceExt;
import entity.marketing.promotion.ConditionJudgementType;

/**
 * 购物车总重量
 * 
 * @author huangjc
 * @date 2016年7月29日
 */
public class ShoppingCartTotalWeight extends BaseCondtValue {
	/*
	 * { "minWeight":1, "maxWeight":2 }
	 */
	// 当不是区间时，只有minWeight才有值
	private double minWeight = 0.0;
	private double maxWeight = 0.0;

	public double getMinWeight() {
		return minWeight;
	}

	public void setMinWeight(double minWeight) {
		this.minWeight = minWeight;
	}

	public double getMaxWeight() {
		return maxWeight;
	}

	public void setMaxWeight(double maxWeight) {
		this.maxWeight = maxWeight;
	}

	@Override
	public String toString() {
		return "ShoppingCartTotalWeight [minWeight=" + minWeight
				+ ", maxWeight=" + maxWeight + "]";
	}

	@Override
	public ConditionMatchResult handle(String condtJgmntType, ConditionInstanceExt condtInstExt,
			OrderPromotionActivityDto dtoArg) {
		ConditionMatchResult dto = new ConditionMatchResult();
		dto.setMatched(false);
		double otherTotalWeight = dtoArg.getTotalWeight();
		switch (condtJgmntType) {
		case ConditionJudgementType.JTYPE_GT:// 大于
			if (minWeight < otherTotalWeight) {
				dto.setMatched(true);
				return dto;
			}
			break;
		case ConditionJudgementType.JTYPE_LT:// 小于
			if (minWeight > otherTotalWeight) {
				dto.setMatched(true);
				return dto;
			}
			break;
		case ConditionJudgementType.JTYPE_GTEQ:// 大于等于
			if (minWeight <= otherTotalWeight) {
				dto.setMatched(true);
				return dto;
			}
			break;
		case ConditionJudgementType.JTYPE_LTEQ:// 小于等于
			if (minWeight >= otherTotalWeight) {
				dto.setMatched(true);
				return dto;
			}
			break;
		case ConditionJudgementType.JTYPE_YES:// 是
			if (minWeight == otherTotalWeight) {
				dto.setMatched(true);
				return dto;
			}
			break;
		case ConditionJudgementType.JTYPE_LTV:// 区间
			if (minWeight <= otherTotalWeight && otherTotalWeight < maxWeight) {
				dto.setMatched(true);
				return dto;
			}
			break;
		case ConditionJudgementType.JTYPE_NO:// 非
			if (minWeight != otherTotalWeight) {
				dto.setMatched(true);
				return dto;
			}
			break;
		}
		return dto;
	}

}
