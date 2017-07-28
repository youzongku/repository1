package dto.sales;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImportResultInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String fileName;// 导入文件名称
	private Integer successCount;// 导入成功数量
	private Integer failCount;// 导入失败数量
	private List<String> messages = new ArrayList<String>();// 提示信息列表

	public ImportResultInfo() {
		super();
	}

	public ImportResultInfo(String fileName, Integer successCount,
			Integer failCount) {
		super();
		this.fileName = fileName;
		this.successCount = successCount;
		this.failCount = failCount;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Integer getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(Integer successCount) {
		this.successCount = successCount;
	}

	public Integer getFailCount() {
		return failCount;
	}

	public void setFailCount(Integer failCount) {
		this.failCount = failCount;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	@Override
	public String toString() {
		return "ImportResultInfo [fileName=" + fileName + ", successCount="
				+ successCount + ", failCount=" + failCount + ", messages="
				+ messages + "]";
	}

}
