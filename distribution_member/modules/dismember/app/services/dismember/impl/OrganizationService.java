package services.dismember.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import dto.dismember.NodeHeaderDto;
import entity.dismember.DisHeader;
import entity.dismember.DisMember;
import entity.dismember.DisSalesman;
import entity.dismember.NodeHeader;
import entity.dismember.Organization;
import entity.dismember.OrganizationHeader;
import mapper.dismember.DisHeaderMapper;
import mapper.dismember.DisHeaderSalesmanMapper;
import mapper.dismember.DisMemberMapper;
import mapper.dismember.DisSalesmanMapper;
import mapper.dismember.NodeHeaderMapper;
import mapper.dismember.OrganizationHeaderMapper;
import mapper.dismember.OrganizationMapper;
import play.Logger;
import services.dismember.IOrganizationService;
import utils.dismember.MD5Util;

public class OrganizationService implements IOrganizationService{
	
	@Inject 
	private OrganizationMapper organizationMapper;
	@Inject
	private NodeHeaderMapper nodeHeaderMapper;//每个组织节点对应的相关负责人映射类
	@Inject
	private OrganizationHeaderMapper organizationHeaderMapper;//每个负责人对应的组织树形结构	
	@Inject
	private DisHeaderMapper disheaderMapper;
	@Inject
	private DisSalesmanMapper disSalesmanMapper;
	@Inject
	private DisMemberMapper memberMapper;
	
	@Inject
	private DisHeaderSalesmanMapper headerSaleMainMapper;
	
	/**
	 * 得到子节点
	 */
	@Override
	public List<Organization> getChildOrganizations(Map<String, String[]> param) {
		List<Organization> organizations = Lists.newArrayList();
		String[] idStr = param.get("id");
		if (null == idStr || idStr.length == 0) {
			return organizations;
		}
		Organization organization = new Organization();
		organization.setParentid(Integer.parseInt(idStr[0]));
		List<Organization> list = organizationMapper.getOrganizations(organization);
		return list;
	}
	
	/**
	 * 根据条件查询节点
	 */
	@Override
	public Map<String, Object> getOrganization(Map<String, String> params) {
		Map<String, Object> result = Maps.newHashMap();
		Organization organization = new Organization();
		organization.setName(params.containsKey("name") && !Strings.isNullOrEmpty(params.get("name")) ? params.get("name") : null);
		organization.setDescription(params.containsKey("description") && !Strings.isNullOrEmpty(params.get("description")) ? params.get("description") : null);
		organization.setIsParent(params.containsKey("isparent") && !Strings.isNullOrEmpty(params.get("isparent")) ? Boolean.valueOf(params.get("isparent")) : null);
        organization.setParentid(params.containsKey("parentid") && !Strings.isNullOrEmpty(params.get("parentid")) ? Integer.valueOf(params.get("parentid")) : null);		
        List<Organization> list = organizationMapper.getOrganizations(organization);
        if (list == null || list.size() == 0) {
        	result.put("code", 3);
        	result.put("msg", "没有查询到指定节点");
        	return result;
        }
        
        result.put("code", 4);
        result.put("data", list);
		return  result;
	}
	
	/**
	 * 对组织架构的节点进行增加操作
	 */
	@Override
	public Map<String, Object> addOrganization(Map<String, String> params) {
		Map<String, Object> result = Maps.newHashMap();
		result.put("suc", false);
		int line;
		Organization organization = new Organization();
		Organization porganization = new Organization();
		DisHeader header = new DisHeader();
		NodeHeader nodeHeader = new NodeHeader();//每个节点所对应的负责人
		organization.setName(params.containsKey("name") && !Strings.isNullOrEmpty(params.get("name")) ? params.get("name") : null);
		organization.setDescription(params.containsKey("description") && !Strings.isNullOrEmpty(params.get("description")) ? params.get("description") : null);
		organization.setIsParent(params.containsKey("isparent") && !Strings.isNullOrEmpty(params.get("isparent")) ? Boolean.valueOf(params.get("isparent")) : null);
		organization.setSort(params.containsKey("sort") && !Strings.isNullOrEmpty(params.get("sort")) ? Integer.valueOf(params.get("sort")) : null);
        organization.setParentid(params.containsKey("parentid") && !Strings.isNullOrEmpty(params.get("parentid")) ? Integer.valueOf(params.get("parentid")) : null);		
        organization.setNodeType(params.containsKey("nodeType") ? Integer.valueOf(params.get("nodeType")) : 1);		
        organization.setCreateDate(new Date());
        line = organizationMapper.insertSelective(organization);
        if (line < 1) {//对应的插入负责人表
        	result.put("msg", "新增组织区域失败");
        	return result;
        }

        if (organization.getParentid() != 0) {//说明在一个节点下进行增加操作,父节点的isParent改为true
    		porganization.setId(organization.getParentid());
        	porganization.setIsParent(true);
    		organizationMapper.updateByPrimaryKeySelective(porganization);
    	}
    	header.setName(params.containsKey("headerName") && !Strings.isNullOrEmpty(params.get("headerName")) ? params.get("headerName") : null);
    	header.setTel(params.containsKey("headerTel") && !Strings.isNullOrEmpty(params.get("headerTel")) ? params.get("headerTel") : null);
    	header.setCreateDate(new Date());
    	line = disheaderMapper.insertSelective(header);
    	if (line < 1) {
    		result.put("msg", "添加相关负责人信息失败");
    		return result;
    	}
    	
		if (organization != null && header != null) {
			nodeHeader.setHeaderid(header.getId());
    		nodeHeader.setOrganizationid(organization.getId());
    		nodeHeaderMapper.insertSelective(nodeHeader);
    		//添加对应的结构信息（为以后做权限做铺垫）
    		queryParent(organization.getId(), header.getId(), Integer.valueOf(params.get("level")));
		}
		result.put("suc", true);
		result.put("msg", "新增组织节点成功");
		return result;
	}
	
