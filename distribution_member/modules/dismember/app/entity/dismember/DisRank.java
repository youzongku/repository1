package entity.dismember;

import java.io.Serializable;
import java.util.Date;

public class DisRank implements Serializable {

    private static final long serialVersionUID = -4350596012293787072L;

    private Integer id;

    private String rankName;//等级名称

    private Integer discount;//折扣，数据库中为整数，设置DTO数据时加上%

    private Boolean bdefault;//是否为默认等级

    private Date createTime;

    private String createUser;//用户的username

    private Date updateTime;

    private String updateUser;//用户的username

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRankName() {
        return rankName;
    }

    public void setRankName(String rankName) {
        this.rankName = rankName;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Boolean getBdefault() {
        return bdefault;
    }

    public void setBdefault(Boolean bdefault) {
        this.bdefault = bdefault;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }
}