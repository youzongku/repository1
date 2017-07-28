package dto.product;

import java.io.Serializable;

/**
 * KA锁库数量实体
 * @author zbc
 * 2017年4月20日 下午4:54:59
 */
public class InvetoryLockNumDto implements Serializable{

	private static final long serialVersionUID = 2807270516613815387L;
	/**
	 * 仓库id
	 */
	private Integer warehouseId;
	/**
	 * 商品编码
	 */
	private String sku;
	/**
	 * 库存减量
	 */
	private Integer substock;
	
	/**
	 * 到期日期
	 */
	private String expirationDate;
	public String getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	public Integer getWarehouseId() {
		return warehouseId;
	}
	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public Integer getSubstock() {
		return substock;
	}
	public void setSubstock(Integer substock) {
		this.substock = substock;
	}
}
