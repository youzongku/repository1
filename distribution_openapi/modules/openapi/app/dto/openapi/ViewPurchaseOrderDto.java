package dto.openapi;

import java.util.Date;
import java.util.List;

/**
 * 采购单订单实体 展示字段
 * @author zbc
 * 2016年9月5日 下午8:00:47
 */
public class ViewPurchaseOrderDto {

    private Integer id;

    private String email;//用户

    private String purchaseOrderNo;//采购单号

    private Integer status;//采购单状态：0待付款，1已付款，2已取消

    private Date purchaseDate;//下单时间

    private String sorderDate;//下单时间(String)

    private String statusMes;//订单状态：待付款，已付款，已取消

    private Double purchaseTotalAmount;//订单总金额

    private Date payDate;//付款时间

    private String spayDate;//付款时间(String)
    
    private Double purchaseDiscountAmount;//订单折后金额

    private List<ViewPurchaseDetailDto> details;
    
    private Integer sid;
    
    
    private Date cancelDate;//取消时间
    
    private String cancelDateStr;//取消时间(String)
           
    

	public Date getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}

	public String getCancelDateStr() {
		return cancelDateStr;
	}

	public void setCancelDateStr(String cancelDateStr) {
		this.cancelDateStr = cancelDateStr;
	}


	public Integer getSid() {
		return sid;
	}

	public void setSid(Integer sid) {
		this.sid = sid;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPurchaseOrderNo() {
        return purchaseOrderNo;
    }

    public void setPurchaseOrderNo(String purchaseOrderNo) {
        this.purchaseOrderNo = purchaseOrderNo;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getSorderDate() {
		return sorderDate;
	}

	public void setSorderDate(String sorderDate) {
		this.sorderDate = sorderDate;
	}

	public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusMes() {
        return statusMes;
    }

    public void setStatusMes(String statusMes) {
        this.statusMes = statusMes;
    }

    public Double getPurchaseTotalAmount() {
        return purchaseTotalAmount;
    }

    public void setPurchaseTotalAmount(Double purchaseTotalAmount) {
        this.purchaseTotalAmount = purchaseTotalAmount;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public String getSpayDate() {
        return spayDate;
    }

    public void setSpayDate(String spayDate) {
        this.spayDate = spayDate;
    }

    public Double getPurchaseDiscountAmount() {
		return purchaseDiscountAmount;
	}

	public void setPurchaseDiscountAmount(Double purchaseDiscountAmount) {
		this.purchaseDiscountAmount = purchaseDiscountAmount;
	}


	public List<ViewPurchaseDetailDto> getDetails() {
		return details;
	}

	public void setDetails(List<ViewPurchaseDetailDto> details) {
		this.details = details;
	}

}
