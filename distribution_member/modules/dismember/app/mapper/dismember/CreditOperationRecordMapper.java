package mapper.dismember;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.dismember.CreditOperationRecord;
/**
 * 信用额度 操作mapper
 *
 */
public interface CreditOperationRecordMapper extends BaseMapper<CreditOperationRecord>{ 
	
	/**
	 * 描述：通过分销商邮箱获得对该分销商的所有的额度操作记录
	 * 2016年4月21日
	 * @param email 分销商用户邮箱
	 * @return
	 */
	public List<CreditOperationRecord> getRecordsByEmail(@Param("email")String email,@Param("operateType")Integer operateType);
	
	/**
	 * 描述：删除所有分销商对应的额度操作记录
	 * 2016年5月12日
	 * @param email
	 * @return
	 */
	int deleteByEmail(@Param("email")String email);

}
