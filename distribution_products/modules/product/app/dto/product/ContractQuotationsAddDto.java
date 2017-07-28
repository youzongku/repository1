package dto.product;

import java.io.Serializable;
import java.util.List;

import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 合同报价新增实体类
 * @author zbc
 * 2017年5月2日 下午4:44:13
 */
public class ContractQuotationsAddDto implements Serializable {

	private static final long serialVersionUID = -1225374856124486831L;
	
	@ApiModelProperty("合同id")
	private Integer cid;
	
	@ApiModelProperty("添加报价商品")
	private List<ContractQuotationsProDto> pros;

	public Integer getCid() {
		return cid;
	}

	public void setCid(Integer cid) {
		this.cid = cid;
	}

	public List<ContractQuotationsProDto> getPros() {
		return pros;
	}

	public void setPros(List<ContractQuotationsProDto> pros) {
		this.pros = pros;
	}
	
}
