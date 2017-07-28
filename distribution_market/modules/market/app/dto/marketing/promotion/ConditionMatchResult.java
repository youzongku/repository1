package dto.marketing.promotion;

import java.util.ArrayList;
import java.util.List;

/**
 * 条件匹配返回的结果集
 * 
 * @author lenovo
 *
 */
public class ConditionMatchResult {
	private boolean matched = false;// 是否匹配
	private Integer Multiple = 1;// 倍数
	//条件实体
	FullCondtInstDto fullCondtInstDto;
	//商品集合，用于存放匹配的商品
	private List<CommodityDetail> commoditys = new ArrayList<>();
	
	public List<CommodityDetail> getCommoditys() {
		return commoditys;
	}

	public void setCommoditys(List<CommodityDetail> commoditys) {
		this.commoditys = commoditys;
	}

	public FullCondtInstDto getFullCondtInstDto() {
		return fullCondtInstDto;
	}

	public void setFullCondtInstDto(FullCondtInstDto fullCondtInstDto) {
		this.fullCondtInstDto = fullCondtInstDto;
	}

	public boolean isMatched() {
		return matched;
	}
	
	public boolean notMathched(){
		return !matched;
	}
	public void setMatched(boolean matched) {
		this.matched = matched;
	}
	
	public Integer getMultiple() {
		return Multiple;
	}

	public void setMultiple(Integer multiple) {
		Multiple = multiple;
	}

	@Override
	public String toString() {
		return "ConditionMatchResult [matched=" + matched + ", Multiple="
				+ Multiple + "]";
	}

	

}
