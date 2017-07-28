package dto.openapi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dto.openapi.enums.PurchaseOrderStatus;

/**
 * Created by luwj on 2015/12/8.
 */
public class ViewPurchaseOrder {

    private Integer id;

    private String email;//用户

    private String purchaseOrderNo;//采购单号

    private Integer status;//采购单状态：0待付款，1已付款，2已取消

    private Date purchaseDate;//下单时间

    private String sorderDate;//下单时间(String)

    private Double purchaseTotalAmount;//订单总金额

    private Date payDate;//付款时间

    private String spayDate;//付款时间(String)
    
    private Double purchaseDiscountAmount;//订单折后金额

    private List<ViewPurchaseDetail> details;
    
    private List<ViewPurchaseOrder> associatedOrders = new ArrayList<>();// 关联的订单
    
    private Integer sid;
    
    private Double discount;//订单折扣
    
    private Double salesAmount;//销售单缺货采购实付金额
    
    private Integer purchaseType;//采购类型（1：常规采购，2：缺货采购）
    
    private Date cancelDate;//取消时间
    
    private String cancelDateStr;//取消时间(String)
    
    private Double bbcPostage;//分销平台运费
    
    private String logisticsMode;//物流方式
    
    private String couponsCode;//优惠码
    
    private Double couponsAmount;//优惠码面额 
    
    private Double deductionAmount;//促销减免金额
    
    private Boolean isPro;//是否存在活动
    
    private Boolean isChoose;//是否已选优惠
    
    private String inputUser;//录入人
    
    private Double offlineMoney;// 现金交易支付金额
    
    private boolean hasAssociatedOrders; // 是否有关联订单
    
    private Double financeMoney;//财务实收金额
	
	private String remark;//财务备注
	
	private String reason;//审核理由
	
	private Double totalOfflineMoney;//总现金支付金额（包含关联订单的现金交易支付金额在内）

	private Double orderProTotal;//订单商品总金额
	
	private Double reducePrice;//减价金额
	
	private String customerService;//客服账号
	
	private String paymentId;//支付方式
	
	private Double tAWPrice;//总到仓价
	
	private String oaAuditNo;// oa审批单号，唯一的

	private String busenessRemarks;// 业务备注
	
	private String nickName;//分销商昵称
	
	// 线下转账的才有
	private PurchaseAudit purchaseAudit;
	// 客服审核记录
	private List<PurchaseOrderAuditLog> csAuditLogs;
	// 财务审核记录
	private List<PurchaseOrderAuditLog> financeAuditLogs;

	private String remarks;
	
	@SuppressWarnings("unused")
	private String statusMes;
	
