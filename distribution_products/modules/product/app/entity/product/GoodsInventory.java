package entity.product;

/**
 * 产品库存信息实体
 * 
 * @author luwj
 */
public class GoodsInventory implements java.io.Serializable {

	private static final long serialVersionUID = 1L;


	private Integer id;
	
	private String sku;

	private Integer warehouseId;// 仓库id

	private String warehouseName;// 仓库名

	private int totalStock;// 库存总数

	private int frozenStock;// 冻结库存数

	private int availableStock;// 可用库存数

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public int getTotalStock() {
		return totalStock;
	}

	public void setTotalStock(int totalStock) {
		this.totalStock = totalStock;
	}

	public int getFrozenStock() {
		return frozenStock;
	}

	public void setFrozenStock(int frozenStock) {
		this.frozenStock = frozenStock;
	}

	public int getAvailableStock() {
		return availableStock;
	}

	public void setAvailableStock(int availableStock) {
		this.availableStock = availableStock;
	}


}