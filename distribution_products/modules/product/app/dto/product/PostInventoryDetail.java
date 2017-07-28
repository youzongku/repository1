package dto.product;

import java.io.Serializable;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel("锁库商品数据")
public class PostInventoryDetail implements Serializable{
	
	private static final long serialVersionUID = 8164621995129655319L;
	@ApiModelProperty("商品编号")
	private String sku; 
	@ApiModelProperty("数量")
	private Integer num;
	@ApiModelProperty("仓库id")
	private Integer warehouseId;
	@ApiModelProperty("到期日期:yyyy-MM-dd")
	private String expirationDate;
	
	public String getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public Integer getWarehouseId() {
		return warehouseId;
	}
	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}
	
}