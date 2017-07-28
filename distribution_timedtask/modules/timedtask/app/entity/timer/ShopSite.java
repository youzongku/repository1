package entity.timer;

import java.util.Date;

public class ShopSite {
    private Integer id;

    private String disemail;//分销商邮箱

    private String mastersite;//总站点名称

    private Date applydate;//申请时间

    private String reviewer;//审核人

    private Date reviewdate;//审核时间

    private Integer status;//状态(1 待审核  2 审核通过  3 审核不通过 4 已取消）

    private String siteurl;//站点url

    private String reviewreason;//审核不通过理由（1 申请的站点域名格式不正确 2 申请原因不明确 3 其他）

    private String remark;//备注

    private Integer shopid;//店铺id
    
    private String applydateStr;
    
    private String reviewdateStr;
    
    private Integer distributionMode;//分销商 模式(1,电商 2，经销商 3 ,KA直营4,进口专营)
    
    private Boolean isUsable;//是否启用

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDisemail() {
        return disemail;
    }

    public void setDisemail(String disemail) {
        this.disemail = disemail;
    }

    public String getMastersite() {
        return mastersite;
    }

    public void setMastersite(String mastersite) {
        this.mastersite = mastersite;
    }

    public Date getApplydate() {
        return applydate;
    }

    public void setApplydate(Date applydate) {
        this.applydate = applydate;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public Date getReviewdate() {
        return reviewdate;
    }

    public void setReviewdate(Date reviewdate) {
        this.reviewdate = reviewdate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getSiteurl() {
        return siteurl;
    }

    public void setSiteurl(String siteurl) {
        this.siteurl = siteurl;
    }

    public String getReviewreason() {
        return reviewreason;
    }

    public void setReviewreason(String reviewreason) {
        this.reviewreason = reviewreason;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getShopid() {
        return shopid;
    }

    public void setShopid(Integer shopid) {
        this.shopid = shopid;
    }

	public String getApplydateStr() {
		return applydateStr;
	}

	public void setApplydateStr(String applydateStr) {
		this.applydateStr = applydateStr;
	}

	public String getReviewdateStr() {
		return reviewdateStr;
	}

	public void setReviewdateStr(String reviewdateStr) {
		this.reviewdateStr = reviewdateStr;
	}

	public Integer getDistributionMode() {
		return distributionMode;
	}

	public void setDistributionMode(Integer distributionMode) {
		this.distributionMode = distributionMode;
	}

	public Boolean getIsUsable() {
		return isUsable;
	}

	public void setIsUsable(Boolean isUsable) {
		this.isUsable = isUsable;
	}
    
}