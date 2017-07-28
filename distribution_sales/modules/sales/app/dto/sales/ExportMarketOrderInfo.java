package dto.sales;

import java.math.BigDecimal;
import java.util.Date;

import entity.marketing.MarketingOrderDetail;
import util.sales.DateUtils;

/**
 * @author zbc 2017年3月22日 上午9:19:51
 */
public class ExportMarketOrderInfo extends MarketingOrderDetail {
	private String marketingOrderNo;
	private String salesOrderNo;
	private Double totalAmount;
	private Integer status;
	private String email;
	private String nickName;
	private Integer disMode;
	private Integer distributorType;
	private String salesman;
	private Integer provinceId;
	private Integer cityId;
	private Integer regionId;
	private String provinceName;
	private String cityName;
	private String regionName;
	private String addressDetail;
	private String receiver;
	private String receiverTel;
	private String receiverPostcode;
	private String logisticsMode;
	private String logisticsTypeCode;
	private Double bbcPostage;
	private String orderer;
	private String ordererTel;
	private String ordererPostcode;
	private String businessRemark;
	private String createUser;
	private Date createDate;
	private String lastUpdateUser;
	private Date lastUpdateDate;
	private String branchName;// 分部名称
	private String statusMsg;
	private String distributorTypeStr;
	private Integer totalQty;
	public Integer getTotalQty() {
		return totalQty;
	}
	public void setTotalQty(Integer totalQty) {
		this.totalQty = totalQty;
	}
	public void setStatusMsg(String statusMsg) {
		this.statusMsg = statusMsg;
	}
	public void setDistributorTypeStr(String distributorTypeStr) {
		this.distributorTypeStr = distributorTypeStr;
	}
	public String getMarketingOrderNo() {
		return marketingOrderNo;
	}
	public void setMarketingOrderNo(String marketingOrderNo) {
		this.marketingOrderNo = marketingOrderNo;
	}
	public String getSalesOrderNo() {
		return salesOrderNo;
	}
	public void setSalesOrderNo(String salesOrderNo) {
		this.salesOrderNo = salesOrderNo;
	}
	public Double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public Integer getDisMode() {
		return disMode;
	}
	public void setDisMode(Integer disMode) {
		this.disMode = disMode;
	}
	public Integer getDistributorType() {
		return distributorType;
	}
	public void setDistributorType(Integer distributorType) {
		this.distributorType = distributorType;
	}
	public String getSalesman() {
		return salesman;
	}
	public void setSalesman(String salesman) {
		this.salesman = salesman;
	}
	public Integer getProvinceId() {
		return provinceId;
	}
	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
	}
	public Integer getCityId() {
		return cityId;
	}
	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}
	public Integer getRegionId() {
		return regionId;
	}
	public void setRegionId(Integer regionId) {
		this.regionId = regionId;
	}
	public String getProvinceName() {
		return provinceName;
	}
	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getRegionName() {
		return regionName;
	}
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
	public String getAddressDetail() {
		return addressDetail;
	}
	public void setAddressDetail(String addressDetail) {
		this.addressDetail = addressDetail;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public String getReceiverTel() {
		return receiverTel;
	}
	public void setReceiverTel(String receiverTel) {
		this.receiverTel = receiverTel;
	}
	public String getReceiverPostcode() {
		return receiverPostcode;
	}
	public void setReceiverPostcode(String receiverPostcode) {
		this.receiverPostcode = receiverPostcode;
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
	public Double getBbcPostage() {
		return bbcPostage;
	}
	public void setBbcPostage(Double bbcPostage) {
		this.bbcPostage = bbcPostage;
	}
	public String getOrderer() {
		return orderer;
	}
	public void setOrderer(String orderer) {
		this.orderer = orderer;
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
	public String getBusinessRemark() {
		return businessRemark;
	}
	public void setBusinessRemark(String businessRemark) {
		this.businessRemark = businessRemark;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public String getLastUpdateUser() {
		return lastUpdateUser;
	}
	public void setLastUpdateUser(String lastUpdateUser) {
		this.lastUpdateUser = lastUpdateUser;
	}
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}
	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getStatusMsg() {
		return statusMsg;
	}
	public String getCreateDateStr() {
		return DateUtils.date2string(createDate, DateUtils.FORMAT_LOCAL_DATE);
	}
	public String getDistributorTypeStr() {
		return distributorTypeStr;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public BigDecimal getArriveTotal() {
		return (getQty() != null && getArriveWarePrice() != null)
				? new BigDecimal(getQty())
						.multiply(new BigDecimal(getArriveWarePrice()))
						.setScale(2, BigDecimal.ROUND_HALF_UP)
				: null;
	}
}
