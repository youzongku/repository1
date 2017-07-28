package mapper.dismember;

import java.util.List;

import dto.dismember.TransferAccountDto;
import entity.dismember.DisTransferAccount;

public interface DisTransferAccountMapper extends BaseMapper<DisTransferAccount>{
	
	/**
	 * 新增付款账户对象
	 * @param DisTransferAccount
	 * @return
	 */
	int insertSelective(DisTransferAccount account);
	
	/**
	 * 根据id删除付款账户对象
	 * @param distributorId
	 * @return
	 */
	int deleteByPrimaryKey(Integer id);
	
	/**
	 * 修改付款账户对象
	 * @param DisTransferAccount
	 * @return
	 */
	int updateByPrimaryKey(DisTransferAccount account);
	
	/**
	 * 根据参数查询付款账户对象
	 * @param TransferAccountDto
	 * @return  
	 */
	List<DisTransferAccount> getAccountByDto(TransferAccountDto dto);
	
	/**
	 * 根据用户账号获取所有付款方式
	 * @param email
	 * @return
	 */
	List<String> getBankNameByEmail(String email);
	
	
}
