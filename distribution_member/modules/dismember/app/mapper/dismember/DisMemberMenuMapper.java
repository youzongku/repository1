package mapper.dismember;

import java.util.List;
import java.util.Map;

import entity.dismember.DisMemberMenu;

/**
 * 用户附加权限数据库操作类
 * @author huchuyin
 * @date 2016年9月13日 下午8:27:37
 */
public interface DisMemberMenuMapper extends BaseMapper<DisMemberMenu>{

    /**
     * 查询指定条件下的用户所关联的权限
     * @param queryMap
     * @return
     */
    List<Map> findMenuByParam(Map queryMap);
	
	/**
	 * 根据用户ID删除附加权限数据
	 * @param memberId
	 * @return
	 * @author huchuyin
	 * @date 2016年9月13日 下午8:27:48
	 */
	public int delMemberMenuByMem(Integer memberId);
	
	/**
	 * 批量新增用户附加权限数据
	 * @param memberMenuList
	 * @return
	 * @author huchuyin
	 * @date 2016年9月13日 下午8:28:26
	 */
	public int batchAddMemMenu(List<DisMemberMenu> memberMenuList);
}