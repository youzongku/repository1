package entity.dismember;

import java.io.Serializable;

/**
 * 分销商发票信息实体
 * @author zbc
 * 2017年2月8日 下午3:36:03
 */
public class Invoice implements Serializable{
	
	private static final long serialVersionUID = -1285238881600335242L;

	/**
	 * 主键
	 */
	private Integer id;

    /**
     * 分销商账号
     */
    private String email;

    /**
     * 发票抬头
     */
    private String invoiceTitle;

    /**
     * 发票纳税号
     */
    private String invoiceTaxNumber;

    /**
     * 银行开户号
     */
    private String invoiceBank;

    /**
     * 银行账号
     */
    private String invoiceBankAccount;

    /**
     * 联系电话
     */
    private String invoiceTel;

    /**
     * 公司地址
     */
    private String invoiceCompanyAddr;

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

    public String getInvoiceTitle() {
        return invoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
    }

    public String getInvoiceTaxNumber() {
        return invoiceTaxNumber;
    }

    public void setInvoiceTaxNumber(String invoiceTaxNumber) {
        this.invoiceTaxNumber = invoiceTaxNumber;
    }

    public String getInvoiceBank() {
        return invoiceBank;
    }

    public void setInvoiceBank(String invoiceBank) {
        this.invoiceBank = invoiceBank;
    }

    public String getInvoiceBankAccount() {
        return invoiceBankAccount;
    }

    public void setInvoiceBankAccount(String invoiceBankAccount) {
        this.invoiceBankAccount = invoiceBankAccount;
    }

    public String getInvoiceTel() {
        return invoiceTel;
    }

    public void setInvoiceTel(String invoiceTel) {
        this.invoiceTel = invoiceTel;
    }

    public String getInvoiceCompanyAddr() {
        return invoiceCompanyAddr;
    }

    public void setInvoiceCompanyAddr(String invoiceCompanyAddr) {
        this.invoiceCompanyAddr = invoiceCompanyAddr;
    }
}