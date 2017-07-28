package entity.product;

import java.util.Date;

/**
 * @author zbc
 *
 * 2016年7月28日 上午11:49:45
 */
public class ProductDisprice {
    private Integer id;

    private String sku;

    private Integer disStockId;// 分销仓库id

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

    private Double cost; //裸采购价

    private Double disFreight;//分销物流费

    private Double disPrice;//分销价

    private Double disCifPrice;//CIF价格

    private Date operateDate;//操作时间

    private Double floorPrice;//最低价

    private Double proposalRetailPrice;//零售价

    private Double distributorPrice;//经销商价格

    private Double electricityPrices;//Bbc价格

    private Double supermarketPrice;//KA经销价格

    private Double arriveWarePrice;//预留  到仓价
    
    private Double disCompanyCost;//营销成本价
    
    private Double marketInterventionPrice;//市场干预供货价
    
    private Double ftzPrice;//自贸区经销价格
    
    private Integer typeId;//产品类型
    
    private String typeName;//产品名称
    
    private Double vipPrice;//VIP价格
    
    private Integer saleStatus;//erp销售状态
    
    private Double  clearanceRate;//清货率
    
    private Double clearancePrice;//清货价
    
    public Double getClearanceRate() {
		return clearanceRate;
	}

	public void setClearanceRate(Double clearanceRate) {
		this.clearanceRate = clearanceRate;
	}

	public Double getClearancePrice() {
		return clearancePrice;
	}

	public void setClearancePrice(Double clearancePrice) {
		this.clearancePrice = clearancePrice;
	}

	public Double getVipPrice() {
		return vipPrice;
	}

	public void setVipPrice(Double vipPrice) {
		this.vipPrice = vipPrice;
	}

	public Double getMarketInterventionPrice() {
		return marketInterventionPrice;
	}

	public void setMarketInterventionPrice(Double marketInterventionPrice) {
		this.marketInterventionPrice = marketInterventionPrice;
	}

	public Double getFtzPrice() {
		return ftzPrice;
	}

	public void setFtzPrice(Double ftzPrice) {
		this.ftzPrice = ftzPrice;
	}

	public Double getDisCompanyCost() {
		return disCompanyCost;
	}

	public void setDisCompanyCost(Double disCompanyCost) {
		this.disCompanyCost = disCompanyCost;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getDisStockId() {
        return disStockId;
    }

    public void setDisStockId(Integer disStockId) {
        this.disStockId = disStockId;
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

    public Date getOperateDate() {
        return operateDate;
    }

    public void setOperateDate(Date operateDate) {
        this.operateDate = operateDate;
    }

    public Double getFloorPrice() {
        return floorPrice;
    }

    public void setFloorPrice(Double floorPrice) {
        this.floorPrice = floorPrice;
    }

    public Double getProposalRetailPrice() {
        return proposalRetailPrice;
    }

    public void setProposalRetailPrice(Double proposalRetailPrice) {
        this.proposalRetailPrice = proposalRetailPrice;
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

    public Double getArriveWarePrice() {
        return arriveWarePrice;
    }

    public void setArriveWarePrice(Double arriveWarePrice) {
        this.arriveWarePrice = arriveWarePrice;
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

	public Integer getSaleStatus() {
		return saleStatus;
	}

	public void setSaleStatus(Integer saleStatus) {
		this.saleStatus = saleStatus;
	}

	@Override
	public String toString() {
		return "ProductDisprice [id=" + id + ", sku=" + sku + ", disStockId=" + disStockId + ", arriveWarePrice="
				+ arriveWarePrice + ", saleStatus=" + saleStatus + ", clearanceRate=" + clearanceRate
				+ ", clearancePrice=" + clearancePrice + "]";
	}
}