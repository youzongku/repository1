package dto.sales;

public class ProductSalesVolumeSearchDto {
	private String beginDate;
	private String endDate;
	private String categoryId;
	private String istatus;
	private String typeId;
	private String title;
	private String currPage;
	private String pageSize;
	private Boolean isBack;
	public String getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public String getIstatus() {
		return istatus;
	}
	public void setIstatus(String istatus) {
		this.istatus = istatus;
	}
	public String getTypeId() {
		return typeId;
	}
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCurrPage() {
		return currPage;
	}
	public void setCurrPage(String currPage) {
		this.currPage = currPage;
	}
	public String getPageSize() {
		return pageSize;
	}
	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}
	public Boolean isBack() {
		return isBack;
	}
	public void setBack(Boolean isBack) {
		this.isBack = isBack;
	}
	@Override
	public String toString() {
		return "ProductSalesVolumeSearchDto [categoryId=" + categoryId + ", istatus=" + istatus + ", typeId=" + typeId
				+ ", title=" + title + ", currPage=" + currPage + ", pageSize=" + pageSize + ", isBack=" + isBack + "]";
	}
	
}
