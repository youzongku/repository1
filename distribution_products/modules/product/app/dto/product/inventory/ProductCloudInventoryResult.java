package dto.product.inventory;

import java.util.List;
/**
 * 锁库返回结果实体
 * @author zbc
 * 2017年4月21日 下午3:42:39
 */
public class ProductCloudInventoryResult<T> {

    private boolean result;

    private String msg;
    
    private List<T> objList;
    
    private T obj;

	public ProductCloudInventoryResult() {
		super();
	}

	public ProductCloudInventoryResult(boolean result, String msg) {
        this.result = result;
        this.msg = msg;
    }
    
    public ProductCloudInventoryResult(boolean result, String msg, List<T> objList, T obj) {
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

	public List<T> getObjList() {
		return objList;
	}

	public void setObjList(List<T> objList) {
		this.objList = objList;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(T obj) {
		this.obj = obj;
	}

	@Override
	public String toString() {
		return "ProductCloudInventoryResult [result=" + result + ", msg=" + msg + "]";
	}
    
}
