package mapper.dismember;

import entity.dismember.Sequence;

public interface SequenceMapper extends BaseMapper<Sequence> {

	int deleteByPrimaryKey(Integer id);

	int insert(Sequence record);

	int insertSelective(Sequence record);

	Sequence selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(Sequence record);

	int updateByPrimaryKey(Sequence record);

	int updateCurrentValue(String seqName);

	String selectCurrentValue(String seqName);

	int restoreCurrentValue(String seqName);

}