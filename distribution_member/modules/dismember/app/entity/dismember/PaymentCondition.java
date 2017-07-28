package entity.dismember;

import java.io.Serializable;
import java.util.Date;

public class PaymentCondition implements Serializable{

	private static final long serialVersionUID = 1930729455268257812L;

	private Integer id;

    /**
     * 分销商模式
     */
    private Integer model;

    /**
     * 分销商类型
     */
    private Integer disType;

    /**
     * 是否后台展示
     */
    private Boolean backstage;

    /**
     * 用途（1：充值、2：采购、3：销售）
     */
    private Integer purpose;

    private Date createTime;

    private Date updateTime;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 是否前台展示
     */
    private Boolean foreground;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getModel() {
        return model;
    }

    public void setModel(Integer model) {
        this.model = model;
    }

    public Integer getDisType() {
        return disType;
    }

    public void setDisType(Integer disType) {
        this.disType = disType;
    }

    public Boolean getBackstage() {
        return backstage;
    }

    public void setBackstage(Boolean backstage) {
        this.backstage = backstage;
    }

    public Integer getPurpose() {
        return purpose;
    }

    public void setPurpose(Integer purpose) {
        this.purpose = purpose;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Boolean getForeground() {
        return foreground;
    }

    public void setForeground(Boolean foreground) {
        this.foreground = foreground;
    }
}