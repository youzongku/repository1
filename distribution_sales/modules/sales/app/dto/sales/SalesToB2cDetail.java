package dto.sales;

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

	private Double disPrice;// 分销价

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

	public Double getDisPrice() {
		return disPrice;
	}

	public void setDisPrice(Double disPrice) {
		this.disPrice = disPrice;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}
}
