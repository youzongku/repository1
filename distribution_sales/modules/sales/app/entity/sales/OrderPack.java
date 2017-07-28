package entity.sales;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class OrderPack implements Serializable {

    private static final long serialVersionUID = 8784203663593816369L;

    private Integer iid;

    private String cordernumber;

    private String csku;

    private Integer iqty;

    private String cshippingtype;

    private Double fshippingprice;

    private String ctrackingnumber;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date dshippingdate;
    
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date dcreatedate;

    private Integer iisregister;

    private String clocaltracknumber;

    private String cshippingname;

    public Integer getIid() {
        return iid;
    }

    public void setIid(Integer iid) {
        this.iid = iid;
    }

    public String getCordernumber() {
        return cordernumber;
    }

    public void setCordernumber(String cordernumber) {
        this.cordernumber = cordernumber;
    }

    public String getCsku() {
        return csku;
    }

    public void setCsku(String csku) {
        this.csku = csku;
    }

    public Integer getIqty() {
        return iqty;
    }

    public void setIqty(Integer iqty) {
        this.iqty = iqty;
    }

    public String getCshippingtype() {
        return cshippingtype;
    }

    public void setCshippingtype(String cshippingtype) {
        this.cshippingtype = cshippingtype;
    }

    public Double getFshippingprice() {
        return fshippingprice;
    }

    public void setFshippingprice(Double fshippingprice) {
        this.fshippingprice = fshippingprice;
    }

    public String getCtrackingnumber() {
        return ctrackingnumber;
    }

    public void setCtrackingnumber(String ctrackingnumber) {
        this.ctrackingnumber = ctrackingnumber;
    }

    public Date getDshippingdate() {
        return dshippingdate;
    }

    public void setDshippingdate(Date dshippingdate) {
        this.dshippingdate = dshippingdate;
    }

    public Date getDcreatedate() {
        return dcreatedate;
    }

    public void setDcreatedate(Date dcreatedate) {
        this.dcreatedate = dcreatedate;
    }

    public Integer getIisregister() {
        return iisregister;
    }

    public void setIisregister(Integer iisregister) {
        this.iisregister = iisregister;
    }

    public String getClocaltracknumber() {
        return clocaltracknumber;
    }

    public void setClocaltracknumber(String clocaltracknumber) {
        this.clocaltracknumber = clocaltracknumber;
    }

    public String getCshippingname() {
        return cshippingname;
    }

    public void setCshippingname(String cshippingname) {
        this.cshippingname = cshippingname;
    }
}