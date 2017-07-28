package dto.marketing.promotion.condt.value;

import dto.marketing.promotion.CommodityDetail;
import dto.marketing.promotion.ConditionMatchResult;
import dto.marketing.promotion.OrderPromotionActivityDto;
import entity.marketing.promotion.ConditionInstanceExt;
import entity.marketing.promotion.ConditionJudgementType;

public class ProductType extends BaseCondtValue {
	/*
	 * { "productType":1 }
	 */
	// 1完税商品，2跨境商品
	private Integer productType = 0;

	public Integer getProductType() {
		return productType;
	}

	public void setProductType(Integer productType) {
		this.productType = productType;
	}

	@Override
	public String toString() {
		return "ProductType [productType=" + productType + "]";
	}

	@Override
	public ConditionMatchResult handle(String condtJgmntType, ConditionInstanceExt condtInstExt,
			OrderPromotionActivityDto dtoArg) {
		ConditionMatchResult dto = new ConditionMatchResult();
		dto.setMatched(false);
		switch (condtJgmntType) {
		case ConditionJudgementType.JTYPE_GT:// 大于

			break;
		case ConditionJudgementType.JTYPE_LT:// 小于

			break;
		case ConditionJudgementType.JTYPE_GTEQ:// 大于等于

			break;
		case ConditionJudgementType.JTYPE_LTEQ:// 小于等于

			break;
		case ConditionJudgementType.JTYPE_YES:// 是
			for (CommodityDetail cd : dtoArg.getCommodity()) {
				if (cd.getCommodityTypeId() == null) {
					dto.setMatched(false);
					return dto;
				}
				// 只要有一个商品是指定的商品类型即可
				if (productType.intValue() == cd.getCommodityTypeId()
						.intValue()) {
					dto.setMatched(true);
					return dto;
				}
			}
			break;
		case ConditionJudgementType.JTYPE_LTV:// 区间

			break;
		case ConditionJudgementType.JTYPE_NO:// 非
			for (CommodityDetail cd : dtoArg.getCommodity()) {
				if (cd.getCommodityTypeId() == null) {
					dto.setMatched(false);
					return dto;
				}
				if (cd.getCommodityTypeId().intValue() != productType
						.intValue()) {
					dto.setMatched(true);
					return dto;
				}
			}
			break;
		}
		return dto;
	}

}
