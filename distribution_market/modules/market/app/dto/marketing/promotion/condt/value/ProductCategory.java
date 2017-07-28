package dto.marketing.promotion.condt.value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import dto.marketing.promotion.CommodityDetail;
import dto.marketing.promotion.ConditionMatchResult;
import dto.marketing.promotion.OrderPromotionActivityDto;
import entity.marketing.promotion.ConditionInstanceExt;
import entity.marketing.promotion.ConditionJudgementType;
import play.libs.Json;

/**
 * 商品分类
 * 
 * @author huangjc
 * @date 2016年7月29日
 */
public class ProductCategory extends BaseCondtMethod {
	/*
	 * { "productCategories":[{categoryId:12,categoryName:"母婴"},{categoryId:12,
	 * categoryName:"零食"}] }
	 */
	private List<SingleProductCategory> productCategories = new ArrayList<SingleProductCategory>();
	// 是否组合
	private boolean combined = false;
	
	@Override
	public String toString() {
		return "ProductCategory [productCategories=" + productCategories
				+ ", combined=" + combined + "]";
	}

	public boolean isCombined() {
		return combined;
	}

	public void setCombined(boolean combined) {
		this.combined = combined;
	}

	public List<SingleProductCategory> getProductCategories() {
		return productCategories;
	}

	public void setProductCategories(
			List<SingleProductCategory> productCategories) {
		this.productCategories = productCategories;
	}

	public static class SingleProductCategory {
		private int categoryId;
		private String categoryName;

		public int getCategoryId() {
			return categoryId;
		}

		public void setCategoryId(int categoryId) {
			this.categoryId = categoryId;
		}

		public String getCategoryName() {
			return categoryName;
		}

		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}

		@Override
		public String toString() {
			return "SingleProductCategory [categoryId=" + categoryId
					+ ", categoryName=" + categoryName + "]";
		}

	}

	@Override
	public ConditionMatchResult handle(String condtJgmntType, ConditionInstanceExt condtInstExt,
			OrderPromotionActivityDto dtoArg) {
		ConditionMatchResult dto = new ConditionMatchResult();
		if(condtInstExt == null||(condtInstExt!= null&&condtInstExt.getCondtInstId() == null)){
			return dto;
		}
		SpecifyAttrValue specifyAttrValue = Json.fromJson(
				Json.parse(condtInstExt.getSpecifyAttrValue()),
				SpecifyAttrValue.class);
		dto.setMatched(false);
		int commoditynumber = 0;
		double commodityPrice = 0.00;
		Map<Integer,List<CommodityDetail>> cateMap =
				dtoArg.getCommodity().stream().collect(Collectors.groupingBy(e->e.getCommodityCategoryId()));
		
		ConditionMatchResult res = null;
		List<Integer> cateList = Lists.transform(this.productCategories, e->e.getCategoryId());
		int multiple = 0;
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
			//可组合 并且能为电商模式
			if (this.isCombined() && dtoArg.getUserMode() != 1) {
				for (CommodityDetail cd : dtoArg.getCommodity()) {
					if (cd.getCommodityCategoryId() == null) {
						dto.setMatched(false);
						return dto;
					}
					for (SingleProductCategory spc : this.productCategories) {
						if (cd.getCommodityCategoryId().intValue() == spc.getCategoryId()) {
							commoditynumber += cd.getNumber();
							commodityPrice += cd.getTotalPrice();
							dto.getCommoditys().add(cd);
						}
					}
				}
				isComLogic(condtInstExt, dto, commoditynumber, commodityPrice);
				return dto;
			}
			// 以下是不可组合的逻辑  拿出数据库存储的所有指定商品
			for ( SingleProductCategory spc : this.productCategories)
			{
				//拿出类目匹配的商品
				List<CommodityDetail> cdLists = cateMap.get(spc.getCategoryId());
				if ( cdLists == null )
				{
					continue;
				}
				commoditynumber = cdLists.stream().mapToInt(w -> w.getNumber()).sum();
				commodityPrice = cdLists.stream().mapToDouble(w -> w.getTotalPrice()).sum();
				// 判断商品购买数量是否小于条件数量
				if(specifyAttrValue.getAttrType() == 1){
					res = isNotSatisfy(specifyAttrValue,commoditynumber);
				//2.金额判断
				}else if(specifyAttrValue.getAttrType() == 2){
					res = isNotSatisfy(specifyAttrValue,commodityPrice);
				}
				if (!res.notMathched())
				{
					dto.getCommoditys().addAll(cdLists);
					dto.setMatched( true );
					//修改逻辑 :不可组合 满赠条件倍数可以叠加
					//校验是否可以翻倍
					if(condtInstExt.isDoubleUp()){
						multiple += res.getMultiple();
					}else{
						multiple += 1;
					}
				}
			}	
			dto.setMultiple( multiple );
			return dto;
		case ConditionJudgementType.JTYPE_LTV:// 区间
			break;
		case ConditionJudgementType.JTYPE_NO:// 非
			//可组合 并且能为电商模式
			if (this.isCombined() && dtoArg.getUserMode() != 1) {
				for (CommodityDetail cd : dtoArg.getCommodity()) {
					if(cateList.contains(cd.getCommodityCategoryId())){
						dto.setMatched(false);
						continue;
					}else{
						dto.getCommoditys().add(cd);
						commoditynumber += cd.getNumber();
						//商品匹配，价格累加
						commodityPrice += cd.getTotalPrice();
					}
				}
				isComLogic(condtInstExt, dto, commoditynumber, commodityPrice);
				return dto;
			}
			//将购物测商品 用仓库ID分组
			Integer key = null;
			for(Map.Entry<Integer,List<CommodityDetail>> entry:cateMap.entrySet()){
				key = entry.getKey();
				if(cateList.contains(key)){
					continue;
				}
				commoditynumber =  cateMap.get(key).stream().mapToInt(w -> w.getNumber()).sum();
				commodityPrice =  cateMap.get(key).stream().mapToDouble(w -> w.getTotalPrice()).sum();
				if(specifyAttrValue.getAttrType() == 1){
					res = isNotSatisfy(specifyAttrValue,commoditynumber);
				//2.金额判断
				}else if(specifyAttrValue.getAttrType() == 2){
					res = isNotSatisfy(specifyAttrValue,commodityPrice);
				}
				if (!res.notMathched())
				{
					dto.setCommoditys(cateMap.get(key));
					dto.setMatched( true );
					//修改逻辑 :不可组合 满赠条件倍数可以叠加
					//校验是否可以翻倍
					if(condtInstExt.isDoubleUp()){
						multiple += res.getMultiple();
					}else{
						multiple += 1;
					}
				}
			}
			dto.setMultiple( multiple );
			return dto;
		}
		return dto;
	}

}
