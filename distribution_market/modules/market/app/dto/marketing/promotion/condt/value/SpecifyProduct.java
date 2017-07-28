package dto.marketing.promotion.condt.value;

import java.util.ArrayList;
import java.util.List;

import dto.marketing.promotion.CommodityDetail;
import dto.marketing.promotion.ConditionMatchResult;
import dto.marketing.promotion.OrderPromotionActivityDto;
import entity.marketing.promotion.ConditionInstanceExt;
import entity.marketing.promotion.ConditionJudgementType;
import play.libs.Json;

/**
 * 指定商品，可以指定多个
 * 
 * @author huangjc
 * @date 2016年7月29日
 */
public class SpecifyProduct extends BaseCondtMethod {
	// {
	// "specifyProductList":[{sku:"IM27","wareHouseId":2024},{sku:"IM21","wareHouseId":2024}]
	// }
	private List<SingleSpecifyProduct> specifyProductList = new ArrayList<SingleSpecifyProduct>();
	// 箱规
	private String unit;
	// 是否组合
	private boolean combined = false;

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public boolean isCombined() {
		return combined;
	}

	public void setCombined(boolean combined) {
		this.combined = combined;
	}

	public List<SingleSpecifyProduct> getSpecifyProductList() {
		return specifyProductList;
	}

	public void setSpecifyProductList(
			List<SingleSpecifyProduct> specifyProductList) {
		this.specifyProductList = specifyProductList;
	}

	// 单个指定商品
	public static class SingleSpecifyProduct {
		private String sku;
		private Integer warehouseId = 0;
		private String cTitle;
		private String warehouseName;
		private String imgUrl;
		// 箱规
		private String unit;// 件/箱
		// 每一箱的件数  费逻辑 没有箱规
		private int unitNum = 0;
		
		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

		public int getUnitNum() {
			return unitNum;
		}

		public void setUnitNum(int unitNum) {
			this.unitNum = unitNum;
		}

		public String getcTitle() {
			return cTitle;
		}

		public void setcTitle(String cTitle) {
			this.cTitle = cTitle;
		}

		public String getWarehouseName() {
			return warehouseName;
		}

		public void setWarehouseName(String warehouseName) {
			this.warehouseName = warehouseName;
		}

		public String getImgUrl() {
			return imgUrl;
		}

		public void setImgUrl(String imgUrl) {
			this.imgUrl = imgUrl;
		}
		public String getSku() {
			return sku;
		}

		public void setSku(String sku) {
			this.sku = sku;
		}

		public Integer getWarehouseId() {
			return warehouseId;
		}

		public void setWarehouseId(Integer warehouseId) {
			this.warehouseId = warehouseId;
		}

		@Override
		public String toString() {
			return "SingleSpecifyProduct [sku=" + sku + ", warehouseId="
					+ warehouseId + ", cTitle=" + cTitle + ", warehouseName="
					+ warehouseName + ", imgUrl=" + imgUrl 
					+ "]";
		}

	}

