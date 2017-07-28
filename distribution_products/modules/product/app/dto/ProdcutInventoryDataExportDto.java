package dto;

public class ProdcutInventoryDataExportDto {
	private String sku;
	private String productName;
	private String expirationTime;
	private String packQty;
	private String plugType;
	private String packageQty;
	private String expirationTimeQty;
	private int expirationTimeSum;
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getExpirationTime() {
		return expirationTime;
	}
	public void setExpirationTime(String expirationTime) {
		this.expirationTime = expirationTime;
	}
	public String getPackQty() {
		return packQty;
	}
	public void setPackQty(String packQty) {
		this.packQty = packQty;
	}
	public String getPlugType() {
		return plugType;
	}
	public void setPlugType(String plugType) {
		this.plugType = plugType;
	}
	public String getPackageQty() {
		return packageQty;
	}
	public void setPackageQty(String packageQty) {
		this.packageQty = packageQty;
	}
	public String getExpirationTimeQty() {
		return expirationTimeQty;
	}
	public void setExpirationTimeQty(String expirationTimeQty) {
		this.expirationTimeQty = expirationTimeQty;
	}
	public int getExpirationTimeSum() {
		return expirationTimeSum;
	}
	public void setExpirationTimeSum(int expirationTimeSum) {
		this.expirationTimeSum = expirationTimeSum;
	}
	
	
}
