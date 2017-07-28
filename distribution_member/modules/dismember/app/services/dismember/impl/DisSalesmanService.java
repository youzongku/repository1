package services.dismember.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import entity.dismember.DisHeaderSalesman;
import entity.dismember.DisMember;
import entity.dismember.DisSalesman;
import entity.dismember.DisSalesmanMember;
import entity.dismember.EmpSalesManMapper;
import entity.dismember.Organization;
import mapper.dismember.DisHeaderSalesmanMapper;
import mapper.dismember.DisMemberMapper;
import mapper.dismember.DisSalesmanMapper;
import mapper.dismember.DisSalesmanMemberMapper;
import mapper.dismember.EmpSalesManMapperMapper;
import mapper.dismember.OrganizationMapper;
import play.Logger;
import services.dismember.IDisMemberService;
import services.dismember.IDisSalesmanService;
import vo.dismember.Page;

public class DisSalesmanService implements IDisSalesmanService {

	@Inject
	private DisSalesmanMapper disSalesmanMapper;
	@Inject
	private DisHeaderSalesmanMapper disHeaderSalesmanMapper;
	@Inject
	private DisSalesmanMemberMapper disSalesmanMemberMapper;
	@Inject
	private DisMemberMapper disMemberMapper;
	@Inject
	private IDisMemberService memberService;
	@Inject
	private OrganizationMapper organizationMapper;
	@Inject
	private EmpSalesManMapperMapper empMapper;
	@Inject
	private DisMemberService disMemberService;

	@Override
	public Map<String, Object> addSalesMan(Map<String, String> params) {
		Map<String, Object> result = Maps.newHashMap();
		Map<String, Object> map = Maps.newHashMap();
		// TODO 查询组织架构类型 若为2 则走新增员工逻辑
		Organization organ = organizationMapper.selectOrganByHeaderId(Integer.valueOf(params.get("headerId")));
		if (organ == null) {
			result.put("suc", false);
			result.put("msg", "找不到对应节点");
			return result;
		}

		Integer nodetype = organ.getNodeType() != null ? organ.getNodeType() : 1;
		String desc = "员工";
		result.put("suc", false);
		map.put("name",
				params.containsKey("name") && !Strings.isNullOrEmpty(params.get("name")) ? params.get("name") : null);
		map.put("tel",
				params.containsKey("tel") && !Strings.isNullOrEmpty(params.get("tel")) ? params.get("tel") : null);
		map.put("nodetype", nodetype);
		List<DisSalesman> disSalesmans = disSalesmanMapper.querySalesmansByCondition(map);
		if (disSalesmans != null && disSalesmans.size() > 0) {
			result.put("msg", "此" + desc + "已经存在");
			return result;
		}
		DisSalesman salesman = new DisSalesman();
		salesman.setName(
				params.containsKey("name") && !Strings.isNullOrEmpty(params.get("name")) ? params.get("name") : null);
		salesman.setErp(
				params.containsKey("erp") && !Strings.isNullOrEmpty(params.get("erp")) ? params.get("erp") : null);
		salesman.setTel(
				params.containsKey("tel") && !Strings.isNullOrEmpty(params.get("tel")) ? params.get("tel") : null);
		salesman.setCreateDate(new Date());
		salesman.setNodeType(nodetype);
		int insertSalesmanCount = disSalesmanMapper.insertSelective(salesman);
		if (insertSalesmanCount < 1) {
			result.put("msg", "添加" + desc + "失败");
			return result;
		}

		DisHeaderSalesman headerSalesman = new DisHeaderSalesman();
		headerSalesman.setHeaderid(params.containsKey("headerId") && !Strings.isNullOrEmpty(params.get("headerId"))
				? Integer.valueOf(params.get("headerId")) : null);
		headerSalesman.setSalesmanid(salesman.getId());
		int insertHeaderSalesmanCount = disHeaderSalesmanMapper.insertSelective(headerSalesman);
		if (insertHeaderSalesmanCount < 1) {
			result.put("msg", "添加" + desc + "与负责人对应关系失败");
			return result;
		}

		result.put("suc", true);
		result.put("msg", desc + "添加成功");
		return result;
	}

