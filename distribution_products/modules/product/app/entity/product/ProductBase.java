package entity.product;

import java.util.Date;

/**
 * t_product_base
 * 
 * @author ye_ziran
 * @since 2015年12月8日 下午2:45:53
 */
public class ProductBase {
	private String clistingid;
	private Integer iwebsiteid;
	private Integer ilanguageid;
	private String csku;
	private Integer istatus;
	private Integer salable;// 非卖状态（0不可卖，1可卖）
	private Date dnewformdate;
	private Date dnewtodate;
	private Boolean bspecial;
	private String cvideoaddress;
	private Integer iqty;
	private Double fprice;
	private Double fcostprice;
	private Double fweight;
	private String ctitle;
	private String cdescription;
	private String cshortdescription;
	private String ckeyword;
	private String cmetatitle;
	private String cmetakeyword;
	private String cmetadescription;
	private String cpaymentexplain;
	private String creturnexplain;
	private String cwarrantyexplain;
	private String ctitle_default;
	private String cdescription_default;
	private String cshortdescription_default;
	private String ckeyword_default;
	private String cmetatitle_default;
	private String cmetakeyword_default;
	private String cmetadescription_default;
	private Boolean bmultiattribute;
	private String cparentsku;
	private Boolean bvisible;
	private Boolean bpulish;
	private String ccreateuser;
	private Date dcreatedate;
	private Double ffreight;
	private Boolean bmain;
	private Boolean bactivity;
	private Double saleprice;
	private Double tariff;
	private Double carriage;
	private Double local_ref_price;
	private String productType;

	private Integer sales;// 已售
	private Integer catId;// 所属类目ID

	public Integer getSalable() {
		return salable;
	}

	public void setSalable(Integer salable) {
		this.salable = salable;
	}

	public Integer getSales() {
		return sales;
	}

	public void setSales(Integer sales) {
		this.sales = sales;
	}

	public Integer getCatId() {
		return catId;
	}

	public void setCatId(Integer catId) {
		this.catId = catId;
	}

	public String getClistingid() {
		return clistingid;
	}

	public void setClistingid(String clistingid) {
		this.clistingid = clistingid;
	}

	public Integer getIwebsiteid() {
		return iwebsiteid;
	}

	public void setIwebsiteid(Integer iwebsiteid) {
		this.iwebsiteid = iwebsiteid;
	}

	public Integer getIlanguageid() {
		return ilanguageid;
	}

	public void setIlanguageid(Integer ilanguageid) {
		this.ilanguageid = ilanguageid;
	}

	public String getCsku() {
		return csku;
	}

	public void setCsku(String csku) {
		this.csku = csku;
	}

	public Integer getIstatus() {
		return istatus;
	}

	public void setIstatus(Integer istatus) {
		this.istatus = istatus;
	}

	public Date getDnewformdate() {
		return dnewformdate;
	}

	public void setDnewformdate(Date dnewformdate) {
		this.dnewformdate = dnewformdate;
	}

	public Date getDnewtodate() {
		return dnewtodate;
	}

	public void setDnewtodate(Date dnewtodate) {
		this.dnewtodate = dnewtodate;
	}

	public Boolean getBspecial() {
		return bspecial;
	}

	public void setBspecial(Boolean bspecial) {
		this.bspecial = bspecial;
	}

	public String getCvideoaddress() {
		return cvideoaddress;
	}

