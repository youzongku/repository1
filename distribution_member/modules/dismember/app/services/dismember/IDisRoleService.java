package services.dismember;

import com.alibaba.fastjson.JSONObject;

/**
 * @Author LSL on 2016-09-14 15:36:15
 */
public interface IDisRoleService {

    /**
     * 分页查询角色
     * @return
     */
    String getRolesByPage(JSONObject params);

    /**
     * 添加新角色
     * @param params
     * @return
     */
    String addDisRole(JSONObject params);

    /**
     * 更新角色信息
     * @param params
     * @return
     */
    String updateDisRole(JSONObject params);

    /**
     * 删除指定角色
     * @param roleId
     * @return
     */
    String deleteDisRole(Integer roleId, Integer userId);

    /**
     * 获取指定角色信息
     * @param params
     * @return
     */
    String getRoleById(JSONObject params);

}
