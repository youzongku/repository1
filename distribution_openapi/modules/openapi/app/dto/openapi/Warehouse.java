package dto.openapi;

import java.io.Serializable;

public class Warehouse implements Serializable{
	
	private static final long serialVersionUID = 5166470099418421224L;
	
	private Integer id;

    private String warehouseId;//仓库编码

    private String warehouseName;//仓库名
    
//    private String type;// 仓库类型，目前假定为1：保税仓，2：完税仓，3：海外直邮，4：外部保税

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

//	public String getType() {
//		return type;
//	}
//
//	public void setType(String type) {
//		this.type = type;
//	}
}
