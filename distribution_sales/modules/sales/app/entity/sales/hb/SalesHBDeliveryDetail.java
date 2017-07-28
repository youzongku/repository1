package entity.sales.hb;

public class SalesHBDeliveryDetail {
    private Integer id;
    // 合并单id
    private Integer salesHbId;
    // 合并单单号
    private String salesHbNo;
    // 发货单单号
    private String salesOrderNo;
    // 毛收入
    private Double grossIncome;
    // 毛利润
    private Double grossProfit;
    
    private Integer shopId;// 店铺id
    private Integer salesOrderId;// 销售单id
    private String purchaseOrderNo;// 缺货采购单

    public SalesHBDeliveryDetail(String salesHbNo, String salesOrderNo) {
		super();
		this.salesHbNo = salesHbNo;
		this.salesOrderNo = salesOrderNo;
	}
    
	public SalesHBDeliveryDetail(String salesHbNo, String salesOrderNo, Double grossIncome, Double grossProfit) {
		super();
		this.salesHbNo = salesHbNo;
		this.salesOrderNo = salesOrderNo;
		this.grossIncome = grossIncome;
		this.grossProfit = grossProfit;
	}

	public Integer getShopId() {
		return shopId;
	}

	public void setShopId(Integer shopId) {
		this.shopId = shopId;
	}

	public Integer getSalesOrderId() {
		return salesOrderId;
	}

	public void setSalesOrderId(Integer salesOrderId) {
		this.salesOrderId = salesOrderId;
	}

	public String getPurchaseOrderNo() {
		return purchaseOrderNo;
	}

	public void setPurchaseOrderNo(String purchaseOrderNo) {
		this.purchaseOrderNo = purchaseOrderNo;
	}

	public Double getGrossIncome() {
		return grossIncome;
	}

	public void setGrossIncome(Double grossIncome) {
		this.grossIncome = grossIncome;
	}

	public Double getGrossProfit() {
		return grossProfit;
	}

	public void setGrossProfit(Double grossProfit) {
		this.grossProfit = grossProfit;
	}

	public SalesHBDeliveryDetail() {
		super();
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

    public String getSalesOrderNo() {
        return salesOrderNo;
    }

    public void setSalesOrderNo(String salesOrderNo) {
        this.salesOrderNo = salesOrderNo;
    }
}