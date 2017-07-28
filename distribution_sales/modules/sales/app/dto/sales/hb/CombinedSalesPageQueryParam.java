package dto.sales.hb;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class CombinedSalesPageQueryParam {
	private Integer currPage;
	private Integer pageSize;
	private Integer status;
	private String combinedStartTime;
	private String combinedEndTime;
	private Integer consumerType;
	private Integer distributionMode;
	private Integer warehouseId;
	private String searchText;
	@ApiModelProperty("排序字段")
	private String sidx;
	@ApiModelProperty("排序规则")
	private String sord;

	public String getSidx() {
		return sidx;
	}

	public void setSidx(String sidx) {
		this.sidx = sidx;
	}

	public String getSord() {
		return sord;
	}

	public void setSord(String sord) {
		this.sord = sord;
	}

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public Integer getCurrPage() {
		return currPage;
	}

	public void setCurrPage(Integer currPage) {
		this.currPage = currPage;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCombinedStartTime() {
		return combinedStartTime;
	}

	public void setCombinedStartTime(String combinedStartTime) {
		this.combinedStartTime = combinedStartTime;
	}

	public String getCombinedEndTime() {
		return combinedEndTime;
	}

	public void setCombinedEndTime(String combinedEndTime) {
		this.combinedEndTime = combinedEndTime;
	}

	public Integer getConsumerType() {
		return consumerType;
	}

	public void setConsumerType(Integer consumerType) {
		this.consumerType = consumerType;
	}

	public Integer getDistributionMode() {
		return distributionMode;
	}

	public void setDistributionMode(Integer distributionMode) {
		this.distributionMode = distributionMode;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	@Override
	public String toString() {
		return "CombinedSalesPageQueryParam [currPage=" + currPage + ", pageSize=" + pageSize + ", status=" + status
				+ ", combinedStartTime=" + combinedStartTime + ", combinedEndTime=" + combinedEndTime
				+ ", consumerType=" + consumerType + ", distributionMode=" + distributionMode + ", warehouseId="
				+ warehouseId + "]";
	}

}
