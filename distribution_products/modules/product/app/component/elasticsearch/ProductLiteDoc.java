package component.elasticsearch;

import java.text.DecimalFormat;

/**
 * 
 * 商品保存在es里的实体，不包含库存
 * @author huangjc
 * @date 2016年11月28日
 */
/**
 * 商品doc文档
 * 
 * @author ye_ziran
 * @since 2017年3月1日 下午12:06:15
 */
public class ProductLiteDoc {
	DecimalFormat df = new DecimalFormat("#.00");

	@MappingType(type = "integer")
	private Integer iid;
	@MappingType(type = "string", index = "analyzed")
	private String ctitle;
	@MappingType(type = "string", index = "not_analyzed")
	private String csku;
	@MappingType(type = "string", index = "not_analyzed")
	private String clistingId;
	@MappingType(type = "string", index = "not_analyzed")
	private String imageUrl; // 图片地址
	@MappingType(type = "integer")
	private Integer sales;// 已售
	@MappingType(type = "double")
	private double originalPrice; // 通淘价（fprice）
	@MappingType(type = "double")
	private double localPrice; // 市场价 local_ref_price
	@MappingType(type = "double")
	private double salePrice; // 促销价格 （促销表t_product_saleprice中）
	@MappingType(type = "double")
	private double fcostprice;// 成本价
	@MappingType(type = "integer")
	private Integer qty;//
	@MappingType(type = "integer")
	private Integer stock = 0;// 云仓库存
	@MappingType(type = "integer")
	private Integer totalstock;// 总仓库存
	@MappingType(type = "string", index = "not_analyzed")
	private String cname;// 所属类目名称
	@MappingType(type = "integer")
	private Integer categoryId;// 所属类目ID

	// 这2个字段来自inventory模块
	@MappingType(type = "string", index = "analyzed")
	private String warehouseName;
	@MappingType(type = "integer")
	private Integer warehouseId;// 仓库ID
	@MappingType(type = "integer")
	private Integer istatus;
	@MappingType(type = "integer")
	private Integer salable;// 非卖状态（0不可卖，1可卖）
	@MappingType(type = "double")
	private Double fweight;// 重量
	@MappingType(type = "double")
	private Double cost;// 裸采购价
	@MappingType(type = "double")
	private Double disFreight;// 分销物流费
	@MappingType(type = "double")
	private Double disPrice;// 分销价
	@MappingType(type = "double")
	private Double disCifPrice;// CIF价格
	@MappingType(type = "integer")
	private Integer packQty;// 箱规
	@MappingType(type = "string", index = "analyzed")
	private String productEnterprise;// 生产厂家
	@MappingType(type = "string", index = "not_analyzed")
	private String componentContent;// 成分含量
	@MappingType(type = "integer")
	private Integer expirationDays;// 保质期
	@MappingType(type = "string", index = "not_analyzed")
	private String packageType;// 包装种类
	@MappingType(type = "string", index = "not_analyzed")
	private String originCountry;// 原产地
	@MappingType(type = "string", index = "not_analyzed")
	private String plugType;// 规格
	@MappingType(type = "string", index = "not_analyzed")
	private String interBarCode;// 国际条码
	@MappingType(type = "string", index = "analyzed")
	private String brand;// 品牌
	@MappingType(type = "string", index = "not_analyzed")
	private String batchNumber;// 起批量
	@MappingType(type = "double")
	private Double postalRate;// 行邮税率
	@MappingType(type = "boolean")
	private Boolean isSpecial;// 是否特价商品
	@MappingType(type = "double")
	private Double specialSale;// 特价
	@MappingType(type = "double")
	private Double distributorPrice;// 经销商价格
	@MappingType(type = "double")
	private Double electricityPrices;// 电商价格
	@MappingType(type = "double")
	private Double supermarketPrice;// 商超价格
	@MappingType(type = "double")
	private Double ftzPrice;// 自贸区价格
	@MappingType(type = "double")
	private Double proposalRetailPrice;// 建议零售价
	@MappingType(type = "double")
	private Double vipPrice;// vip价格
	@MappingType(type = "double")
	private Double gstRate;// 消费税税率
	@MappingType(type = "double")
	private Double vatRate;// 增值税税率
	@MappingType(type = "double")
	private Double importTarRate;// 关税税率
	@MappingType(type = "double")
	private Double postalFeeRate;// 行邮税税率
	@MappingType(type = "double")
	private Double logisticFee;// 头程运费
	@MappingType(type = "integer")
	private Integer typeId;// 商品类别
	@MappingType(type = "string", index = "not_analyzed")
	private String typeName;// 商品类别名称

