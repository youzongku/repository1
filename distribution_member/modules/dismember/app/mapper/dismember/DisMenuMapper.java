package mapper.dismember;

import java.util.List;

import entity.dismember.DisMemberMenu;
import entity.dismember.DisMenu;

public interface DisMenuMapper extends BaseMapper<DisMenu> {

	List<DisMenu> getMenus(DisMenu menu);
	
	/**
	 * 根据用户ID查询用户所具有的权限
	 * @param memberId
	 * @return
	 * @author huchuyin
	 * @date 2016年9月13日 下午4:40:57
	 */
	List<DisMenu> getMemberMenuList(DisMemberMenu memberMenu);
}