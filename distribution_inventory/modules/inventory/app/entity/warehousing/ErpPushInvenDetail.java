package entity.warehousing;

import java.util.Date;

/**
 * erp推送库存明细实体
 * @author luwj
 */
public class ErpPushInvenDetail implements java.io.Serializable{

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer pushId;//erp推送批次id

    private String sku;//商品编码

    private Integer warehouseId;//仓库id

    private Integer stock;//入库数量

    private Double costPrice;//产品成本价

    /**
     * 1.盘点，2.采购入库，3.其他入库，0.库存同步
     * （
     *  盘点-盘盈盘亏结果，
     *  采购入库—采购入库数量，
     *  其他入库-其他入库数量，
     *  库存同步—商品在该仓库的全部库存数量（重新初始化库存数量）
     * ）';
     */
    private Integer type;

    private Date created;//创建时间

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPushId() {
        return pushId;
    }

    public void setPushId(Integer pushId) {
        this.pushId = pushId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Double costPrice) {
        this.costPrice = costPrice;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}