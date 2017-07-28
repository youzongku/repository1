package dto.product;

import java.util.List;

import entity.product.OperateProductPrice;

/**
 * 查询价格修改日志 实体类
 * @author zbc
 * 2016年8月4日 上午9:57:51
 */
public class OperatePriceLogDto extends OperateProductPrice {
	
	private Integer pageNo;//页码
	
	private Integer pageSize;//页长
	
	private String startDate;//开始时间
	
	private String endDate;//结束时间
	
	private String key;//搜索条件
	
	private List<String> skuList;//sku集合
	
	private List<String> fNameList;//字段名集合
	
	private String type;//价格栏目 类型

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getfNameList() {
		return fNameList;
	}

	public void setfNameList(List<String> fNameList) {
		this.fNameList = fNameList;
	}

	public List<String> getSkuList() {
		return skuList;
	}

	public void setSkuList(List<String> skuList) {
		this.skuList = skuList;
	}

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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
