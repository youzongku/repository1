package entity.timer;

import java.util.Date;
import java.util.List;

/**
 * b2b to b2c 订单
 * Created by luwj on 2016/1/20.
 */
public class SalesToB2cIterm {

    private Integer salesorderid;
    private Integer iid;
    private String cemail;
    private String ccountry;
    private String ccountrysn;
    private String cprovince;
    private String ccity;
    private String cstreetaddress;
    private String cpostalcode;
    private String ctelephone;
    private String cfirstname;
    private String cmiddlename;
    private String clastname;
    private Integer istorageid;
    private Integer ishippingmethodid;
    private Double fshippingprice;
    private double fordersubtotal;
    private double fextra;
    private double fgrandtotal;
    private String ccartid;
    private Integer istatus;
    private String cpaymentid;
    private String ccurrency;
    private Date dcreatedate;
    private Date dpaymentdate;
    private String corigin;
    private Integer iwebsiteid;
    private String cmemberemail;
    private String cmessage;
    private Integer ishow;
    private String ctransactionid;
    private String cip;
    private String cremark;
    private String creceiveraccount;//收款账户
    private String cshippingcode;
    private String cordernumber;
    private String cvhost;
    private Integer isendtime;
    private String idtype;
    private String idcard;
    private String payman;
    private String platformtype;//平台店辅类型
    private String plattypename;//平台名称
    private String receiveraddr;//收货人地址
    private String receiveridcard;//收货人身份证号
    private Integer orderchannel;//订单渠道：1，b2b;0,b2c;默认0
    private String buyerid;//第三方用户id
    private String customerservice;//客服账号
    
    private Double platformamount;//平台收入，商品采购价总和
    private Double clearanceamount;//报关金额=实付款
    private Double taxFee;// 税金
    /**
	 * 收货地址“地区”中文值
	 */
	private String carea;

    private List<SalesToB2cDetail> details;
    
	private String shopName;// 平台店铺名称

	private String shopKeeper;// 店主名称

	private String shopTelephone;// 联系电话

	private String shopAddress;// 店铺地址

	private Integer shopId;//店铺地址 
	
	private String tradeNo;//外部订单交易号
	private Integer distributorType;//分销商类型
	private String purchasePayno;//缺货采购支付交易号
	private String purchasePaytype;//缺货采购支付类型
	
	private Integer orderLevel;//为50则表示营销单，其他都是普通订单
	//业务备注
	private String businessRemark;
	//财务备注
	private String financeRemark;
	
	public String getBusinessRemark() {
		return businessRemark;
	}

	public void setBusinessRemark(String businessRemark) {
		this.businessRemark = businessRemark;
	}

	public String getFinanceRemark() {
		return financeRemark;
	}

	public void setFinanceRemark(String financeRemark) {
		this.financeRemark = financeRemark;
	}

	public Integer getOrderLevel() {
		return orderLevel;
	}

	public void setOrderLevel(Integer orderLevel) {
		this.orderLevel = orderLevel;
	}

	public Double getTaxFee() {
		return taxFee;
	}

