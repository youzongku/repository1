package dto.discart;

import java.io.Serializable;
import java.util.List;

/**
 * 采购单发货实体
 * @author zbc
 * 2017年5月25日 下午7:15:35
 */
public class PurchaseSaleDto implements Serializable {

	private static final long serialVersionUID = 1154614914969086745L;
	
	private String address;//收货地址
	
	private Double orderActualAmount;//订单实付款
	
	private String receiver;//收货人
	
	private String LogisticsTypeCode;//物流代码
	
	private String remark;//分销商备注信息
	
	private String warehouseName;//仓库名称
	
	private Integer  isNotified = 1;//是否通知发货 默认为1
	
	private Integer warehouseId;//仓库id
	
	private Double orderTotalAmount;//订单总金额
	
	private Double bbcPostage;//bbc 平台运费
	
	private String telphone;//手机号码
	
	private String idcard;//身份证
	
	private String orderer;//订购人
	
	private String logisticsMode;//物流名称
	
	private String createUser;//下单人
	
	private String email;//分销商
	
	private Double orderPostage;
	private Integer shopId;
	private String collectAccount;
	private String tel;
	private String postCode;
	private String ordererIDCard;
	private String ordererTel;
	private String buyerID;
	private String purchaseOrderNo;//采购单号，用于指定发货


	public String getPurchaseOrderNo() {
		return purchaseOrderNo;
	}

	public void setPurchaseOrderNo(String purchaseOrderNo) {
		this.purchaseOrderNo = purchaseOrderNo;
	}

	public Double getOrderPostage() {
		return orderPostage;
	}

	public void setOrderPostage(Double orderPostage) {
		this.orderPostage = orderPostage;
	}

	public Integer getShopId() {
		return shopId;
	}

	public void setShopId(Integer shopId) {
		this.shopId = shopId;
	}

	public String getCollectAccount() {
		return collectAccount;
	}

	public void setCollectAccount(String collectAccount) {
		this.collectAccount = collectAccount;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getOrdererIDCard() {
		return ordererIDCard;
	}

	public void setOrdererIDCard(String ordererIDCard) {
		this.ordererIDCard = ordererIDCard;
	}

	public String getOrdererTel() {
		return ordererTel;
	}

	public void setOrdererTel(String ordererTel) {
		this.ordererTel = ordererTel;
	}

	public String getBuyerID() {
		return buyerID;
	}

	public void setBuyerID(String buyerID) {
		this.buyerID = buyerID;
	}

	private List<PurchaseSaleDetailDto> skuList;//商品信息

	public PurchaseSaleDto() {
		super();
	}

	public PurchaseSaleDto(String purchaseOrderNo,DeliveryInfoDto delivery, CartSaleDto cartSale,String account,List<PurchaseSaleDetailDto> skuList) {
		this.purchaseOrderNo = purchaseOrderNo;
		this.address = cartSale.getAddress();
		this.orderActualAmount = cartSale.getOrderActualAmount();
		this.receiver = cartSale.getReceiver();
		this.LogisticsTypeCode = delivery.getLogisticsTypeCode();
		this.remark = cartSale.getRemark();
		this.warehouseName = delivery.getWarehouseName();
		this.warehouseId = delivery.getWarehouseId();
		this.bbcPostage = delivery.getBbcPostage();
		this.telphone = cartSale.getTel();
		this.idcard = cartSale.getIdcard();
		this.orderer = cartSale.getOrderer();
		this.logisticsMode = delivery.getLogisticsMode();
		this.createUser = account;
		this.email = account;
		this.skuList = skuList;
		this.orderPostage = cartSale.getOrderPostage();
		this.shopId = cartSale.getShopId();
		this.collectAccount = cartSale.getCollectAccount();
		this.postCode = cartSale.getPostCode();
		this.ordererIDCard =cartSale.getOrdererIDCard() ;
		this.ordererTel = cartSale.getOrdererTel();
		this.buyerID = cartSale.getBuyerID();
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Double getOrderActualAmount() {
		return orderActualAmount;
	}

	public void setOrderActualAmount(Double orderActualAmount) {
		this.orderActualAmount = orderActualAmount;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getLogisticsTypeCode() {
		return LogisticsTypeCode;
	}

	public void setLogisticsTypeCode(String logisticsTypeCode) {
		this.LogisticsTypeCode = logisticsTypeCode;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public Integer getIsNotified() {
		return isNotified;
	}

	public void setIsNotified(Integer isNotified) {
		this.isNotified = isNotified;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Double getOrderTotalAmount() {
		return orderTotalAmount;
	}

	public void setOrderTotalAmount(Double orderTotalAmount) {
		this.orderTotalAmount = orderTotalAmount;
	}

	public Double getBbcPostage() {
		return bbcPostage;
	}

	public void setBbcPostage(Double bbcPostage) {
		this.bbcPostage = bbcPostage;
	}

	public String getTelphone() {
		return telphone;
	}

	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}

	public String getIdcard() {
		return idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

	public String getOrderer() {
		return orderer;
	}

	public void setOrderer(String orderer) {
		this.orderer = orderer;
	}

	public String getLogisticsMode() {
		return logisticsMode;
	}

	public void setLogisticsMode(String logisticsMode) {
		this.logisticsMode = logisticsMode;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<PurchaseSaleDetailDto> getSkuList() {
		return skuList;
	}

	public void setSkuList(List<PurchaseSaleDetailDto> skuList) {
		this.skuList = skuList;
	}
}
