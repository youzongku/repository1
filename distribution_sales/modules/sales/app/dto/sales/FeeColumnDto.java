package dto.sales;

import java.io.Serializable;

import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 返回合同费用列名等信息
 * @author zbc
 * 2017年5月16日 下午3:57:15
 */
public class FeeColumnDto implements Serializable {

	private static final long serialVersionUID = 4949320058307974510L;

	@ApiModelProperty("列名")
	private String columnName;
	@ApiModelProperty("字段名称")
	private String name;
	@ApiModelProperty("排序字段")
	private String index;
	
	public FeeColumnDto(){
		
	}
	
	public FeeColumnDto(String columnName, String name, String index) {
		super();
		this.columnName = columnName;
		this.name = name;
		this.index = index;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}

}
