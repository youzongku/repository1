package dto.dismember;

import java.io.Serializable;

/**
 * Created by LSL on 2016/2/5.
 */
public class RankDto implements Serializable {

    private static final long serialVersionUID = -1138304992070319462L;

    private Integer id;

    private String rankName;//等级名称

    private String discount;//折扣，以%为单位

    private Boolean bdefault;//是否为默认等级

    private String createTime;

    private String createUser;

    private String updateTime;

    private String updateUser;

    private Integer userNumber;//用户人数

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

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public Boolean getBdefault() {
        return bdefault;
    }

    public void setBdefault(Boolean bdefault) {
        this.bdefault = bdefault;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Integer getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(Integer userNumber) {
        this.userNumber = userNumber;
    }
}
