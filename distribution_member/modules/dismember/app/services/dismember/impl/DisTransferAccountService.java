package services.dismember.impl;

import java.util.Date;
import java.util.List;

import com.google.inject.Inject;

import dto.dismember.TransferAccountDto;
import entity.dismember.DisTransferAccount;
import mapper.dismember.DisTransferAccountMapper;
import services.dismember.IDisTransferAccountService;

public class DisTransferAccountService implements IDisTransferAccountService {

	@Inject
	private DisTransferAccountMapper accountMapper;
	
	@Override
	public int addTransferAccount(DisTransferAccount account) {
		account.setCreateDate(new Date(System.currentTimeMillis()));
		return accountMapper.insertSelective(account);
	}

	@Override
	public int editTransferAccount(DisTransferAccount account) {
		return accountMapper.updateByPrimaryKey(account);
	}

	@Override
	public List<DisTransferAccount> getAccountByDto(TransferAccountDto dto) {
		return accountMapper.getAccountByDto(dto);
	}

	@Override
	public List<String> getBankNameByEmail(String email) {
		return accountMapper.getBankNameByEmail(email);
	}

	@Override
	public int delTransferAccount(Integer id) {
		return accountMapper.deleteByPrimaryKey(id);
	}

	@Override
	public DisTransferAccount getTransferAccountByid(Integer id) {
		return accountMapper.selectByPrimaryKey(id);
	}

	@Override
	public boolean checkBankNameByDto(TransferAccountDto dto,Integer id) {
		List<DisTransferAccount> list =  accountMapper.getAccountByDto(dto);
		if(null == id){
			return list.size()>0;
		}
		
		if(list.size() == 1){
			return !list.get(0).getId().equals(id);
		}
		
		return list.size()>0;
	}
}
