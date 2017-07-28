package entity.dismember;

import java.util.Date;

public class DisApplyFile {
    private Integer id;

    private String name;//文件名

    private String url;//文件url

    private Integer applyId;//申请注册id

    private Date createDate;//创建时间

    private Date updateDate;//更新时间
    
    private String type;//文件用途
    
    private String typeDesc;//文件描述
    
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getApplyId() {
        return applyId;
    }

    public void setApplyId(Integer applyId) {
        this.applyId = applyId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTypeDesc() {
		return typeDesc;
	}
	
	public void setTypeDesc(String type) {
		if (type.equals("business-licence")){
			this.typeDesc = "营业执照";
		} else if (type.equals("organization-code")) {
			this.typeDesc = "组织机构代码";
		} else if (type.equals("tax-licence")) {
			this.typeDesc = "税务登记证";
		} else if (type.equals("taxpayer-licence")) {
			this.typeDesc = "一般纳税人资格证";
		} else if (type.equals("food-licence")) {
			this.typeDesc = "食品流通许可证";
		} else {
			this.typeDesc = "收货授权书";
		}
	}
}