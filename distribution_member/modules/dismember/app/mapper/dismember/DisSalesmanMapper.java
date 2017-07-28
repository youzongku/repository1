package mapper.dismember;

import java.util.List;
import java.util.Map;

import dto.dismember.AdminDto;
import entity.dismember.DisSalesman;

public interface DisSalesmanMapper extends BaseMapper<DisSalesman> {
    int deleteByPrimaryKey(Integer id);

    int insert(DisSalesman record);

    int insertSelective(DisSalesman record);

    DisSalesman selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DisSalesman record);

    int updateByPrimaryKey(DisSalesman record);
    
	List<DisSalesman> querySalesmansByCondition(Map<String, Object> map);

	int getCountByCondition(Map<String, Object> map);

	Integer getRelatedMemberCount(Map<String, Object> userMap);

	List<AdminDto> getRelatedMember(Map<String, Object> userMap);
	
	List<DisSalesman> selectByAccountAndNoName(DisSalesman record);
}