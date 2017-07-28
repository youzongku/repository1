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
 * 指定仓库，可以指定多个仓库
 * 
 * @author huangjc
 * @date 2016年7月29日
 */
public class SpecifyWarehouse extends BaseCondtMethod {
	/*
	 * {
	 * "specifyWarehouses":[{warehouseId:12,warehouseName:"深圳仓"},{warehouseId:13
	 * ,warehouseName:"广州仓"}] }
	 */
	private List<SingleSpecifyWarehouse> specifyWarehouses = new ArrayList<SingleSpecifyWarehouse>();
	// 是否组合
	private boolean combined = false;

	@Override
	public String toString() {
		return "SpecifyWarehouse [specifyWarehouses=" + specifyWarehouses
				+ ", combined=" + combined + "]";
	}

	public boolean isCombined() {
		return combined;
	}

	public void setCombined(boolean combined) {
		this.combined = combined;
	}

	public List<SingleSpecifyWarehouse> getSpecifyWarehouses() {
		return specifyWarehouses;
	}

	public void setSpecifyWarehouses(
			List<SingleSpecifyWarehouse> specifyWarehouses) {
		this.specifyWarehouses = specifyWarehouses;
	}

	public static class SingleSpecifyWarehouse {
		private int warehouseId;
		private String warehouseName;

		public int getWarehouseId() {
			return warehouseId;
		}

		public void setWarehouseId(int warehouseId) {
			this.warehouseId = warehouseId;
		}

		public String getWarehouseName() {
			return warehouseName;
		}

		public void setWarehouseName(String warehouseName) {
			this.warehouseName = warehouseName;
		}

		@Override
		public String toString() {
			return "SingleSpecifyWarehouse [warehouseId=" + warehouseId
					+ ", warehouseName=" + warehouseName + "]";
		}

	}

	@Override
	public ConditionMatchResult handle(String condtJgmntType, ConditionInstanceExt condtInstExt,
			OrderPromotionActivityDto dtoArg) {
		ConditionMatchResult dto = new ConditionMatchResult();
		if(condtInstExt == null||(condtInstExt != null && condtInstExt.getSpecifyAttrValue() != null)){
			return dto;
		}
		SpecifyAttrValue specifyAttrValue = Json.fromJson(
				Json.parse(condtInstExt.getSpecifyAttrValue()),
				SpecifyAttrValue.class);
		dto.setMatched(false);
		int commoditynumber = 0;
		double commodityPrice = 0.00;
		int multiple = 0;
		
		Map<Integer,List<CommodityDetail>>  wareMap =
				dtoArg.getCommodity().stream().collect(Collectors.groupingBy(e->e.getWarehouseId()));
		ConditionMatchResult res = null;
		List<Integer> wareList = Lists.transform(this.getSpecifyWarehouses(), e->e.getWarehouseId());
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
					if (cd.getWarehouseId() == null) {
						dto.setMatched(false);
						return dto;
					}
					for (SingleSpecifyWarehouse ssw : this.specifyWarehouses) {
						if (cd.getWarehouseId().intValue() == ssw.getWarehouseId()) {
							dto.getCommoditys().add(cd);
							commoditynumber += cd.getNumber();
							commodityPrice += cd.getTotalPrice();
						}
					}
				}
				isComLogic(condtInstExt, dto, commoditynumber, commodityPrice);
				return dto;
			}
			// 以下是不可组合的逻辑  拿出数据库存储的所有指定商品
			for ( SingleSpecifyWarehouse ssw : this.specifyWarehouses )
			{
				// 那么仓库匹配的商品
				List<CommodityDetail> cdLists = wareMap.get(ssw.getWarehouseId());
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
					if(wareList.contains(cd.getWarehouseId())){
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
			for(Map.Entry<Integer,List<CommodityDetail>> entry:wareMap.entrySet()){
				key = entry.getKey();
				if(wareList.contains(key)){
					continue;
				}
				commoditynumber =  wareMap.get(key).stream().mapToInt(w -> w.getNumber()).sum();
				commodityPrice =  wareMap.get(key).stream().mapToDouble(w -> w.getTotalPrice()).sum();
				if(specifyAttrValue.getAttrType() == 1){
					res = isNotSatisfy(specifyAttrValue,commoditynumber);
				//2.金额判断
				}else if(specifyAttrValue.getAttrType() == 2){
					res = isNotSatisfy(specifyAttrValue,commodityPrice);
				}
				if (!res.notMathched())
				{
					dto.getCommoditys().addAll(wareMap.get(key));
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
