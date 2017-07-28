package dto.product.search;


import com.wordnik.swagger.annotations.ApiModelProperty;

import dto.JqGridBaseSearch;

/**
 * @author zbc
 * 2017年4月19日 下午5:41:16
 */
public class InventoryLockSearch extends JqGridBaseSearch {
	
	@ApiModelProperty("是否剩余库存:0:否，1:是")
	private Integer isLeftStock;

	public Integer getIsLeftStock() {
		return isLeftStock;
	}

	public void setIsLeftStock(Integer isLeftStock) {
		this.isLeftStock = isLeftStock;
	}
	
}
