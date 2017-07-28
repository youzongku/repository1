package dto.warehousing;

import entity.warehousing.InventoryChangeHistory;

public class InventoryChangeHistoryDto extends InventoryChangeHistory {
	private static final long serialVersionUID = 1L;

	private Integer pageSize;// 每页数量

	private Integer curPage;// 第几页

	private Integer changeTimeDesc;// 是否需要按照变更时间排序，0：asc，1：desc，null：默认排序

	private String morderType;

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getCurPage() {
		return curPage;
	}

	public void setCurPage(Integer curPage) {
		this.curPage = curPage;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Integer getChangeTimeDesc() {
		return changeTimeDesc;
	}

	public void setChangeTimeDesc(Integer changeTimeDesc) {
		this.changeTimeDesc = changeTimeDesc;
	}

	@Override
	public String toString() {
		return "InventoryChangeHistoryDto [pageSize=" + pageSize + ", curPage="
				+ curPage + ", changeTimeDesc=" + changeTimeDesc + "]";
	}


	public String getMorderType() {
		return morderType;
	}

	public void setMorderType(String morderType) {
		this.morderType = morderType;
	}
}
