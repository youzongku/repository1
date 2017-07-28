package dto.sales;
//销售发货（前台）导出物流信息实体类
public class ExportSaleLogistic {
	
	private Integer id;
	
    private String saleOrderNO; //销售单号
	
    private String platformOrderNo;//订单编号  对应于platformOrderNo（平台订单编号）

    private String buyerId;//Buyer ID 对应于buyerId（客户昵称）

    private String receiver;//收货人姓名 对应于receiver（收货人）
    
	private String tel;//手机号码 对应于tel（收货人手机号码）
	
	private String shippingName;//快递公司
	
	private String trackingNumber;//快递单号
	
	private String localTrackNumber;//平邮号
	
	private String logisticsMode;//导出备选物流方式（对应t_product_sales_order_base的logistics_mode)

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSaleOrderNO() {
		return saleOrderNO;
	}

	public void setSaleOrderNO(String saleOrderNO) {
		this.saleOrderNO = saleOrderNO;
	}

	public String getPlatformOrderNo() {
		return platformOrderNo;
	}

	public void setPlatformOrderNo(String platformOrderNo) {
		this.platformOrderNo = platformOrderNo;
	}

	public String getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getShippingName() {
		return shippingName;
	}

	public void setShippingName(String shippingName) {
		this.shippingName = shippingName;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}

	public String getLocalTrackNumber() {
		return localTrackNumber;
	}

	public void setLocalTrackNumber(String localTrackNumber) {
		this.localTrackNumber = localTrackNumber;
	}

	public String getLogisticsMode() {
		return logisticsMode;
	}

	public void setLogisticsMode(String logisticsMode) {
		this.logisticsMode = logisticsMode;
	}
	
}
