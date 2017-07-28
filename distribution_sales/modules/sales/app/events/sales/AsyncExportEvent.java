package events.sales;

import java.io.Serializable;
import java.util.Map;

import dto.sales.AsyncExportDto;

public class AsyncExportEvent implements Serializable{
	
	private static final long serialVersionUID = -6350153291142192274L;
	private Map<String, String[]> map;
	private  AsyncExportDto asyncExportDto;
	private String[] headerString;
	private  Map<String, String> fieldsMap;
	public AsyncExportEvent(Map<String, String[]> map, AsyncExportDto asyncExportDto, String[] headerString,
			Map<String, String> fieldsMap) {
		super();
		this.map = map;
		this.asyncExportDto = asyncExportDto;
		this.headerString = headerString;
		this.fieldsMap = fieldsMap;
	}
	public AsyncExportEvent() {
		super();
	}
	public Map<String, String[]> getMap() {
		return map;
	}
	public void setMap(Map<String, String[]> map) {
		this.map = map;
	}
	public AsyncExportDto getAsyncExportDto() {
		return asyncExportDto;
	}
	public void setAsyncExportDto(AsyncExportDto asyncExportDto) {
		this.asyncExportDto = asyncExportDto;
	}
	public String[] getHeaderString() {
		return headerString;
	}
	public void setHeaderString(String[] headerString) {
		this.headerString = headerString;
	}
	public Map<String, String> getFieldsMap() {
		return fieldsMap;
	}
	public void setFieldsMap(Map<String, String> fieldsMap) {
		this.fieldsMap = fieldsMap;
	}
}
