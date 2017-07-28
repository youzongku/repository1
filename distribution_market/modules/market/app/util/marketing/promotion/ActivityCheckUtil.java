package util.marketing.promotion;

import play.Logger;
import play.libs.Json;
import dto.marketing.promotion.ConditionMatchResult;
import dto.marketing.promotion.OrderPromotionActivityDto;
import dto.marketing.promotion.condt.value.ProductCategory;
import dto.marketing.promotion.condt.value.ProductTotalCount;
import dto.marketing.promotion.condt.value.ProductType;
import dto.marketing.promotion.condt.value.ShippingRegion;
import dto.marketing.promotion.condt.value.ShoppingCartTotalWeight;
import dto.marketing.promotion.condt.value.SpecifyProduct;
import dto.marketing.promotion.condt.value.SpecifyWarehouse;
import dto.marketing.promotion.condt.value.Subtotal;
import entity.marketing.promotion.ConditionInstance;
import entity.marketing.promotion.ConditionInstanceExt;
import entity.marketing.promotion.PromotionCondition;

/**
 * 促销活动条件判断校验类
 * 
 * @author lenovo
 *
 */
public class ActivityCheckUtil {
	/**
	 * 购物数据满足条件？
	 * 
	 * @param condtInst
	 *            条件
	 * @param dtoArg
	 *            购物数据
	 * @return true满足，false不满足
	 */
	public static ConditionMatchResult match(ConditionInstance condtInst,ConditionInstanceExt condtInstExt,
			OrderPromotionActivityDto dtoArg) {
		switch (condtInst.getCondtId()) {
		// 商品分类
		case PromotionCondition.CTYPE_PRODUCT_CATEGORY:
			return processProductCategoriesCondt(condtInst, condtInstExt, dtoArg);

			// 指定商品
		case PromotionCondition.CTYPE_SPECIFY_PRODUCT:
			return processSpecifyProductCondt(condtInst, condtInstExt, dtoArg);

			// 指定仓库
		case PromotionCondition.CTYPE_SPECIFY_WAREHOUSE:
			return processSpecifyWarehouseCondt(condtInst, condtInstExt, dtoArg);

			// 商品类型
		case PromotionCondition.CTYPE_PRODUCT_TYPE:
			return processProductTypeCondt(condtInst, condtInstExt, dtoArg);
			// 小计金额

		case PromotionCondition.CTYPE_SUBTOTAL:
			return processSubtotalCondt(condtInst, condtInstExt, dtoArg);
			// 总商品数量
		case PromotionCondition.CTYPE_PRODUCT_TOTAL_COUNT:
			return processProductTotalCountCondt(condtInst, condtInstExt, dtoArg);

			// 总重量
		case PromotionCondition.CTYPE_PRODUCT_TOTAL_WEIGHT:
			return processShoppingCartTotalWeightCondt(condtInst, condtInstExt, dtoArg);

			// 运送地区
		case PromotionCondition.CTYPE_SHIPPING_REGION:
			return processShippingRegionCondt(condtInst, condtInstExt, dtoArg);

			// // 普通分销商
			// case PromotionCondition.CTYPE_COMMON_DISTRIBUTOR:
			// processCommonDistributorCont(condtInst, dtoArg);
			//
			// // 合营分销商
			// case PromotionCondition.CTYPE_CO_DISTRIBUTOR:
			// processCoDistributor(condtInst, dtoArg);
			//
			// // 内部分销商
			// case PromotionCondition.CTYPE_INTER_DISTRIBUTOR:
			// processInterDistributorCondt(condtInst, dtoArg);
			//
			// // 新注册用户
			// case PromotionCondition.CTYPE_NEW_USER:
			// processNewUserCondt(condtInst, dtoArg);
			//
			// // 第一次购物的用户
			// case PromotionCondition.CTYPE_FIRST_SHOPPING:
			// processFirstShoppingCondt(condtInst, dtoArg);
			//
			// // 三个月内未购物的用户
			// case PromotionCondition.CTYPE_NO_SHOPPING_IN_THREE_MONTH:
			// processNoShoppingInThreeMonthCondt(condtInst, dtoArg);

		default:
			ConditionMatchResult dto = new ConditionMatchResult();
			dto.setMatched(false);
			return dto;
		}
	}

