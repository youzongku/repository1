package entity.sales.hb;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import util.sales.SalesCombinationStatus;

public class SalesHBDeliveryLog {
    private Integer id;

    private Integer salesHbId;

    private String salesHbNo;

    private Integer status;

    private String remarks;
    
    // 操作类型：1合并发货单，2客服审核，3财务审核
    private Integer optType;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date optTime;

    private String optUser;

    public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Integer getOptType() {
		return optType;
	}

	public void setOptType(Integer optType) {
		this.optType = optType;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSalesHbId() {
        return salesHbId;
    }

    public void setSalesHbId(Integer salesHbId) {
        this.salesHbId = salesHbId;
    }

    public String getSalesHbNo() {
        return salesHbNo;
    }

    public void setSalesHbNo(String salesHbNo) {
        this.salesHbNo = salesHbNo;
    }

    public Integer getStatus() {
        return status;
    }

    public String getStatusMsg() {
    	return SalesCombinationStatus.getStatusMsg(status);
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getOptTime() {
        return optTime;
    }

    public void setOptTime(Date optTime) {
        this.optTime = optTime;
    }

    public String getOptUser() {
        return optUser;
    }

    public void setOptUser(String optUser) {
        this.optUser = optUser;
    }
    
    public String getDesc(){
    	String desc = null;
    	if(optType != null){
    		switch (optType) {
			case 1:
				desc = "合并发货成功";
				break;
			case 2:
				desc = "客服确认"+(status==SalesCombinationStatus.WAITING_AUDIT_FINANCE?"通过":"不通过");
				break;
			case 3:
				desc = "财务审核"+(status==SalesCombinationStatus.WAITING_DELIVERY?"通过":"不通过");
				break;
			default:
				break;
			}
    	}
    	return desc; 
    }
}