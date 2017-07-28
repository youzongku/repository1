package services.dismember;

import java.util.List;

import dto.dismember.TransferAccountDto;
import entity.dismember.DisTransferAccount;

public interface IDisTransferAccountService {
	

	int addTransferAccount(DisTransferAccount account);

	int editTransferAccount(DisTransferAccount account);

	List<DisTransferAccount> getAccountByDto(TransferAccountDto dto);

	List<String> getBankNameByEmail(String email);

	int delTransferAccount(Integer id);

	DisTransferAccount getTransferAccountByid(Integer id);

	boolean checkBankNameByDto(TransferAccountDto dto,Integer id);
}
