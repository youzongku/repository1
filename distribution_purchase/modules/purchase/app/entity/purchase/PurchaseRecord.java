package entity.purchase;

import java.util.Date;

import utils.purchase.DateUtils;

public class PurchaseRecord {
	
    private Integer id;

    private String operator;

    private Date opdate;
    
    private String opdesc;

    private Integer orderid;
    
    public String getCreateDate(){
    	return DateUtils.date2string(opdate, DateUtils.FORMAT_FULL_DATETIME);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Date getOpdate() {
        return opdate;
    }

    public void setOpdate(Date opdate) {
        this.opdate = opdate;
    }

    public String getOpdesc() {
        return opdesc;
    }

    public void setOpdesc(String opdesc) {
        this.opdesc = opdesc;
    }

    public Integer getOrderid() {
        return orderid;
    }

    public void setOrderid(Integer orderid) {
        this.orderid = orderid;
    }
}