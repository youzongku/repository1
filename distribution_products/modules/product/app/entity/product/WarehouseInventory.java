package entity.product;

public class WarehouseInventory {
    private Integer id;

    private Integer warehouseId;

    private String warehouseName;

    private Integer warehouseProductId;

    private Integer stock;//可用库存
    
    private String sku;//SKU
    
    private Double costprice;//成本价
    
    private Double fprice;//分销价，字段暂未使用
    
    private Integer frozenStock;//冻结库存
    
    public Integer getFrozenStock() {
		return frozenStock;
	}

	public void setFrozenStock(Integer frozenStock) {
		this.frozenStock = frozenStock;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public Double getCostprice() {
		return costprice;
	}

	public void setCostprice(Double costprice) {
		this.costprice = costprice;
	}

	public Double getFprice() {
		return fprice;
	}

	public void setFprice(Double fprice) {
		this.fprice = fprice;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getWarehouseProductId() {
        return warehouseProductId;
    }

    public void setWarehouseProductId(Integer warehouseProductId) {
        this.warehouseProductId = warehouseProductId;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}