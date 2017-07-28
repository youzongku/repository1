package mapper.dismember;

import entity.dismember.DisRole;

import java.util.List;
import java.util.Map;

public interface DisRoleMapper extends BaseMapper<DisRole> {

    /**
     * 分页查询角色记录数
     * @return
     */
    int getCountByPage(Map<String, Object> map);

    /**
     * 分页查询角色
     * @return
     */
    List<DisRole> getRolesByPage(Map<String, Object> map);

}