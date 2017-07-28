package mapper.dismember;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.dismember.Organization;

public interface OrganizationMapper extends BaseMapper<Organization> {
    int deleteByPrimaryKey(Integer id);

    int insert(Organization record);

    int insertSelective(Organization record);

    Organization selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Organization record);

    int updateByPrimaryKey(Organization record);
    
    List<Organization> getOrganizations(Organization organization);
    
    Organization selectOrganByHeaderId(@Param("headerId")Integer headerId);

	String getDataConfig(String account);
}