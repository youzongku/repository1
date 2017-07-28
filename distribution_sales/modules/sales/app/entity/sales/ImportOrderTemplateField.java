package entity.sales;

import java.io.Serializable;

import com.google.common.base.Strings;

/**
 * @author hanfs
 * 描述：导入订单模板属性实体类
 *2016年5月14日
 */
public class ImportOrderTemplateField implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;//主键id
	private String templateName;//模板中的名称（例如：订单编号）
	private String propertyName;//实体中的属性名称（例如：orderNo）
	private boolean isNull;//是否允许为空标识(默认可以为空，true：可以为空，false：不能为空)
	private Integer type;//模板类型（1：淘宝，2：....）
	private Integer position;//属性在模板中的位置

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public boolean isNull() {
		return isNull;
	}

	public void setNull(boolean isNull) {
		this.isNull = isNull;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}
	
	/** 
	 * 描述：模板属性名称是否正确（true:正确，false:错误）
	 * 2016年5月16日
	 * @param templateName
	 * @return
	 */
	public boolean isRightForTemplateName(String templateName){
		if (Strings.isNullOrEmpty(templateName)) {
			return false;
		}
		return this.templateName.trim().equals(templateName.trim());
	}
	
	/**
	 * 描述：excel单元格内容空值的校验（true:正确，false：错误）
	 * 2016年5月16日
	 * @param dataValue
	 * @return
	 */
	public boolean isRightForNullValue(String dataValue){
		if(isNull){
			return true;
		}
		if (!isNull&&(dataValue==null || Strings.isNullOrEmpty(dataValue.trim()))) {
			return false;
		}
		return true;
	}
	
}
