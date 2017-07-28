package services.dismember.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import dto.dismember.RoleDto;
import entity.dismember.DisMember;
import entity.dismember.DisRole;
import mapper.dismember.DisMemberMapper;
import mapper.dismember.DisRoleMapper;
import mapper.dismember.RoleMenusMapper;
import org.joda.time.DateTime;
import org.springframework.util.CollectionUtils;
import play.Logger;
import services.dismember.IDisRoleService;
import utils.dismember.StringUtils;
import vo.dismember.Page;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author LSL on 2016-09-14 15:36:29
 */
public class DisRoleService implements IDisRoleService {

    @Inject
    private DisRoleMapper disRoleMapper;

    @Inject
    private DisMemberMapper disMemberMapper;

    @Inject
    private RoleMenusMapper roleMenusMapper;

    @Override
    public String getRolesByPage(JSONObject params) {
        Logger.debug("getRolesByPage    params----->" + params.toString());
        JSONObject result = new JSONObject();
        Integer currPage = params.containsKey("currPage") ? params.getInteger("currPage") : null;
        Integer pageSize = params.containsKey("pageSize") ? params.getInteger("pageSize") : null;
        String createUser = params.containsKey("createUser") ? params.getString("createUser") : null;
        Integer userId = params.containsKey("userId") ? params.getInteger("userId") : null;
        Map<String, Object> queryMap = Maps.newHashMap();
        if (currPage != null && pageSize != null) {
            queryMap.put("pageSize", pageSize);
            queryMap.put("startNum", (currPage - 1) * pageSize);
        }
        
        DisMember disMember = disMemberMapper.selectByPrimaryKey(userId);
        if (disMember == null) {
            result.put("suc", false);
            result.put("msg", "当前用户不存在");
            return result.toString();
        }
        if (disMember.getRoleId() == 2) {
            result.put("suc", false);
            result.put("msg", "分销商用户无权查询角色");
            return result.toString();
        }
        
        if (disMember.getRoleId() == 1) {
            //超级管理员
            result = this.getRoleDTOs(currPage, pageSize, queryMap);
        } else {
            //非超级管理员或分销商用户
            queryMap.put("createUser", createUser);
            result = this.getRoleDTOs(currPage, pageSize, queryMap);
        }
        return result.toString();
    }

