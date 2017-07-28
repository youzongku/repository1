package entity.platform.order.template;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Strings;

import play.Logger;
import services.base.utils.DateFormatUtils;
import util.sales.AddressUtils;
import util.sales.DateJsonSerialUtils;
import util.sales.ImportOrderUtils;

/**
 * @author hanfs
 * 描述：
 *2016年5月16日
 */
public class TaoBaoOrder implements Serializable {

	private static final long serialVersionUID = 6389341511175289506L;
	private Integer id;
	private String buyerAccount;// 买家账号
	private String receiverName;// 收货人姓名
	private String receiverCardNumber;// 收货人身份证号码
	private String receiverPhone;// 收货人手机号
	private String receiverTelephone;//收货人联系电话
	// 下列三行为有赞模板所设定和address进行字符串拼接
	private String province;//收货人省份
	private String city;//收货人城市
	private String area;//收货人区，县，乡
	
	private String address;// 收货地址
	private String shopName;// 店铺名称
	private Integer platformid;//店铺平台id
	private String orderNo;// 订单编号
	private Double orderTotal;// 订单总额
	private String orderStatus;// 订单状态
	private Date paymentDate;// 付款日期
	private String paymentNo;// 支付交易号
	private String paymentPhone;// 买家(订购人)手机号
	private String paymentName;// 买家(订购人)姓名
	private String paymentCardNumber;// 买家(订购人)身份证号码
	private String paymentPostCode;//买家(订购人)邮编
	private String sellerRemark;// 卖家备注
	private String buyerMessage;// 买家留言
	private String invoiceInfo;// 发票信息
	private String postCode;//邮编
	private Double logisticsCost;//物流费用
	private String logisticsTypeCode;//物流方式代码
	private String logisticsTypeName;//物流方式名称

	private Integer oprateStatus;// 订单状态(平台状态，0 待补全、1 待生成 ...)
	private Integer isDeleted;// 逻辑删除（0 未删除 1 删除）
	
	private Integer isCreate;//是否生成平台销售单（0没有  1已生成）
	
	private Date updateDate;//导入和修改时间用于排序
	private String email;//订单归属人
	
	private String financeRemark;//财务备注
	
	private Boolean isPart;//是否部分生成
	
	private Double thirdPostfee;//第三方运费
	
	private Integer plateform;//平台类型 14：拼多多
	
	private List<TaoBaoOrderGoods> goods;//商品信息
	
	private Integer isComplete;//订单信息是否完整(对应所有商品仓库信息等完整)
	
	private Boolean isNeedInvoice;// 是否需要开发票 
	
	private Integer invoiceType;//发票类型(1:个人，2:公司)
	
	private String invoiceTitle;//发票抬头    

	public Boolean getIsNeedInvoice() {
		return isNeedInvoice;
	}

	public void setIsNeedInvoice(Boolean isNeedInvoice) {
		this.isNeedInvoice = isNeedInvoice;
	}

	public Integer getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(Integer invoiceType) {
		this.invoiceType = invoiceType;
	}

	public String getInvoiceTitle() {
		return invoiceTitle;
	}

	public void setInvoiceTitle(String invoiceTitle) {
		this.invoiceTitle = invoiceTitle;
	}

	public Integer getIsComplete() {
		return isComplete;
	}

	public void setIsComplete(Integer isComplete) {
		this.isComplete = isComplete;
	}

	public List<TaoBaoOrderGoods> getGoods() {
		return goods;
	}

	public void setGoods(List<TaoBaoOrderGoods> goods) {
		this.goods = goods;
	}

	public Integer getPlateform() {
		return plateform;
	}

	public void setPlateform(Integer plateform) {
		this.plateform = plateform;
	}

	public Double getThirdPostfee() {
		return thirdPostfee;
	}

	public void setThirdPostfee(Double thirdPostfee) {
		this.thirdPostfee = thirdPostfee;
	}

	public TaoBaoOrder() {
		super();
	}

