package dto.sales;

import java.io.Serializable;

//销售发货（后台）订单数据导出实体类
public class ExportSaleOrderInfo implements Serializable {

	private static final long serialVersionUID = -8073487550402691045L;

	private String nickName;//昵称

	private String email;//账号

	private Integer orderId; // 订单主表id

	private String saleOrderNO; // 销售单号

	private String platformOrderNo;// 订单编号 对应于platformOrderNo（平台订单编号）

	private String buyerId;// Buyer ID 对应于buyerId（客户昵称）

	private String receiver;// 收货人姓名 对应于receiver（收货人）

	private String tel;// 手机号码 对应于tel（收货人手机号码）

	private String address;// 收货人地址 对应于address(收货人地址)

	private String sku;// 产品sku 对应于sku（商品编号）

	private String productName;// 产品名称 对应于productName（产品名称）

	private Double capfee;//均摊价

	private Integer num;//数量

	private Integer qty;// 商品QTY 对应于qty（商品购买数量）

	private String status;// 订单状态 对应于status（订单状态）

	private Double orderActualAmount;// 店铺实收金额 对应于orderActualAmount（实际付款）

	private String payAccount;// 付款账户 暂时没有TODO-------

	private Double orderActualPayAmount;// bbc付款金额 对应于orderActualAmount（实际付款）

	private String shopName;// 店铺名称 对应于shopName(店铺名称)

	private Double cost;// 裸采购价

	private Double bbcPostage;// 运费

	private String orderDateStr;// 下单时间

	private String disAccount;// 分销账号

	private String erp;

	private String distributionModelStr;

	private String distributorType;// 分销商类型

	private String warehouseName;// 仓库名称

	private String tradeNo;// 交易号

	private String receiverIDcard;// 收货人身份证

	private Double finalSellingPrice;//最终售价，作为单价

	private String createUser;//创建人

	private String remark;//订单备注

	private String expirationDate;//到期时间
	
	private Double platformAmount;//毛收入
	
	private String erpOrderNo;//erp订单号
	
	public String getErpOrderNo() {
		return erpOrderNo;
	}

	public void setErpOrderNo(String erpOrderNo) {
		this.erpOrderNo = erpOrderNo;
	}

	public Double getPlatformAmount() {
		return platformAmount;
	}

	public void setPlatformAmount(Double platformAmount) {
		this.platformAmount = platformAmount;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Double getFinalSellingPrice() {
		return finalSellingPrice;
	}

	public void setFinalSellingPrice(Double finalSellingPrice) {
		this.finalSellingPrice = finalSellingPrice;
	}

	public String getDistributorType() {
		return distributorType;
	}

	public void setDistributorType(String distributorType) {
		this.distributorType = distributorType;
	}

	public String getOrderDateStr() {
		return orderDateStr;
	}

	public void setOrderDateStr(String orderDateStr) {
		this.orderDateStr = orderDateStr;
	}

	public String getDisAccount() {
		return disAccount;
	}

	public void setDisAccount(String disAccount) {
		this.disAccount = disAccount;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getReceiverIDcard() {
		return receiverIDcard;
	}

	public void setReceiverIDcard(String receiverIDcard) {
		this.receiverIDcard = receiverIDcard;
	}

	public Double getBbcPostage() {
		return bbcPostage;
	}

	public void setBbcPostage(Double bbcPostage) {
		this.bbcPostage = bbcPostage;
	}

	public String getSaleOrderNO() {
		return saleOrderNO;
	}

	public void setSaleOrderNO(String saleOrderNO) {
		this.saleOrderNO = saleOrderNO;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Double getOrderActualAmount() {
		return orderActualAmount;
	}

	public void setOrderActualAmount(Double orderActualAmount) {
		this.orderActualAmount = orderActualAmount;
	}

	public String getPayAccount() {
		return payAccount;
	}

	public void setPayAccount(String payAccount) {
		this.payAccount = payAccount;
	}

	public Double getOrderActualPayAmount() {
		return orderActualPayAmount;
	}

	public void setOrderActualPayAmount(Double orderActualPayAmount) {
		this.orderActualPayAmount = orderActualPayAmount;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Double getCapfee() {
		return capfee;
	}

	public void setCapfee(Double capfee) {
		this.capfee = capfee;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public String getErp() {
		return erp;
	}

	public void setErp(String erp) {
		this.erp = erp;
	}

	public String getDistributionModelStr() {
		return distributionModelStr;
	}

	public void setDistributionModelStr(String distributionModelStr) {
		this.distributionModelStr = distributionModelStr;
	}
}
