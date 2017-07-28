package entity.sales;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.wordnik.swagger.annotations.ApiModel;

import util.sales.DateUtils;

@ApiModel
public class SaleDetail implements Serializable {

	private static final long serialVersionUID = -3678736552781224703L;
	private Integer id;
	private String purchaseOrderNo; // 采购订单单号，此商品是属于哪个采购单的
	private String productPurchaseId;
	private String sku;
	private Integer qty;
	private String productImg;
	private String productName;
	private String interBarCode;
	private String plateformName;
	private Double purchasePrice;
	private Integer warehouseId;
	private String warehouseName;
	private Integer salesOrderId;
	private String salesOrderNo;
	private Integer isDeducted;
	private Double marketPrice;
	private Integer isAfterService;

	/********************************* 一下内容为分销价格体系，主要为推送ERP所设置 ***********************************/
	private Double disProfitRate;// 分销利润率
	private Double disProfit;// 分销毛利润
	private Double disVat;// 分销增值税
	private Double disStockFee;// 分销操作费
	private String disShippingType;// 分销物流方式
	private Double disOtherCost;// 分销其他费用
	private Double disTotalCost;// 分销总成本
	private Double disTransferFee;// 分销转仓费
	private Double disListFee;// 分销登录费
	private Double disTradeFee;// 分销平台 交易费
	private Double disPayFee;// 分销支付费
	private Double disPostalFee;// 分销行邮税
	private Double disImportTar;// 分销进口关税
	private Double disGst;// 分销消费税
	private Double disInsurance;// 分销保险费
	private Double disTotalVat;// 分销增值税
	private Double cost;// 裸采购价
	private Double disFreight;// 分销物流费
	private Double disPrice;// 分销价
	private Double disCifPrice;// CIF价格
	private Double finalSellingPrice;// 其他平台最终售价
	private Double postalRate;// 行邮税税率
	private Double gstRate;// 消费税税率
	private Double vatRate;// 增值税税率
	private Double importTarRate;// 关税税率
	private Double postalFeeRate;// 行邮税税率
	private Double logisticFee; // 头程运费
	private Integer isDeductedHistory; // 扣除商品 的微仓入库历史数据标识 (0:否 1:是)
	private Integer giftNum;// 赠品数量
	private Boolean isgift;// 是否包含赠品
	private Double capFee;// 均摊价(入库历史数据)
	private Double arriveWarePrice;// 到仓价(入库历史数据)
	private Date expirationDate;// 到期日期(微仓入库明细)
	private Integer categoryId;// 类目名称
	private String categoryName;// 类目id
	private String contractNo;//合同号
	private Double clearancePrice;//清货价

	private Integer shOrderId;//对应的售后单id
	private Integer count;//发起售后次数
	private Integer status;//对应的售后单状态

	public Double getClearancePrice() {
		return clearancePrice;
	}