	// ------初始化后再维护进doc---------
	@MappingType(type = "string", index = "not_analyzed")
	private String categoryIdTree;
	@MappingType(type = "string", index = "analyzed")
	private String categoryNameTree;
	@MappingType(type = "string", index = "not_analyzed")
	private String virCategoryIdTree;
	@MappingType(type = "string", index = "analyzed")
	private String virCategoryNameTree;

	public Integer getSalable() {
		return salable;
	}

	public void setSalable(Integer salable) {
		this.salable = salable;
	}

	public Double getLogisticFee() {
		return logisticFee;
	}

	public void setLogisticFee(Double logisticFee) {
		this.logisticFee = logisticFee;
	}

	public Double getGstRate() {
		return gstRate;
	}

	public void setGstRate(Double gstRate) {
		this.gstRate = gstRate;
	}

	public Double getVatRate() {
		return vatRate;
	}

	public void setVatRate(Double vatRate) {
		this.vatRate = vatRate;
	}

	public Double getImportTarRate() {
		return importTarRate;
	}

	public void setImportTarRate(Double importTarRate) {
		this.importTarRate = importTarRate;
	}

	public Double getPostalFeeRate() {
		return postalFeeRate;
	}

	public void setPostalFeeRate(Double postalFeeRate) {
		this.postalFeeRate = postalFeeRate;
	}

	public Double getProposalRetailPrice() {
		return proposalRetailPrice;
	}

	public void setProposalRetailPrice(Double proposalRetailPrice) {
		this.proposalRetailPrice = proposalRetailPrice;
	}

	public Double getFtzPrice() {
		return ftzPrice;
	}

	public void setFtzPrice(Double ftzPrice) {
		this.ftzPrice = ftzPrice;
	}

	public Double getDistributorPrice() {
		return distributorPrice;
	}

	public void setDistributorPrice(Double distributorPrice) {
		this.distributorPrice = distributorPrice;
	}

	public Double getElectricityPrices() {
		return electricityPrices;
	}

	public void setElectricityPrices(Double electricityPrices) {
		this.electricityPrices = electricityPrices;
	}

	public Double getSupermarketPrice() {
		return supermarketPrice;
	}

	public void setSupermarketPrice(Double supermarketPrice) {
		this.supermarketPrice = supermarketPrice;
	}

	public Boolean getIsSpecial() {
		return isSpecial;
	}

	public void setIsSpecial(Boolean isSpecial) {
		this.isSpecial = isSpecial;
	}

	public Double getSpecialSale() {
		return specialSale;
	}

	public void setSpecialSale(Double specialSale) {
		this.specialSale = specialSale;
	}

	public Double getPostalRate() {
		return postalRate;
	}

	public void setPostalRate(Double postalRate) {
		this.postalRate = postalRate;
	}

	public Integer getSales() {
		return sales;
	}

