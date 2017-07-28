package dto.product;

import java.io.Serializable;

import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 合同报价新增 商品 实体类
 * @author zbc
 * 2017年5月2日 下午4:44:13
 */
public class ContractQuotationsProDto implements Serializable {

	private static final long serialVersionUID = -1225374856124486831L;
	
	@ApiModelProperty("仓库id")
	private Integer warehouseId;
	
	@ApiModelProperty("商品编码")
	private String sku;
	
	@ApiModelProperty("商品分类")
	private Integer categoryId;
	
	@ApiModelProperty("合同价")
	private Double contractPrice;

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

	public Double getContractPrice() {
		return contractPrice;
	}

	public void setContractPrice(Double contractPrice) {
		this.contractPrice = contractPrice;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}
	
}