	@Override
	public Map<String, Object> querySalesmansByCondition(Map<String, String> params) {
		Map<String, Object> result = Maps.newHashMap();
		Integer id = params.containsKey("id") ? Integer.valueOf(params.get("id")) : null;
		Integer currPage = params.containsKey("currPage") ? Integer.valueOf(params.get("currPage")) : 1;
		Integer pageSize = params.containsKey("pageSize") ? Integer.valueOf(params.get("pageSize")) : 10;
		String desc = params.containsKey("desc") ? params.get("desc") : null;
		Integer nodeType = params.containsKey("nodeType") ? Integer.valueOf(params.get("nodeType")) : null;
		Boolean notRelation = params.containsKey("notRelation") ? Boolean.valueOf(params.get("notRelation")) : null;
		Integer empId = params.containsKey("empId") ? Integer.valueOf(params.get("empId")) : null;
		Map<String, Object> map = Maps.newHashMap();
		map.put("pageSize", pageSize);
		map.put("currPage", currPage);
		map.put("id", id);
		map.put("desc", desc);
		map.put("empId", empId);
		if (id != null) {
			Organization organ = organizationMapper.selectOrganByHeaderId(id);
			map.put("nodeType", organ != null ? organ.getNodeType() : null);
		}
		map.put("notRelation", notRelation);
		if (nodeType != null) {
			map.put("nodeType", nodeType);
		}
		List<DisSalesman> list = disSalesmanMapper.querySalesmansByCondition(map);
		int rows = disSalesmanMapper.getCountByCondition(map);
		if (list == null) {
			result.put("suc", false);
			result.put("msg", "查询员工信息异常");
			return result;
		}
		// 查询每个员工所对应的分销商数量
		DisSalesmanMember salesmanMember = new DisSalesmanMember();
		for (DisSalesman salesman : list) {
			salesmanMember.setSalesmanid(salesman.getId());
			salesman.setMemberCount(disSalesmanMemberMapper.getCountByCondition(salesmanMember));
			salesman.setSalesManCount(empMapper.selectBySaleManId(salesman.getId()).size());
		}
		result.put("suc", true);
		result.put("page", new Page<DisSalesman>(currPage, pageSize, rows, list));
		return result;
	}

	@Override
	public Map<String, Object> updateSalesman(Map<String, String> params) {
		Map<String, Object> result = Maps.newHashMap();
		result.put("suc", false);
		Map<String, Object> map = Maps.newHashMap();
		if ((params.containsKey("name") && params.containsKey("oldname")
				&& !params.get("name").equals(params.get("oldname")))
				|| (params.containsKey("tel") && params.containsKey("oldTel")
						&& !params.get("tel").equals(params.get("oldTel")))) {
			map.put("name", params.containsKey("name") && !Strings.isNullOrEmpty(params.get("name"))
					? params.get("name") : null);
			map.put("tel",
					params.containsKey("tel") && !Strings.isNullOrEmpty(params.get("tel")) ? params.get("tel") : null);
			List<DisSalesman> disSalesmans = disSalesmanMapper.querySalesmansByCondition(map);
			if (disSalesmans != null && disSalesmans.size() > 0) {
				result.put("msg", "此员工已经存在");
				return result;
			}
		}
		if (params.containsKey("account") && !Strings.isNullOrEmpty(params.get("account"))) {
			map = Maps.newHashMap();
			map.put("email", params.get("account"));
			map.put("roleId", 0);
			List<DisMember> members = disMemberMapper.getMembersByPage(map);
			if (members == null || members.size() == 0) {
				result.put("msg", "该账户不存在，请核对");
				return result;
			}
			// 判断该后台账户是否已经被其他员工关联过
			DisSalesman saleSearch = new DisSalesman();
			saleSearch.setAccount(params.get("account"));
			saleSearch.setName(params.get("name"));
			List<DisSalesman> sales = disSalesmanMapper.selectByAccountAndNoName(saleSearch);// 查询此后台账户，是否被其他员工关联过
			if (sales.size() > 0) {
				result.put("msg", "该后台账户已经被关联过，请核对");
				return result;
			}
		}
		DisSalesman salesman = new DisSalesman();
		salesman.setId(Integer.valueOf(params.get("id")));
		salesman.setName(
				params.containsKey("name") && !Strings.isNullOrEmpty(params.get("name")) ? params.get("name") : null);
		salesman.setErp(
				params.containsKey("erp") && !Strings.isNullOrEmpty(params.get("erp")) ? params.get("erp") : null);
		salesman.setAccount(params.containsKey("account") && !Strings.isNullOrEmpty(params.get("account"))
				? params.get("account") : null);
		salesman.setTel(
				params.containsKey("tel") && !Strings.isNullOrEmpty(params.get("tel")) ? params.get("tel") : null);
		salesman.setUpdateDate(new Date());
		salesman.setWorkNo(params.containsKey("workNo") ? params.get("workNo") : null);
		int line = disSalesmanMapper.updateByPrimaryKeySelective(salesman);
		if (line > 0) {
			result.put("suc", true);
			result.put("msg", "修改员工成功");
			return result;
		}

		result.put("msg", "修改员工失败");
		return result;
	}

