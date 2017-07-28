package dto.product.inventory;

import java.io.Serializable;

import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * KA锁库释放库存
 * @author zbc
 * 2017年5月9日 上午10:17:33
 */
public class LockResetDetialDto implements Serializable {

	private static final long serialVersionUID = 4782061761600840153L;

	@ApiModelProperty("锁库详情ID:必传")
	private Integer  detailId;
	
	@ApiModelProperty("释放数量:不能大于剩余数量,必传")
	private Integer num;

	/**
	 * @param detailId
	 * @param num
	 */
	public LockResetDetialDto(Integer detailId, Integer num) {
		super();
		this.detailId = detailId;
		this.num = num;
	}
	public LockResetDetialDto(){
		
	}
	public Integer getDetailId() {
		return detailId;
	}

	public void setDetailId(Integer detailId) {
		this.detailId = detailId;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}
	
}
