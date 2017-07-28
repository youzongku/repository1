package dto.product;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 创建KA锁库实体类
 * @author zbc
 * 2017年4月18日 下午3:44:03
 */
@ApiModel("创建KA锁库实体类")
@JsonSerialize
public class PostInventoryLockDto implements Serializable {

	private static final long serialVersionUID = 2312970923817753932L;

	@ApiModelProperty("分销商账号")
	private String account;
	
	@ApiModelProperty("备注")
	private String remark;
	
	@ApiModelProperty("预计发货时间")
    private String estimatedShippingTime;
	
	@ApiModelProperty("锁库商品数据")
	private List<PostInventoryDetail> details;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getEstimatedShippingTime() {
		return estimatedShippingTime;
	}

	public void setEstimatedShippingTime(String estimatedShippingTime) {
		this.estimatedShippingTime = estimatedShippingTime;
	}

	public List<PostInventoryDetail> getDetails() {
		return details;
	}

	public void setDetails(List<PostInventoryDetail> details) {
		this.details = details;
	}
}

