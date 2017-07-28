package entity.discart;

import java.io.Serializable;
import java.util.Date;

/**
 * 分销商购物车单元表实体
 */
public class DisCartItem implements Serializable {

	private static final long serialVersionUID = -3214929620697557267L;

	private Integer id;// 主键id

	private Integer cartId;// 购物车主表id

	private String csku;// 商品csku

	private Integer iqty;// 商品数量

	private Integer warehouseId;// 仓库id

	private String warehouseName;// 仓库名称

	private String title;// 商品标题

	private String publicImg;// 商品图片

	private Integer isOrder;// 是否提交订单(0:未提交订单，1：已提交订单)

	private Integer isRemove;// 是否从购物车删除商品（0：未删除商品，1：已删除商品）

	private Date createTime;// 商品添加时间

	private Boolean bselected;// 是否选中，数据库中默认为true即选中

	private Integer batchnum;// 起批量

	private Integer categoryId;// 所属类目ID

	private String categoryName;// 类目名称

	private String interBarCode;

	public String getInterBarCode() {
		return interBarCode;
	}

	public void setInterBarCode(String interBarCode) {
		this.interBarCode = interBarCode;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Integer getBatchnum() {
		return batchnum;
	}

	public void setBatchnum(Integer batchnum) {
		this.batchnum = batchnum;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCartId() {
		return cartId;
	}

	public void setCartId(Integer cartId) {
		this.cartId = cartId;
	}

	public String getCsku() {
		return csku;
	}

	public void setCsku(String csku) {
		this.csku = csku;
	}

	public Integer getIqty() {
		return iqty;
	}

	public void setIqty(Integer iqty) {
		this.iqty = iqty;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPublicImg() {
		return publicImg;
	}

	public void setPublicImg(String publicImg) {
		this.publicImg = publicImg;
	}

	public Integer getIsOrder() {
		return isOrder;
	}

	public void setIsOrder(Integer isOrder) {
		this.isOrder = isOrder;
	}

	public Integer getIsRemove() {
		return isRemove;
	}

	public void setIsRemove(Integer isRemove) {
		this.isRemove = isRemove;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Boolean getBselected() {
		return bselected;
	}

	public void setBselected(Boolean bselected) {
		this.bselected = bselected;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}
}