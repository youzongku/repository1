package dto.marketing.promotion.condt.value;

import dto.marketing.promotion.ConditionMatchResult;
import dto.marketing.promotion.OrderPromotionActivityDto;
import entity.marketing.promotion.ConditionInstanceExt;


public abstract class BaseCondtValue {
	/**
	 * 检查传入的参数是否符合此条件
	 * @param condtJgmntType 判断类型
	 * @param dtoArg 传入的参数
	 * @return 符合true，不符合false
	 */
	public abstract ConditionMatchResult handle(String condtJgmntType, ConditionInstanceExt condtInstExt, OrderPromotionActivityDto dtoArg);
}
