package dto.marketing.promotion.condt.value;

import dto.marketing.promotion.ConditionMatchResult;
import dto.marketing.promotion.OrderPromotionActivityDto;
import dto.marketing.promotion.condt.value.SpecifyAttrValue.LtvVal;
import entity.marketing.promotion.ConditionInstanceExt;
import entity.marketing.promotion.ConditionJudgementType;
import play.libs.Json;

/**
 * 公共条件 逻辑 方法类
 * @author zbc
 * 2016年10月22日 上午9:14:27
 */
public class BaseCondtMethod extends BaseCondtValue{
	public ConditionMatchResult isNotSatisfy(SpecifyAttrValue specifyAttrValue, double Commoditynumber) {
		ConditionMatchResult res = new ConditionMatchResult();
		boolean isNotSatisfy = false;
		int multiple = 1;
		switch (specifyAttrValue.getjType()) {
		case ConditionJudgementType.JTYPE_GT://大于
			if(specifyAttrValue.getSingleVal() != 0){
				isNotSatisfy = Commoditynumber > specifyAttrValue.getSingleVal();
				multiple = (int)(Commoditynumber/specifyAttrValue.getSingleVal());
			}else{
				isNotSatisfy = true;
			}
			break;
		case ConditionJudgementType.JTYPE_GTEQ://大于等于
			// 如果条件需要的商品数量/金额为0,那就默认为true
			if(specifyAttrValue.getSingleVal() != 0){
				isNotSatisfy = Commoditynumber >= specifyAttrValue.getSingleVal();
				multiple = (int)(Commoditynumber/specifyAttrValue.getSingleVal());
			}else{
				isNotSatisfy = true;
			}
			break;
		case ConditionJudgementType.JTYPE_LTV://区间
			LtvVal val = specifyAttrValue.getLtvVal();
			isNotSatisfy = (val.getMinJType().equals(ConditionJudgementType.JTYPE_GT)?
					Commoditynumber > val.getMinValue().intValue():Commoditynumber >= val.getMinValue().intValue())&&
					(val.getMaxJType().equals(ConditionJudgementType.JTYPE_LT)?
					Commoditynumber < val.getMaxValue().intValue():Commoditynumber <= val.getMaxValue().intValue());
			break;
		default:
			break;
		}
		res.setMatched(isNotSatisfy);
		res.setMultiple(multiple);
		return res;
		
	}
	
	/**
	 * 不可组合 逻辑判断
	 * @author zbc
	 * @since 2016年10月22日 下午12:01:18
	 */
	protected void isComLogic(ConditionInstanceExt condtInstExt, ConditionMatchResult dto,
			 int commoditynumber, double commodityPrice) {
		ConditionMatchResult res = null;
		SpecifyAttrValue specifyAttrValue = Json.fromJson(
				Json.parse(condtInstExt.getSpecifyAttrValue()),
				SpecifyAttrValue.class);
		
		//1.数量判断  
		if(specifyAttrValue.getAttrType() == 1){
			res = isNotSatisfy(specifyAttrValue,commoditynumber);
		//2.金额判断
		}else if(specifyAttrValue.getAttrType() == 2){
			res = isNotSatisfy(specifyAttrValue,commodityPrice);
		}
		// 如果购买的数量小于要求的数量就设为不满足
		if(res != null){
			if (res.notMathched()) {
				dto.setMatched(false);
			} else {
				dto.setMatched(true);
				dto.setMultiple(res.getMultiple());
			}
		}
		// 校验是否可以翻倍
		if (!condtInstExt.isDoubleUp()) {
			dto.setMultiple(1);
		}
	}

	@Override
	public ConditionMatchResult handle(String condtJgmntType, ConditionInstanceExt condtInstExt,
			OrderPromotionActivityDto dtoArg) {
		// TODO Auto-generated method stub
		return null;
	}
}
