package entity.sales;

import java.io.Serializable;
import java.util.Date;

import com.wordnik.swagger.annotations.ApiModel;

@ApiModel
public class SaleBase implements Serializable {

	private static final long serialVersionUID = -6303684803955062069L;

	private Integer id;// 主键

	private String platformOrderNo;// 平台订单号

	private Integer platformType;// 平台类型（b2b_member数据库维护常量）

	private String platformTypeName;// 平台名称

	private String platformName;

	private Double orderTotalAmount;// 发货单总金额（不包含运费）

	private Double orderActualAmount;// 发货单实际金额（不包含运费）

	private Double orderPostage;// 发货单邮费

	private Date orderingDate;// 顾客下单时间

	private String orderingDateStr;// 顾客下单时间 字符串

	private String remark;// 发货单备注

	private Integer salesOrderId;// 发货单主表id

	private Integer shopId;// 店铺id

	private Integer addrId;// 地址id

	private String address;// 详细地址（不依赖收货地址列表，以本字段信息为准）

	private String receiver;// 收货人（不依赖收货地址列表，以本字段信息为准）

	private String tel;// 收货人电话（不依赖收货地址列表，以本字段信息为准）

	private String idcard;// 收货人身份证号码（不依赖收货地址列表，以本字段信息为准）

	private String postCode;// 邮编
	
	private String tradeNo;// 交易号

	private String orderer;// 订购人姓名

	private String ordererIDCard;// 订购人身份证

	private String ordererTel;// 订购人手机号

	private String ordererPostcode;// 订购人邮编

	private String buyerID;// 淘宝ID

	private String collectAccount;// 收款账户

	private String customerservice;// 客服账号

	private String logisticsTypeCode;// 物流方式代码
	
	private String thirdPartLogisticsTypeCode;//第三方物流代码

	private Double bbcPostage;// 分销平台运费

	private Double originalFreight;// 分销商平台原始运费，bbc_postage有可能被修改

	private String logisticsMode; // 物流方式

	private String couponsCode;// 优惠码

	private Double couponsAmount;// 优惠金额

	private String createUser;// 录入人

	private Boolean isBack;// 是否为后台录入

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

	public String getLogisticsMode() {
		return logisticsMode;
	}

	public void setLogisticsMode(String logisticsMode) {
		this.logisticsMode = logisticsMode;
	}

	public String getCustomerservice() {
		return customerservice;
	}

	public void setCustomerservice(String customerservice) {
		this.customerservice = customerservice;
	}

	public String getCollectAccount() {
		return collectAccount;
	}

	public void setCollectAccount(String collectAccount) {
		this.collectAccount = collectAccount;
	}

	public String getBuyerID() {
		return buyerID;
	}

	public void setBuyerID(String buyerID) {
		this.buyerID = buyerID;
	}

	public String getOrdererTel() {
		return ordererTel;
	}

	public void setOrdererTel(String ordererTel) {
		this.ordererTel = ordererTel;
	}

	public String getOrdererPostcode() {
		return ordererPostcode;
	}

	public void setOrdererPostcode(String ordererPostcode) {
		this.ordererPostcode = ordererPostcode;
	}

	public String getOrderer() {
		return orderer;
	}

	public void setOrderer(String orderer) {
		this.orderer = orderer;
	}

	public String getOrdererIDCard() {
		return ordererIDCard;
	}

	public void setOrdererIDCard(String ordererIDCard) {
		this.ordererIDCard = ordererIDCard;
	}

	public Integer getShopId() {
		return shopId;
	}

	public void setShopId(Integer shopId) {
		this.shopId = shopId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPlatformTypeName() {
		return platformTypeName;
	}

	public void setPlatformTypeName(String platformTypeName) {
		this.platformTypeName = platformTypeName;
	}

	public String getPlatformOrderNo() {
		return platformOrderNo;
	}

	public void setPlatformOrderNo(String platformOrderNo) {
		this.platformOrderNo = platformOrderNo;
	}

	public Integer getPlatformType() {
		return platformType;
	}

	public void setPlatformType(Integer platformType) {
		this.platformType = platformType;
	}

	public String getPlatformName() {
		return platformName;
	}

	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}

	public Double getOrderTotalAmount() {
		return orderTotalAmount;
	}

	public void setOrderTotalAmount(Double orderTotalAmount) {
		this.orderTotalAmount = orderTotalAmount;
	}

	public Double getOrderActualAmount() {
		return orderActualAmount;
	}

	public void setOrderActualAmount(Double orderActualAmount) {
		this.orderActualAmount = orderActualAmount;
	}

	public Double getOrderPostage() {
		return orderPostage;
	}

	public void setOrderPostage(Double orderPostage) {
		this.orderPostage = orderPostage;
	}

	public Date getOrderingDate() {
		return orderingDate;
	}

	public void setOrderingDate(Date orderingDate) {
		this.orderingDate = orderingDate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getSalesOrderId() {
		return salesOrderId;
	}

	public void setSalesOrderId(Integer salesOrderId) {
		this.salesOrderId = salesOrderId;
	}

	public Integer getAddrId() {
		return addrId;
	}

	public void setAddrId(Integer addrId) {
		this.addrId = addrId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public String getIdcard() {
		return idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getLogisticsTypeCode() {
		return logisticsTypeCode;
	}

	public void setLogisticsTypeCode(String logisticsTypeCode) {
		this.logisticsTypeCode = logisticsTypeCode;
	}

	public Double getBbcPostage() {
		return bbcPostage;
	}

	public void setBbcPostage(Double bbcPostage) {
		this.bbcPostage = bbcPostage;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Boolean getIsBack() {
		return isBack;
	}

	public void setIsBack(Boolean isBack) {
		this.isBack = isBack;
	}

	public Double getOriginalFreight() {
		return originalFreight;
	}

	public void setOriginalFreight(Double originalFreight) {
		this.originalFreight = originalFreight;
	}

	@Override
	public String toString() {
		return "SaleBase [id=" + id + ", platformOrderNo=" + platformOrderNo + ", platformType=" + platformType
				+ ", platformTypeName=" + platformTypeName + ", platformName=" + platformName + ", orderTotalAmount="
				+ orderTotalAmount + ", orderActualAmount=" + orderActualAmount
				+ ", orderPostage="
				+ orderPostage + ", orderingDate=" + orderingDate + ", orderingDateStr=" + orderingDateStr + ", remark="
				+ remark + ", salesOrderId=" + salesOrderId + ", shopId=" + shopId + ", addrId=" + addrId + ", address="
				+ address + ", receiver=" + receiver + ", tel=" + tel + ", idcard=" + idcard + ", postCode=" + postCode
				+ ", tradeNo=" + tradeNo + ", orderer=" + orderer + ", ordererIDCard=" + ordererIDCard + ", ordererTel="
				+ ordererTel + ", ordererPostcode=" + ordererPostcode + ", buyerID=" + buyerID + ", collectAccount="
				+ collectAccount + ", customerservice=" + customerservice + ", logisticsTypeCode=" + logisticsTypeCode
				+ ", bbcPostage=" + bbcPostage + ", originalFreight=" + originalFreight + ", logisticsMode="
				+ logisticsMode + ", couponsCode=" + couponsCode + ", couponsAmount=" + couponsAmount + ", createUser="
				+ createUser + ", isBack=" + isBack + "]";
	}

	public String getThirdPartLogisticsTypeCode() {
		return thirdPartLogisticsTypeCode;
	}

	public void setThirdPartLogisticsTypeCode(String thirdPartLogisticsTypeCode) {
		this.thirdPartLogisticsTypeCode = thirdPartLogisticsTypeCode;
	}

}