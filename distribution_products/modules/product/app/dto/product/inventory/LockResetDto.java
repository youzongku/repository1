package dto.product.inventory;

import java.io.Serializable;
import java.util.List;

import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * KA锁库释放库存
 * @author zbc
 * 2017年5月9日 上午10:17:33
 */
public class LockResetDto implements Serializable {

	private static final long serialVersionUID = -2454210209736344701L;
	
	@ApiModelProperty("锁库ID:必传")
	private Integer lockId;
	
	@ApiModelProperty("备注信息:必传")
	private String remark;

	@ApiModelProperty("释放详情:如果不传isAll:true,必传")
	private List<LockResetDetialDto> details;
	
	@ApiModelProperty("是否全部释放:isAll:true与details必传一个")
	private Boolean isAll;

	public Boolean getIsAll() {
		return isAll;
	}

	public void setIsAll(Boolean isAll) {
		this.isAll = isAll;
	}

	public Integer getLockId() {
		return lockId;
	}

	public void setLockId(Integer lockId) {
		this.lockId = lockId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<LockResetDetialDto> getDetails() {
		return details;
	}

	public void setDetails(List<LockResetDetialDto> details) {
		this.details = details;
	}

}
