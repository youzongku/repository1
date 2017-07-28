package entity.marketing.promotion;

/**
 * 促销活动的模式
 * 
 * @author huangjc
 * @date 2016年10月17日
 */
public class PromotionActivityDisMode {
	private Integer id;

	private Integer proActId;

	private Integer disModeId;

	private String disModeName;

	public PromotionActivityDisMode() {
	}

	public PromotionActivityDisMode(Integer proActId, Integer disModeId,
			String disModeName) {
		super();
		this.proActId = proActId;
		this.disModeId = disModeId;
		this.disModeName = disModeName;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getProActId() {
		return proActId;
	}

	public void setProActId(Integer proActId) {
		this.proActId = proActId;
	}

	public Integer getDisModeId() {
		return disModeId;
	}

	public void setDisModeId(Integer disModeId) {
		this.disModeId = disModeId;
	}

	public String getDisModeName() {
		return disModeName;
	}

	public void setDisModeName(String disModeName) {
		this.disModeName = disModeName;
	}
}