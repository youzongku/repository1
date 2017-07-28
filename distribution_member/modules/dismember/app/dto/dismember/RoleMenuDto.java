package dto.dismember;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//角色菜单实体类
public class RoleMenuDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer roleId;//角色id
	
	private Integer menuId;//菜单栏id
	
	private String roleName;//角色名称
	
	private String roleDesc;//角色描述
	
	private Boolean buttonAuth;//是否有按钮操作权限
	
	private String menuName;//菜单名称
	
	private Integer level;//菜单层级
	
	private Integer position;//菜单对应加载内容位置
	
	private Integer parentId;//父菜单id
	
	private String menuDescription;//菜单描述
	
	private Boolean isParent;//是否是父菜单
	
	private Date createTime;//菜单创建时间
	
	private Date updateTime;//菜单更新时间
	
	private String type;//栏目类型
	
	private List<RoleMenuDto> childMenus = new ArrayList<RoleMenuDto>();//用户对应角色的子菜单
	
	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Integer getMenuId() {
		return menuId;
	}

	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getMenuDescription() {
		return menuDescription;
	}

	public void setMenuDescription(String menuDescription) {
		this.menuDescription = menuDescription;
	}

	public Boolean getIsParent() {
		return isParent;
	}

	public void setIsParent(Boolean isParent) {
		this.isParent = isParent;
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

	public String getRoleDesc() {
		return roleDesc;
	}

	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}

	public Boolean getButtonAuth() {
		return buttonAuth;
	}

	public void setButtonAuth(Boolean buttonAuth) {
		this.buttonAuth = buttonAuth;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<RoleMenuDto> getChildMenus() {
		return childMenus;
	}

	public void setChildMenus(List<RoleMenuDto> childMenus) {
		this.childMenus = childMenus;
	}

}