	public void setCvideoaddress(String cvideoaddress) {
		this.cvideoaddress = cvideoaddress;
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

	public Double getFcostprice() {
		return fcostprice;
	}

	public void setFcostprice(Double fcostprice) {
		this.fcostprice = fcostprice;
	}

	public Double getFweight() {
		return fweight;
	}

	public void setFweight(Double fweight) {
		this.fweight = fweight;
	}

	public String getCtitle() {
		return ctitle;
	}

	public void setCtitle(String ctitle) {
		this.ctitle = ctitle;
	}

	public String getCdescription() {
		return cdescription;
	}

	public void setCdescription(String cdescription) {
		this.cdescription = cdescription;
	}

	public String getCshortdescription() {
		return cshortdescription;
	}

	public void setCshortdescription(String cshortdescription) {
		this.cshortdescription = cshortdescription;
	}

	public String getCkeyword() {
		return ckeyword;
	}

	public void setCkeyword(String ckeyword) {
		this.ckeyword = ckeyword;
	}

	public String getCmetatitle() {
		return cmetatitle;
	}

	public void setCmetatitle(String cmetatitle) {
		this.cmetatitle = cmetatitle;
	}

	public String getCmetakeyword() {
		return cmetakeyword;
	}

	public void setCmetakeyword(String cmetakeyword) {
		this.cmetakeyword = cmetakeyword;
	}

	public String getCmetadescription() {
		return cmetadescription;
	}

	public void setCmetadescription(String cmetadescription) {
		this.cmetadescription = cmetadescription;
	}

	public String getCpaymentexplain() {
		return cpaymentexplain;
	}

	public void setCpaymentexplain(String cpaymentexplain) {
		this.cpaymentexplain = cpaymentexplain;
	}

	public String getCreturnexplain() {
		return creturnexplain;
	}

	public void setCreturnexplain(String creturnexplain) {
		this.creturnexplain = creturnexplain;
	}

	public String getCwarrantyexplain() {
		return cwarrantyexplain;
	}

	public void setCwarrantyexplain(String cwarrantyexplain) {
		this.cwarrantyexplain = cwarrantyexplain;
	}

	public String getCtitle_default() {
		return ctitle_default;
	}

	public void setCtitle_default(String ctitle_default) {
		this.ctitle_default = ctitle_default;
	}

	public String getCdescription_default() {
		return cdescription_default;
	}

	public void setCdescription_default(String cdescription_default) {
		this.cdescription_default = cdescription_default;
	}

	public String getCshortdescription_default() {
		return cshortdescription_default;
	}

	public void setCshortdescription_default(String cshortdescription_default) {
		this.cshortdescription_default = cshortdescription_default;
	}

	public String getCkeyword_default() {
		return ckeyword_default;
	}

	public void setCkeyword_default(String ckeyword_default) {
		this.ckeyword_default = ckeyword_default;
	}

	public String getCmetatitle_default() {
		return cmetatitle_default;
	}

	public void setCmetatitle_default(String cmetatitle_default) {
		this.cmetatitle_default = cmetatitle_default;
	}

	public String getCmetakeyword_default() {
		return cmetakeyword_default;
	}

	public void setCmetakeyword_default(String cmetakeyword_default) {
		this.cmetakeyword_default = cmetakeyword_default;
	}

	public String getCmetadescription_default() {
		return cmetadescription_default;
	}

	public void setCmetadescription_default(String cmetadescription_default) {
		this.cmetadescription_default = cmetadescription_default;
	}

	public Boolean getBmultiattribute() {
		return bmultiattribute;
	}

	public void setBmultiattribute(Boolean bmultiattribute) {
		this.bmultiattribute = bmultiattribute;
	}

	public String getCparentsku() {
		return cparentsku;
	}

	public void setCparentsku(String cparentsku) {
		this.cparentsku = cparentsku;
	}

	public Boolean getBvisible() {
		return bvisible;
	}

	public void setBvisible(Boolean bvisible) {
		this.bvisible = bvisible;
	}

	public Boolean getBpulish() {
		return bpulish;
	}

	public void setBpulish(Boolean bpulish) {
		this.bpulish = bpulish;
	}

	public String getCcreateuser() {
		return ccreateuser;
	}

	public void setCcreateuser(String ccreateuser) {
		this.ccreateuser = ccreateuser;
	}

	public Date getDcreatedate() {
		return dcreatedate;
	}

	public void setDcreatedate(Date dcreatedate) {
		this.dcreatedate = dcreatedate;
	}

	public Double getFfreight() {
		return ffreight;
	}

	public void setFfreight(Double ffreight) {
		this.ffreight = ffreight;
	}

	public Boolean getBmain() {
		return bmain;
	}

	public void setBmain(Boolean bmain) {
		this.bmain = bmain;
	}

	public Boolean getBactivity() {
		return bactivity;
	}

	public void setBactivity(Boolean bactivity) {
		this.bactivity = bactivity;
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

	public Double getLocal_ref_price() {
		return local_ref_price;
	}

	public void setLocal_ref_price(Double local_ref_price) {
		this.local_ref_price = local_ref_price;
	}

	public Double getCarriage() {
		return carriage;
	}

	public void setCarriage(Double carriage) {
		this.carriage = carriage;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}
}
