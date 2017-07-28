package vo.payment;

import java.util.Date;

public class PaymentRecord {
    private Integer id;

    private String orderno;

    private Boolean isdeal;

    private Date create;

    private Date update;

    public PaymentRecord(){
    	
    }
    
    public PaymentRecord(String orderno) {
		super();
		this.orderno = orderno;
		this.isdeal = true;
		this.create = new Date();
		this.update = new Date();
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public Boolean getIsdeal() {
        return isdeal;
    }

    public void setIsdeal(Boolean isdeal) {
        this.isdeal = isdeal;
    }

    public Date getCreate() {
        return create;
    }

    public void setCreate(Date create) {
        this.create = create;
    }

    public Date getUpdate() {
        return update;
    }

    public void setUpdate(Date update) {
        this.update = update;
    }

	@Override
	public String toString() {
		return "PaymentRecord [id=" + id + ", orderno=" + orderno + ", isdeal=" + isdeal + ", create=" + create
				+ ", update=" + update + "]";
	}
    
}