package entity.sales.hb;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import dto.sales.SaleBeforeCombineDto;
import util.sales.DismemberModeTypeUtil;
import util.sales.SalesCombinationStatus;

public class SalesHBDelivery {
    private Integer id;
    // 合并单单号 
    private String salesHbNo;
    // 发货单的订单金额总计，包含运费
    private Double totalAmountPostageInclusive;
    // 总的运费
    private Double totalBbcPostage;
    // 合并之前总的运费
    private Double originalTotalBbcPostage;
    
    // 分销商
    private String account;
    
    private Integer warehouseId;

    private String warehouseName;
    // 合并的发货单数量
    private Integer qties;
    // 状态
    private Integer status;

    private Integer consumerType;

    private Integer distributionMode;
    // 业务员
    private String salesman;
    // 名称
    private String nickName;
    // 收货人
    private String receiver;
    // 收货人电话
    private String telephone;
    // 收货人地址：广东省深圳市平湖街道华南城1号交易广场
    private String address;
    // 物流信息：德邦
    private String logisticsInformation;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    private String createUser;
    
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date lastUpdateTime;
    
    private String lastUpdateUser;
    
    //是否需要开发票
    private Boolean isNeedInvoice;
    
    public SalesHBDelivery(String salesHbNo,String receiver,SaleBeforeCombineDto combine,Integer status,String createUser) {
		this.salesHbNo = salesHbNo;
		this.totalBbcPostage = combine.getBbcPostage();
		this.account = combine.getAccount();
		this.warehouseId = combine.getWarehouseId();
		this.warehouseName = combine.getWarehouseName();
		this.qties = combine.getTotalQty();
		this.status = status;
		this.consumerType = combine.getDistributorType();
		this.distributionMode = combine.getDisMode();
		this.salesman = combine.getSaleMan();
		this.nickName = combine.getNickName();
		this.receiver = receiver;
		this.telephone = combine.getTel();
		this.address = combine.getAddress();
		this.logisticsInformation = combine.getLogisticsMode();
		this.createUser = createUser;
		this.totalAmountPostageInclusive = combine.getOrderTotalAmount();
		this.originalTotalBbcPostage = combine.getOriginalTotalBbcPostage();
	}
    
	public Boolean getIsNeedInvoice() {
		return isNeedInvoice;
	}

	public void setIsNeedInvoice(Boolean isNeedInvoice) {
		this.isNeedInvoice = isNeedInvoice;
	}

	public Double getOriginalTotalBbcPostage() {
		return originalTotalBbcPostage;
	}

	public void setOriginalTotalBbcPostage(Double originalTotalBbcPostage) {
		this.originalTotalBbcPostage = originalTotalBbcPostage;
	}

	public SalesHBDelivery() {
		super();
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

    public Double getTotalAmountPostageInclusive() {
		return totalAmountPostageInclusive;
	}

	public void setTotalAmountPostageInclusive(Double totalAmountPostageInclusive) {
		this.totalAmountPostageInclusive = totalAmountPostageInclusive;
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
        return SalesCombinationStatus.getStatusMsg(status);
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
    
}