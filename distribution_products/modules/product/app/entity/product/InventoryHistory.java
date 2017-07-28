package entity.product;

import java.util.Date;

public class InventoryHistory {
    private Integer iid;

    private Integer iwebsiteid;

    private String clistingid;

    private Integer ibeforechangeqty;

    private Integer iqty;

    private Integer iafterchangeqty;

    private String creference;

    private Date dcreatedate;

    private String ctype;

    private Boolean benabled;

    public Integer getIid() {
        return iid;
    }

    public void setIid(Integer iid) {
        this.iid = iid;
    }

    public Integer getIwebsiteid() {
        return iwebsiteid;
    }

    public void setIwebsiteid(Integer iwebsiteid) {
        this.iwebsiteid = iwebsiteid;
    }

    public String getClistingid() {
        return clistingid;
    }

    public void setClistingid(String clistingid) {
        this.clistingid = clistingid == null ? null : clistingid.trim();
    }

    public Integer getIbeforechangeqty() {
        return ibeforechangeqty;
    }

    public void setIbeforechangeqty(Integer ibeforechangeqty) {
        this.ibeforechangeqty = ibeforechangeqty;
    }

    public Integer getIqty() {
        return iqty;
    }

    public void setIqty(Integer iqty) {
        this.iqty = iqty;
    }

    public Integer getIafterchangeqty() {
        return iafterchangeqty;
    }

    public void setIafterchangeqty(Integer iafterchangeqty) {
        this.iafterchangeqty = iafterchangeqty;
    }

    public String getCreference() {
        return creference;
    }

    public void setCreference(String creference) {
        this.creference = creference == null ? null : creference.trim();
    }

    public Date getDcreatedate() {
        return dcreatedate;
    }

    public void setDcreatedate(Date dcreatedate) {
        this.dcreatedate = dcreatedate;
    }

    public String getCtype() {
        return ctype;
    }

    public void setCtype(String ctype) {
        this.ctype = ctype == null ? null : ctype.trim();
    }

    public Boolean getBenabled() {
        return benabled;
    }

    public void setBenabled(Boolean benabled) {
        this.benabled = benabled;
    }
}