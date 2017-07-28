package service.timer;

/**
 * 客户订单单号序列值
 * @author Alvin Du
 *
 */
public interface ISequenceService {
	
	
    /**
     * 获取标识下的当前序列值
     * @param seqName
     * @return
     */
    public String selectNextValue(String seqName);
    
}
