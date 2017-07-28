package dto.discart;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.wordnik.swagger.annotations.ApiModelProperty;

import utils.discart.PriceFormatUtil;

/**
 * 购物车发货信息
 * @author zbc
 * 2017年5月26日 上午8:56:27
 */
public class DeliveryInfoDto implements Serializable {

	private static final long serialVersionUID = -7376161237123375646L;
	
	@ApiModelProperty("商品信息")
	private List<OrderDetail> goods;
	
	@ApiModelProperty("运费")
	private Double bbcPostage;

	@ApiModelProperty("订单金额")
	private Double totalPrice;
	
	@ApiModelProperty("待支付金额")
	private Double paymentPrice;

	@ApiModelProperty("商品总金额")
	private Double goodsTotalPrice;
	
	@ApiModelProperty("活动优惠")
	private Double activityPrice;
	
	@ApiModelProperty("优惠码")
	private String couponsCode;
	
	@ApiModelProperty("优惠金额")
	private Double couponsAmount;
	
	@ApiModelProperty("仓库id")
	private Integer warehouseId;
	
	@ApiModelProperty("仓库名称")
	private String warehouseName;
	
	@ApiModelProperty("商品总数")
	private Integer totalQty;
	
	@ApiModelProperty
	private List<Integer> itemIdList;
	
	private String logisticsMode;//物流名称
	
	private String logisticsTypeCode;//物流代码
	
	private Boolean validOrder = false;//是否可以下单标识
	
	@SuppressWarnings("unused")
	private Double purchaseTotalPrice;
	
	private Integer isPackageMail;//是否包邮标识
	
	public Integer getIsPackageMail() {
		return isPackageMail;
	}

	public void setIsPackageMail(Integer isPackageMail) {
		this.isPackageMail = isPackageMail;
	}

	public void setPurchaseTotalPrice(Double purchaseTotalPrice) {
		this.purchaseTotalPrice = purchaseTotalPrice;
	}

	//采购总金额
	public Double getPurchaseTotalPrice() {
		BigDecimal purchaseTotalPrice = BigDecimal.ZERO;
		if(this.goodsTotalPrice != null){
			purchaseTotalPrice = purchaseTotalPrice.add(new BigDecimal(this.goodsTotalPrice));
		}
		if(this.activityPrice != null){
			purchaseTotalPrice = purchaseTotalPrice.subtract(new BigDecimal(this.activityPrice));
		}
		if(getBbcPostage() != null){
			purchaseTotalPrice = purchaseTotalPrice.add(new BigDecimal(getBbcPostage()));
		}
		return purchaseTotalPrice.compareTo(BigDecimal.ZERO)>0?PriceFormatUtil.toFix2(purchaseTotalPrice):0.0;
	}

	public Boolean getValidOrder() {
		return validOrder;
	}

	public void setValidOrder(Boolean validOrder) {
		this.validOrder = validOrder;
	}

	public String getLogisticsMode() {
		return logisticsMode;
	}

	public void setLogisticsMode(String logisticsMode) {
		this.logisticsMode = logisticsMode;
	}

	public String getLogisticsTypeCode() {
		return logisticsTypeCode;
	}

	public void setLogisticsTypeCode(String logisticsTypeCode) {
		this.logisticsTypeCode = logisticsTypeCode;
	}

	public DeliveryInfoDto(String uid) {
		super();
		this.uid = uid;
	}
	public DeliveryInfoDto() {
		super();
	}

	private String uid;
	
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public List<Integer> getItemIdList() {
		return itemIdList;
	}

	public void setItemIdList(List<Integer> itemIdList) {
		this.itemIdList = itemIdList;
	}

	public Double getPaymentPrice() {
		BigDecimal paymentPrice = BigDecimal.ZERO;
		if(this.goodsTotalPrice != null){
			paymentPrice = paymentPrice.add(new BigDecimal(this.goodsTotalPrice));
		}
		if(this.activityPrice != null){
			paymentPrice = paymentPrice.subtract(new BigDecimal(this.activityPrice));
		}
		if(getBbcPostage() != null){
			paymentPrice = paymentPrice.add(new BigDecimal(getBbcPostage()));
		}
		if(this.couponsAmount != null){
			paymentPrice = paymentPrice.subtract(new BigDecimal(this.couponsAmount));
		}
		return paymentPrice.compareTo(BigDecimal.ZERO)>0?PriceFormatUtil.toFix2(paymentPrice):0.0;
	}
	
	public void setPaymentPrice(Double paymentPrice) {
		this.paymentPrice = paymentPrice;
	}

	public Integer getTotalQty() {
		return totalQty;
	}

	public void setTotalQty(Integer totalQty) {
		this.totalQty = totalQty;
	}

	public List<OrderDetail> getGoods() {
		return goods;
	}

	public void setGoods(List<OrderDetail> goods) {
		this.goods = goods;
	}

	public Double getBbcPostage() {
		if(isPackageMail == 1){
			return 0d;
		}
		return bbcPostage;
	}

	public void setBbcPostage(Double bbcPostage) {
		this.bbcPostage = bbcPostage;
	}

	public Double getTotalPrice() {
		BigDecimal totalPrice = BigDecimal.ZERO;
		if(this.goodsTotalPrice != null){
			totalPrice = totalPrice.add(new BigDecimal(this.goodsTotalPrice));
		}
		if(this.activityPrice != null){
			totalPrice = totalPrice.subtract(new BigDecimal(this.activityPrice));
		}
		return totalPrice.compareTo(BigDecimal.ZERO)>0?PriceFormatUtil.toFix2(totalPrice):0.0;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Double getGoodsTotalPrice() {
		return goodsTotalPrice;
	}

	public void setGoodsTotalPrice(Double goodsTotalPrice) {
		this.goodsTotalPrice = goodsTotalPrice;
	}

	public Double getActivityPrice() {
		return activityPrice;
	}

	public void setActivityPrice(Double activityPrice) {
		this.activityPrice = activityPrice;
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

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}
	
}
