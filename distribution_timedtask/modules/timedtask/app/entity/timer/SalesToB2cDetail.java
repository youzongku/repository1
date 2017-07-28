package entity.timer;

import java.util.Date;

/**
 * b2b to b2c 订单详情
 * Created by luwj on 2016/1/20.
 */
public class SalesToB2cDetail {

    private String cid;
    private String ctitle;
    private Integer iorderid;
    private String clistingid;
    private Integer iqty;
    private Double fprice;
    private Double ftotalprices;
    private String csku;
    private Date dcreatedate;
    private String cparentid;
    private Double foriginalprice;
    private Double fweight;
    private Double saleprice;
    private Double tariff;
    private Double carriage;
	private Integer warehouseId;
    private String warehousename;
    
    
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
	
	private Double finalSellingPrice;//其他平台最终售价
	
	private Date expirationDate;//到期日期
	
    public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Double getFinalSellingPrice() {
		return finalSellingPrice;
	}

	public void setFinalSellingPrice(Double finalSellingPrice) {
		this.finalSellingPrice = finalSellingPrice;
	}

	public Double getSaleprice() {
        return saleprice;
    }

    public void setSaleprice(Double saleprice) {
        this.saleprice = saleprice;
    }

    public Double getTariff() {
        return tariff;
    }

    public void setTariff(Double tariff) {
        this.tariff = tariff;
    }

    public Double getCarriage() {
        return carriage;
    }

    public void setCarriage(Double carriage) {
        this.carriage = carriage;
    }

    public String getCtitle() {
        return ctitle;
    }

    public void setCtitle(String ctitle) {
        this.ctitle = ctitle;
    }

    public Integer getIorderid() {
        return iorderid;
    }

    public void setIorderid(Integer iorderid) {
        this.iorderid = iorderid;
    }

    public String getClistingid() {
        return clistingid;
    }

    public void setClistingid(String clistingid) {
        this.clistingid = clistingid;
    }

    public Integer getIqty() {
        return iqty;
    }

    public void setIqty(Integer iqty) {
        this.iqty = iqty;
    }

    public Double getFprice() {
        return fprice;
    }

    public void setFprice(Double fprice) {
        this.fprice = fprice;
    }

    public Double getFtotalprices() {
        return ftotalprices;
    }

    public void setFtotalprices(Double ftotalprices) {
        this.ftotalprices = ftotalprices;
    }

    public String getCsku() {
        return csku;
    }

    public void setCsku(String csku) {
        this.csku = csku;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public Date getDcreatedate() {
        return dcreatedate;
    }

    public void setDcreatedate(Date dcreatedate) {
        this.dcreatedate = dcreatedate;
    }

    public String getCparentid() {
        return cparentid;
    }

    public void setCparentid(String cparentid) {
        this.cparentid = cparentid;
    }

    public Double getForiginalprice() {
        return foriginalprice;
    }

    public void setForiginalprice(Double foriginalprice) {
        this.foriginalprice = foriginalprice;
    }

    public Double getFweight() {
        return fweight;
    }

    public void setFweight(Double fweight) {
        this.fweight = fweight;
    }

    public String getWarehousename() {
        return warehousename;
    }

    public void setWarehousename(String warehousename) {
        this.warehousename = warehousename;
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

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}
}