	/**
	 * 对组织架构的节点进行修改操作
	 */
	@Override
	public Map<String, Object> updateOrganization(Map<String, String> params) {
		Map<String, Object> result = Maps.newHashMap();
		int line;
		Organization organization = new Organization();
		DisHeader header = new DisHeader();
		organization.setId(Integer.valueOf(params.get("id")));
		organization.setName(params.containsKey("name") && !Strings.isNullOrEmpty(params.get("name")) ? params.get("name") : null);
		organization.setIsParent(params.containsKey("isparent") && !Strings.isNullOrEmpty(params.get("isparent")) ? Boolean.valueOf(params.get("isparent")) : null);
        organization.setParentid(params.containsKey("parentid") && !Strings.isNullOrEmpty(params.get("parentid")) ? Integer.valueOf(params.get("parentid")) : null);		
        line = organizationMapper.updateByPrimaryKeySelective(organization);
        if (line < 1) {
        	result.put("suc", false);
        	result.put("msg","更新组织架构失败 ");
        	return result;
        }
        
        header.setId(params.containsKey("headerId") && !Strings.isNullOrEmpty(params.get("headerId")) ? Integer.valueOf(params.get("headerId")) : null);
    	header.setName(params.containsKey("headerName") && !Strings.isNullOrEmpty(params.get("headerName")) ? params.get("headerName") : null);
        header.setTel(params.containsKey("headerTel") && !Strings.isNullOrEmpty(params.get("headerTel")) ? params.get("headerTel") : null);
        header.setUpdateDate(new Date());
        line = disheaderMapper.updateByPrimaryKeySelective(header);
        if (line > 0) {
        	result.put("suc", true);
        	result.put("msg", "组织架构更新成功");
        	return result;
        }
        
        result.put("suc", false);
    	result.put("msg", "负责人更新失败");
		return result;
	}
	
	/**
	 * 删除节点
	 */
	@Override
	public Map<String, Object> deleteOrganzitionById(Map<String, String> params) {
		Map<String, Object> result = Maps.newHashMap();
		Map<String, String[]> map = new HashMap<String, String[]>();
		map.put("id",new String[]{params.get("id")});
		List<Organization> organizations = getChildOrganizations(map);//查询此节点下是否存在子节点
		if (organizations != null && organizations.size() > 0) {
			result.put("suc", false);
			result.put("msg", "该架构下存有子区域,无法删除");
			return result;
		}
		//无子节点，再查询此节点关联的负责人下是否存在业务人员
		Map<String, Object> saleParam = Maps.newHashMap();
		saleParam.put("id", params.containsKey("headerId") && !Strings.isNullOrEmpty(params.get("headerId")) ? Integer.valueOf(params.get("headerId")) : null);
		List<DisSalesman> salesmans = disSalesmanMapper.querySalesmansByCondition(saleParam);
		if (salesmans != null && salesmans.size() > 0) {
			result.put("suc", false);
			result.put("msg", "该架构下面含有业务人员，无法删除");
			return result;
		}
		int line = 0;
		//1.删除节点
		line = organizationMapper.deleteByPrimaryKey(Integer.valueOf(params.get("id")));
		if (line < 1) {
			result.put("suc", false);
			result.put("msg", "删除节点失败");
			return result;
		}
		
		//2.1删除节点与责任人映射关系表中对应的数据
		NodeHeader nodeHeader = new NodeHeader();
		nodeHeader.setHeaderid(params.containsKey("headerId") && !Strings.isNullOrEmpty(params.get("headerId")) ? Integer.valueOf(params.get("headerId")) : null);
		nodeHeader.setOrganizationid(Integer.valueOf(params.get("id")));
		line = nodeHeaderMapper.deleteByCondition(nodeHeader);
		//2.2如果此节点为它父节点的下唯一的子节点，唯一的子节点被删除，其父节点就不能称之为父节点
		if (Integer.valueOf(params.get("parentId")) != 0) {
			map.put("id",new String[]{params.get("parentId")});
			organizations = getChildOrganizations(map);
			if (organizations == null || organizations.size() == 0) {
				Organization organization = new Organization();
				organization.setId(Integer.valueOf(params.get("parentId")));
				organization.setIsParent(false);
				organizationMapper.updateByPrimaryKeySelective(organization);
			}
		}
		if (line < 1){
			result.put("suc", false);
			result.put("msg", "删除责任人失败");
			return result;
		}
		
		line = disheaderMapper.deleteByPrimaryKey(Integer.valueOf(params.get("headerId")));
		if (line < 1) {
			result.put("suc", false);
			result.put("msg", "删除此节点与责任人的对应关系失败");
			return result;
		}
		
		//3.删除此节点责任人所拥有的节点权限
		OrganizationHeader organizationHeader = new OrganizationHeader();
		organizationHeader.setHeaderid(params.containsKey("headerId") && !Strings.isNullOrEmpty(params.get("headerId")) ? Integer.valueOf(params.get("headerId")) : null);
		line = organizationHeaderMapper.deleteByCondition(organizationHeader);
		if (line < 1) {
			result.put("suc", false);
			result.put("msg", "删除责任人权限失败");
			return result;
		}
		
		result.put("suc", true);
		result.put("msg", "删除成功");
		return result;
	}
	
