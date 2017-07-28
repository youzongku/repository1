package entity.dismember;

import java.io.Serializable;
import java.util.Date;

public class DisRole implements Serializable {

    private static final long serialVersionUID = -4898105101635729518L;

    private Integer id;

    private String roleName;

    private String roleDesc;

    private Date createDate;
    
    private Boolean	buttonOperate;//是否拥有按钮操作权限
    
    private Boolean isactive;//角色是否启用
    
    private Boolean ismessage;//角色是否有发送短信的请求

    private String createUser;//创建人
    
    public Boolean getIsmessage() {
		return ismessage;
	}

	public void setIsmessage(Boolean ismessage) {
		this.ismessage = ismessage;
	}

	public Boolean getIsactive() {
		return isactive;
	}

	public void setIsactive(Boolean isactive) {
		this.isactive = isactive;
	}

	public Boolean getButtonOperate() {
		return buttonOperate;
	}

	public void setButtonOperate(Boolean buttonOperate) {
		this.buttonOperate = buttonOperate;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
}