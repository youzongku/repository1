package dto.marketing.promotion;

import java.util.List;

/**
 * 完整的活动实例信息，包含添加实例及优惠实例
 * 
 * @author huangjc
 * @date 2016年10月17日
 */
public class FullActInstDto {
	public static final int MATCH_TYPE_ALL = 1;
	public static final int MATCH_TYPE_ANY = 2;
	private Integer id;
	/** 促销活动id */
	private Integer proActId;
	/** 促销类型id */
	private Integer proTypeId;
	/** 活动实例名称，即促销类型名称 */
	private String name;
	/** 是否删除，默认false */
	private boolean isDelete = Boolean.FALSE;
	/** 当活动实例有多个条件时，在设置值时会有此字段，1全部、2、任意，默认为1 */
	private Integer matchType = MATCH_TYPE_ALL;
	/** 属性，于条件的属性一致 */
	private Short attr;

	private List<FullCondtInstDto> fullCondtInstDtoList;

//	private List<FullPvlgInstDto> fullPvlgInstDtoList;
	
	private List<CommodityDetail> commodits;
	
	public List<CommodityDetail> getCommodits() {
		return commodits;
	}

	public void setCommodits(List<CommodityDetail> commodits) {
		this.commodits = commodits;
	}

	/**add by根据商品属性获取sku集合  */
	private List<String> skus;

	public List<String> getSkus() {
		return skus;
	}

	public void setSkus(List<String> skus) {
		this.skus = skus;
	}

	public boolean isMatchAll() {
		return matchType.intValue() == 1;
	}

	public Short getAttr() {
		return attr;
	}

	public void setAttr(Short attr) {
		this.attr = attr;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getProActId() {
		return proActId;
	}

	public void setProActId(Integer proActId) {
		this.proActId = proActId;
	}

	public Integer getProTypeId() {
		return proTypeId;
	}

	public void setProTypeId(Integer proTypeId) {
		this.proTypeId = proTypeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public Integer getMatchType() {
		return matchType;
	}

	public void setMatchType(Integer matchType) {
		this.matchType = matchType;
	}

	public List<FullCondtInstDto> getFullCondtInstDtoList() {
		return fullCondtInstDtoList;
	}

	public void setFullCondtInstDtoList(
			List<FullCondtInstDto> fullCondtInstDtoList) {
		this.fullCondtInstDtoList = fullCondtInstDtoList;
	}

//	public List<FullPvlgInstDto> getFullPvlgInstDtoList() {
//		return fullPvlgInstDtoList;
//	}
//
//	public void setFullPvlgInstDtoList(List<FullPvlgInstDto> fullPvlgInstDtoList) {
//		this.fullPvlgInstDtoList = fullPvlgInstDtoList;
//	}

}