	public void setSales(Integer sales) {
		this.sales = sales;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public Integer getIid() {
		return iid;
	}

	public void setIid(Integer iid) {
		this.iid = iid;
	}

	public String getCtitle() {
		return ctitle;
	}

	public void setCtitle(String ctitle) {
		this.ctitle = ctitle;
	}

	public String getCsku() {
		return csku;
	}

	public void setCsku(String csku) {
		this.csku = csku;
	}

	public String getClistingId() {
		return clistingId;
	}

	public void setClistingId(String clistingId) {
		this.clistingId = clistingId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public double getOriginalPrice() {
		return originalPrice;
	}

	public void setOriginalPrice(double originalPrice) {
		this.originalPrice = Double.parseDouble(df.format(originalPrice));
	}

	public double getLocalPrice() {
		return localPrice;
	}

	public void setLocalPrice(double localPrice) {
		this.localPrice = Double.parseDouble(df.format(localPrice));
	}

	public double getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(double salePrice) {
		this.salePrice = Double.parseDouble(df.format(salePrice));
	}

	public double getFcostprice() {
		return fcostprice;
	}

	public void setFcostprice(double fcostprice) {
		this.fcostprice = Double.parseDouble(df.format(fcostprice));
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public Integer getTotalstock() {
		return totalstock;
	}

	public void setTotalstock(Integer totalstock) {
		this.totalstock = totalstock;
	}

	public Integer getIstatus() {
		return istatus;
	}

	public void setIstatus(Integer istatus) {
		this.istatus = istatus;
	}

	public Double getFweight() {
		return fweight;
	}

	public void setFweight(Double fweight) {
		this.fweight = fweight;
	}

	public Integer getPackQty() {
		return packQty;
	}

	public void setPackQty(Integer packQty) {
		this.packQty = packQty;
	}

	public String getProductEnterprise() {
		return productEnterprise;
	}

	public void setProductEnterprise(String productEnterprise) {
		this.productEnterprise = productEnterprise;
	}

	public String getComponentContent() {
		return componentContent;
	}

	public void setComponentContent(String componentContent) {
		this.componentContent = componentContent;
	}

	public Integer getExpirationDays() {
		return expirationDays;
	}

	public void setExpirationDays(Integer expirationDays) {
		this.expirationDays = expirationDays;
	}

	public String getPackageType() {
		return packageType;
	}

	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}

	public String getOriginCountry() {
		return originCountry;
	}

	public void setOriginCountry(String originCountry) {
		this.originCountry = originCountry;
	}

	public String getPlugType() {
		return plugType;
	}

	public void setPlugType(String plugType) {
		this.plugType = plugType;
	}

	public String getInterBarCode() {
		return interBarCode;
	}

	public void setInterBarCode(String interBarCode) {
		this.interBarCode = interBarCode;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public Double getDisPrice() {
		return disPrice;
	}

	public void setDisPrice(Double disPrice) {
		this.disPrice = disPrice;
	}

	public Double getDisFreight() {
		return disFreight;
	}

	public void setDisFreight(Double disFreight) {
		this.disFreight = disFreight;
	}

	public Double getDisCifPrice() {
		return disCifPrice;
	}

	public void setDisCifPrice(Double disCifPrice) {
		this.disCifPrice = disCifPrice;
	}

	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public Double getVipPrice() {
		return vipPrice;
	}

	public void setVipPrice(Double vipPrice) {
		this.vipPrice = vipPrice;
	}

	public String getCategoryIdTree() {
		return categoryIdTree;
	}

	public void setCategoryIdTree(String categoryIdTree) {
		this.categoryIdTree = categoryIdTree;
	}

	public String getCategoryNameTree() {
		return categoryNameTree;
	}

	public void setCategoryNameTree(String categoryNameTree) {
		this.categoryNameTree = categoryNameTree;
	}

	public String getVirCategoryIdTree() {
		return virCategoryIdTree;
	}

	public void setVirCategoryIdTree(String virCategoryIdTree) {
		this.virCategoryIdTree = virCategoryIdTree;
	}

	public String getVirCategoryNameTree() {
		return virCategoryNameTree;
	}

	public void setVirCategoryNameTree(String virCategoryNameTree) {
		this.virCategoryNameTree = virCategoryNameTree;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clistingId == null) ? 0 : clistingId.hashCode());
		result = prime * result + ((csku == null) ? 0 : csku.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductLiteDoc other = (ProductLiteDoc) obj;
		if (clistingId == null) {
			if (other.clistingId != null)
				return false;
		} else if (!clistingId.equals(other.clistingId))
			return false;
		if (csku == null) {
			if (other.csku != null)
				return false;
		} else if (!csku.equals(other.csku))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ProductLiteDoc [df=" + df + ", iid=" + iid + ", ctitle=" + ctitle + ", csku=" + csku + ", clistingId="
				+ clistingId + ", imageUrl=" + imageUrl + ", sales=" + sales + ", originalPrice=" + originalPrice
				+ ", localPrice=" + localPrice + ", salePrice=" + salePrice + ", fcostprice=" + fcostprice + ", qty="
				+ qty + ", stock=" + stock + ", totalstock=" + totalstock + ", cname=" + cname + ", categoryId="
				+ categoryId + ", warehouseName=" + warehouseName + ", warehouseId=" + warehouseId + ", istatus="
				+ istatus + ", salable=" + salable + ", fweight=" + fweight + ", cost=" + cost + ", disFreight="
				+ disFreight + ", disPrice=" + disPrice + ", disCifPrice=" + disCifPrice + ", packQty=" + packQty
				+ ", productEnterprise=" + productEnterprise + ", componentContent=" + componentContent
				+ ", expirationDays=" + expirationDays + ", packageType=" + packageType + ", originCountry="
				+ originCountry + ", plugType=" + plugType + ", interBarCode=" + interBarCode + ", brand=" + brand
				+ ", batchNumber=" + batchNumber + ", postalRate=" + postalRate + ", isSpecial=" + isSpecial
				+ ", specialSale=" + specialSale + ", distributorPrice=" + distributorPrice + ", electricityPrices="
				+ electricityPrices + ", supermarketPrice=" + supermarketPrice + ", ftzPrice=" + ftzPrice
				+ ", proposalRetailPrice=" + proposalRetailPrice + ", vipPrice=" + vipPrice + ", gstRate=" + gstRate
				+ ", vatRate=" + vatRate + ", importTarRate=" + importTarRate + ", postalFeeRate=" + postalFeeRate
				+ ", logisticFee=" + logisticFee + ", typeId=" + typeId + ", typeName=" + typeName + ", categoryIdTree="
				+ categoryIdTree + ", categoryNameTree=" + categoryNameTree + ", virCategoryIdTree=" + virCategoryIdTree
				+ ", virCategoryNameTree=" + virCategoryNameTree + "]";
	}

}