	public void setClearancePrice(Double clearancePrice) {
		this.clearancePrice = clearancePrice;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	/**
	 * 将到期日期格式化为：yyyy-MM-dd
	 *
	 * @return
	 */
	public String getExpirationDateStr() {
		if(expirationDate==null){
			return null;
		}
		return DateUtils.date2string(expirationDate, DateUtils.FORMAT_DATE_PAGE);
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Double getCapFee() {
		return capFee;
	}

	public void setCapFee(Double capFee) {
		this.capFee = capFee;
	}

	public Double getArriveWarePrice() {
		return arriveWarePrice;
	}

	public void setArriveWarePrice(Double arriveWarePrice) {
		this.arriveWarePrice = arriveWarePrice;
	}

	public String getPurchaseOrderNo() {
		return purchaseOrderNo;
	}

	public void setPurchaseOrderNo(String purchaseOrderNo) {
		this.purchaseOrderNo = purchaseOrderNo;
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

	public Integer getGiftNum() {
		return giftNum;
	}

	public void setGiftNum(Integer giftNum) {
		this.giftNum = giftNum;
	}

	public Boolean getIsgift() {
		return isgift;
	}

	public void setIsgift(Boolean isgift) {
		this.isgift = isgift;
	}

	public Integer getIsDeductedHistory() {
		return isDeductedHistory;
	}

	public void setIsDeductedHistory(Integer isDeductedHistory) {
		this.isDeductedHistory = isDeductedHistory;
	}

	public Double getPostalRate() {
		return postalRate;
	}

	public void setPostalRate(Double postalRate) {
		this.postalRate = postalRate;
	}

	public Double getFinalSellingPrice() {
		return finalSellingPrice;
	}

	public void setFinalSellingPrice(Double finalSellingPrice) {
		this.finalSellingPrice = finalSellingPrice;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getProductPurchaseId() {
		return productPurchaseId;
	}

	public void setProductPurchaseId(String productPurchaseId) {
		this.productPurchaseId = productPurchaseId;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getPlateformName() {
		return plateformName;
	}

	public void setPlateformName(String plateformName) {
		this.plateformName = plateformName;
	}

	public Double getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(Double purchasePrice) {
		this.purchasePrice = purchasePrice;
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

	public Integer getSalesOrderId() {
		return salesOrderId;
	}

	public void setSalesOrderId(Integer salesOrderId) {
		this.salesOrderId = salesOrderId;
	}

	public String getProductImg() {
		return productImg;
	}

	public void setProductImg(String productImg) {
		this.productImg = productImg;
	}

	public String getSalesOrderNo() {
		return salesOrderNo;
	}

	public void setSalesOrderNo(String salesOrderNo) {
		this.salesOrderNo = salesOrderNo;
	}

	public Integer getIsDeducted() {
		return isDeducted;
	}

	public void setIsDeducted(Integer isDeducted) {
		this.isDeducted = isDeducted;
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

	public Double getDisFreight() {
		return disFreight;
	}

	public void setDisFreight(Double disFreight) {
		this.disFreight = disFreight;
	}

	public Double getDisPrice() {
		return disPrice;
	}

	public void setDisPrice(Double disPrice) {
		this.disPrice = disPrice;
	}

	public Double getDisCifPrice() {
		return disCifPrice;
	}

	public void setDisCifPrice(Double disCifPrice) {
		this.disCifPrice = disCifPrice;
	}

	public Double getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(Double marketPrice) {
		this.marketPrice = marketPrice;
	}

	public Integer getIsAfterService() {
		return isAfterService;
	}

	public void setIsAfterService(Integer isAfterService) {
		this.isAfterService = isAfterService;
	}

	/**
	 * 获取此商品的总的最终售价
	 *
	 * @return
	 */
	public BigDecimal calculateTotalFinalSellingPrice() {
		if (finalSellingPrice == null) {
			return new BigDecimal(0);
		}
		return new BigDecimal(finalSellingPrice).multiply(new BigDecimal(qty));
	}

	/**
	 * 关税
	 *
	 * @return
	 */
	public BigDecimal calculateImportTar(String warehouseType, int warehouseId,
			BigDecimal bbcPostage2AProduct, Double gstRateArg,
			Double vatRateArg, Double importTarRateArg,
			Double postalFeeRateArg, Double logisticFeeArg, Double costArg) {
		// TODO 由于warehouseType数据有误，先按照仓库id来判断
		// 海外直邮：美国（73）、英国（6）
		// 保税仓：福州仓（2050）、南沙保税仓（2029）
		// 外部保税：杭州保税仓（2012）、福州1仓（2062）
		// 完税仓：深圳仓（2024）
		if (2050 == warehouseId || 2029 == warehouseId) {
			// 内部保税仓，福州仓/南沙保税仓
			// 若订单产生运费（如不包邮），按金额分摊到每个商品零售价上进行税金计算
			// 商品以订单商品零售价（即真实售价）征关税打七折；
			BigDecimal seventyPercent = new BigDecimal(new Double(0.7));
			BigDecimal finalSellingPriceBD = new BigDecimal(finalSellingPrice);
			if (bbcPostage2AProduct != null) {
				// 平摊的运费
				finalSellingPriceBD = finalSellingPriceBD
						.add(bbcPostage2AProduct);
			}
			// 关税 = 商品CIF价 * 关税税率 * 0.7
			if (importTarRate == null)
				return new BigDecimal(0);
			BigDecimal importTar = finalSellingPriceBD.multiply(
					new BigDecimal(importTarRate)).multiply(seventyPercent);
			return importTar.multiply(new BigDecimal(qty));
		} else if (2024 == warehouseId) {
			// 内部完税仓，深圳仓
			// 商品以订单商品CIF价（=裸采价+头程）征关税；
			// 商品CIF价 = 裸采价 + 头程运费
			/*
			 * BigDecimal cifPrice = calculateCif(); // 关税 = 商品CIF价 * 关税税率
			 * if(importTarRate==null) return new BigDecimal(0); BigDecimal
			 * importTar_20 = cifPrice.multiply(new BigDecimal( importTarRate));
			 * return importTar_20.multiply(new BigDecimal(qty));
			 */
			return new BigDecimal(0);
		} else if (73 == warehouseId || 6 == warehouseId) {
			// 海外直邮仓，美国/英国
			return new BigDecimal(0);
		} else if (2012 == warehouseId || 2062 == warehouseId) {
			// 外部保税仓，杭州保税仓/福州1仓
			return new BigDecimal(0);
		} else {
			return new BigDecimal(0);
		}
	}

	/**
	 * 增值税
	 *
	 * @return
	 */
	public BigDecimal calculateVat(String warehouseType, int warehouseId,
			BigDecimal bbcPostage2AProduct, Double gstRateArg,
			Double vatRateArg, Double importTarRateArg,
			Double postalFeeRateArg, Double logisticFeeArg, Double costArg) {
		// TODO 由于warehouseType数据有误，先按照仓库id来判断
		// 海外直邮：美国（73）、英国（6）
		// 保税仓：福州仓（2050）、南沙保税仓（2029）
		// 外部保税：杭州保税仓（2012）、福州1仓（2062）
		// 完税仓：深圳仓（2024）
		if (2050 == warehouseId || 2029 == warehouseId) {
			// 内部保税仓，福州仓/南沙保税仓
			// 若订单产生运费（如不包邮），按金额分摊到每个商品零售价上进行税金计算
			// 商品以订单商品零售价（即真实售价）征增值税，打七折；
			BigDecimal seventyPercent = new BigDecimal(new Double(0.7));
			BigDecimal finalSellingPriceBD = new BigDecimal(finalSellingPrice);
			if (bbcPostage2AProduct != null) {
				// 平摊的运费
				finalSellingPriceBD = finalSellingPriceBD
						.add(bbcPostage2AProduct);
			}
			// 增值税 = 商品CIF价 * 增值税税率 * 0.7
			if (vatRate == null)
				return new BigDecimal(0);
			BigDecimal vat = finalSellingPriceBD.multiply(
					new BigDecimal(vatRate)).multiply(seventyPercent);
			return vat.multiply(new BigDecimal(qty));
		} else if (2024 == warehouseId) {
			// 内部完税仓，深圳仓
			// 商品以订单商品CIF价（=裸采价+头程）征增值税；
			// 商品CIF价 = 裸采价 + 头程运费
			/*
			 * BigDecimal cifPrice = calculateCif(); // 增值税 = 商品CIF价 * 增值税税率
			 * if(vatRate==null) return new BigDecimal(0); BigDecimal vat_20 =
			 * cifPrice.multiply(new BigDecimal(vatRate)); return
			 * vat_20.multiply(new BigDecimal(qty));
			 */
			return new BigDecimal(0);
		} else if (73 == warehouseId || 6 == warehouseId) {
			// 海外直邮仓，美国/英国
			return new BigDecimal(0);
		} else if (2012 == warehouseId || 2062 == warehouseId) {
			// 外部保税仓，杭州保税仓/福州1仓
			// 税金为0
			// do nothing
			return new BigDecimal(0);
		} else {
			return new BigDecimal(0);
		}
	}

	/**
	 * 消费税
	 *
	 * @return
	 */
	public BigDecimal calculateGst(String warehouseType, int warehouseId,
			BigDecimal bbcPostage2AProduct, Double gstRateArg,
			Double vatRateArg, Double importTarRateArg,
			Double postalFeeRateArg, Double logisticFeeArg, Double costArg) {
		// TODO 由于warehouseType数据有误，先按照仓库id来判断
		// 海外直邮：美国（73）、英国（6）
		// 内部保税仓：福州仓（2050）、南沙保税仓（2029）
		// 外部保税：杭州保税仓（2012）、福州1仓（2062）
		// 完税仓：深圳仓（2024）
		if (2050 == warehouseId || 2029 == warehouseId) {
			// 内部保税仓，福州仓/南沙保税仓
			// 若订单产生运费（如不包邮），按金额分摊到每个商品零售价上进行税金计算
			// 商品以订单商品零售价（即真实售价）征消费税，打七折；
			BigDecimal seventyPercent = new BigDecimal(new Double(0.7));
			BigDecimal finalSellingPriceBD = new BigDecimal(finalSellingPrice);
			if (bbcPostage2AProduct != null) {
				// 平摊的运费
				finalSellingPriceBD = finalSellingPriceBD
						.add(bbcPostage2AProduct);
			}

			// 消费税 = 商品CIF价 * 消费税率 * 0.7
			if (gstRate == null)
				return new BigDecimal(0);
			BigDecimal gst = finalSellingPriceBD.multiply(
					new BigDecimal(gstRate)).multiply(seventyPercent);
			return gst.multiply(new BigDecimal(qty));
		} else if (2024 == warehouseId) {
			// 内部完税仓，深圳仓
			// 商品以订单商品CIF价（=裸采价+头程）征消费税；
			// 商品CIF价 = 裸采价 + 头程运费
			/*
			 * BigDecimal cifPrice = calculateCif(); // 消费税 = 商品CIF价 * 消费税率
			 * if(gstRate==null) return new BigDecimal(0); BigDecimal gst_20 =
			 * cifPrice.multiply(new BigDecimal(gstRate)); return
			 * gst_20.multiply(new BigDecimal(qty));
			 */
			return new BigDecimal(0);
		} else if (73 == warehouseId || 6 == warehouseId) {
			// 海外直邮仓，美国/英国
			return new BigDecimal(0);
		} else if (2012 == warehouseId || 2062 == warehouseId) {
			// 外部保税仓，杭州保税仓/福州1仓
			return new BigDecimal(0);
		} else {
			return new BigDecimal(0);
		}
	}

	/**
	 * CIF价（=裸采价+头程）
	 *
	 * @return
	 */
	private BigDecimal calculateCif() {
		return new BigDecimal(cost).add(new BigDecimal(logisticFee));
	}

	/**
	 * 行邮税
	 *
	 * @return
	 */
	public BigDecimal getPostalFee(String warehouseType, int warehouseId,
			BigDecimal bbcPostage2AProduct, Double gstRateArg,
			Double vatRateArg, Double importTarRateArg,
			Double postalFeeRateArg, Double logisticFeeArg, Double costArg) {
		// TODO 由于warehouseType数据有误，先按照仓库id来判断
		// 海外直邮：美国（73）、英国（6）
		// 保税仓：福州仓（2050）、南沙保税仓（2029）
		// 外部保税：杭州保税仓（2012）、福州1仓（2062）
		// 完税仓：深圳仓（2024）
		if (2050 == warehouseId || 2029 == warehouseId) {
			// 内部保税仓，福州仓/南沙保税仓
			return new BigDecimal(0);
		} else if (2024 == warehouseId) {
			// 内部完税仓，深圳仓
			return new BigDecimal(0);
		} else if (73 == warehouseId || 6 == warehouseId) {
			// 海外直邮仓，美国/英国
			// 若订单产生运费（如不包邮），按金额分摊到每个商品零售价上进行税金计算
			// 商品以订单商品零售价（即真实售价）征行邮税
			/*
			 * BigDecimal finalSellingPrice_30 = new
			 * BigDecimal(finalSellingPrice); if (bbcPostage2AProduct != null) {
			 * // 平摊的运费 finalSellingPrice_30 = finalSellingPrice_30
			 * .add(bbcPostage2AProduct); } if(postalFeeRate==null) return new
			 * BigDecimal(0); return finalSellingPrice_30.multiply(new
			 * BigDecimal(postalFeeRate)) .multiply(new BigDecimal(qty));
			 */
			return new BigDecimal(0);
		} else if (2012 == warehouseId || 2062 == warehouseId) {
			// 外部保税仓，杭州保税仓/福州1仓
			return new BigDecimal(0);
		} else {
			return new BigDecimal(0);
		}
	}

	/**
	 * 纠正数据
	 *
	 * @param warehouseType
	 *            仓库类型
	 * @param bbcPostage2AProduct
	 *            每个商品平摊的运费，有可能为null
	 * @return
	 */
	public void normalizeParametersBeforeCalculateTaxFee(String warehouseType,
			int warehouseId, Double gstRateArg, Double vatRateArg,
			Double importTarRateArg, Double postalFeeRateArg,
			Double logisticFeeArg, Double costArg) {
		// TODO 由于warehouseType数据有误，先按照仓库id来判断
		// 海外直邮：美国（73）、英国（6）
		// 保税仓：福州仓（2050）、南沙保税仓（2029）
		// 外部保税：杭州保税仓（2012）、福州1仓（2062）
		// 完税仓：深圳仓（2024）
		if (cost == null) {
			cost = costArg;
		}
		if (importTarRate == null) {
			importTarRate = importTarRateArg;
		}
		if (vatRate == null) {
			vatRate = vatRateArg;
		}
		if (gstRate == null) {
			gstRate = gstRateArg;
		}
		if (logisticFee == null) {
			logisticFee = logisticFeeArg;
		}
		if (postalFeeRate == null) {
			postalFeeRate = postalFeeRateArg;
		}
	}

	public String getInterBarCode() {
		return interBarCode;
	}

	public void setInterBarCode(String interBarCode) {
		this.interBarCode = interBarCode;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getShOrderId() {
		return shOrderId;
	}

	public void setShOrderId(Integer shOrderId) {
		this.shOrderId = shOrderId;
	}

	public Boolean getIsClearance(){
		return this.clearancePrice != null && this.clearancePrice>0.0;
	}
	@Override
	public String toString() {
		return "SaleDetail [id=" + id + ", purchaseOrderNo=" + purchaseOrderNo
				+ ", productPurchaseId=" + productPurchaseId + ", sku=" + sku
				+ ", qty=" + qty + ", productImg=" + productImg
				+ ", productName=" + productName + ", interBarCode="
				+ interBarCode + ", plateformName=" + plateformName
				+ ", purchasePrice=" + purchasePrice + ", warehouseId="
				+ warehouseId + ", warehouseName=" + warehouseName
				+ ", salesOrderId=" + salesOrderId + ", salesOrderNo="
				+ salesOrderNo + ", isDeducted=" + isDeducted
				+ ", marketPrice=" + marketPrice + ", isAfterService="
				+ isAfterService + ", disProfitRate=" + disProfitRate
				+ ", disProfit=" + disProfit + ", disVat=" + disVat
				+ ", disStockFee=" + disStockFee + ", disShippingType="
				+ disShippingType + ", disOtherCost=" + disOtherCost
				+ ", disTotalCost=" + disTotalCost + ", disTransferFee="
				+ disTransferFee + ", disListFee=" + disListFee
				+ ", disTradeFee=" + disTradeFee + ", disPayFee=" + disPayFee
				+ ", disPostalFee=" + disPostalFee + ", disImportTar="
				+ disImportTar + ", disGst=" + disGst + ", disInsurance="
				+ disInsurance + ", disTotalVat=" + disTotalVat + ", cost="
				+ cost + ", disFreight=" + disFreight + ", disPrice="
				+ disPrice + ", disCifPrice=" + disCifPrice
				+ ", finalSellingPrice=" + finalSellingPrice + ", postalRate="
				+ postalRate + ", gstRate=" + gstRate + ", vatRate=" + vatRate
				+ ", importTarRate=" + importTarRate + ", postalFeeRate="
				+ postalFeeRate + ", logisticFee=" + logisticFee
				+ ", isDeductedHistory=" + isDeductedHistory + ", giftNum="
				+ giftNum + ", isgift=" + isgift + ", capFee=" + capFee
				+ ", arriveWarePrice=" + arriveWarePrice + ", expirationDate="
				+ expirationDate + ", categoryId=" + categoryId
				+ ", categoryName=" + categoryName + "]";
	}

}