	@Override
	public Map<String, Object> deleteSalesman(Map<String, String> params) {
		Map<String, Object> result = Maps.newHashMap();
		result.put("suc", false);
		int line = disSalesmanMapper.deleteByPrimaryKey(Integer.valueOf(params.get("saleid")));
		if (line < 1) {
			result.put("msg", "删除员工失败");
			return result;
		}

		DisHeaderSalesman headerSalesman = new DisHeaderSalesman();
		headerSalesman.setHeaderid(params.get("headerid") == null ? null : Integer.valueOf(params.get("headerid")));
		headerSalesman.setSalesmanid(Integer.valueOf(params.get("saleid")));
		line = disHeaderSalesmanMapper.deleteByConditon(headerSalesman);
		if (line < 1) {
			result.put("suc", false);
			result.put("msg", "删除员工与责任人的对应关系失败");
			return result;
		}

		result.put("suc", true);
		result.put("msg", "删除员工成功");
		return result;
	}

	/**
	 * 根据条件得到所有的分销商
	 */
	@Override
	public Map<String, Object> getAllUsers(Map<String, String> params) {
		Map<String, Object> result = Maps.newHashMap();
		Integer currPage = params.containsKey("currPage") ? Integer.valueOf(params.get("currPage")) : 1;
		Integer pageSize = params.containsKey("pageSize") ? Integer.valueOf(params.get("pageSize")) : 10;
		Map<String, Object> map = Maps.newHashMap();
		map.put("currPage", currPage);
		map.put("pageSize", pageSize);
		map.put("startNum", (currPage - 1) * pageSize);
		map.put("roleId", Integer.valueOf(params.get("role")));
		map.put("rankId", params.containsKey("rank") && !Strings.isNullOrEmpty(params.get("rank"))
				? Integer.valueOf(params.get("rank")) : null);
		map.put("search", params.containsKey("search") ? params.get("search") : "");
		map.put("salesmanid", Integer.valueOf(params.get("salesmanid")));
		List<DisMember> members = new ArrayList<DisMember>();
		members = disMemberMapper.getMembersByPage(map);// 得到所有的分销商
		int rows = disMemberMapper.getCountByPage(map);
		result.put("suc", true);
		result.put("page", new Page<DisMember>(currPage, pageSize, rows, members));
		return result;
	}

	@Override
	public Map<String, Object> relatedDistributors(Map<String, String> params) {
		Map<String, Object> result = Maps.newHashMap();
		String str = params.get("memberids");
		List<String> strList = Arrays.asList(str.split(","));
		DisSalesmanMember salesmanMember = new DisSalesmanMember();
		salesmanMember.setSalesmanid(Integer.valueOf(params.get("salesmanid")));
		DisMember member = null;
		boolean flag = true;
		for (String memberId : strList) {
			salesmanMember.setMemberid(Integer.valueOf(memberId));
			int line = disSalesmanMemberMapper.insertSelective(salesmanMember);
			member = disMemberMapper.selectByPrimaryKey(Integer.valueOf(memberId));
			member.setSalesmanErp(params.get("salesmanErp"));
			disMemberService.update(member);
			if (line == 0) {
				flag = false;
			}
		}
		if (flag) {
			result.put("suc", true);
			result.put("msg", "分销商关联成功");
			return result;
		}

		result.put("suc", false);
		result.put("msg", "部分分销商关联失败");
		return result;
	}

	@Override
	public Map<String, Object> getSalesmanMember(Map<String, String> params) {
		Map<String, Object> result = Maps.newHashMap();
		DisSalesmanMember salesmanMember = new DisSalesmanMember();
		salesmanMember
				.setSalesmanid(params.get("salesmanid") == null ? null : Integer.valueOf(params.get("salesmanid")));
		salesmanMember
				.setMemberid(params.get("distributeid") == null ? null : Integer.valueOf(params.get("distributeid")));
		List<DisSalesmanMember> list = disSalesmanMemberMapper.getDisSalesmanMember(salesmanMember);
		if (list != null && list.size() != 0) {
			result.put("code", 3);
			result.put("data", list);
			return result;
		}

		result.put("code", 4);
		result.put("msg", "没有查询到指定分销商");
		return result;
	}

