package dto.product.search;

import com.wordnik.swagger.annotations.ApiModelProperty;

import dto.JqGridBaseSearch;

/**
 * @author zbc
 * 2017年4月19日 下午5:41:16
 */
public class InventoryLockDeSearch extends JqGridBaseSearch {
	
	@ApiModelProperty("锁库id")
	private Integer lockId;
	
	@ApiModelProperty("释放操作日志id")
	private Integer recordId;

	public Integer getRecordId() {
		return recordId;
	}

	public void setRecordId(Integer recordId) {
		this.recordId = recordId;
	}

	public Integer getLockId() {
		return lockId;
	}

	public void setLockId(Integer lockId) {
		this.lockId = lockId;
	}
	
}
