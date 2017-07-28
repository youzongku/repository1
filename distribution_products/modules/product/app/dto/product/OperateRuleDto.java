package dto.product;

import entity.product.OperateProductPriceRule;

/**
 * 默认价格设置 操作记录
 * @author zbc
 * 2016年8月4日 下午3:12:13
 */
public class OperateRuleDto extends OperateProductPriceRule {
	
	private Integer pageNo;
	
	private Integer pageSize;
	
	private String key;
	
	private String startDate;
	
	private String endDate;

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
}
