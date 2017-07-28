package dto.marketing;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import util.sales.DateUtils;
import util.sales.MarketingOrderStatus;
import util.sales.PriceFormatUtil;

public class ViewMarketingOrderDto {
	private Integer id;
	private String marketingOrderNo;
	private String salesOrderNo;
	private Double totalAmount;//营销单商品金额，不包含运费
	private Integer status;//状态
	private String email;//分销商
	private String nickName;//分销商昵称
	private Integer disMode;//分销模式:1、电商，2、经销商，3、KA直营，4、进口专营
	private Integer distributorType;//分销商类型（1：普通 2：合营 3：内部）
	private String salesman;//业务员
	private Integer provinceId;
	private Integer cityId;
	private Integer regionId;
	private String provinceName;
	private String cityName;
	private String regionName;
	private String addressDetail;
	private String receiver;//收货人
	private String receiverTel;//收货人电话
	private String receiverPostcode;//收货人邮编
	private String logisticsMode;//物流方式
	private String logisticsTypeCode;//物流方式代码
	private Double bbcPostage;
	private String orderer;
	private String ordererTel;
	private String ordererPostcode;
	private String businessRemark;
	private String createUser;
	private Date createDate;
	private String lastUpdateUser;
	private Date lastUpdateDate;
	private List<ViewMarketingOrderDetail> detailList;
	private String branchName;//组织架构节点名称
	private Double tAWPrice; // 总的到仓价

	public Double gettAWPrice() {
		// 营销单主信息有总到仓价就取本身的，没有的话，就从详情那取
		if (tAWPrice==null) {
			// 详情没有
			if (CollectionUtils.isEmpty(detailList)) {
				return 0.00;
			}
			// 从详情那拿到到仓价
			BigDecimal bd = detailList.stream()
					.map(detail -> {
						if (detail.getArriveWarePrice()==null || detail.getQty()==null) {
							return new BigDecimal(0);
						}
						return new BigDecimal(detail.getArriveWarePrice()).multiply(new BigDecimal(detail.getQty()));
					}).reduce(new BigDecimal(0.00), (x, y) -> x.add(y));
			tAWPrice = PriceFormatUtil.toFix2(bd);
		}
		return tAWPrice;
	}

	public void settAWPrice(Double tAWPrice) {
		this.tAWPrice = tAWPrice;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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
	
	public String getDistributorTypeStr(){
		switch (distributorType) {
		case 1:
			return "普通分销商";
		case 2:
			return "合营分销商";
		case 3:
			return "内部分销商";
		default:
			return "";
		}
	}

	public String getStatusMsg() {
		switch (status) {
		case MarketingOrderStatus.WAITING_AUDIT_FIRSTLY:
			return "待初审";
		case MarketingOrderStatus.WAITING_AUDIT_SECONDLY:
			return "待复审";
		case MarketingOrderStatus.AUDIT_PASSED:
			return "审核通过";
		case MarketingOrderStatus.AUDIT_NOT_PASSED:
			return "审核不通过";
		default:
			return "";
		}
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
	public String getCreateDateStr() {
		return DateUtils.date2string(createDate, DateUtils.FORMAT_FULL_DATETIME);
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
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

	public List<ViewMarketingOrderDetail> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<ViewMarketingOrderDetail> detailList) {
		this.detailList = detailList;
	}

}
