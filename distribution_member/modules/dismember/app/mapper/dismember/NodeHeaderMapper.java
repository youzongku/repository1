package mapper.dismember;

import java.util.List;

import dto.dismember.NodeHeaderDto;
import entity.dismember.NodeHeader;

public interface NodeHeaderMapper extends BaseMapper<NodeHeader> {
    int deleteByPrimaryKey(Integer id);

    int insert(NodeHeader record);

    int insertSelective(NodeHeader record);

    NodeHeader selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(NodeHeader record);

    int updateByPrimaryKey(NodeHeader record);

	List<NodeHeaderDto> queryHeaderByOrganizationId(Integer id);

	int deleteByCondition(NodeHeader nodeHeader);
	
	List<NodeHeader> getNodeHeaderMapper();
}