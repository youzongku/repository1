package dto.dismember;

public class CommonExportDto {
	private String id;
	private String functionId;
	private String functionParam;
	private String functionResult;
	
	private String excelRows;
	private String excelTitle;
	private String fileName;
	private String excelWidth;
	private String mergeKey;
	private String rowsMerge;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFunctionId() {
		return functionId;
	}
	public void setFunctionId(String functionId) {
		this.functionId = functionId;
	}
	public String getFunctionParam() {
		return functionParam;
	}
	public void setFunctionParam(String functionParam) {
		this.functionParam = functionParam;
	}
	public String getFunctionResult() {
		return functionResult;
	}
	public void setFunctionResult(String functionResult) {
		this.functionResult = functionResult;
	}
	public String getExcelRows() {
		return excelRows;
	}
	public void setExcelRows(String excelRows) {
		this.excelRows = excelRows;
	}
	public String getExcelTitle() {
		return excelTitle;
	}
	public void setExcelTitle(String excelTitle) {
		this.excelTitle = excelTitle;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getExcelWidth() {
		return excelWidth;
	}
	public void setExcelWidth(String excelWidth) {
		this.excelWidth = excelWidth;
	}
	public String getMergeKey() {
		return mergeKey;
	}
	public void setMergeKey(String mergeKey) {
		this.mergeKey = mergeKey;
	}
	public String getRowsMerge() {
		return rowsMerge;
	}
	public void setRowsMerge(String rowsMerge) {
		this.rowsMerge = rowsMerge;
	}
}