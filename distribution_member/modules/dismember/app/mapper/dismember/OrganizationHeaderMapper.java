package mapper.dismember;

import java.util.List;

import entity.dismember.OrganizationHeader;

public interface OrganizationHeaderMapper extends BaseMapper<OrganizationHeader> {
    int deleteByPrimaryKey(Integer id);

    int insert(OrganizationHeader record);

    int insertSelective(OrganizationHeader record);

    OrganizationHeader selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrganizationHeader record);

    int updateByPrimaryKey(OrganizationHeader record);

	int deleteByCondition(OrganizationHeader organizationHeader);
	
	List<OrganizationHeader> selectByOrganizationId(Integer id);
}