	@Override
	public Map<String, Object> gainMemberByCondition(Map<String, String> params) {
		Map<String, Object> result = Maps.newHashMap();
		Map<String, Object> map = Maps.newHashMap();
		String str = params.get("mids");
		List<String> strList = Arrays.asList(str.split(","));
		List<Integer> mids = new ArrayList<Integer>();
		for (String id : strList) {
			mids.add(Integer.valueOf(id));
		}
		Integer currPage = params.containsKey("currPage") ? Integer.valueOf(params.get("currPage")) : 1;
		Integer pageSize = params.containsKey("pageSize") ? Integer.valueOf(params.get("pageSize")) : 10;
		String desc = params.containsKey("desc") ? params.get("desc") : null;
		Integer salesmanid = params.containsKey("salesmanid") ? Integer.valueOf(params.get("salesmanid")) : null;
		map.put("pageSize", pageSize);
		map.put("currPage", currPage);
		map.put("ids", mids);
		map.put("desc", desc);
		map.put("salesmanid", salesmanid);
		List<DisMember> distributes = disMemberMapper.getMemberByCondition(map);
		int row = disMemberMapper.getMemberCountByCondition(map);
		if (distributes == null) {
			result.put("suc", false);
			result.put("msg", "查询分销商信息异常");
			return result;
		}

		result.put("suc", true);
		result.put("page", new Page<DisMember>(currPage, pageSize, row, distributes));
		return result;
	}

	@Override
	public Map<String, Object> removeRelated(Map<String, String> params) {
		Map<String, Object> result = Maps.newHashMap();
		DisSalesmanMember salesmanMember = new DisSalesmanMember();
		salesmanMember
				.setSalesmanid(params.get("salesmanid") == null ? null : Integer.valueOf(params.get("salesmanid")));
		salesmanMember.setMemberid(params.get("memberid") == null ? null : Integer.valueOf(params.get("memberid")));
		int line = disSalesmanMemberMapper.deleteByCondition(salesmanMember);

		// 查询到邀请码所属的分销商
		DisMember disMember = disMemberMapper.selectByPrimaryKey(salesmanMember.getMemberid());
		DisMember inviteMember = new DisMember();
		inviteMember.setSelfInviteCode(disMember.getRegisterInviteCode());
		inviteMember = disMemberMapper.getMember(inviteMember);

		if (inviteMember == null) {
			disMember.setSalesmanErp("10176");
		} else {
			String account = memberService.getCustomerServiceAccount(inviteMember.getEmail()).get("account").toString();
			Logger.info("查询到的account={}", account);
			disMember.setSalesmanErp(account);
		}
		disMemberService.update(disMember);
		if (line > 0) {
			result.put("suc", true);
			result.put("msg", "员工删除成功");
			return result;
		}

		result.put("suc", false);
		result.put("msg", "员工删除失败");
		return result;
	}