	// private static boolean processNewUserCondt(ConditionInstance condtInst,
	// OrderPromotionActivityDto dtoArg) {
	// return false;
	// }
	//
	// private static boolean processNoShoppingInThreeMonthCondt(
	// ConditionInstance condtInst, OrderPromotionActivityDto dtoArg) {
	// return false;
	// }
	//
	// private static boolean processFirstShoppingCondt(
	// ConditionInstance condtInst, OrderPromotionActivityDto dtoArg) {
	// return false;
	// }
	//
	// private static boolean processInterDistributorCondt(
	// ConditionInstance condtInst, OrderPromotionActivityDto dtoArg) {
	// return false;
	// }
	//
	// private static boolean processCoDistributor(ConditionInstance condtInst,
	// OrderPromotionActivityDto dtoArg) {
	// return false;
	// }
	//
	// private static boolean processCommonDistributorCont(
	// ConditionInstance condtInst, OrderPromotionActivityDto dtoArg) {
	// return false;
	// }

	/**
	 * 处理运送地区
	 * 
	 * @param condtInst
	 * @param dtoArg
	 * @return
	 */
	private static ConditionMatchResult processShippingRegionCondt(
			ConditionInstance condtInst, ConditionInstanceExt condtInstExt, OrderPromotionActivityDto dtoArg) {
		ConditionMatchResult result = null;
		if (dtoArg == null || dtoArg.getCityId() == null) {
			result = new ConditionMatchResult();
			result.setMatched(false);
			return result;
		}
		// {"shippingRegions":[{cityId:12,cityName:"北京"},{cityId:12,cityName:"广州"}]
		// }
		ShippingRegion shippingRegion = Json.fromJson(
				Json.parse(condtInst.getCondtJgmntValue()),
				ShippingRegion.class);
		result = condtInst.handle(shippingRegion,condtInstExt, dtoArg);
		return result;
	}
	

	/**
	 * 处理商品总重量
	 * 
	 * @param condtInst
	 * @param dtoArg
	 * @return
	 */
	private static ConditionMatchResult processShoppingCartTotalWeightCondt(
			ConditionInstance condtInst,ConditionInstanceExt condtInstExt, OrderPromotionActivityDto dtoArg) {
		ConditionMatchResult result = null;
		if (dtoArg.getTotalWeight() == null) {
			result = new ConditionMatchResult();
			result.setMatched(false);
			return result;
		}
		// {"minWeight":1,"maxWeight":2}
		ShoppingCartTotalWeight shoppingCartTotalWeight = Json.fromJson(
				Json.parse(condtInst.getCondtJgmntValue()),
				ShoppingCartTotalWeight.class);
		result = condtInst.handle(shoppingCartTotalWeight,condtInstExt, dtoArg);
		return result;
	}

	/**
	 * 处理商品总数量
	 * 
	 * @param condtInst
	 * @param dtoArg
	 * @return
	 */
	private static ConditionMatchResult processProductTotalCountCondt(
			ConditionInstance condtInst,ConditionInstanceExt condtInstExt, OrderPromotionActivityDto dtoArg) {
		ConditionMatchResult result = null;
		if (dtoArg == null || dtoArg.getTotalNumber() == null) {
			result = new ConditionMatchResult();
			result.setMatched(false);
			return result;
		}
		// {"minTotalCount":1,"maxTotalCount":2}
		ProductTotalCount productTotalCount = Json.fromJson(
				Json.parse(condtInst.getCondtJgmntValue()),
				ProductTotalCount.class);
		result = condtInst.handle(productTotalCount,condtInstExt, dtoArg);
		return result;
	}

