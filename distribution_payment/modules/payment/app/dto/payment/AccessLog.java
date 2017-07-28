package dto.payment;

import java.io.Serializable;

/**
 * 访问日志实体类
 * @author zbc
 * 2016年10月31日 上午10:35:07
 */
public class AccessLog implements Serializable{
	
	private static final long serialVersionUID = 3745230636344990231L;

	private String accessUser;
	
	private String accessIP;
	
	private Long accessTime;
	
	private String host;
	
	private String accessInterface;
	
	public Long getAccessTime() {
		return accessTime;
	}

	public void setAccessTime(Long accessTime) {
		this.accessTime = accessTime;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getAccessUser() {
		return accessUser;
	}

	public void setAccessUser(String accessUser) {
		this.accessUser = accessUser;
	}

	public String getAccessIP() {
		return accessIP;
	}

	public void setAccessIP(String accessIP) {
		this.accessIP = accessIP;
	}

	public String getAccessInterface() {
		return accessInterface;
	}

	public void setAccessInterface(String accessInterface) {
		this.accessInterface = accessInterface;
	}

}
