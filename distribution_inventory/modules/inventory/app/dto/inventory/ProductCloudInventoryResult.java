package dto.inventory;

import java.util.List;

/**
 * @author longhuashen
 * @since 2016/12/6
 */
public class ProductCloudInventoryResult {

    private boolean result;

    private String msg;
    
    private List objList;
    
    private Object obj;

    
    
    public ProductCloudInventoryResult(boolean result, String msg) {
        this.result = result;
        this.msg = msg;
    }
    
    public ProductCloudInventoryResult(boolean result, String msg, List objList, Object obj) {
        this.result = result;
        this.msg = msg;
        this.objList=objList;
        this.obj=obj;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

	public List getObjList() {
		return objList;
	}

	public void setObjList(List objList) {
		this.objList = objList;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	@Override
	public String toString() {
		return "ProductCloudInventoryResult [result=" + result + ", msg=" + msg + ", objList=" + objList + ", obj="
				+ obj + "]";
	}
    
}
