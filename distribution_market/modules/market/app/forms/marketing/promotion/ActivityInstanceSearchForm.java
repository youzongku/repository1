package forms.marketing.promotion;


public class ActivityInstanceSearchForm extends BaseForm {
	private String name;
	private String startTime;
	private String endTime;
	private Integer status;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "ActivityInstanceSearchForm [name=" + name + ", startTime="
				+ startTime + ", endTime=" + endTime + ", status=" + status
				+ ", curr=]"+this.getCurr()+", pageSize="+this.getPageSize()+", offset="+this.getOffset();
	}

	

}
