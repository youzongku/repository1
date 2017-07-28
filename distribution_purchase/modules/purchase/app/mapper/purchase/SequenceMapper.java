package mapper.purchase;

import entity.purchase.Sequence;

/**
 * luwj
 */
public interface SequenceMapper extends BaseMapper<Sequence> {

    /**
     * 获取标识下的当前值
     * @param seqName
     * @return
     */
    public int selectCurrentval(String seqName);

    /**
     * 更新标识的当前值
     * @param seqName
     * @return
     */
    public int updateSequence(String seqName);
}