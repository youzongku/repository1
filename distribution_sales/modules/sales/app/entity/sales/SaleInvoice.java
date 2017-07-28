package entity.sales;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zbc
 * 2017年6月22日 上午11:11:51
 */
public class SaleInvoice implements Serializable{

	private static final long serialVersionUID = 1570187231395200848L;

	/**
     * 主键
     */
    private Integer id;

    /**
     * 发货单号
     */
    private String salesOrderNo;

    /**
     * 发票类型(1:个人，2:公司)
     */
    private Integer invoiceType;

    /**
     * 发票抬头 
     */
    private String invoiceTitle;

    /**
     * 发票金额
     */
    private Double invoiceAmount;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 更新时间
     */
    private Date updateDate;

    /**
     * 创建人
     */
    private String createUser;
    
    public SaleInvoice(){
    	super();
    }
    
    public SaleInvoice(Integer invoiceType, String invoiceTitle) {
		this.invoiceType = invoiceType;
		this.invoiceTitle = invoiceTitle;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSalesOrderNo() {
        return salesOrderNo;
    }

    public void setSalesOrderNo(String salesOrderNo) {
        this.salesOrderNo = salesOrderNo;
    }

    public Integer getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(Integer invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getInvoiceTitle() {
        return invoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
    }

    public Double getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(Double invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
}