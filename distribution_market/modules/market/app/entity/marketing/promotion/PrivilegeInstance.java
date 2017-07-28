package entity.marketing.promotion;

/**
 * 优惠实例
 * 
 * @author huangjc
 * @date 2016年7月22日
 */
public class PrivilegeInstance {
	private Integer id;
	/** 活动实例id */
	private Integer actInstId;
	/** 条件实例实例id */
	private Integer condtInstId;
	/** 优惠id */
	private Integer proPvlgId;

	private String name;

	private Integer pType;
	/** 优惠的具体值 */
	private String pvlgValue;
	
	private int Multiple=1;

	/** 是否删除，默认false */
	private boolean isDelete = Boolean.FALSE;

	public Integer getCondtInstId() {
		return condtInstId;
	}

	public void setCondtInstId(Integer condtInstId) {
		this.condtInstId = condtInstId;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
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

	public Integer getProPvlgId() {
		return proPvlgId;
	}

	public void setProPvlgId(Integer proPvlgId) {
		this.proPvlgId = proPvlgId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getpType() {
		return pType;
	}

	public void setpType(Integer pType) {
		this.pType = pType;
	}

	public String getPvlgValue() {
		return pvlgValue;
	}

	public void setPvlgValue(String pvlgValue) {
		this.pvlgValue = pvlgValue;
	}

	public int getMultiple() {
		return Multiple;
	}

	public void setMultiple(int multiple) {
		Multiple = multiple;
	}

}