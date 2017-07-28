package dto.dismember;

import java.io.Serializable;

/**
 * Created by LSL on 2015/12/22.
 */
public class RoleDto implements Serializable {

    private static final long serialVersionUID = 2401281570201674591L;

    private Integer id;

    private String name;

    private String desc;

    private String createTime;
    
    private Boolean isactive;

    private String createUser;
    
    public Boolean getIsactive() {
		return isactive;
	}

	public void setIsactive(Boolean isactive) {
		this.isactive = isactive;
	}

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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
}
