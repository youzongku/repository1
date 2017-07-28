package entity.marketing;

import java.io.Serializable;
import java.util.Date;

public class DisSpricePoster implements Serializable {

    private static final long serialVersionUID = -8194528940121340667L;

    private Integer id;

    private Integer activityId;

    private String imageName;

    private String imageSize;//单位字节

    private String imageUrl;

    private Integer imageWidth;

    private Integer imageHeight;

    private String createUser;

    private Date createTime;
    
    private boolean isBanner;//是不是banner图片
    
    private boolean isDelete;//是否被删除

    private String base64;//非数据库字段
    
    private boolean status;//图片的状态
    
    private String relatedInterfaceUrl;//相关界面的URL

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageSize() {
        return imageSize;
    }

    public void setImageSize(String imageSize) {
        this.imageSize = imageSize;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(Integer imageWidth) {
        this.imageWidth = imageWidth;
    }

    public Integer getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(Integer imageHeight) {
        this.imageHeight = imageHeight;
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

	public boolean isBanner() {
		return isBanner;
	}

	public void setBanner(boolean isBanner) {
		this.isBanner = isBanner;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}
	
	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getRelatedInterfaceUrl() {
		return relatedInterfaceUrl;
	}

	public void setRelatedInterfaceUrl(String relatedInterfaceUrl) {
		this.relatedInterfaceUrl = relatedInterfaceUrl;
	}

	public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }
}