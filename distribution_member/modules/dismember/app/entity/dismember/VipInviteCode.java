package entity.dismember;

import java.util.Date;

/**
 * vip注册邀请码实体
 * @author zbc
 * 2016年12月17日 上午9:45:12
 */
public class VipInviteCode {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 邀请码
     */
    private String inviteCode;

    /**
     * 预留字段(使用人数,目前支持使用一次)
     */
    private Integer count;

    /**
     * 是否被使用
     */
    private Boolean inUse;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 更新时间
     */
    private Date updateDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Boolean getInUse() {
        return inUse;
    }

    public void setInUse(Boolean inUse) {
        this.inUse = inUse;
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
}