	public TaoBaoOrder(Integer isDeleted, Integer isCreate,String email) {
		super();
		this.isDeleted = isDeleted;
		this.isCreate = isCreate;
		this.email = email;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBuyerAccount() {
		return buyerAccount;
	}

	public void setBuyerAccount(String buyerAccount) {
		this.buyerAccount = buyerAccount;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getReceiverCardNumber() {
		return receiverCardNumber;
	}

	public void setReceiverCardNumber(String receiverCardNumber) {
		this.receiverCardNumber = ImportOrderUtils.getStringNotZeroForCellValue(receiverCardNumber);
	}

	public String getReceiverPhone() {
		return receiverPhone;
	}

	public void setReceiverPhone(String receiverPhone) {
		this.receiverPhone = StringUtils.isBlank(receiverPhone) ? null : receiverPhone.replace(".00", "").replaceAll("[^\\w]|_", "");
	}

	
	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public Integer getPlatformid() {
		return platformid;
	}

	public void setPlatformid(Integer platformid) {
		this.platformid = platformid;
	}
	
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = StringUtils.isBlank(orderNo) ? null : orderNo.replace(".00", "");
	}

	public Double getOrderTotal() {
		return orderTotal;
	}

	public void setOrderTotal(Double orderTotal) {
		this.orderTotal = orderTotal;
	}
	
	public void setOrderTotalStr(String orderTotalStr) {
		if (!Strings.isNullOrEmpty(orderTotalStr)) {
			this.orderTotal = Double.valueOf(orderTotalStr);
		}
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getPaymentNo() {
		return paymentNo;
	}

	public void setPaymentNo(String paymentNo) {
		this.paymentNo = ImportOrderUtils.getStringNotZeroForCellValue(paymentNo);
	}

	public String getPaymentPostCode() {
		return paymentPostCode;
	}

	public void setPaymentPostCode(String paymentPostCode) {
		this.paymentPostCode = ImportOrderUtils.getStringNotZeroForCellValue(paymentPostCode);
	}

	@JsonSerialize(using = DateJsonSerialUtils.class)
	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
	
	public void setPaymentDateStr(String paymentDateStr) {
		Date payDate = null;
		//支持的时间格式有（yyyy-MM-dd，yyyy-MM-dd HH:mm:ss，yyyy/MM/dd）
		if (paymentDateStr != null && !"".equals(paymentDateStr.trim())) {
			payDate = DateFormatUtils.getFormatDateByStr(paymentDateStr.replaceAll("/", "-"));
			if (payDate == null) {
				payDate = DateFormatUtils.getFormatDateYmdhmsByStr(paymentDateStr);
			}
		}
	    this.paymentDate = payDate;
	}

	public String getPaymentPhone() {
		return paymentPhone;
	}

	public void setPaymentPhone(String paymentPhone) {
		this.paymentPhone = ImportOrderUtils.getStringNotZeroForCellValue(paymentPhone);
	}

	public String getPaymentName() {
		return paymentName;
	}

	public void setPaymentName(String paymentName) {
		this.paymentName = paymentName;
	}

	public String getPaymentCardNumber() {
		return paymentCardNumber;
	}

	public void setPaymentCardNumber(String paymentCardNumber) {
		this.paymentCardNumber = ImportOrderUtils.getStringNotZeroForCellValue(paymentCardNumber);
	}

	public String getSellerRemark() {
		return sellerRemark;
	}

	public void setSellerRemark(String sellerRemark) {
		this.sellerRemark = sellerRemark;
	}

	public String getBuyerMessage() {
		return buyerMessage;
	}

	public void setBuyerMessage(String buyerMessage) {
		this.buyerMessage = buyerMessage;
	}

	public String getInvoiceInfo() {
		return invoiceInfo;
	}

	public void setInvoiceInfo(String invoiceInfo) {
		this.invoiceInfo = invoiceInfo;
	}

	public Integer getOprateStatus() {
		return oprateStatus;
	}

	public void setOprateStatus(Integer oprateStatus) {
		this.oprateStatus = oprateStatus;
	}

	public Integer getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Integer isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Integer getIsCreate() {
		return isCreate;
	}

	public void setIsCreate(Integer isCreate) {
		this.isCreate = isCreate;
	}

	@JsonSerialize(using = DateJsonSerialUtils.class)
	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = ImportOrderUtils.getStringNotZeroForCellValue(postCode);
	}

	public Double getLogisticsCost() {
		return logisticsCost;
	}

	public void setLogisticsCost(Double logisticsCost) {
		this.logisticsCost = logisticsCost;
	}
	
	public void setLogisticsCostStr(String logisticsCostStr) {
		if (!Strings.isNullOrEmpty(logisticsCostStr)) {
			this.logisticsCost = Double.valueOf(logisticsCostStr);
		}
	}

	public String getFinanceRemark() {
		return financeRemark;
	}

	public void setFinanceRemark(String financeRemark) {
		this.financeRemark = financeRemark;
	}

	public String getLogisticsTypeCode() {
		return logisticsTypeCode;
	}

	public void setLogisticsTypeCode(String logisticsTypeCode) {
		this.logisticsTypeCode = logisticsTypeCode;
	}

	public String getLogisticsTypeName() {
		return logisticsTypeName;
	}

	public void setLogisticsTypeName(String logisticsTypeName) {
		this.logisticsTypeName = logisticsTypeName;
	}

	public String getReceiverTelephone() {
		return receiverTelephone;
	}

	public void setReceiverTelephone(String receiverTelephone) {
		this.receiverTelephone = StringUtils.isBlank(receiverTelephone) ? null : receiverTelephone.replace(".00", "").replaceAll("'", "").replaceAll("'", "");
	}
	
	public Boolean getIsPart() {
		return isPart;
	}

	public void setIsPart(Boolean isPart) {
		this.isPart = isPart;
	}

	@Override
	public String toString() {
		return "TaoBaoOrder [buyerAccount=" + buyerAccount + ", shopName=" + shopName + ", orderNo=" + orderNo
				+ ", orderTotal=" + orderTotal + ", orderStatus=" + orderStatus + ", paymentNo=" + paymentNo
				+ ", email=" + email + "]";
	}
	/**
	 * 描述：通过属性与值得映射map设置订单属性值
	 * 2016年5月16日
	 * @param fieldValueMap 属性与值的映射map
	 * @param rowNum 
	 */
	public void parseOrderDataFromFieldAndValueMap(Map<String,String> fieldValueMap, int rowNum,Integer tempalateType){
		if (!fieldValueMap.isEmpty()) {
			Set<String> fieldSet = fieldValueMap.keySet();
			for (String field : fieldSet) {
				if (!Strings.isNullOrEmpty(field) && !field.equals("notRegister")) {
					String methodName = "set" + field.substring(0, 1).toUpperCase() + field.substring(1);
					Method setFieldMethod = null;
					try {
						setFieldMethod = super.getClass().getMethod(methodName + "Str", String.class);
					} catch (Exception e) {
						// Logger.info(e.toString());
					}
					if(tempalateType != null && tempalateType == 5){
						if("address".equals(field) ){
							fieldValueMap.put("address",AddressUtils.dealAddress(fieldValueMap.get("address"))); 
						}
					}
					try {
						if (setFieldMethod == null) {
							setFieldMethod = super.getClass().getMethod(methodName, String.class);
						}
						String val = fieldValueMap.get(field);
						if (setFieldMethod != null && StringUtils.isNotEmpty(val)) {
							setFieldMethod.invoke(this, val);
						} else {
							Logger.info("文件第" + rowNum + "行，获取属性[" + field + "]的set方法失败");
						}
					} catch (Exception e) {
						Logger.error("文件第" + rowNum + "行，设置订单属性[" + field + "]的值失败！");
					}
				}
			}
		}
	}
	

}
