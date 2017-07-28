package dto.sales;

public class ProductSalesVolume {
	private String ctitle;
	private String csku;
	private String warehouseName;
	private String cname;
	private String typeName;
	private Integer istatus;
	private String istatusName;
	private String brand;
	private String interBarCode;
	private String packQty;
	private String arriveWarePrice;
	private Integer salesvolume;
	
	public String getCtitle() {
		return ctitle;
	}
	public void setCtitle(String ctitle) {
		this.ctitle = ctitle;
	}
	public String getCsku() {
		return csku;
	}
	public void setCsku(String csku) {
		this.csku = csku;
	}
	public String getWarehouseName() {
		return warehouseName;
	}
	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public Integer getIstatus() {
		return istatus;
	}
	public void setIstatus(Integer istatus) {
		this.istatus = istatus;
	}
	public String getIstatusName() {
		return istatusName;
	}
	public void setIstatusName(String istatusName) {
		this.istatusName = istatusName;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getInterBarCode() {
		return interBarCode;
	}
	public void setInterBarCode(String interBarCode) {
		this.interBarCode = interBarCode;
	}
	public String getPackQty() {
		return packQty;
	}
	public void setPackQty(String packQty) {
		this.packQty = packQty;
	}
	public String getArriveWarePrice() {
		return arriveWarePrice;
	}
	public void setArriveWarePrice(String arriveWarePrice) {
		this.arriveWarePrice = arriveWarePrice;
	}
	public Integer getSalesvolume() {
		return salesvolume;
	}
	public void setSalesvolume(Integer salesvolume) {
		this.salesvolume = salesvolume;
	}
	@Override
	public String toString() {
		return "ProductSalesVolume [ctitle=" + ctitle + ", csku=" + csku + ", warehouseName=" + warehouseName
				+ ", cname=" + cname + ", typeName=" + typeName + ", istatus=" + istatus + ", brand=" + brand
				+ ", interBarCode=" + interBarCode + ", packQty=" + packQty + ", arriveWarePrice=" + arriveWarePrice
				+ ", salesvolume=" + salesvolume + "]";
	}
	
}
