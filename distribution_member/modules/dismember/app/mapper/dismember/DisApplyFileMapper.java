package mapper.dismember;

import java.util.List;

import entity.dismember.DisApplyFile;

public interface DisApplyFileMapper extends BaseMapper<DisApplyFile> {
    int deleteByPrimaryKey(Integer id);

    int insert(DisApplyFile record);

    int insertSelective(DisApplyFile record);

    DisApplyFile selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DisApplyFile record);

    int updateByPrimaryKey(DisApplyFile record);

	int batchInsert(List<DisApplyFile> list);

	List<DisApplyFile> getFileByApplyId(Integer applyId);

	int batchUpdate(List<DisApplyFile> list);
}