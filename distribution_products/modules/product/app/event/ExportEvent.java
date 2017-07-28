package event;

public class ExportEvent {
	private String reqParam;

	
	public ExportEvent(String reqParam) {
		super();
		this.reqParam = reqParam;
	}
	public String getReqParam() {
		return reqParam;
	}

	public void setReqParam(String reqParam) {
		this.reqParam = reqParam;
	}
	
}
