package services.dismember.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import constant.dismember.Constant;
import dto.dismember.RoleMenuDto;
import entity.dismember.DisMember;
import entity.dismember.DisMemberMenu;
import entity.dismember.DisMenu;
import entity.dismember.RoleMenus;
import mapper.dismember.DisMemberMapper;
import mapper.dismember.DisMemberMenuMapper;
import mapper.dismember.DisMenuMapper;
import mapper.dismember.RoleMenusMapper;
import play.Logger;
import services.dismember.ILoginService;
import services.dismember.IMenuService;
import vo.dismember.LoginContext;

public class MenuServiceImpl implements IMenuService{

	@Inject
	private DisMenuMapper menuMapper;
	@Inject
	private RoleMenusMapper roleMenusMapper;
	@Inject
    private ILoginService loginService;
	@Inject
	private DisMemberMapper disMemberMapper;
    @Inject
    private DisMemberMenuMapper disMemberMenuMapper;

	@SuppressWarnings("rawtypes")
	@Override
	public List<DisMenu> getChildNodes(Map<String, String[]> param,Integer roleId, Integer userId) {
		DisMember dm = disMemberMapper.selectByPrimaryKey(userId);
		List<DisMenu> list = this.getMenuList(dm, null);
		List<Map> menusmapper = roleMenusMapper.getAllMenuByRoleId(roleId, null);
		List<Integer> mapperId = Lists.newArrayList();
		for (Map map : menusmapper) {
			mapperId.add(Integer.parseInt(map.get("menuid") + ""));
		}
		for (DisMenu disMenu : list) {
			if(null != mapperId && mapperId.contains(disMenu.getId())){
				disMenu.setChecked(true);
			}
		}
		return list;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public List<RoleMenuDto> getMenuByRoleId(Integer roleId) {
		//获取当前登录用户对应角色的菜单
        List<RoleMenuDto> roleMenuDtos = new ArrayList<RoleMenuDto>();
        List<Map> allMenu = roleMenusMapper.getAllMenuByRoleId(roleId, null);
        roleMenuDtos = this.getRoleMenuOfUser(allMenu);
		return roleMenuDtos;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<RoleMenuDto> getMenuByParam(DisMember member, boolean ishfive, Integer menuId) {
		//获取当前登录用户对应角色的菜单
        List<RoleMenuDto> roleMenuDtos = new ArrayList<RoleMenuDto>();
        List<Map> allMenu = null;
        if (member.getIfAddPermision()) {
            //有附加权限
            Map queryMap = Maps.newHashMap();
            queryMap.put("userId", member.getId());
            queryMap.put("ishfive", ishfive);
            allMenu = disMemberMenuMapper.findMenuByParam(queryMap);
        } else {
            //无附加权限
            allMenu = roleMenusMapper.getAllMenuByParam(member.getRoleId(), ishfive);
        }
        roleMenuDtos = this.getRoleMenuOfUser(allMenu);
        if(menuId != null){
        	Map<Boolean, List<RoleMenuDto>> seleMenu = roleMenuDtos.stream().collect(Collectors.groupingBy(e->e.getMenuId().equals(menuId)));
        	roleMenuDtos = seleMenu.get(true);
        }
		return roleMenuDtos;
	}
	
	@SuppressWarnings("rawtypes")
	private List<RoleMenuDto> getRoleMenuOfUser(List<Map> allMenu){
	    	//父节点菜单
	        List<RoleMenuDto> parentMenus = new ArrayList<RoleMenuDto>();
	        //子菜单map
	        Map<Integer,List<RoleMenuDto>> childMenusMap = new HashMap<Integer,List<RoleMenuDto>>();
	        //将所有菜单归类到父菜单和子菜单中
	        for (Map map : allMenu) {
				RoleMenuDto roleMenuDto = new RoleMenuDto();
				roleMenuDto.setButtonAuth(Boolean.valueOf(utils.dismember.StringUtils.getStringBlank(map.get("buttonauth"), false)));
			    boolean isParent = (boolean) map.get("isparent");
			    roleMenuDto.setIsParent(isParent);
	            roleMenuDto.setLevel(Integer.valueOf(map.get("level").toString()));
	            roleMenuDto.setMenuDescription((String)map.get("description"));
	            roleMenuDto.setMenuId((Integer) map.get("menuid"));
	            roleMenuDto.setMenuName((String)map.get("menuname"));
	            Integer parentId = (Integer) map.get("parentid");
	            roleMenuDto.setParentId(parentId);
	            roleMenuDto.setPosition((Integer) map.get("position"));
	            roleMenuDto.setRoleDesc((String)map.get("roledesc"));
	            roleMenuDto.setRoleId((Integer) map.get("roleid"));
	            roleMenuDto.setRoleName((String)map.get("rolename"));
	            roleMenuDto.setType((String)map.get("type"));
//	            roleMenuDto.setUpdateTime(DateFormatUtils.getFormatDateByStr((String)map.get("updatetime")));
	            if (isParent) {
	            	parentMenus.add(roleMenuDto);
				}else{
					List<RoleMenuDto> list = childMenusMap.get(parentId);
					if (CollectionUtils.isEmpty(list)) {
						list = new ArrayList<RoleMenuDto>();
						list.add(roleMenuDto);
						childMenusMap.put(parentId, list);
					}else{
						childMenusMap.get(parentId).add(roleMenuDto);
					}
				}
	        }
	        for (RoleMenuDto rmd : parentMenus) {
				rmd.setChildMenus(childMenusMap.get(rmd.getMenuId()));
			}
	        return parentMenus;
	    }

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> dealConfigure(Map<String, String[]> json) {
		Map<String, Object> result = Maps.newHashMap();
		Integer roleId = Integer.parseInt(json.get("roleId")[0]);
		List<Map> menusmapper = roleMenusMapper.getAllMenuByRoleId(roleId, null);
		// 去重后的所有栏目节点
		Set<Integer> setMenus = Sets.newHashSet();
		// 展开的节点 以下get值时，key值添加[]原因是由于前台不管传什么都会自动加这个，问题未找到。
		List<Integer> idms = new ArrayList<>();
		String[] itm = json.get("select[]");
		// 已经存在的节点
		List<Integer> ides = new ArrayList<>();
		// 未展开的父节点
		List<Integer> idps = new ArrayList<>();
		String[] itp = json.get("notExpend[]");
		if (null != itm) {
			for (int i = 0; i < itm.length; i++) {
				idms.add(Integer.parseInt(itm[i]));
			}
		}
		for (Map map : menusmapper) {
			ides.add(Integer.parseInt(map.get("menuid") + ""));
		}
		if (null != itp) {
			for (int i = 0; i < itp.length; i++) {
				idps.add(Integer.parseInt(itp[i]));
			}
		}
		getChild(setMenus, idps);
		// 去重
		setMenus.addAll(idms);
		setMenus.addAll(idps);
		//原有的栏目权限与现有栏目权限对比，找出差异，即原来有限制没有的栏目删除
		RoleMenus rm = null;
		if (setMenus.size() > 0) {
			for (Integer id : setMenus) {
				//以前不存在的新增
				if(!ides.contains(id)){
					rm = new RoleMenus();
					rm.setRoleid(roleId);
					rm.setMenuid(id);
					roleMenusMapper.insertSelective(rm);
				}else{
					ides.remove(id);
				}
			}
		} 
		if(ides.size() > 0){
			for (Integer ide : ides) {
				rm = new RoleMenus();
				rm.setRoleid(roleId);
				rm.setMenuid(ide);
				roleMenusMapper.deleteRows(rm);
			}
		}
		result.put("success", true);
		return result;
	}

	/**
	 * 查找树上未展开的父节点的子节点
	 * 
	 * @param setMenus
	 * @param idps
	 */
	private void getChild(Set<Integer> setMenus, List<Integer> idps) {
		if (null != idps && idps.size() > 0) {
			DisMenu menu = null;
			for (Integer parentId : idps) {
				if (parentId != 0) {
					setMenus.add(parentId);
				}
				menu = new DisMenu();
				menu.setParentid(parentId);
				List<DisMenu> list = menuMapper.getMenus(menu);
				List<Integer> ids = Lists.transform(list, m -> m.getId());
				if (null != ids && ids.size() > 0) {
					setMenus.addAll(ids);
					getChild(setMenus, ids);
				}
			}
		}

	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<Integer> getMapperRoleId(Integer roleId) {
		List<Map> menusmapper = roleMenusMapper.getAllMenuByRoleId(roleId, null);
		List<Integer> mapperId = Lists.newArrayList();
		for (Map map : menusmapper) {
			mapperId.add(Integer.parseInt(map.get("menuid") + ""));
		}
		return mapperId;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String checkMenuAuthority(LoginContext lc, Integer position) {
		JSONObject result = new JSONObject();
		result.put("success", true);
		Integer userId = Integer.valueOf(lc.getUserID());
		DisMember member = disMemberMapper.selectByPrimaryKey(userId);
		if(null != member && member.getRoleId() != 2){
            boolean isFlag = false;
            List<Map> allMenu = null;
            if (member.getIfAddPermision()) {
                //有附加权限
                Map queryMap = Maps.newHashMap();
                queryMap.put("userId", member.getId());
                allMenu = disMemberMenuMapper.findMenuByParam(queryMap);
            } else {
                //无附加权限
                allMenu = roleMenusMapper.getAllMenuByRoleId(member.getRoleId(), null);
            }
            if (position != null) {
                for (Map map : allMenu) {
                    if (position.equals((Integer) map.get("position"))) {
                        isFlag = true;
                        break;
                    }
                }
            }
            result.put("isFlag", isFlag);
            return result.toString();
		}
		
		result.put("isFlag", false);
		return result.toString();
	}

	@Override
	public Map<String, Object> createMenu(JsonNode node) {
		Map<String, Object> result = Maps.newHashMap();
		DisMenu menu = new DisMenu();
		menu.setIsParent(node.get("isParent").asBoolean());
		menu.setDescription(node.get("menuName").asText());
		menu.setLevel(node.get("level").asInt());
		menu.setName(node.get("menuName").asText());
		menu.setPosition(node.get("position").asInt());
		menu.setParentid(node.get("parentId").asInt());
		menuMapper.insertSelective(menu);
		RoleMenus rm = new RoleMenus();
		rm.setRoleid(1);
		rm.setMenuid(menu.getId());
		roleMenusMapper.insertSelective(rm);
		result.put("suc", "SUCCESS");
		return result;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public List<DisMenu> getLoginMemMenuList(Integer roleId) {
		List<DisMenu> disMenuList = Lists.newArrayList();
		//获取登录信息
		LoginContext lc = loginService.getLoginContext(Constant.LOGIN_FROM_MARK_BACK);
		//根据ID获取登陆者是否有附加权限
		DisMember member = disMemberMapper.selectByPrimaryKey(Integer.valueOf(lc.getUserID()));
		if(member != null) {
			disMenuList = this.getMenuList(member, null);
			//根据所传角色ID查询所关联的角色权限数据，控制选中
			List<Map> roleMenuList = roleMenusMapper.getAllMenuByRoleId(roleId, null);
			List<Integer> mapperId = Lists.newArrayList();
			for (Map map : roleMenuList) {
				//将所查询出的栏目数据ID，保存在列表中，用户下个循环判断
				mapperId.add(Integer.parseInt(map.get("menuid") + ""));
			}
			for (DisMenu disMenu:disMenuList) {
				//若所查询出的栏目数据ID包含于所有栏目列表中，则设置选中
				if(null != mapperId && mapperId.contains(disMenu.getId())){
					disMenu.setChecked(true);
				} 
			}
		}
		return disMenuList;
	}
	
	/**
	 * 获取用户所持有的所有栏目列表
	 * @param member
	 * @param parentId
	 * @return
	 * @author huchuyin
	 * @date 2016年9月13日 下午6:15:56
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List<DisMenu> getMenuList(DisMember member,Integer parentId) {
		List<DisMenu> disMenuList = Lists.newArrayList();
		if(member.getIfAddPermision()) {
			Logger.info(this.getClass().getName()+" getMenuList hasAddPermison");
			//有附加权限时，查询用户附加权限数据
			DisMemberMenu memberMenu = new DisMemberMenu();
			memberMenu.setMemberId(member.getId());
			memberMenu.setParentid(parentId);
			disMenuList = menuMapper.getMemberMenuList(memberMenu);
			return disMenuList;
		}
		
		Logger.info(this.getClass().getName()+" getMenuList notAddPermison");
		//没有附件权限时，查询用户角色权限数据
		List<Map> menusmapper = roleMenusMapper.getAllMenuByRoleId(member.getRoleId(), parentId);
		DisMenu menu = null;
		for (Map map : menusmapper) {
			//将查询出的栏目数据转换到栏目实体类中
			menu = new DisMenu();
			menu.setId((Integer) map.get("menuid"));
			menu.setName((String) map.get("menuname"));
			menu.setLevel((Integer) map.get("level"));
			menu.setPosition((Integer) map.get("position"));
			menu.setParentid((Integer) map.get("parentid"));
			menu.setDescription((String) map.get("description"));
			menu.setIsParent((Boolean) map.get("isparent"));
			menu.setType((String) map.get("type"));
			//循环增加元素
			disMenuList.add(menu);
		}
		return disMenuList;
	}
	
	@Override
	public List<DisMenu> getMemberMenuList(Integer memberId) {
		List<DisMenu> disMenuList = Lists.newArrayList();
		List<DisMenu> disMenuList2 = Lists.newArrayList();
		//获取登录信息
		LoginContext lc = loginService.getLoginContext(Constant.LOGIN_FROM_MARK_BACK);
		//根据ID获取登陆者是否有附加权限
		DisMember member = disMemberMapper.selectByPrimaryKey(Integer.valueOf(lc.getUserID()));
		if(member != null) {
			disMenuList = this.getMenuList(member, null);
			//根据所传的用户ID，查询用户信息
			DisMember member2 = disMemberMapper.selectByPrimaryKey(memberId);
			disMenuList2 = this.getMenuList(member2, null);
			List<Integer> mapperId = Lists.newArrayList();
			for (DisMenu menu : disMenuList2) {
				//将所查询出的栏目数据ID，保存在列表中，用户下个循环判断
				mapperId.add(menu.getId());
			}
			for (DisMenu disMenu:disMenuList) {
				//若所查询出的栏目数据ID包含于所有栏目列表中，则设置选中
				if(null != mapperId && mapperId.contains(disMenu.getId())){
					disMenu.setChecked(true);
				}
			}
		}
		return disMenuList;
	}
	
}
