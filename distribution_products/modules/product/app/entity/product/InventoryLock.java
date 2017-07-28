package entity.product;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * @author zbc
 * 2017年4月18日 下午2:47:17
 */
@ApiModel(value="KA锁库实体")
public class InventoryLock implements Serializable{

	private static final long serialVersionUID = -3515130161645953960L;
	
	//状态常量
	public static final int BE_RELEASE = 0;//已释放
	public static final int CAN_BE_USE = 1;//可使用
	
	/**
     * 主键
     */
    private Integer id;

    @ApiModelProperty("分销商账号")
    private String account;
    
    @ApiModelProperty("锁库号")
    private String lockNo;
    
    @ApiModelProperty("分销商名称")
    private String nickName;

    @ApiModelProperty("业务员")
    private String saleMan;

    @ApiModelProperty("创建人")
    private String createUser;

    @ApiModelProperty("是否剩余库存(0:否;1:是)")
    private Integer isLeftStock;

    @ApiModelProperty("锁库时间")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createDate;

    @ApiModelProperty("更新时间")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateDate;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("预计发货时间")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd",timezone = "GMT+8")
    private Date estimatedShippingTime;

    @ApiModelProperty("锁库状态(0：已释放, 1：可使用)")
    private Integer status;

    public InventoryLock(){
    	
    }
    /**
	 * @param account
	 * @param lockNo
	 * @param nickName
	 * @param saleMan
	 * @param createUser
	 * @param createDate
	 * @param remark
	 * @param estimatedShippingTime
	 * @param status
	 */
	public InventoryLock(String account, String lockNo, String nickName, String saleMan,
			String createUser, String remark,Date estimatedShippingTime) {
		super();
		this.account = account;
		this.lockNo = lockNo;
		this.nickName = nickName;
		this.saleMan = saleMan;
		this.createUser = createUser;
		this.remark = remark;
		this.estimatedShippingTime = estimatedShippingTime;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getLockNo() {
        return lockNo;
    }

    public void setLockNo(String lockNo) {
        this.lockNo = lockNo;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSaleMan() {
        return saleMan;
    }

    public void setSaleMan(String saleMan) {
        this.saleMan = saleMan;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Integer getIsLeftStock() {
        return isLeftStock;
    }

    public void setIsLeftStock(Integer isLeftStock) {
        this.isLeftStock = isLeftStock;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getEstimatedShippingTime() {
        return estimatedShippingTime;
    }

    public void setEstimatedShippingTime(Date estimatedShippingTime) {
        this.estimatedShippingTime = estimatedShippingTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}