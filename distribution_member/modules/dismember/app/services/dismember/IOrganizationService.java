package services.dismember;

import java.util.List;
import java.util.Map;

import entity.dismember.DisMember;
import entity.dismember.Organization;

public interface IOrganizationService {

	List<Organization> getChildOrganizations(Map<String, String[]> param);

	Map<String, Object> addOrganization(Map<String, String> params);

	Map<String, Object> updateOrganization(Map<String, String> params);
	
	Map<String, Object> deleteOrganzitionById(Map<String, String> params);

	Map<String, Object> queryHeaderByOrganizationId(Map<String, String> params);

	Map<String, Object> getOrganization(Map<String, String> params);

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月20日 上午10:58:39
	 */
	List<DisMember> getRelate(Integer id);

	/**
	 * 查询组织架构数据
	 * @param key 
	 * @param account 
	 * @return
	 */
	Map<String, Object> organizationalData(String account, String key);

}
