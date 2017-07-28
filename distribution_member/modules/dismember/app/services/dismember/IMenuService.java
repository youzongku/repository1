package services.dismember;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import dto.dismember.RoleMenuDto;

import entity.dismember.DisMember;
import entity.dismember.DisMenu;
import vo.dismember.LoginContext;

public interface IMenuService {

	public List<RoleMenuDto> getMenuByRoleId(Integer roleId);
	
	List<DisMenu> getChildNodes(Map<String, String[]> param, Integer roleId, Integer userId);

	public Map<String, Object> dealConfigure(Map<String, String[]> json);

	public List<Integer> getMapperRoleId(Integer roleId);
	
	public String checkMenuAuthority(LoginContext lc, Integer position);

	public Map<String, Object> createMenu(JsonNode node);

	public List<RoleMenuDto> getMenuByParam(DisMember member, boolean ishfive, Integer menuId);

	/**
	 * 根据用户与角色ID查询栏目列表数据
	 * @param roleId
	 * @return
	 * @author huchuyin
	 * @date 2016年9月13日 下午6:01:27
	 */
	public List<DisMenu> getLoginMemMenuList(Integer roleId);
	
	/**
	 * 根据用户信息获取栏目
	 * @param member
	 * @param parentId
	 * @return
	 * @author huchuyin
	 * @date 2016年9月13日 下午7:29:19
	 */
	public List<DisMenu> getMenuList(DisMember member,Integer parentId);
	
	/**
	 * 根据用户信息获取栏目
	 * @param memberId
	 * @return
	 * @author huchuyin
	 * @date 2016年9月13日 下午8:16:59
	 */
	public List<DisMenu> getMemberMenuList(Integer memberId);

}
