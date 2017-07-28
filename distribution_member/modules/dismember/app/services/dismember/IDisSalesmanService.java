package services.dismember;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;


public interface IDisSalesmanService {

	Map<String, Object> addSalesMan(Map<String, String> params);

	Map<String, Object> querySalesmansByCondition(Map<String, String> params);

	Map<String, Object> updateSalesman(Map<String, String> params);

	Map<String, Object> relatedDistributors(Map<String, String> params);

	Map<String, Object> getSalesmanMember(Map<String, String> params);

	Map<String, Object> gainMemberByCondition(Map<String, String> params);

	Map<String, Object> removeRelated(Map<String, String> params);

	Map<String, Object> deleteSalesman(Map<String, String> params);

	Map<String, Object> getAllUsers(Map<String, String> params);

	Map<String, Object> relatedMember(String email, JsonNode node);

	Map<String, Object> relatedSalesMan(Map<String, String> params);

	Map<String, Object> cancelEmpRelate(Integer salesManId);

	/**
	 * 查询关联分销商
	 * @author zbc
	 * @since 2017年3月15日 下午5:29:57
	 */
	List<String> relateAccounts(String email);

}
