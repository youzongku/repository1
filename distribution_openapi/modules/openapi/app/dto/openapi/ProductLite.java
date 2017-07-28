package dto.openapi;

import java.text.DecimalFormat;

public class ProductLite {

	DecimalFormat df = new DecimalFormat("#.00");

	private Integer iid;
	private String ctitle;
	private String csku;
	private String clistingId;
	private String imageUrl; // 图片地址
	
	private Integer sales;//已售

	private double originalPrice; // 通淘价（fprice）
	private double localPrice; // 市场价 local_ref_price
	private double salePrice; // 促销价格 （促销表t_product_saleprice中）
	private double fcostprice;// 成本价
	private Integer qty;// 库存
	private String cname;// 所属类目名称
	private Integer categoryId;//所属类目ID

	// 这2个字段来自inventory模块
	private String warehouseName;
	private Integer stock = 0;//云仓库存
	private Integer totalstock;//总仓库存

	private Integer warehouseId;// 仓库ID

	private Integer istatus;
	
    private Double fweight;//重量

	//ERP推送b2b新增字段

    private Integer disStockId;//仓库ID

    private Double disProfitRate;//分销利润率

    private Double disProfit;//分销毛利润

    private Double disVat;//分销增值税

    private Double disStockFee;//分销操作费

    private String disShippingType;//分销物流方式

    private Double disOtherCost;//分销其他费用

    private Double disTotalCost;//分销总成本

    private Double disTransferFee;//分销转仓费

    private Double disListFee;//分销登录费

    private Double disTradeFee;//分销平台 交易费

    private Double disPayFee;//分销支付费

    private Double disPostalFee;//分销行邮税

    private Double disImportTar;//分销进口关税

    private Double disGst;//分销消费税

    private Double disInsurance;//分销保险费

    private Double disTotalVat;//分销增值税

    private Double cost;//裸采购价

    private Double disFreight;//分销物流费

    private Double disPrice;//分销价

    private Double disCifPrice;//CIF价格

    private Integer packQty;//箱规

    private String productEnterprise;//生产厂家

    private String componentContent;//成分含量

    private Integer expirationDays;//保质期

    private String packageType;//包装种类

    private String originCountry;//原产地

    private String plugType;//规格

    private String interBarCode;//国际条码

    private String brand;//品牌

	private String batchNumber;//起批量
	
	private Double postalRate;//行邮税率
	
	private Boolean isSpecial;//是否特价商品
	
	private Double specialSale;//特价
	
	private Integer microStock = 0;//微仓库存
	
	private Double distributorPrice;// 经销商价格

	private Double electricityPrices;// 电商价格

	private Double supermarketPrice;// 商超价格
	
	private Double ftzPrice;//自贸区价格
	
	private Double proposalRetailPrice;//建议零售价
	
	private Double vipPrice;// vip价格
	
	private Double gstRate;// 消费税税率
	
	private Double vatRate;// 增值税税率
	
	private Double importTarRate;// 关税税率
	
	private Double postalFeeRate;// 行邮税税率
	
	private Double logisticFee;// 头程运费
	
	private Integer typeId;//商品类别
	
	private String typeName;//商品类别名称
	
	private Integer salable;// 非卖状态（0不可卖，1可卖）
	
	private String contractNo;// 合同号

	private Double clearancePrice;// 清货价

	private String expirationDate;// 到期日期
	
	private String imageName;// 图片名称
	
	public Double getVipPrice() {
		return vipPrice;
	}

	public void setVipPrice(Double vipPrice) {
		this.vipPrice = vipPrice;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public Integer getSalable() {
		return salable;
	}

	public void setSalable(Integer salable) {
		this.salable = salable;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public Double getClearancePrice() {
		return clearancePrice;
	}

	public void setClearancePrice(Double clearancePrice) {
		this.clearancePrice = clearancePrice;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
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

	public Integer getMicroStock() {
		return microStock;
	}

	public void setMicroStock(Integer microStock) {
		this.microStock = microStock;
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

	public ProductLite() {
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

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
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



	public Double getDisProfitRate() {
		return disProfitRate;
	}

	public void setDisProfitRate(Double disProfitRate) {
		this.disProfitRate = disProfitRate;
	}



	public Double getDisProfit() {
		return disProfit;
	}

	public void setDisProfit(Double disProfit) {
		this.disProfit = disProfit;
	}



	public Double getDisVat() {
		return disVat;
	}

	public void setDisVat(Double disVat) {
		this.disVat = disVat;
	}


	public Double getDisStockFee() {
		return disStockFee;
	}

	public void setDisStockFee(Double disStockFee) {
		this.disStockFee = disStockFee;
	}


	public String getDisShippingType() {
		return disShippingType;
	}

	public void setDisShippingType(String disShippingType) {
		this.disShippingType = disShippingType;
	}



	public Double getDisOtherCost() {
		return disOtherCost;
	}

	public void setDisOtherCost(Double disOtherCost) {
		this.disOtherCost = disOtherCost;
	}

	public Double getDisTotalCost() {
		return disTotalCost;
	}

	public void setDisTotalCost(Double disTotalCost) {
		this.disTotalCost = disTotalCost;
	}

	public Double getDisTransferFee() {
		return disTransferFee;
	}

	public void setDisTransferFee(Double disTransferFee) {
		this.disTransferFee = disTransferFee;
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
	
	public Integer getDisStockId() {
		return disStockId;
	}

	public void setDisStockId(Integer disStockId) {
		this.disStockId = disStockId;
	}

	public Double getDisListFee() {
		return disListFee;
	}

	public void setDisListFee(Double disListFee) {
		this.disListFee = disListFee;
	}

	public Double getDisTradeFee() {
		return disTradeFee;
	}

	public void setDisTradeFee(Double disTradeFee) {
		this.disTradeFee = disTradeFee;
	}

	public Double getDisPayFee() {
		return disPayFee;
	}

	public void setDisPayFee(Double disPayFee) {
		this.disPayFee = disPayFee;
	}

	public Double getDisPostalFee() {
		return disPostalFee;
	}

	public void setDisPostalFee(Double disPostalFee) {
		this.disPostalFee = disPostalFee;
	}

	public Double getDisImportTar() {
		return disImportTar;
	}

	public void setDisImportTar(Double disImportTar) {
		this.disImportTar = disImportTar;
	}

	public Double getDisGst() {
		return disGst;
	}

	public void setDisGst(Double disGst) {
		this.disGst = disGst;
	}

	public Double getDisInsurance() {
		return disInsurance;
	}

	public void setDisInsurance(Double disInsurance) {
		this.disInsurance = disInsurance;
	}

	public Double getDisTotalVat() {
		return disTotalVat;
	}

	public void setDisTotalVat(Double disTotalVat) {
		this.disTotalVat = disTotalVat;
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
	
	public Integer getTotalstock() {
		return totalstock;
	}

	public void setTotalstock(Integer totalstock) {
		this.totalstock = totalstock;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((clistingId == null) ? 0 : clistingId.hashCode());
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
		ProductLite other = (ProductLite) obj;
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


}
