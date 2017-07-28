package mapper.dismember;

import java.util.List;
import java.util.Map;

import entity.dismember.DisRegisterApply;

public interface DisRegisterApplyMapper extends BaseMapper<DisRegisterApply> {
    int deleteByPrimaryKey(Integer id);

    int insert(DisRegisterApply record);

    int insertSelective(DisRegisterApply record);

    DisRegisterApply selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DisRegisterApply record);

    int updateByPrimaryKey(DisRegisterApply record);

	List<DisRegisterApply> getApplysByConditon(Map<String, Object> map);

	int getCountByConditon(Map<String, Object> map);
}