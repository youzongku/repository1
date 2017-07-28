package mapper.dismember;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import dto.dismember.RoleMenuDto;
import entity.dismember.RoleMenus;

public interface RoleMenusMapper extends BaseMapper<RoleMenus> {

    int deleteRows(RoleMenus rm);

    @SuppressWarnings("rawtypes")
	List<Map> getAllMenuByRoleId(@Param("roleId") Integer roleId, @Param("parentId") Integer parentId);

	RoleMenus getRoleMapper(RoleMenus rm);

	List<Map> getAllMenuByParam(@Param("roleId")Integer roleId,@Param("ishfive")boolean ishfive);

    /**
     * 删除指定角色所关联的栏目
     */
    int deleteRoleMenus(Integer roleId);

}