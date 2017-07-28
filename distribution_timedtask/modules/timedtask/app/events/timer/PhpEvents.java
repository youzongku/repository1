package events.timer;

import java.io.Serializable;
import java.util.Map;

/**
 * @author zbc
 * 2017年2月13日 上午11:02:14
 */
public class PhpEvents implements Serializable{

	private static final long serialVersionUID = -6592729235677068352L;

	private String key;
	
	private Map<String,Object> map;
	
	private String optType;

	public PhpEvents(String key, Map<String, Object> map, String optType) {
		this.key = key;
		this.map = map;
		this.optType = optType;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

	public String getOptType() {
		return optType;
	}

	public void setOptType(String optType) {
		this.optType = optType;
	}
}
