package dto.inventory;

import java.util.List;

/**
 * Created by LSL on 2015/12/2.
 */
public class DisInventoryDto {

	private String sku;// 商品SKU

	private String ctitle;// 商品名称

	private Integer totalQty;// 当前SKU商品的总库存
	
	private List<DisProductDto> disStorages;//sku所属微仓
	
	public List<DisProductDto> getDisStorages() {
		return disStorages;
	}

	public void setDisStorages(List<DisProductDto> disStorages) {
		this.disStorages = disStorages;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getCtitle() {
		return ctitle;
	}

	public void setCtitle(String ctitle) {
		this.ctitle = ctitle;
	}

	public Integer getTotalQty() {
		return totalQty;
	}

	public void setTotalQty(Integer totalQty) {
		this.totalQty = totalQty;
	}

}
