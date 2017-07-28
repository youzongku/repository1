package dto.marketing.promotion.condt.value;

import dto.marketing.promotion.ConditionMatchResult;
import dto.marketing.promotion.OrderPromotionActivityDto;
import entity.marketing.promotion.ConditionInstanceExt;
import entity.marketing.promotion.ConditionJudgementType;

/**
 * 商品总数量
 * 
 * @author huangjc
 * @date 2016年7月30日
 */
public class ProductTotalCount extends BaseCondtValue {
	/*
	 * { "minTotalCount":1, "maxTotalCount":2 }
	 */
	// 当不是区间时，只有minTotalCount有值
	private Integer minTotalCount = 0;
	private Integer maxTotalCount = 0;

	public Integer getMinTotalCount() {
		return minTotalCount;
	}

	public void setMinTotalCount(Integer minTotalCount) {
		this.minTotalCount = minTotalCount;
	}

	public Integer getMaxTotalCount() {
		return maxTotalCount;
	}

	public void setMaxTotalCount(Integer maxTotalCount) {
		this.maxTotalCount = maxTotalCount;
	}

	@Override
	public String toString() {
		return "ProductTotalCount [minTotalCount=" + minTotalCount
				+ ", maxTotalCount=" + maxTotalCount + "]";
	}

	@Override
	public ConditionMatchResult handle(String condtJgmntType, ConditionInstanceExt condtInstExt,
			OrderPromotionActivityDto dtoArg) {
		int otherTotalNumber = dtoArg.getTotalNumber().intValue();
		ConditionMatchResult dto = new ConditionMatchResult();
		dto.setMatched(false);
		int multiple;
		switch (condtJgmntType) {
		case ConditionJudgementType.JTYPE_GT:// 大于
			if (otherTotalNumber > minTotalCount.intValue()) {
				multiple = otherTotalNumber /  minTotalCount.intValue();
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
			if (otherTotalNumber < minTotalCount.intValue()) {
				dto.setMatched(true);
				return dto;
			}
			break;
		case ConditionJudgementType.JTYPE_GTEQ:// 大于等于
			if (otherTotalNumber >= minTotalCount.intValue()) {
				multiple = otherTotalNumber /  minTotalCount.intValue();
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
			if (otherTotalNumber <= minTotalCount.intValue()) {
				dto.setMatched(true);
				return dto;
			}
			break;
		case ConditionJudgementType.JTYPE_YES:// 是
			if (minTotalCount.intValue() == otherTotalNumber) {
				dto.setMatched(true);
				return dto;
			}
			break;
		case ConditionJudgementType.JTYPE_LTV:// 区间
			if (minTotalCount.intValue() <= otherTotalNumber
					&& otherTotalNumber < maxTotalCount.intValue()) {
				dto.setMatched(true);
				return dto;
			}
			break;
		case ConditionJudgementType.JTYPE_NO:// 非
			if (minTotalCount.intValue() != otherTotalNumber) {
				dto.setMatched(true);
				return dto;
			}
			break;
		}
		return dto;
	}

}