    private JSONObject getRoleDTOs(Integer currPage, Integer pageSize, Map<String, Object> queryMap) {
        JSONObject result = new JSONObject();
        List<DisRole> disRoles = disRoleMapper.getRolesByPage(queryMap);
        List<RoleDto> roleDtos = new ArrayList<RoleDto>();
        Integer rows = null;
        if (CollectionUtils.isEmpty(disRoles)) {
            result.put("page", new Page<RoleDto>(currPage, pageSize, rows, roleDtos));
            result.put("suc", true);
            return result;
        }
        
        if (currPage != null && pageSize != null) {
            rows = disRoleMapper.getCountByPage(queryMap);
        }
        roleDtos = Lists.transform(disRoles, role -> {
            RoleDto dto = new RoleDto();
            dto.setId(role.getId());
            dto.setName(role.getRoleName());
            dto.setDesc(role.getRoleDesc());
            dto.setCreateTime(new DateTime(role.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
            dto.setCreateUser(role.getCreateUser());
            dto.setIsactive(role.getIsactive());
            return dto;
        });
        result.put("page", new Page<RoleDto>(currPage, pageSize, rows, roleDtos));
        result.put("suc", true);
        return result;
    }

    @Override
    public String addDisRole(JSONObject params) {
        Logger.info("addDisRole    params----->" + params.toString());
        JSONObject result = new JSONObject();
        String roleName = params.containsKey("roleName") ? params.getString("roleName") : null;
        String roleDesc = params.containsKey("roleDesc") ? params.getString("roleDesc") : null;
        Boolean ismessage = params.containsKey("ismessage") ? params.getBoolean("ismessage") : null;
        String createUser = params.containsKey("createUser") ? params.getString("createUser") : null;
        Map<String, Object> queryMap = Maps.newHashMap();
        queryMap.put("roleName", roleName);
        queryMap.put("createUser", createUser);
        
        int count = disRoleMapper.getCountByPage(queryMap);
        if (count != 0) {
        	result.put("suc", false);
            result.put("msg", "当前角色名称已存在");
            return result.toString();
        }
        
        DisRole role = new DisRole();
        role.setRoleName(roleName);
        role.setRoleDesc(roleDesc);
        role.setCreateDate(new Date());
        role.setCreateUser(createUser);
        role.setIsmessage(ismessage);
        int line = disRoleMapper.insertSelective(role);
        Logger.info("addDisRole    [insert DisRole]line----->" + line);
        result.put("suc", true);
        return result.toString();
    }

    @Override
    public String updateDisRole(JSONObject params) {
        Logger.info("updateDisRole    params----->" + params.toString());
        JSONObject result = new JSONObject();
        Integer id = params.containsKey("rid") ? params.getInteger("rid") : null;
        String roleName = params.containsKey("roleName") ? params.getString("roleName") : null;
        String roleDesc = params.containsKey("roleDesc") ? params.getString("roleDesc") : null;
        Boolean ismessage = params.containsKey("ismessage") ? params.getBoolean("ismessage") : null;
        DisRole role = new DisRole();
        role.setId(id);
        role.setRoleName(roleName);
        role.setRoleDesc(roleDesc);
        role.setIsmessage(ismessage);
        String flag = params.getString("flag");
        if(StringUtils.isNotBlankOrNull(flag)){
        	if("true".equals(flag)){
        		role.setIsactive(false);
        	}else if("false".equals(flag)){
        		role.setIsactive(true);
        	}
        }
        int line = disRoleMapper.updateByPrimaryKeySelective(role);
        Logger.info("updateDisRole    [update DisRole]line----->" + line);
        result.put("suc", true);
        return result.toString();
    }

    @Override
    public String deleteDisRole(Integer roleId, Integer userId) {
        
        DisMember disMember = disMemberMapper.selectByPrimaryKey(userId);
        
        JSONObject result = new JSONObject();
        if (disMember == null) {
            result.put("suc", false);
            result.put("msg", "不存在此分销商用户");
            return result.toString();
        }
        
        if (disMember.getRoleId() == 2) {
            result.put("suc", false);
            result.put("msg", "分销商用户无权删除角色");
            return result.toString();
        }
        
        if (roleId == 1 || roleId == 2) {
            result.put("suc", false);
            result.put("msg", "当前角色禁止删除");
            return result.toString();
        }
        
        DisRole role = disRoleMapper.selectByPrimaryKey(roleId);
        //除超级管理员外，当前用户只能删除自己创建的角色。
        if (disMember.getRoleId() > 2 && !disMember.getEmail().equals(role.getCreateUser())) {
            result.put("suc", false);
            result.put("msg", "当前用户只能删除自己创建的角色");
            return result.toString();
        }
        
        int count = disMemberMapper.getCountByRoleIdOrRankId(roleId, null);
        //未使用的角色才可以删除
        if (count > 0) {
            result.put("suc", false);
            result.put("msg", "当前角色正在使用，不能删除。");
            return result.toString();
        }
        
        int line = disRoleMapper.deleteByPrimaryKey(roleId);
        Logger.info("deleteDisRole    [delete DisRole]line----->" + line);
        if (line > 0) {
            int lines = roleMenusMapper.deleteRoleMenus(roleId);
            Logger.info("deleteDisRole    [delete DisRoleMenu]lines----->" + lines);
        }
        result.put("suc", true);
        return result.toString();
    }

	@Override
	public String getRoleById(JSONObject params) {
        JSONObject result = new JSONObject();
        Integer roleId = params.containsKey("rid") ? params.getInteger("rid") : null;
		DisRole role = disRoleMapper.selectByPrimaryKey(roleId);
		if (role == null){
			result.put("suc", false);
			result.put("msg", "没有查询到指定角色");
			return result.toString();
		}
		
		result.put("suc", true);
        result.put("data", role);
		return result.toString();
	}
}
