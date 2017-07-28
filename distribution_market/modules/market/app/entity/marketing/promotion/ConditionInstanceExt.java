package entity.marketing.promotion;

/**
 * 指定商品属性/指定购物车属性
 * 
 * @author huangjc
 * @date 2016年10月17日
 */
public class ConditionInstanceExt {
	private Integer id;

	private Integer actInstId;

	private Integer condtInstId;
	
	private String specifyAttrValue;

	private boolean stepped = false;// 可阶梯

	private boolean doubleUp = false;// 可翻倍

	public ConditionInstanceExt() {
	}

	public ConditionInstanceExt(Integer actInstId, Integer condtInstId,
			boolean stepped, boolean doubleUp) {
		super();
		this.actInstId = actInstId;
		this.condtInstId = condtInstId;
		this.stepped = stepped;
		this.doubleUp = doubleUp;
	}

	public Integer getCondtInstId() {
		return condtInstId;
	}

	public void setCondtInstId(Integer condtInstId) {
		this.condtInstId = condtInstId;
	}

	public boolean isStepped() {
		return stepped;
	}

	public void setStepped(boolean stepped) {
		this.stepped = stepped;
	}

	public boolean isDoubleUp() {
		return doubleUp;
	}

	public void setDoubleUp(boolean doubleUp) {
		this.doubleUp = doubleUp;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getActInstId() {
		return actInstId;
	}

	public void setActInstId(Integer actInstId) {
		this.actInstId = actInstId;
	}

	public String getSpecifyAttrValue() {
		return specifyAttrValue;
	}

	public void setSpecifyAttrValue(String specifyAttrValue) {
		this.specifyAttrValue = specifyAttrValue;
	}
}