package mapper.dismember;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import entity.dismember.DisCredit;

public interface DisCreditMapper {
	int deleteByPrimaryKey(Integer id);
	
	/**
	 * 描述：通过用户邮箱删除永久额度
	 * 2016年5月12日
	 * @param email
	 * @return
	 */
	int deleteByEmail(String email);

	int insert(DisCredit record);

	int insertSelective(DisCredit record);

	DisCredit selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(DisCredit record);

	int updateByPrimaryKey(DisCredit record);
	
	/**
	 * 根据条件分页查询信用用户数
	 * 
	 * @param map
	 * @return
	 */
	int getCountByPage(Map<String, Object> map);

	/**
	 * 根据条件分页查询用户信用额度
	 * 
	 * @param map
	 * @return
	 */
	List<DisCredit> getMembersByPage(Map<String, Object> map);

	/**
	 * 描述：通过用户邮箱,信用额度状态查询所对应的所有信用额度 2016年4月20日
	 * 
	 * @param map
	 *           信用度筛选条件
	 * @return
	 */
	List<DisCredit> getCreditsByCondition(Map<String, Object> map);

	DisCredit getDisCredit(@Param("credit") DisCredit credit, @Param("optype") Integer optype);

	int updateCredit(DisCredit newcredit);

	DisCredit getDisCreditInfo(@Param("email") String email);

	int updateStatusForOverDate();
	
	int updatestatusForisFinshed();

}