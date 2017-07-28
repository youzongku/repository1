package services.dismember;

import dto.dismember.ReceiptModeDto;
import entity.dismember.DisBank;

import java.util.List;

/**
 * Created by LSL on 2016/1/7.
 */
public interface IDisBankService {

    /**
     * 获取所有银行
     * @return
     */
    List<DisBank> getAllBanks();

    /**
     * 获取所有收款方式
     * @return
     */
    List<ReceiptModeDto> getAllReceiptModes();

}
