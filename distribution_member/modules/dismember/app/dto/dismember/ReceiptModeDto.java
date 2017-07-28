package dto.dismember;

import java.io.Serializable;

/**
 * Created by LSL on 2016/1/7.
 */
public class ReceiptModeDto implements Serializable {

    private static final long serialVersionUID = -1867838801333949628L;

    private Integer id;

    private String bankName;

    private String account;

    private String payee;

    private String remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
