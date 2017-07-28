package dto.product_inventory;

import java.io.Serializable;

/**
 * Erp到期日志库存查询实体
 * @author zbc
 * 2017年1月12日 下午5:22:28
 */
public class ErpExpDetail implements Serializable {

	private static final long serialVersionUID = -3281767004005488993L;
	
	private String productionDate;
	private Integer leftCount;
	
	public Integer getLeftCount() {
		return leftCount;
	}
	public String getProductionDate() {
		return productionDate;
	}
	public void setProductionDate(String productionDate) {
		this.productionDate = productionDate;
	}
	public void setLeftCount(Integer leftCount) {
		this.leftCount = leftCount;
	}
}
