package entity.product;

import java.io.Serializable;
import java.util.Date;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import dto.product.inventory.CloudLockPro;
import dto.product.inventory.SaleLockDetailDto;

/**
 * KA锁库订单使用记录表实体
 * @author zbc
 * 2017年4月18日 下午2:36:32
 */
@ApiModel(value=" KA锁库订单使用记录表实体")
public class InventoryOrder implements Serializable{

	private static final long serialVersionUID = -5435927335137441399L;

	private Integer id;

    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("扣除数量")
    private Integer qty;
    
    @ApiModelProperty("商品编号")
    private String sku;

    @ApiModelProperty("仓库id")
    private Integer warehouseId;

    @ApiModelProperty("仓库名称")
    private String warehouseName;

    @ApiModelProperty("商品名称")
    private String title;

    @ApiModelProperty("国际条形码")
    private String interBarCode;

    @ApiModelProperty("锁库详情id")
    private Integer detailId;

    @ApiModelProperty("锁库号")
    private String lockNo;

    /**
     * 锁定状态(-1:释放,0:临时锁定，1，永久锁定) 预留字段，目前不做释放操作
     */
    private Integer status;

    /**
     * 分销商账号
     */
    private String account;
    
    /**
     * 创建时间
     */
    private Date createDate;
    
    /**
     * 到期时间
     */
    private Date expirationDate;
    
	public InventoryOrder(){
    	
    }
    
    /**
	 * @param orderNo
	 * @param qty
	 * @param sku
	 * @param warehouseId
	 * @param warehouseName
	 * @param title
	 * @param interBarCode
	 * @param detailId
	 * @param lockNo
	 * @param status
	 * @param account
	 */
	public InventoryOrder(Date createDate,Integer num,String orderNo,InventoryLockDetail lockDe,CloudLockPro pro,
			String account) {
		super();
		this.orderNo = orderNo;
		this.qty = num;
		this.sku = pro.getSku();
		this.warehouseId = pro.getWarehouseId();
		this.warehouseName = pro.getWarehouseName();
		this.title = pro.getProductTitle();
		this.interBarCode = lockDe.getInterBarCode();
		this.detailId = lockDe.getId();
		this.lockNo = lockDe.getLockNo();
		this.account = account;
		this.createDate = createDate;
		this.expirationDate = lockDe.getExpirationDate();
	}
	
	/**
	 * @param now
	 * @param num
	 * @param orderNo2
	 * @param lockDe
	 * @param pro
	 * @param account2
	 */
	public InventoryOrder(Date now, Integer num, String orderNo, InventoryLockDetail lockDe, SaleLockDetailDto pro,
			String account) {
		super();
		this.orderNo = orderNo;
		this.qty = num;
		this.sku = pro.getSku();
		this.warehouseId = lockDe.getWarehouseId();
		this.warehouseName = lockDe.getWarehouseName();
		this.title = pro.getProductTitle();
		this.interBarCode = lockDe.getInterBarCode();
		this.detailId = lockDe.getId();
		this.lockNo = lockDe.getLockNo();
		this.account = account;
		this.createDate = now;
		this.expirationDate = lockDe.getExpirationDate();
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
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

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
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

    public Integer getDetailId() {
        return detailId;
    }

    public void setDetailId(Integer detailId) {
        this.detailId = detailId;
    }

    public String getLockNo() {
        return lockNo;
    }

    public void setLockNo(String lockNo) {
        this.lockNo = lockNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}