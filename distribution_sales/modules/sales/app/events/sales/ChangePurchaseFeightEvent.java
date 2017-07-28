package events.sales;

import java.util.List;
import java.util.Map;

/**
 * @author zbc
 * 2017年5月24日 上午11:46:14
 */
public class ChangePurchaseFeightEvent {
	
	private List<Map<String,Object>> changeFreightList;

	public List<Map<String, Object>> getChangeFreightList() {
		return changeFreightList;
	}

	public void setChangeFreightList(List<Map<String, Object>> changeFreightList) {
		this.changeFreightList = changeFreightList;
	}

	public ChangePurchaseFeightEvent(List<Map<String, Object>> changeFreightList) {
		super();
		this.changeFreightList = changeFreightList;
	}
	public ChangePurchaseFeightEvent(){
		
	}
	
}
