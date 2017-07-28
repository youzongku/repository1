package dto.sales.hb;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import entity.sales.hb.SalesHBDeliveryDetail;
import util.sales.DismemberModeTypeUtil;

public class SalesHBDeliveryDto {
	private Integer id;

	private String salesHbNo;

	private Double totalBbcPostage;

	private String account;

	private Integer warehouseId;

	private String warehouseName;

	private Integer qties;

	private Integer status;

	private Integer consumerType;

	private Integer distributionMode;

	private String salesman;

	private String nickName;

	private String receiver;

	private String telephone;

	private String address;

	private String logisticsInformation;

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date createTime;

	private String createUser;
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date lastUpdateTime;
    
    private String lastUpdateUser;

	private List<SalesHBDeliveryDetail> detailList;

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getLastUpdateUser() {
		return lastUpdateUser;
	}

	public void setLastUpdateUser(String lastUpdateUser) {
		this.lastUpdateUser = lastUpdateUser;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSalesHbNo() {
		return salesHbNo;
	}

	public void setSalesHbNo(String salesHbNo) {
		this.salesHbNo = salesHbNo;
	}

	public Double getTotalBbcPostage() {
		return totalBbcPostage;
	}

	public void setTotalBbcPostage(Double totalBbcPostage) {
		this.totalBbcPostage = totalBbcPostage;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
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

	public Integer getQties() {
		return qties;
	}

	public void setQties(Integer qties) {
		this.qties = qties;
	}

	public Integer getStatus() {
		return status;
	}
	
	public String getStatusMsg() {
    	String[] msgs = {"", "待客服审核", "待财务审核", "已关闭", "待发货"};
        return msgs[status];
    }

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getConsumerType() {
		return consumerType;
	}
	
	public String getConsumerTypeMsg() {
        return DismemberModeTypeUtil.getTypeName(consumerType);
    }

	public void setConsumerType(Integer consumerType) {
		this.consumerType = consumerType;
	}

	public Integer getDistributionMode() {
		return distributionMode;
	}
	
	public String getDistributionModeMsg() {
        return DismemberModeTypeUtil.getModeName(distributionMode);
    }

	public void setDistributionMode(Integer distributionMode) {
		this.distributionMode = distributionMode;
	}

	public String getSalesman() {
		return salesman;
	}

	public void setSalesman(String salesman) {
		this.salesman = salesman;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLogisticsInformation() {
		return logisticsInformation;
	}

	public void setLogisticsInformation(String logisticsInformation) {
		this.logisticsInformation = logisticsInformation;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public List<SalesHBDeliveryDetail> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<SalesHBDeliveryDetail> detailList) {
		this.detailList = detailList;
	}

}