	/**
	 * 处理小计金额
	 * 
	 * @param condtInst
	 * @param dtoArg
	 * @return
	 */
	private static ConditionMatchResult processSubtotalCondt(
			ConditionInstance condtInst,ConditionInstanceExt condtInstExt, OrderPromotionActivityDto dtoArg) {
		ConditionMatchResult result = null;
		if (dtoArg == null || dtoArg.getMoney() == null) {
			result = new ConditionMatchResult();
			result.setMatched(false);
			return result;
		}
		// {"minPrice":1,"maxPrice":2}
		Subtotal subtotal = Json.fromJson(
				Json.parse(condtInst.getCondtJgmntValue()), Subtotal.class);
		result = condtInst.handle(subtotal,condtInstExt, dtoArg);
		return result;
	}

	/**
	 * 处理商品类型
	 * 
	 * @param condtInst
	 * @param dtoArg
	 * @return
	 */
	private static ConditionMatchResult processProductTypeCondt(
			ConditionInstance condtInst,ConditionInstanceExt condtInstExt, OrderPromotionActivityDto dtoArg) {
		ConditionMatchResult result = null;
		if (dtoArg.getCommodity() == null) {
			result = new ConditionMatchResult();
			result.setMatched(false);
			return result;
		}
		// {"productType":1}
		ProductType productType = Json.fromJson(
				Json.parse(condtInst.getCondtJgmntValue()), ProductType.class);
		result = condtInst.handle(productType,condtInstExt, dtoArg);
		return result;
	}

	/**
	 * 处理指定仓库
	 * 
	 * @param condtInst
	 * @param dtoArg
	 * @return
	 */
	private static ConditionMatchResult processSpecifyWarehouseCondt(
			ConditionInstance condtInst,ConditionInstanceExt condtInstExt, OrderPromotionActivityDto dtoArg) {
		ConditionMatchResult result = null;
		if (dtoArg.getCommodity() == null) {
			result = new ConditionMatchResult();
			result.setMatched(false);
			return result;
		}
		// {"specifyWarehouses":[{warehouseId:12,warehouseName:"深圳仓"},{warehouseId:13,warehouseName:"广州仓"}]}
		SpecifyWarehouse specifyWarehouse = Json.fromJson(
				Json.parse(condtInst.getCondtJgmntValue()),
				SpecifyWarehouse.class);
		result = condtInst.handle(specifyWarehouse,condtInstExt, dtoArg);
		return result;
	}

	/**
	 * 处理指定商品
	 * 
	 * @param condtInst
	 * @param dtoArg
	 * @return
	 */
	private static ConditionMatchResult processSpecifyProductCondt(
			ConditionInstance condtInst,ConditionInstanceExt condtInstExt, OrderPromotionActivityDto dtoArg) {
		ConditionMatchResult result = null;
		
		if (dtoArg.getCommodity() == null) {
			result = new ConditionMatchResult();
			result.setMatched(false);
			return result;
		}
		// {"specifyProductList":[{sku:"IM27","wareHouseId":2024,num:1},{sku:"IM21","wareHouseId":2024,num:1}]
		// }
		SpecifyProduct specifyProduct = Json.fromJson(
				Json.parse(condtInst.getCondtJgmntValue()),
				SpecifyProduct.class);

		result = condtInst.handle(specifyProduct,condtInstExt, dtoArg);
		
		return result;
	}

	/**
	 * 处理商品分类
	 * 
	 * @param condtInst
	 * @param dtoArg
	 * @return
	 */
	private static ConditionMatchResult processProductCategoriesCondt(
			ConditionInstance condtInst,ConditionInstanceExt condtInstExt, OrderPromotionActivityDto dtoArg) {
		ConditionMatchResult result = null;
		if (dtoArg.getCommodity() == null) {
			result = new ConditionMatchResult();
			result.setMatched(false);
			return result;
		}

		Logger.info("productCategory:" + condtInst.getCondtJgmntValue());
		ProductCategory productCategory = Json.fromJson(
				Json.parse(condtInst.getCondtJgmntValue()),
				ProductCategory.class);
		result = condtInst.handle(productCategory,condtInstExt, dtoArg);
		return result;
	}
}
