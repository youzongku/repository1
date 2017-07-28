package entity.banner;

import java.util.Date;

public class BannerInfo {
	private Integer id;

	private String describe;

	private String imgUrl;

	private int status;

	private Boolean isDelete;

	private String relatedInterfaceUrl;

	private String createUser;

	private Date createTime;

	private String lastUpdateUser;

	private Date lastUpdateTime;

	private Integer sort;

	//广告图类型
	private Integer type;

	//背景色(针对banner)
	private String bgColor;

	//关联的类目id
	private Integer categoryId;

	//父类目id
	private Integer parentId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Boolean getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Boolean isDelete) {
		this.isDelete = isDelete;
	}

	public String getRelatedInterfaceUrl() {
		return relatedInterfaceUrl;
	}

	public void setRelatedInterfaceUrl(String relatedInterfaceUrl) {
		this.relatedInterfaceUrl = relatedInterfaceUrl;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getLastUpdateUser() {
		return lastUpdateUser;
	}

	public void setLastUpdateUser(String lastUpdateUser) {
		this.lastUpdateUser = lastUpdateUser;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getBgColor() {
		return bgColor;
	}

	public void setBgColor(String bgColor) {
		this.bgColor = bgColor;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	@Override
	public String toString() {
		return "BannerInfo{" +
				"id=" + id +
				", describe='" + describe + '\'' +
				", imgUrl='" + imgUrl + '\'' +
				", status=" + status +
				", isDelete=" + isDelete +
				", relatedInterfaceUrl='" + relatedInterfaceUrl + '\'' +
				", createUser='" + createUser + '\'' +
				", createTime=" + createTime +
				", lastUpdateUser='" + lastUpdateUser + '\'' +
				", lastUpdateTime=" + lastUpdateTime +
				", sort=" + sort +
				", type=" + type +
				", bgColor=" + bgColor +
				", categoryId=" + categoryId +
				", parentId=" + parentId +
				'}';
	}
}