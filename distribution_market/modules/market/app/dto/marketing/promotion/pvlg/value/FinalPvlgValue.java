package dto.marketing.promotion.pvlg.value;

/**
 * 封装更新优惠实例的对象
 * 
 * @author huangjc
 * @date 2016年8月1日
 */
public class FinalPvlgValue {
	private int pvlgInstId;// 优惠实例id
	private String jsonValue;

	public String getJsonValue() {
		return jsonValue;
	}

	public void setJsonValue(String jsonValue) {
		this.jsonValue = jsonValue;
	}

	public int getPvlgInstId() {
		return pvlgInstId;
	}

	public void setPvlgInstId(int pvlgInstId) {
		this.pvlgInstId = pvlgInstId;
	}

	@Override
	public String toString() {
		return "FinalPvlgValue [pvlgInstId=" + pvlgInstId + ", jsonValue="
				+ jsonValue + "]";
	}

}
