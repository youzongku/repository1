package dto.product_inventory;

import java.io.Serializable;
import java.util.List;

/**
 * Erp到期日志库存查询实体
 * @author zbc
 * 2017年1月12日 下午5:18:09
 */
public class ErpProExpiration implements Serializable{

	private static final long serialVersionUID = -6423388977875010581L;

	/**
	 * id
	 */
	private Integer _id;
	/**
	 * 商品编号
	 */
	private String sku;
	/**
	 * 待发货数量
	 */
	private Integer wait_delivery_count;
	/**
	 * 到期日期明细
	 */
	private List<ErpExpDetail> expiration;
	
	private List<String> itemIds;
	
	public List<String> getItemIds() {
		return itemIds;
	}
	public void setItemIds(List<String> itemIds) {
		this.itemIds = itemIds;
	}
	public Integer getStock() {
		Integer stock = 0;
		if(expiration != null){
			for(ErpExpDetail exp:expiration){
				stock += exp.getLeftCount();
			}
		}
		return stock-wait_delivery_count;
	}
	public List<ErpExpDetail> getExpiration() {
		return expiration;
	}
	public void setExpiration(List<ErpExpDetail> expiration) {
		this.expiration = expiration;
	}
	public Integer get_id() {
		return _id;
	}
	public void set_id(Integer _id) {
		this._id = _id;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public Integer getWait_delivery_count() {
		return wait_delivery_count;
	}
	public void setWait_delivery_count(Integer wait_delivery_count) {
		this.wait_delivery_count = wait_delivery_count;
	}
}
