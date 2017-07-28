package mapper.dismember;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import entity.dismember.DisMember;

/**
 * 
 * @author luwj
 *
 */
public interface DisMemberMapper extends BaseMapper<DisMember>{
	
	List<DisMember> selectByEmailList(@Param("emailList")List<String> emailList);
	
	@Override
	public int insertSelective(DisMember record);
	
	@Override
	public int updateByPrimaryKeySelective(DisMember record);
	
	/**
     * 根据用户唯一标识查询用户信息
     * @param disMember
     * @return
     */
    DisMember getMember(DisMember disMember);

    /**
     * 查询某一类用户的用户信息
     * @param member
     * @return
     */
	List<DisMember> queryMembers(DisMember member);

    /**
     * 根据条件分页查询用户信息记录数
     * @param map
     * @return
     */
    int getCountByPage(Map<String, Object> map);

    /**
     * 根据条件分页查询用户信息
     * @param map
     * @return
     */
    List<DisMember> getMembersByPage(Map<String, Object> map);

    /**
     * 根据等级ID或角色ID查询用户人数
     */
    int getCountByRoleIdOrRankId(@Param("roleId")Integer roleId, @Param("rankId")Integer rankId);

	List<DisMember> getMemberByCondition(Map<String, Object> map);

	int getMemberCountByCondition(Map<String, Object> map);
	
	/**
	 * 删除用户与子用户
	 * @param member
	 * @return
	 * @author huchuyin
	 * @date 2016年9月19日 下午3:28:49
	 */
	int delMemberAndChildMem(DisMember member);
	
	/**
	 * 根据用户ID查询与业务员关联数据
	 * @param id
	 * @return
	 * @author huchuyin
	 * @date 2016年9月19日 下午3:35:09
	 */
	Integer getMemberSalesCount(Integer id);
	
	/**
	 * 根据邮箱查询用户总数
	 * @param email
	 * @return
	 * @author huchuyin
	 * @date 2016年9月19日 下午4:19:35
	 */
	Integer getMemCountByEmail(String email);

	/**
	 * 根据id更新此分销商对应的业务员erp账号
	 * @param member
	 * @return
	 */
	int updateSalesErpByPrimaryKey(DisMember member);

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月20日 上午11:16:28
	 */
	List<DisMember> getMemberByOrgId(@Param("orgId")Integer id);

	/**
	 * @param userMap
	 * @return
	 */
	public int relatedMemberCount(Map<String, Object> userMap);

	/**
	 * @param userMap
	 * @return
	 */
	public List<DisMember> relatedMember(Map<String, Object> userMap);     
}