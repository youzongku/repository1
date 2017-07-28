package dto.marketing.promotion;

import java.util.Date;
import java.util.List;

import entity.marketing.promotion.PromotionActivityDisMode;

/**
 * 活动基本信息及活动实例基本信息
 * 
 * @author huangjc
 * @date 2016年10月17日
 */
public class ProActWithActInstsAndProTypesDto {
	private Integer id;
	/** 促销活动名称 */
	private String name;
	/** 描述 */
	private String description;
	@Deprecated
	private String modeIds;
	@Deprecated
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
	
	// 实例及模板
	private List<ActInstWithProTypeDto> actInstWithProTypeDtoList;

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
	@Deprecated
	public String getModeIds() {
		return modeIds;
	}
	@Deprecated
	public void setModeIds(String modeIds) {
		this.modeIds = modeIds;
	}
	@Deprecated
	public String getModeNames() {
		return modeNames;
	}
	@Deprecated
	public void setModeNames(String modeNames) {
		this.modeNames = modeNames;
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

	public List<ActInstWithProTypeDto> getActInstWithProTypeDtoList() {
		return actInstWithProTypeDtoList;
	}

	public void setActInstWithProTypeDtoList(
			List<ActInstWithProTypeDto> actInstWithProTypeDtoList) {
		this.actInstWithProTypeDtoList = actInstWithProTypeDtoList;
	}

}