	/*
	 * mark: 1、表示查询错误，未获取到参数 2、查询所有的用户 3、查询员工关联的用户
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * services.dismember.IDisSalesmanService#relatedMember(java.lang.String,
	 * com.fasterxml.jackson.databind.JsonNode)
	 */
	@Override
	public Map<String, Object> relatedMember(String email, JsonNode node) {
		Map<String, Object> result = Maps.newHashMap();
		if (node == null) {
			result.put("mark", 1);
			result.put("data", new Page<>(1, 10, 0, Lists.newArrayList()));
			return result;
		}

		Map<String, Object> map = Maps.newHashMap();
		map.put("account", email);
		// 1、获取账号关联的员工
		List<DisSalesman> list = disSalesmanMapper.querySalesmansByCondition(map);
		Integer currPage = node.has("currPage") ? node.get("currPage").asInt() : 1;
		Integer pageSize = node.has("pageSize") ? node.get("pageSize").asInt() : null;
		Map<String, Object> userMap = Maps.newHashMap();
		userMap.put("currPage", currPage);
		userMap.put("pageSize", pageSize);
		userMap.put("roleId", 2);
		userMap.put("search", node.has("search") ? node.get("search").asText() : "");
		userMap.put("notType", node.has("notType") ? node.get("notType").asInt() : null);// 表示不属于某个类型的分销商

		if (CollectionUtils.isEmpty(list)) {
			// 为空则查询所有用户
			result.put("mark", 2);
			result.put("data", memberService.allDistributions(userMap));
			return result;
		}

		// 一个后台账号只能被一个员工关联
		DisSalesman salesman = list.get(0);
		map = Maps.newHashMap();
		map.put("salesmanId", salesman.getId());
		list = disSalesmanMapper.querySalesmansByCondition(map);// 查询出此业务员所属区域下所有子区域的业务员
		List<Integer> saleMans = Lists.newArrayList();
		// 获取nodeType 为2的员工
		Map<Boolean, List<DisSalesman>> empMap = list.stream()
				.collect(Collectors.partitioningBy(e -> e.getNodeType() == 2));
		List<DisSalesman> empList = empMap.get(true);
		if (!CollectionUtils.isEmpty(empList)) {
			List<EmpSalesManMapper> esList = empMapper.selectBySaleManIds(Lists.transform(empList, i -> i.getId()));
			saleMans.addAll(Lists.transform(esList, i -> i.getSalesmanId()));
		}
		if (!CollectionUtils.isEmpty(empMap.get(false))) {
			// 不为空则取员工Id，用来获取员工关联的分销商
			saleMans.addAll(Lists.transform(empMap.get(false), i -> i.getId()));
		}
		userMap.put("saleMans", saleMans);
		Integer total = disSalesmanMapper.getRelatedMemberCount(userMap);
		result.put("mark", 3);
		result.put("data", new Page<>(currPage, pageSize, total, disSalesmanMapper.getRelatedMember(userMap)));
		return result;
	}
	
	
	
	
	
	
	
	
	

	@Override
	public Map<String, Object> relatedSalesMan(Map<String, String> params) {
		Map<String, Object> result = Maps.newHashMap();
		String str = params.get("memberids");
		List<String> strList = Arrays.asList(str.split(","));
		EmpSalesManMapper empSalesMan = new EmpSalesManMapper();
		empSalesMan.setEmpId(Integer.valueOf(params.get("salesmanid")));
		boolean flag = true;
		for (String memberId : strList) {
			empSalesMan.setSalesmanId(Integer.valueOf(memberId));
			int line = empMapper.insertSelective(empSalesMan);
			if (line == 0) {
				flag = false;
			}
		}
		if (flag) {
			result.put("suc", true);
			result.put("msg", "员工关联成功");
			return result;
		}

		result.put("suc", false);
		result.put("msg", "部分员工关联失败");
		return result;
	}

	@Override
	public Map<String, Object> cancelEmpRelate(Integer salesManId) {
		Map<String, Object> res = Maps.newHashMap();
		boolean red = empMapper.deleteBySalemanId(salesManId) > 0;
		res.put("suc", red);
		res.put("msg", red ? "删除关联成功" : "删除关联失败");
		return res;
	}

	@Override
	public List<String> relateAccounts(String email) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("account", email);
		// 1、获取账号关联的员工
		List<DisSalesman> list = disSalesmanMapper.querySalesmansByCondition(map);
		Map<String, Object> userMap = Maps.newHashMap();
		userMap.put("roleId", 2);
		if (CollectionUtils.isEmpty(list)) {
			// 为空则查询所有用户
			return Lists.newArrayList();
		}
		List<Integer> saleMans = Lists.newArrayList();
		// 获取nodeType 为2的员工
		Map<Boolean, List<DisSalesman>> empMap = list.stream()
				.collect(Collectors.partitioningBy(e -> e.getNodeType() == 2));
		List<DisSalesman> empList = empMap.get(true);
		if (!CollectionUtils.isEmpty(empList)) {
			List<EmpSalesManMapper> esList = empMapper.selectBySaleManIds(Lists.transform(empList, i -> i.getId()));
			saleMans.addAll(Lists.transform(esList, i -> i.getSalesmanId()));
		}
		if (!CollectionUtils.isEmpty(empMap.get(false))) {
			// 不为空则取员工Id，用来获取员工关联的分销商
			saleMans.addAll(Lists.transform(empMap.get(false), i -> i.getId()));
		}
		userMap.put("saleMans", saleMans);
		List<String> accounts = Lists.transform(disSalesmanMapper.getRelatedMember(userMap), e -> e.getEmail());
		accounts.add("change_email");
		return accounts;
	}

}
