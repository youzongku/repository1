package entity.dismember;

import java.io.Serializable;

/**
 * 用户附件权限实体类
 * @author huchuyin
 * @date 2016年9月13日 上午10:13:16
 */
public class DisMemberMenu implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 主键ID */
    private Integer id;
    /** 用户ID */
    private Integer memberId;
    /** 菜单ID */
    private Integer menuId;
    /** 父栏目ID */
    private Integer parentid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }

	public Integer getParentid() {
		return parentid;
	}

	public void setParentid(Integer parentid) {
		this.parentid = parentid;
	}
    
}