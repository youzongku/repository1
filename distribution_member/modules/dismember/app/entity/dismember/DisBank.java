package entity.dismember;

import java.io.Serializable;
import java.util.Date;

public class DisBank implements Serializable {

    private static final long serialVersionUID = -1975273255673876015L;

    private Integer id;

    private String bankName;

    private Date createDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}