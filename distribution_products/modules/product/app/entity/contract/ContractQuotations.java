package entity.contract;

import java.util.Date;

import dto.product.ContractQuotationsProDto;
import dto.product.ProductLite;

/**
 * 合同报价
 * @author Administrator
 *
 */
public class ContractQuotations {
	
	/**
	 * 1：未开始    2：已开始   3：已结束
	 */
	public static final int  HAVE_NOT_STARTED = 1;
	
	public static final int  HAS_BEGUN = 2;
	
	public static final int FINISHED = 3;
	
    private Integer id;//

    private String contractNo;//合同编号

    private String sku;//SKU
    
    private Integer categoryId;

    private String title;//商品标题

    private String imgUrl;//图片地址

    private Integer warehouseId;//仓库id

    private String warehouseName;//仓库名称

    private Double purchasePrice;//采购价

    private Double contractPrice;//合同价

    private Double arriveWarePrice;//到仓价

    private Boolean isDiscount;//是否折扣(预留字段)

    private Double discount;//折扣值(预留字段)

    private Date contractStart;//合同报价开始

    private Date contractEnd;//合同报价结束

    private Integer status;//状态    1：未开始    2：已开始   3：已结束

    private Date createTime;//创建时间

    private Date updateTime;//更新时间

    private String createUser;//创建人
    
    private String interBarCode;//国际条码
    
    private String account;//分销商账号

    private String distributionName;//名称

    private String bussinessErp;//业务员

    private Integer distributionMode;//分销商类型

    public ContractQuotations(){
    	
    }

	public ContractQuotations(Contract con, ContractQuotationsProDto pro,ProductLite product,Date createTime,
			String operator, int status) {
		this.contractNo = con.getContractNo();
		this.sku = pro.getSku();
		this.categoryId = pro.getCategoryId();
		this.warehouseId = pro.getWarehouseId();
		this.contractPrice = pro.getContractPrice();
		this.contractStart = con.getContractStart();
		this.contractEnd = con.getContractEnd();
		this.distributionMode = con.getDistributionMode();
		this.createTime = createTime;
		this.createUser = operator;
		this.status = status;
		this.title = product.getCtitle();
		this.purchasePrice = product.getDisPrice();
		this.warehouseName = product.getWarehouseName();
		this.imgUrl = product.getImageUrl();
		this.interBarCode = product.getInterBarCode();
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getInterBarCode() {
		return interBarCode;
	}

	public void setInterBarCode(String interBarCode) {
		this.interBarCode = interBarCode;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
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

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Double getContractPrice() {
        return contractPrice;
    }

    public void setContractPrice(Double contractPrice) {
        this.contractPrice = contractPrice;
    }

    public Double getArriveWarePrice() {
        return arriveWarePrice;
    }

    public void setArriveWarePrice(Double arriveWarePrice) {
        this.arriveWarePrice = arriveWarePrice;
    }

    public Boolean getIsDiscount() {
        return isDiscount;
    }

    public void setIsDiscount(Boolean isDiscount) {
        this.isDiscount = isDiscount;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Date getContractStart() {
        return contractStart;
    }

    public void setContractStart(Date contractStart) {
        this.contractStart = contractStart;
    }

    public Date getContractEnd() {
        return contractEnd;
    }

    public void setContractEnd(Date contractEnd) {
        this.contractEnd = contractEnd;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getDistributionName() {
        return distributionName;
    }

    public void setDistributionName(String distributionName) {
        this.distributionName = distributionName;
    }

    public String getBussinessErp() {
        return bussinessErp;
    }

    public void setBussinessErp(String bussinessErp) {
        this.bussinessErp = bussinessErp;
    }

    public Integer getDistributionMode() {
        return distributionMode;
    }

    public void setDistributionMode(Integer distributionMode) {
        this.distributionMode = distributionMode;
    }


}