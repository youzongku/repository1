package dto.product;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class ContractQuotationsDto {
	
	private Integer id;//

	@ApiModelProperty("合同号")
    private String contractNo;//合同编号

	@ApiModelProperty("商品编码")
    private String sku;//SKU
	
	@ApiModelProperty("商品分类")
	private Integer categoryId;

	@ApiModelProperty("商品标题")
    private String title;//商品标题

	@ApiModelProperty("图片地址")
    private String imgUrl;//图片地址

	@ApiModelProperty("库id")
    private Integer warehouseId;//仓库id

	@ApiModelProperty("仓库名称")
    private String warehouseName;//仓库名称

	@ApiModelProperty("采购价")
    private Double purchasePrice;//采购价

	@ApiModelProperty("合同价")
    private Double contractPrice;//合同价

//    private Double arriveWarePrice;//到仓价

//    private Boolean isDiscount;//是否折扣(预留字段)
//
//    private Double discount;//折扣值(预留字段)

    @ApiModelProperty("合同报价开始")
    private String contractStart;//合同报价开始

    @ApiModelProperty("合同报价结束")
    private String contractEnd;//合同报价结束

    @ApiModelProperty("状态")
    private Integer status;//状态

//    private Date createTime;//创建时间
//
//    private Date updateTime;//更新时间

//    private String createUser;//创建人
    @ApiModelProperty("国际条码")
    private String interBarCode;//国际条码
    
    @ApiModelProperty("分销商账号")
    private String account;//分销商账号

    @ApiModelProperty("名称")
	private String distributionName;//名称

    @ApiModelProperty("业务员")
	private String bussinessErp;//业务员

    @ApiModelProperty("分销商类型")
	private Integer distributionMode;//分销商类型

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

	public String getContractStart() {
		return contractStart;
	}

	public void setContractStart(String contractStart) {
		this.contractStart = contractStart;
	}

	public String getContractEnd() {
		return contractEnd;
	}

	public void setContractEnd(String contractEnd) {
		this.contractEnd = contractEnd;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getInterBarCode() {
		return interBarCode;
	}

	public void setInterBarCode(String interBarCode) {
		this.interBarCode = interBarCode;
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