	public void setStatusMes(String statusMes) {
		this.statusMes = statusMes;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public PurchaseAudit getPurchaseAudit() {
		return purchaseAudit;
	}

	public void setPurchaseAudit(PurchaseAudit purchaseAudit) {
		this.purchaseAudit = purchaseAudit;
	}

	public List<PurchaseOrderAuditLog> getCsAuditLogs() {
		return csAuditLogs;
	}

	public void setCsAuditLogs(List<PurchaseOrderAuditLog> csAuditLogs) {
		this.csAuditLogs = csAuditLogs;
	}

	public List<PurchaseOrderAuditLog> getFinanceAuditLogs() {
		return financeAuditLogs;
	}

	public void setFinanceAuditLogs(List<PurchaseOrderAuditLog> financeAuditLogs) {
		this.financeAuditLogs = financeAuditLogs;
	}

	public Double gettAWPrice() {
		return tAWPrice;
	}

	public void settAWPrice(Double tAWPrice) {
		this.tAWPrice = tAWPrice;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getCustomerService() {
		return customerService;
	}

	public void setCustomerService(String customerService) {
		this.customerService = customerService;
	}

	public Double getOrderProTotal() {
		return orderProTotal;
	}

	public void setOrderProTotal(Double orderProTotal) {
		this.orderProTotal = orderProTotal;
	}

	public Double getReducePrice() {
		return reducePrice;
	}

	public void setReducePrice(Double reducePrice) {
		this.reducePrice = reducePrice;
	}

	public Double getTotalOfflineMoney() {
		return totalOfflineMoney;
	}

	public void setTotalOfflineMoney(Double totalOfflineMoney) {
		this.totalOfflineMoney = totalOfflineMoney;
	}

	public Double getOfflineMoney() {
		return offlineMoney;
	}

	public void setOfflineMoney(Double offlineMoney) {
		this.offlineMoney = offlineMoney;
	}

	public boolean isHasAssociatedOrders() {
		return hasAssociatedOrders;
	}

	public void setHasAssociatedOrders(boolean hasAssociatedOrders) {
		this.hasAssociatedOrders = hasAssociatedOrders;
	}

	public List<ViewPurchaseOrder> getAssociatedOrders() {
		return associatedOrders;
	}

	public void setAssociatedOrders(List<ViewPurchaseOrder> associatedOrders) {
		this.associatedOrders = associatedOrders;
	}

	public String getInputUser() {
		return inputUser;
	}

	public void setInputUser(String inputUser) {
		this.inputUser = inputUser;
	}

	public String getCouponsCode() {
		return couponsCode;
	}

	public void setCouponsCode(String couponsCode) {
		this.couponsCode = couponsCode;
	}

	public Double getCouponsAmount() {
		return couponsAmount;
	}

	public void setCouponsAmount(Double couponsAmount) {
		this.couponsAmount = couponsAmount;
	}

	public Double getBbcPostage() {
		return bbcPostage;
	}

	public void setBbcPostage(Double bbcPostage) {
		this.bbcPostage = bbcPostage;
	}

	public String getLogisticsMode() {
		return logisticsMode;
	}

	public void setLogisticsMode(String logisticsMode) {
		this.logisticsMode = logisticsMode;
	}

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

	public Integer getPurchaseType() {
		return purchaseType;
	}

	public void setPurchaseType(Integer purchaseType) {
		this.purchaseType = purchaseType;
	}

	public Double getSalesAmount() {
		return salesAmount;
	}

	public void setSalesAmount(Double salesAmount) {
		this.salesAmount = salesAmount;
	}
    
    public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
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
    	switch (status) {
		case 0:
			return PurchaseOrderStatus.待付款.name();
		case 1:
			return PurchaseOrderStatus.已付款.name();
		case 2:
			return PurchaseOrderStatus.已取消.name();
		case 3:
			return PurchaseOrderStatus.已失效.name();
		case 4:
			return PurchaseOrderStatus.待审核.name();
		case 5:
			return PurchaseOrderStatus.审核不通过.name();
		}
    	return null;
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

	public List<ViewPurchaseDetail> getDetails() {
        return details;
    }

    public void setDetails(List<ViewPurchaseDetail> details) {
        this.details = details;
    }

	public Double getDeductionAmount() {
		return deductionAmount;
	}

	public void setDeductionAmount(Double deductionAmount) {
		this.deductionAmount = deductionAmount;
	}

	public Boolean getIsPro() {
		return isPro;
	}

	public void setIsPro(Boolean isPro) {
		this.isPro = isPro;
	}

	public Boolean getIsChoose() {
		return isChoose;
	}

	public void setIsChoose(Boolean isChoose) {
		this.isChoose = isChoose;
	}

	public Double getFinanceMoney() {
		return financeMoney;
	}

	public void setFinanceMoney(Double financeMoney) {
		this.financeMoney = financeMoney;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getOaAuditNo() {
		return oaAuditNo;
	}

	public void setOaAuditNo(String oaAuditNo) {
		this.oaAuditNo = oaAuditNo;
	}

	public String getBusenessRemarks() {
		return busenessRemarks;
	}

	public void setBusenessRemarks(String busenessRemarks) {
		this.busenessRemarks = busenessRemarks;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
}