	/**
	 * 查询父节点
	 */
	public void queryParent(Integer organzitionid, Integer headerid, Integer level){
		Organization organization = new Organization();
		OrganizationHeader organizationHeader = new OrganizationHeader();
		organizationHeader.setHeaderid(headerid);
		organizationHeader.setLevel(level);
		organizationHeader.setOrganizationid(organzitionid);
		organizationHeaderMapper.insertSelective(organizationHeader);
		organization = organizationMapper.selectByPrimaryKey(organzitionid);
		if (organization != null && organization.getParentid() != 0) {
			queryParent(organization.getParentid(), headerid, level-1);
		}
	}

	/**
	 * 通过组织节点id得到对应的负责人信息
	 */
	@Override
	public Map<String, Object> queryHeaderByOrganizationId(Map<String, String> params) {
		Map<String, Object> result = Maps.newHashMap();
		Integer id = Integer.valueOf(params.get("id"));
		List<NodeHeaderDto> list = nodeHeaderMapper.queryHeaderByOrganizationId(id);
		if (list == null || list.size() == 0) {
			result.put("suc", false);
			result.put("msg", "没有查询到指定的责任人");
			return result;
		}
		result.put("suc", true);
		result.put("data", list);
		return result;
	}
	
	@Override
	public List<DisMember> getRelate(Integer id) {
		return memberMapper.getMemberByOrgId(id);
	}

	@Override
	public Map<String, Object> organizationalData(String account,String key) {
		Map<String,Object> result = Maps.newHashMap();
		if(StringUtils.isEmpty(key) || StringUtils.isEmpty(account)) {
			result.put("success", false);
			result.put("message", "必要参数缺失。");
			result.put("code", 101);
			return result;
		}
		String md5Key = organizationMapper.getDataConfig(account);
		String targetStr = account+"_"+md5Key;
		Logger.info("---->" + targetStr);
		String md5 = MD5Util.MD5Encode(targetStr, MD5Util.CHARSET_UTF_8);
		Logger.info("---->" + md5);
		if(!key.equals(md5)) {
			result.put("success", false);
			result.put("message", "签名校验失败。");
			result.put("code", 102);
			return result;
		}
		Map<String,Object> data = Maps.newHashMap();
		try {
			result.put("success", true);
			data.put("dis_organizational", organizationMapper.getOrganizations(new Organization()));
			data.put("dis_header", disheaderMapper.getHeader(new DisHeader()));
			data.put("dis_salesman", disSalesmanMapper.selectByAccountAndNoName(new DisSalesman()));
			data.put("dis_node_header_mapper", nodeHeaderMapper.getNodeHeaderMapper());
			data.put("dis_header_salesman_mapper", headerSaleMainMapper.getHeaderSalesmanMapper());
			result.put("message", data);
			result.put("code", 100);
		} catch (Exception e) {
			result.put("success", false);
			result.put("message", "系统异常。");
			result.put("code", 103);
			Logger.error("ERP拉取组织架构异常，" + e);
		}
		return result;
	}
	
	public static void main(String[] args) {
		System.err.println(MD5Util.MD5Encode("bbc_organization_a46850d6-03c6-43fd-9958-467e487272d9", MD5Util.CHARSET_UTF_8));
	}
}
