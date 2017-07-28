package entity.product;

import java.util.Date;

public class Warehouse {
    private Integer id;

    private String warehouseNo;

    private String warehouseName;

    private Date created;

    private Integer status;

    private Date lastUpdated;

    private String province;

    private String city;

    private String area;

    private String street;

    private String remarks;

    private String batchNo;

    private String type;

    private Double dealFee;

    private Double dealSkuFee;

    private Double dealItemFee;

    private Double otherFee;

    private Boolean isOptFeeActived;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWarehouseNo() {
        return warehouseNo;
    }

    public void setWarehouseNo(String warehouseNo) {
        this.warehouseNo = warehouseNo;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
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

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getDealFee() {
        return dealFee;
    }

    public void setDealFee(Double dealFee) {
        this.dealFee = dealFee;
    }

    public Double getDealSkuFee() {
        return dealSkuFee;
    }

    public void setDealSkuFee(Double dealSkuFee) {
        this.dealSkuFee = dealSkuFee;
    }

    public Double getDealItemFee() {
        return dealItemFee;
    }

    public void setDealItemFee(Double dealItemFee) {
        this.dealItemFee = dealItemFee;
    }

    public Double getOtherFee() {
        return otherFee;
    }

    public void setOtherFee(Double otherFee) {
        this.otherFee = otherFee;
    }

    public Boolean getIsOptFeeActived() {
        return isOptFeeActived;
    }

    public void setIsOptFeeActived(Boolean isOptFeeActived) {
        this.isOptFeeActived = isOptFeeActived;
    }
}