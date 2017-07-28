package dto.sales;


import java.io.Serializable;
import java.util.List;

/**
 * 异步导入发货单dto
 * @author zbc
 * 2017年6月26日 上午11:12:17
 */
public class AsyncExportDto implements Serializable {

	private static final long serialVersionUID = 1549906476505914909L;
	
	private String admin;//后台用户
	
	private String ip;//登录ip
	
	private List<String> accounts;//关联账号
	
	private String filename;//文件名称
	
	private String exportType;
	
	private String path;
	
	public AsyncExportDto(){}

	public AsyncExportDto(String admin, String ip, List<String> accounts, String filename,String exportType) {
		super();
		this.admin = admin;
		this.ip = ip;
		this.accounts = accounts;
		this.filename = filename;
		this.exportType = exportType;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getExportType() {
		return exportType;
	}

	public void setExportType(String exportType) {
		this.exportType = exportType;
	}

	public String getAdmin() {
		return admin;
	}

	public void setAdmin(String admin) {
		this.admin = admin;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public List<String> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<String> accounts) {
		this.accounts = accounts;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
}
