package services.dismember;

import java.util.List;

import entity.dismember.CreditOperationRecord;

/**
 * @author hanfs
 * 描述：用户信用额度操作记录服务接口，包含 操作记录的添加和查询
 *2016年4月21日
 */
public interface ICreditOperationRecordService {
/**
 * 描述：添加用户信用额度操作记录
 * 2016年4月21日
 * @param creditOperationRecord 要添加的用户信用额度操作记录信息
 * @return
 */
public boolean addOperationRecord(CreditOperationRecord creditOperationRecord);

/**
 * 描述：通过该用户邮箱获取该用户对应的额度操作记录
 * 2016年4月21日
 * @param email 用户邮箱
 * @param operateType 操作额度类型
 * @return
 */
public List<CreditOperationRecord> getOperationRecordsByEmail(String email,Integer operateType);
}
