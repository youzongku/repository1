package entity.marketing.promotion;

import java.util.Date;
import java.util.List;

/**
 * 促销活动
 * 
 * @author huangjc
 * @date 2016年7月22日
 */
public class PromotionActivity {
	private Integer id;
	/** 促销活动名称 */
	private String name;
	/** 描述 */
	private String description;
	private String modeIds;
	private String modeNames;
	/** 开始时间 */
	private Date startTime;
	/** 结束时间 */
	private Date endTime;

	private String createUser;

	private String lastUpdateUser;

	private Date createTime;

	private Date lastUpdateTime;
	/** 是否删除，默认false */
	private boolean isDelete = Boolean.FALSE;
	/** 活动状态 */
	private Integer status;

	// 模式
	private List<PromotionActivityDisMode> disModeList;

	public String getModeIds() {
		return modeIds;
	}

	public void setModeIds(String modeIds) {
		this.modeIds = modeIds;
	}

	public String getModeNames() {
		return modeNames;
	}

	public void setModeNames(String modeNames) {
		this.modeNames = modeNames;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getLastUpdateUser() {
		return lastUpdateUser;
	}

	public void setLastUpdateUser(String lastUpdateUser) {
		this.lastUpdateUser = lastUpdateUser;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public List<PromotionActivityDisMode> getDisModeList() {
		return disModeList;
	}

	public void setDisModeList(List<PromotionActivityDisMode> disModeList) {
		this.disModeList = disModeList;
	}

	@Override
	public String toString() {
		return "PromotionActivity [id=" + id + ", name=" + name
				+ ", description=" + description + ", modeIds=" + modeIds
				+ ", modeNames=" + modeNames + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", createUser=" + createUser
				+ ", lastUpdateUser=" + lastUpdateUser + ", createTime="
				+ createTime + ", lastUpdateTime=" + lastUpdateTime
				+ ", isDelete=" + isDelete + ", status=" + status
				+ ", disModeList=" + disModeList + "]";
	}

}