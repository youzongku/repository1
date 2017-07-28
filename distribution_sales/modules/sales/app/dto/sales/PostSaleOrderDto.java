package dto.sales;

import java.io.Serializable;
import java.util.List;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import entity.sales.SaleBase;
import entity.sales.SaleInvoice;
import entity.sales.SaleMain;

/**
 * 发货单下单实体
 * @author zbc
 * 2017年4月13日 下午7:07:57
 */
@ApiModel(value="发货单下单实体",description="sdsf")
public class PostSaleOrderDto implements Serializable {
	private static final long serialVersionUID = 6628994330007737091L;
	@ApiModelProperty("分销商账号")
	private String email;
	@ApiModelProperty("备注")
	private String remark;
	@ApiModelProperty("收货地址")
	private String address;
	@ApiModelProperty("店铺id")
	private String shopId;
	@ApiModelProperty("收货人手机号码")
	private String telphone;
	@ApiModelProperty("收货人邮编")
	private String postCode;
	@ApiModelProperty("快递方式代码")
	private String LogisticsTypeCode;
	@ApiModelProperty("快递方式名称")
	private String logisticsMode;
	@ApiModelProperty("创建人")
	private String createUser;
	@ApiModelProperty("是否后台录入")
	private Boolean isBack;
	@ApiModelProperty("省id")
	private Integer provinceId;
	@ApiModelProperty("是否自动扣款")
	private Boolean isPay;
	@ApiModelProperty("发货仓库名称")
	private String warehouseName;
	@ApiModelProperty("发货仓库id")
	private Integer warehouseId;
	@ApiModelProperty("商品数据")
	private List<PostSalePro> skuList;
	@ApiModelProperty("城市id")
	private Integer cityId;
	@ApiModelProperty("收货人")
	private String receiver;
	// change by zbc 默认通知
	@ApiModelProperty("通知发货标识，不传默认为1")
	private Integer isNotified = 1;
	@ApiModelProperty("交易号")
	private String tradeNo;	
	@ApiModelProperty("平台单号")
	private String platformOrderNo;	
	@ApiModelProperty("运费")
	private Double orderPostage;
	@ApiModelProperty("收货人身份证号码")
	private String idcard;
	@ApiModelProperty("实付款")
	private Double orderActualAmount;
	@ApiModelProperty("买家姓名")
	private String orderer;
	@ApiModelProperty("买家身份证")
	private String ordererIDCard;
	@ApiModelProperty("买家手机号")
	private String ordererTel;
	@ApiModelProperty("客户名称")
	private String buyerID;
	@ApiModelProperty("收款账号")
	private String collectAccount;
	@ApiModelProperty("是否要开发票(true：需要,false：不需要 )")
	private Boolean  isNeedInvoice;
	@ApiModelProperty("发票抬头")
	private String invoiceTitle;
	@ApiModelProperty("发票类型(1:个人，2:公司)")
	private Integer invoiceType;
	
	public PostSaleOrderDto(SaleInvoice invoice,SaleMain main,SaleBase base,String createUser, Boolean isBack,
			Integer provinceId,Integer cityId, List<PostSalePro> skuList) {
		this.email = main.getEmail();
		this.remark = base.getRemark();
		this.address = base.getAddress();
		this.receiver = base.getReceiver();
		this.telphone = base.getTel();
		this.postCode = base.getPostCode();
		this.LogisticsTypeCode = base.getLogisticsTypeCode();
		this.logisticsMode = base.getLogisticsMode();
		this.createUser = createUser;
		this.isBack = isBack;
		this.provinceId = provinceId;
		this.warehouseName = main.getWarehouseName();
		this.warehouseId = main.getWarehouseId();
		this.skuList = skuList;
		this.cityId = cityId;
		if(invoice != null){
			this.isNeedInvoice = true;
		    this.invoiceTitle = invoice.getInvoiceTitle();
		    this.invoiceType = invoice.getInvoiceType();
		}
	}

	public Boolean getIsNeedInvoice() {
		return isNeedInvoice;
	}


	public void setIsNeedInvoice(Boolean isNeedInvoice) {
		this.isNeedInvoice = isNeedInvoice;
	}


	public String getInvoiceTitle() {
		return invoiceTitle;
	}


	public void setInvoiceTitle(String invoiceTitle) {
		this.invoiceTitle = invoiceTitle;
	}


	public Integer getInvoiceType() {
		return invoiceType;
	}


	public void setInvoiceType(Integer invoiceType) {
		this.invoiceType = invoiceType;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public String getTelphone() {
		return telphone;
	}

	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}
	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getLogisticsTypeCode() {
		return LogisticsTypeCode;
	}

	public void setLogisticsTypeCode(String logisticsTypeCode) {
		LogisticsTypeCode = logisticsTypeCode;
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

	public Boolean getIsBack() {
		return isBack;
	}

	public void setIsBack(Boolean isBack) {
		this.isBack = isBack;
	}

	public Integer getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
	}

	public Boolean getIsPay() {
		return isPay;
	}

	public void setIsPay(Boolean isPay) {
		this.isPay = isPay;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public List<PostSalePro> getSkuList() {
		return skuList;
	}

	public void setSkuList(List<PostSalePro> skuList) {
		this.skuList = skuList;
	}
}

