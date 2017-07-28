package entity.product;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import util.product.DateUtils;

/**
 * KA锁库详情实体
 * @author zbc
 * 2017年4月18日 下午3:03:18
 */
@ApiModel("KA锁库详情实体")
public class InventoryLockDetail implements Serializable {

	private static final long serialVersionUID = -5814599610679491010L;

	/**
     * 主键
     */
    private Integer id;

    @ApiModelProperty("商品编码")
    private String sku;

    @ApiModelProperty("到期日期")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd",timezone = "GMT+8")
    private Date expirationDate;

    @ApiModelProperty("锁定数量")
    private Integer lockNum;

    @ApiModelProperty("仓库id")
    private Integer warehouseId;

    @ApiModelProperty("仓库名称")
    private String warehouseName;

    @ApiModelProperty("商品名称")
    private String title;

    @ApiModelProperty("国际条形码")
    private String interBarCode;

    @ApiModelProperty("剩余数量")
    private Integer leftNum;

    @ApiModelProperty("KA锁库id")
    private Integer lockId;

    @ApiModelProperty("锁库号")
    private String lockNo;
    
	public InventoryLockDetail() {
	}

	/**
	 * @param sku
	 * @param expirationDate
	 * @param lockNum
	 * @param warehouseId
	 * @param warehouseName
	 * @param title
	 * @param interBarCode
	 * @param leftNum
	 * @param lockId
	 * @param lockNo
	 */
	public InventoryLockDetail(String sku, Date expirationDate, Integer lockNum, Integer warehouseId,
			String warehouseName, String title, String interBarCode, Integer leftNum, Integer lockId, String lockNo) {
		super();
		this.sku = sku;
		this.expirationDate = expirationDate;
		this.lockNum = lockNum;
		this.warehouseId = warehouseId;
		this.warehouseName = warehouseName;
		this.title = title;
		this.interBarCode = interBarCode;
		this.leftNum = leftNum;
		this.lockId = lockId;
		this.lockNo = lockNo;
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

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
    
    public String  expirationDateStr(){
    	return DateUtils.date2string(expirationDate, DateUtils.FORMAT_DATE_PAGE);
    }

    public Integer getLockNum() {
        return lockNum;
    }

    public void setLockNum(Integer lockNum) {
        this.lockNum = lockNum;
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

    public Integer getLeftNum() {
        return leftNum;
    }

    public void setLeftNum(Integer leftNum) {
        this.leftNum = leftNum;
    }

    public Integer getLockId() {
        return lockId;
    }

    public void setLockId(Integer lockId) {
        this.lockId = lockId;
    }

    public String getLockNo() {
        return lockNo;
    }

    public void setLockNo(String lockNo) {
        this.lockNo = lockNo;
    }
}