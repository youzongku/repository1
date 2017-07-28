package dto.product;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import entity.category.ProductVcategoryMapper;

/**
 * 产品搜索条件Dto
 * 
 * @since 2015年12月9日
 */
public class ProductSearchParamDto {

	public static final int VIP_PRICE = 5;
	public static final int FTZ_PRICE = 4;
	public static final int SUPERMARKET_PRICE = 3;
	public static final int DISTRIBUTOR_PRICE = 2;
	public static final int ELETRICITY_PRICE = 1;

	private String sku;
	// 传List
	private List<String> skuList;
	// iid list
	private List<Integer> iids;

	// add by xu_shengen
	private Integer categoryId;// 真实类目id
	private List<Integer> vCategoryId;// 虚拟类目id
	private Integer pageSize;
	private Integer currPage;
	private Integer istatus;
	/**
	 * 非卖状态（0不可卖，1可卖）
	 */
	public static final int SALABLE_NO = 0;
	/**
	 * 非卖状态（0不可卖，1可卖）
	 */
	public static final int SALABLE_YES = 1;
	/**
	 * 非卖状态（0不可卖，1可卖）
	 */
	private Integer salable;
	private String title;
	private Double minPrice;
	private Double maxPrice;

	private Integer warehouseId;

	// 都云涛新增：折扣，新增用户折扣价格之后，搜索的价格需要做折扣处理之后才能准确匹配
	private Double disCount;

	// add by zdc 用于微仓查询
	private String email;

	// 微仓库存排序 0 或不传，以云仓库存排序，1的话 以微仓库存查询
	private Integer microSort;
	// 根据模式拼接SQL，查询需要返回的价格字段
	private String queryStr;
	// 用户模式，根据模式查询价格，默认电商价
	private Integer model;
	// 商品类型
	private Integer typeId;

	private String sidx;// jqGrid相关的排序关键字
	private String sord;// jqGrid相关排序方式

	private String disPriceSort;// 按价格排序
	
	private String wType;//虚拟仓类型，传入-10表示不查询虚拟仓商品
	
	private Boolean isBack;

	public String getwType() {
		return wType;
	}

	public void setwType(String wType) {
		this.wType = wType;
	}

	public Boolean getIsBack() {
		return isBack;
	}

	public void setIsBack(Boolean isBack) {
		this.isBack = isBack;
	}

	public Integer getModel() {
		return model;
	}

	public void setModel(Integer model) {
		this.model = model;

		if (this.model == null) {
			return;
		}
		if (model == 4) {
			setQueryStr("ftzPrice");
		} else if (model == 3) {
			setQueryStr("supermarketPrice");
		} else if (model == 2) {
			setQueryStr("distributorPrice");
		} else if (model == 1) {
			setQueryStr("electricityPrices");
		} else if (model == 5) {
			setQueryStr("vipPrice");
		}
	}

	public Integer getSalable() {
		return salable;
	}

	public void setSalable(Integer salable) {
		this.salable = salable;
	}

	public String getQueryStr() {
		return queryStr;
	}

	public void setQueryStr(String queryStr) {
		this.queryStr = queryStr;
	}

	private List<ProductVcategoryMapper> proVList;// 查询虚拟类目参数

	public List<ProductVcategoryMapper> getProVList() {
		return proVList;
	}

	public void setProVList(List<ProductVcategoryMapper> proVList) {
		this.proVList = proVList;
	}

	public Integer getMicroSort() {
		return microSort;
	}

	public void setMicroSort(Integer microSort) {
		this.microSort = microSort;
	}

	public String getEmail() {
		if(isBack != null && isBack){
			return null;
		}
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Double getDisCount() {
		return disCount;
	}

	public void setDisCount(Double disCount) {
		this.disCount = disCount;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Double getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(Double minPrice) {
		this.minPrice = minPrice;
	}

	public Double getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(Double maxPrice) {
		this.maxPrice = maxPrice;
	}

	public Integer getIstatus() {
		return istatus;
	}

	public void setIstatus(Integer istatus) {
		this.istatus = istatus;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getCurrPage() {
		return currPage;
	}

	public void setCurrPage(Integer currPage) {
		this.currPage = currPage;
	}

	public String getSku() {
		return sku;
	}

	public List<Integer> getvCategoryId() {
		return vCategoryId;
	}

	public void setvCategoryId(List<Integer> vCategoryId) {
		this.vCategoryId = vCategoryId;
	}

	/**
	 * 
	 * 
	 * @param sku
	 *            多个sku时，以逗号隔开
	 * @author ye_ziran
	 * @since 2016年3月29日 下午3:53:50
	 */
	public void setSku(String sku) {
		if (sku != null) {
			// 防止sku重复
			Set<String> skuSet = new HashSet<>();
			String[] skuArr = sku.split(",");
			for (String aSku : skuArr) {
				skuSet.add(aSku.trim());
			}
			this.skuList = Lists.newArrayList(skuSet);
		}
		this.sku = sku;
	}

	public List<String> getSkuList() {
		return skuList;
	}

	public void setSkuList(List<String> skuList) {
		this.skuList = skuList;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public List<Integer> getIids() {
		return iids;
	}

	public void setIids(List<Integer> iids) {
		this.iids = iids;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public String getSidx() {
		return sidx;
	}

	public void setSidx(String sidx) {
		this.sidx = sidx;
	}

	public String getSord() {
		return sord;
	}

	public void setSord(String sord) {
		this.sord = sord;
	}

	public String getDisPriceSort() {
		return disPriceSort;
	}

	public void setDisPriceSort(String disPriceSort) {
		this.disPriceSort = disPriceSort;
	}

	@Override
	public String toString() {
		return "ProductSearchParamDto [sku=" + sku + ", skuList=" + skuList + ", iids=" + iids + ", categoryId="
				+ categoryId + ", pageSize=" + pageSize + ", currPage=" + currPage + ", istatus=" + istatus + ", title="
				+ title + ", minPrice=" + minPrice + ", maxPrice=" + maxPrice + ", warehouseId=" + warehouseId
				+ ", disCount=" + disCount + ", email=" + email + ", microSort=" + microSort + ", queryStr=" + queryStr
				+ ", model=" + model + ", typeId=" + typeId + ", proVList=" + proVList + "]";
	}

}