	public void setTaxFee(Double taxFee) {
		this.taxFee = taxFee;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public Integer getDistributorType() {
		return distributorType;
	}

	public void setDistributorType(Integer distributorType) {
		this.distributorType = distributorType;
	}

	public String getPurchasePayno() {
		return purchasePayno;
	}

	public void setPurchasePayno(String purchasePayno) {
		this.purchasePayno = purchasePayno;
	}

	public String getPurchasePaytype() {
		return purchasePaytype;
	}

	public void setPurchasePaytype(String purchasePaytype) {
		this.purchasePaytype = purchasePaytype;
	}

	public Integer getShopId() {
		return shopId;
	}

	public void setShopId(Integer shopId) {
		this.shopId = shopId;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getShopKeeper() {
		return shopKeeper;
	}

	public void setShopKeeper(String shopKeeper) {
		this.shopKeeper = shopKeeper;
	}

	public String getShopTelephone() {
		return shopTelephone;
	}

	public void setShopTelephone(String shopTelephone) {
		this.shopTelephone = shopTelephone;
	}

	public String getShopAddress() {
		return shopAddress;
	}

	public void setShopAddress(String shopAddress) {
		this.shopAddress = shopAddress;
	}

	public Integer getSalesorderid() {
        return salesorderid;
    }

    public void setSalesorderid(Integer salesorderid) {
        this.salesorderid = salesorderid;
    }

    public List<SalesToB2cDetail> getDetails() {
        return details;
    }

    public void setDetails(List<SalesToB2cDetail> details) {
        this.details = details;
    }

    public Integer getIid() {
        return iid;
    }

    public void setIid(Integer iid) {
        this.iid = iid;
    }

    public String getCemail() {
        return cemail;
    }

    public void setCemail(String cemail) {
        this.cemail = cemail;
    }

    public String getCcountry() {
        return ccountry;
    }

    public void setCcountry(String ccountry) {
        this.ccountry = ccountry;
    }

    public String getCcountrysn() {
        return ccountrysn;
    }

    public void setCcountrysn(String ccountrysn) {
        this.ccountrysn = ccountrysn;
    }

    public String getCprovince() {
        return cprovince;
    }

    public void setCprovince(String cprovince) {
        this.cprovince = cprovince;
    }

    public String getCcity() {
        return ccity;
    }

    public void setCcity(String ccity) {
        this.ccity = ccity;
    }

    public String getCstreetaddress() {
        return cstreetaddress;
    }

    public void setCstreetaddress(String cstreetaddress) {
        this.cstreetaddress = cstreetaddress;
    }

    public String getCpostalcode() {
        return cpostalcode;
    }

    public void setCpostalcode(String cpostalcode) {
        this.cpostalcode = cpostalcode;
    }

    public String getCtelephone() {
        return ctelephone;
    }

    public void setCtelephone(String ctelephone) {
        this.ctelephone = ctelephone;
    }

    public String getCfirstname() {
        return cfirstname;
    }

    public void setCfirstname(String cfirstname) {
        this.cfirstname = cfirstname;
    }

    public String getCmiddlename() {
        return cmiddlename;
    }

    public void setCmiddlename(String cmiddlename) {
        this.cmiddlename = cmiddlename;
    }

    public String getClastname() {
        return clastname;
    }

    public void setClastname(String clastname) {
        this.clastname = clastname;
    }

    public Integer getIstorageid() {
        return istorageid;
    }

    public void setIstorageid(Integer istorageid) {
        this.istorageid = istorageid;
    }

    public Integer getIshippingmethodid() {
        return ishippingmethodid;
    }

    public void setIshippingmethodid(Integer ishippingmethodid) {
        this.ishippingmethodid = ishippingmethodid;
    }

    public Double getFshippingprice() {
        return fshippingprice;
    }

    public void setFshippingprice(Double fshippingprice) {
        this.fshippingprice = fshippingprice;
    }

    public double getFordersubtotal() {
        return fordersubtotal;
    }

    public void setFordersubtotal(double fordersubtotal) {
        this.fordersubtotal = fordersubtotal;
    }

    public double getFextra() {
        return fextra;
    }

    public void setFextra(double fextra) {
        this.fextra = fextra;
    }

    public double getFgrandtotal() {
        return fgrandtotal;
    }

    public void setFgrandtotal(double fgrandtotal) {
        this.fgrandtotal = fgrandtotal;
    }

    public String getCcartid() {
        return ccartid;
    }

    public void setCcartid(String ccartid) {
        this.ccartid = ccartid;
    }

    public Integer getIstatus() {
        return istatus;
    }

    public void setIstatus(Integer istatus) {
        this.istatus = istatus;
    }

    public String getCcurrency() {
        return ccurrency;
    }

    public void setCcurrency(String ccurrency) {
        this.ccurrency = ccurrency;
    }

    public Date getDcreatedate() {
        return dcreatedate;
    }

    public void setDcreatedate(Date dcreatedate) {
        this.dcreatedate = dcreatedate;
    }

    public Date getDpaymentdate() {
        return dpaymentdate;
    }

    public void setDpaymentdate(Date dpaymentdate) {
        this.dpaymentdate = dpaymentdate;
    }

    public String getCpaymentid() {
        return cpaymentid;
    }

    public void setCpaymentid(String cpaymentid) {
        this.cpaymentid = cpaymentid;
    }

    public String getCorigin() {
        return corigin;
    }

    public void setCorigin(String corigin) {
        this.corigin = corigin;
    }

    public Integer getIwebsiteid() {
        return iwebsiteid;
    }

    public void setIwebsiteid(Integer iwebsiteid) {
        this.iwebsiteid = iwebsiteid;
    }

    public String getCmemberemail() {
        return cmemberemail;
    }

    public void setCmemberemail(String cmemberemail) {
        this.cmemberemail = cmemberemail;
    }

    public String getCmessage() {
        return cmessage;
    }

    public void setCmessage(String cmessage) {
        this.cmessage = cmessage;
    }

    public Integer getIshow() {
        return ishow;
    }

    public void setIshow(Integer ishow) {
        this.ishow = ishow;
    }

    public String getCtransactionid() {
        return ctransactionid;
    }

    public void setCtransactionid(String ctransactionid) {
        this.ctransactionid = ctransactionid;
    }

    public String getCip() {
        return cip;
    }

    public void setCip(String cip) {
        this.cip = cip;
    }

    public String getCremark() {
        return cremark;
    }

    public void setCremark(String cremark) {
        this.cremark = cremark;
    }

    public String getCreceiveraccount() {
        return creceiveraccount;
    }

    public void setCreceiveraccount(String creceiveraccount) {
        this.creceiveraccount = creceiveraccount;
    }

    public String getCshippingcode() {
        return cshippingcode;
    }

    public void setCshippingcode(String cshippingcode) {
        this.cshippingcode = cshippingcode;
    }

    public String getCordernumber() {
        return cordernumber;
    }

    public void setCordernumber(String cordernumber) {
        this.cordernumber = cordernumber;
    }

    public String getCvhost() {
        return cvhost;
    }

    public void setCvhost(String cvhost) {
        this.cvhost = cvhost;
    }

    public Integer getIsendtime() {
        return isendtime;
    }

    public void setIsendtime(Integer isendtime) {
        this.isendtime = isendtime;
    }

    public String getIdtype() {
        return idtype;
    }

    public void setIdtype(String idtype) {
        this.idtype = idtype;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getPayman() {
        return payman;
    }

    public void setPayman(String payman) {
        this.payman = payman;
    }

    public String getPlatformtype() {
        return platformtype;
    }

    public void setPlatformtype(String platformtype) {
        this.platformtype = platformtype;
    }

    public String getPlattypename() {
        return plattypename;
    }

    public void setPlattypename(String plattypename) {
        this.plattypename = plattypename;
    }

    public String getReceiveraddr() {
        return receiveraddr;
    }

    public void setReceiveraddr(String receiveraddr) {
        this.receiveraddr = receiveraddr;
    }

    public String getReceiveridcard() {
        return receiveridcard;
    }

    public Integer getOrderchannel() {
        return orderchannel;
    }

    public void setOrderchannel(Integer orderchannel) {
        this.orderchannel = orderchannel;
    }

    public void setReceiveridcard(String receiveridcard) {
        this.receiveridcard = receiveridcard;
    }

    public String getBuyerid() {
        return buyerid;
    }

    public void setBuyerid(String buyerid) {
        this.buyerid = buyerid;
    }

    public String getCustomerservice() {
        return customerservice;
    }

    public void setCustomerservice(String customerservice) {
        this.customerservice = customerservice;
    }

	public String getCarea() {
		return carea;
	}

	public void setCarea(String carea) {
		this.carea = carea;
	}

	public Double getPlatformamount() {
		return platformamount;
	}

	public void setPlatformamount(Double platformamount) {
		this.platformamount = platformamount;
	}

	public Double getClearanceamount() {
		return clearanceamount;
	}

	public void setClearanceamount(Double clearanceamount) {
		this.clearanceamount = clearanceamount;
	}

}
