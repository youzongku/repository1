package dto.discart;

import java.io.Serializable;
import java.util.List;

/**
 * 商品查询实体
 * 
 * @author zbc 2017年3月22日 下午5:59:23
 */
public class ProSearch implements Serializable {

	private static final long serialVersionUID = -5731827122932346607L;
	private List<String> skuList;
	private Integer warehouseId;
	private Integer model;
	private String email;

	public ProSearch() {
	}

	/**
	 * @param skuList
	 * @param warehouseId
	 * @param model
	 * @param email
	 */
	public ProSearch(List<String> skuList, Integer warehouseId, Integer model, String email) {
		super();
		this.skuList = skuList;
		this.warehouseId = warehouseId;
		this.model = model;
		this.email = email;
	}

	public List<String> getSkuList() {
		return skuList;
	}

	public void setSkuList(List<String> skuList) {
		this.skuList = skuList;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Integer getModel() {
		return model;
	}

	public void setModel(Integer model) {
		this.model = model;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "ProSearch [skuList=" + skuList + ", warehouseId=" + warehouseId + ", model=" + model + ", email="
				+ email + "]";
	}

}
