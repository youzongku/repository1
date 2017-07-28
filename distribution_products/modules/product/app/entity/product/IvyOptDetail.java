package entity.product;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * @author zbc
 * 2017年5月9日 下午3:21:39
 */
public class IvyOptDetail implements Serializable{

	private static final long serialVersionUID = -6607039947174369712L;

	/**
	 * 主键
	 */
	private Integer id;

    /**
     * 商品编码
     */
	@ApiModelProperty("商品编码")
    private String sku;

    /**
     * 商品名称
     */
	@ApiModelProperty("商品名称")
    private String title;

    /**
     * 国际条形码
     */
	@ApiModelProperty("国际条形码")
    private String interBarCode;

    /**
     * 仓库id
     */
	@ApiModelProperty("仓库id")
    private Integer warehouseId;

    /**
     * 仓库名称
     */
	@ApiModelProperty("仓库名称")
    private String warehouseName;

    /**
     * 到期日期
     */
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd",timezone = "GMT+8")
    @ApiModelProperty("到期日期")
    private Date expirationDate;

    /**
     * 释放数量
     */
    @ApiModelProperty("释放数量")
    private Integer num;

    /**
     * 日志表id
     */
    private Integer oprecordId;

    public IvyOptDetail(InventoryLockDetail detial,Integer num) {
		super();
		this.sku = detial.getSku();
		this.title = detial.getTitle();
		this.interBarCode = detial.getInterBarCode();
		this.warehouseId = detial.getWarehouseId();
		this.warehouseName = detial.getWarehouseName();
		this.expirationDate = detial.getExpirationDate();
		this.num = num;
	}
    public IvyOptDetail(){
    	
    }

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getInterBarCode() {
        return interBarCode;
    }

    public void setInterBarCode(String interBarCode) {
        this.interBarCode = interBarCode;
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

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getOprecordId() {
        return oprecordId;
    }

    public void setOprecordId(Integer oprecordId) {
        this.oprecordId = oprecordId;
    }
}