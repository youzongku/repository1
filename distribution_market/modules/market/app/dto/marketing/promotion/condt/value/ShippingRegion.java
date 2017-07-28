package dto.marketing.promotion.condt.value;

import java.util.ArrayList;
import java.util.List;

import dto.marketing.promotion.ConditionMatchResult;
import dto.marketing.promotion.OrderPromotionActivityDto;
import entity.marketing.promotion.ConditionInstanceExt;
import entity.marketing.promotion.ConditionJudgementType;

/**
 * 运送地区
 * 
 * @author huangjc
 * @date 2016年7月29日
 */
public class ShippingRegion extends BaseCondtValue {
	/*
	 {
	 		"shippingRegions":[{cityId:12,cityName:"北京"},{cityId:12,cityName:"广州"}]
	 }
	 */
	private List<SingleShippingRegion> shippingRegions = new ArrayList<SingleShippingRegion>();

	@Override
	public String toString() {
		return "ShippingRegion [shippingRegions=" + shippingRegions + "]";
	}

	public List<SingleShippingRegion> getShippingRegions() {
		return shippingRegions;
	}

	public void setShippingRegions(List<SingleShippingRegion> shippingRegions) {
		this.shippingRegions = shippingRegions;
	}

	// 城市id=城市名称
	public static class SingleShippingRegion {
		private int cityId;
		private String cityName;

		public int getCityId() {
			return cityId;
		}

		public void setCityId(int cityId) {
			this.cityId = cityId;
		}

		public String getCityName() {
			return cityName;
		}

		public void setCityName(String cityName) {
			this.cityName = cityName;
		}

		@Override
		public String toString() {
			return "SingleShippingRegion [cityId=" + cityId + ", cityName="
					+ cityName + "]";
		}

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
			for (SingleShippingRegion ssr : shippingRegions) {
				if (ssr.getCityId() == dtoArg.getCityId().intValue()) {
					dto.setMatched(true);
					return dto;
				}
			}
			break;
		case ConditionJudgementType.JTYPE_LTV:// 区间
			break;
		case ConditionJudgementType.JTYPE_NO:// 非
			for (SingleShippingRegion ssr : shippingRegions) {
				if (ssr.getCityId() != dtoArg
						.getCityId().intValue()) {
					dto.setMatched(true);
					return dto;
				}
			}
			break;
		}
		return dto;
	}

}
