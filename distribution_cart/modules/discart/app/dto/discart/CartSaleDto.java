package dto.discart;

import java.io.Serializable;

import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 结算发货实体
 * @author zbc
 * 2017年5月26日 下午3:34:56
 */
public class CartSaleDto implements Serializable {

	private static final long serialVersionUID = 2792945695391093177L;
	
	@ApiModelProperty("发货信息uid")
	private String uid;
	@ApiModelProperty("实付款")
	private Double orderActualAmount;
	@ApiModelProperty("运费")
	private Double orderPostage;
	@ApiModelProperty("店铺ID")
	private Integer shopId;
	@ApiModelProperty("收款账号")
	private String collectAccount;
	@ApiModelProperty("优惠码金额")
	private Double  couponsAmount;
	@ApiModelProperty("优惠码")
	private String couponsCode;
	@ApiModelProperty("收货地址")
	private String address;
	@ApiModelProperty("收货人")
	private String receiver;
	@ApiModelProperty("联系方式")
	private String tel;
	@ApiModelProperty("收货人身份证")
	private String idcard; 
	@ApiModelProperty("邮编")
	private String postCode;
	@ApiModelProperty("订购人")
	private String orderer;
	@ApiModelProperty("订购人身份证")
	private String ordererIDCard;
	@ApiModelProperty("订购人电话")
	private String ordererTel;
	@ApiModelProperty("买家姓名")
	private String buyerID;
	@ApiModelProperty("备注信息")
	private String remark;
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
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
	public Double getCouponsAmount() {
		return couponsAmount;
	}
	public void setCouponsAmount(Double couponsAmount) {
		this.couponsAmount = couponsAmount;
	}
	public String getCouponsCode() {
		return couponsCode;
	}
	public void setCouponsCode(String couponsCode) {
		this.couponsCode = couponsCode;
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
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

}