	@Override
	public ConditionMatchResult handle(String condtJgmntType, ConditionInstanceExt condtInstExt,
			OrderPromotionActivityDto dtoArg) {
		ConditionMatchResult dto = new ConditionMatchResult();
		if(condtInstExt ==  null){
			return dto;
		}
		SpecifyAttrValue specifyAttrValue = Json.fromJson(
				Json.parse(condtInstExt.getSpecifyAttrValue()),
				SpecifyAttrValue.class);
		int multiple = 0;// 倍数
		int commoditynumber = 0;// 组合 商品数量 
		double commodityPrice = 0.00; 
		ConditionMatchResult res = null;
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
			// 可组合
			if (this.isCombined() && dtoArg.getUserMode() != 1) {
				for (CommodityDetail cd : dtoArg.getCommodity()) {
					// 检验参数
					if (cd.getWarehouseId() == null || cd.getSku() == null
							|| "".equals(cd.getSku())) {
						dto.setMatched(false);
						return dto;
					}
					for (SingleSpecifyProduct ssp : specifyProductList) {
						// 比较sku和仓库id，才能确定是否同一件商品
						if (cd.getSku().equals(ssp.getSku())
								&& cd.getWarehouseId().intValue() == ssp
										.getWarehouseId().intValue()) {
							// 如果unitNum数量不为零，代表商品的单位是箱，把购物车传过来的商品转换成箱再去下面作比较
							if (ssp.unitNum != 0) {
								commoditynumber += cd.getNumber() / ssp.unitNum;
							} else {
								commoditynumber += cd.getNumber();
							}
							// change by zbc 将匹配到商品存起来
							dto.getCommoditys().add(cd);
							//商品匹配，价格累加
							commodityPrice += cd.getTotalPrice();
						}
					}
				}
				/**
				 * 数量 specifyAttrValue.getAttrType() == 1
				 * 金额 specifyAttrValue.getAttrType() == 2
				 * jType:gt/gteq/ltv,大于/大于等于/区间  前两个是单值  singleVal取
				 * ltv 区间 
				 * ltvVal:
				 * 	{
				 * 	 	minJType:gt/gteq,  左区间 开/闭
				 * 		minValue:1,		        最小值	
				 * 		maxJType:lt/lteq,  右区间 开/ 闭
				 * 		maxValue:3                       最大值 
				 * 	}
			 	 */	
				isComLogic(condtInstExt, dto, commoditynumber, commodityPrice);
				return dto;
			}
			// 以下是不可组合的逻辑  拿出数据库存储的所有指定商品
			for ( SingleSpecifyProduct ssp : specifyProductList )
			{
				// 比较sku和仓库id，才能确定是否同一件商品
				CommodityDetail cd = checkConditionsSatisfy( dtoArg.getCommodity(),ssp);
				if ( cd == null )
				{
					continue;
				}
				 // change by zbc  不可组合 逻辑修改
				 //修改前：假如选定商品A,B,C 数量为2,满足条件须：A==2&&B==2&&C==2 
				 //修改后：假如选定商品A,B,C 数量为2,满足条件须：A==2||B==2||C==2  
				// 如果unitNum数量不为零，代表商品的单位是箱，把购物车传过来的商品转换成箱再去下面作比较
				
				commoditynumber = ssp.getUnitNum() != 0?
						(cd.getNumber() / ssp.getUnitNum()):cd.getNumber();
				// 判断商品购买数量是否小于条件数量
				if(specifyAttrValue.getAttrType() == 1){
					res = isNotSatisfy(specifyAttrValue,commoditynumber);
				//2.金额判断
				}else if(specifyAttrValue.getAttrType() == 2){
					res = isNotSatisfy(specifyAttrValue,cd.getTotalPrice());
				}
				if (!res.notMathched())
				{
					dto.setMatched( true );
					//change by zbc 将匹配到商品存起来
					dto.getCommoditys().add(cd);
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
			// 可组合
			if (this.isCombined() && dtoArg.getUserMode() != 1) {
				for (CommodityDetail cd : dtoArg.getCommodity()) {
					cd = checktCommoditSatisfy(specifyProductList,cd);
					if(cd == null ){
						dto.setMatched(false);
						continue;
					}else{
						// change by zbc 将匹配到商品存起来
						dto.getCommoditys().add(cd);
						commoditynumber += cd.getNumber();
						//商品匹配，价格累加
						commodityPrice += cd.getTotalPrice();
					}
				}
				isComLogic(condtInstExt, dto, commoditynumber, commodityPrice);
				return dto;
			}
			// 以下是不可组合的逻辑  拿出数据库存储的所有指定商品
			for ( CommodityDetail cd : dtoArg.getCommodity() )
			{
				// 比较sku和仓库id，才能确定是否同一件商品
				cd = checktCommoditSatisfy( specifyProductList, cd);
				if ( cd == null )
				{
					continue;
				}
				if(specifyAttrValue.getAttrType() == 1){
					res = isNotSatisfy(specifyAttrValue,cd.getNumber());
				//2.金额判断
				}else if(specifyAttrValue.getAttrType() == 2){
					res = isNotSatisfy(specifyAttrValue,cd.getTotalPrice());
				}
				if (!res.notMathched())
				{
					// change by zbc 将匹配到商品存起来
					dto.getCommoditys().add(cd);
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

	/**
	 * @param commodity 购物测商品
	 * @param ssp 指定商品条件
	 * @author zbc
	 * @since 2016年10月22日 上午9:39:48
	 */
	public CommodityDetail checkConditionsSatisfy(List<CommodityDetail> commodity,
			SingleSpecifyProduct ssp) {
		// 不可组合
		for (CommodityDetail cd : commodity) {
			// 检验参数
			if (cd.getWarehouseId() == null || cd.getSku() == null|| "".equals(cd.getSku())) {
				return null;
			}
			if (cd.getSku().equals(ssp.getSku())&& cd.getWarehouseId().intValue() == ssp.getWarehouseId().intValue()) {
				return cd;
			}
		}
		return null;
	}
	public CommodityDetail checktCommoditSatisfy(List<SingleSpecifyProduct> sspList,CommodityDetail cd){
		boolean flag = true;
		// 检验参数
		if (cd.getWarehouseId() == null || cd.getSku() == null|| "".equals(cd.getSku())) {
			return null;
		}else{
			for(SingleSpecifyProduct ssp:sspList){
				if (cd.getSku().equals(ssp.getSku())&&cd.getWarehouseId().intValue() == ssp.getWarehouseId().intValue()) {
					flag = false;
				}
			}
		}
		if(flag){
			return cd;
		}
		return null;
	}
	
	
	
}
