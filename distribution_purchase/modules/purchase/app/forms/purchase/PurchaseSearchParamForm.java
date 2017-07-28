package forms.purchase;

/**
 * 
 * 
 * @author ye_ziran
 * @since 2016年3月23日 下午2:11:53
 */
public class PurchaseSearchParamForm {
	
	private String email;
	private String pageSize;
	private String status;
	private String pageCount;
	private String seachFlag;
	private Integer orderDate;
	private String sorderDate;
	private String eorderDate;
	private String spaydate;
	private String epaydate;
	private Boolean isChoose;
	private Boolean isPro;
	
	
	public Integer getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Integer orderDate) {
		this.orderDate = orderDate;
	}
	public String getSorderDate() {
		return sorderDate;
	}
	public void setSorderDate(String sorderDate) {
		this.sorderDate = sorderDate;
	}
	public String getEorderDate() {
		return eorderDate;
	}
	public void setEorderDate(String eorderDate) {
		this.eorderDate = eorderDate;
	}
	public String getSpaydate() {
		return spaydate;
	}
	public void setSpaydate(String spaydate) {
		this.spaydate = spaydate;
	}
	public String getEpaydate() {
		return epaydate;
	}
	public void setEpaydate(String epaydate) {
		this.epaydate = epaydate;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPageSize() {
		return pageSize;
	}
	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}
	public String getPageCount() {
		return pageCount;
	}
	public void setPageCount(String pageCount) {
		this.pageCount = pageCount;
	}
	public String getSeachFlag() {
		return seachFlag;
	}
	public void setSeachFlag(String seachFlag) {
		this.seachFlag = seachFlag;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Boolean getIsChoose() {
		return isChoose;
	}
	public void setIsChoose(Boolean isChoose) {
		this.isChoose = isChoose;
	}
	public Boolean getIsPro() {
		return isPro;
	}
	public void setIsPro(Boolean isPro) {
		this.isPro = isPro;
	}
	
}
