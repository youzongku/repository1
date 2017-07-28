package services.dismember.impl;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import dto.dismember.ReceiptModeDto;
import entity.dismember.DisBank;
import entity.dismember.ReceiptMode;
import mapper.dismember.DisBankMapper;
import mapper.dismember.ReceiptModeMapper;
import services.dismember.IDisBankService;

import java.util.List;

/**
 * Created by LSL on 2016/1/7.
 */
public class DisBankService implements IDisBankService {

    @Inject
    private DisBankMapper disBankMapper;
    @Inject
    private ReceiptModeMapper receiptModeMapper;

    @Override
    public List<DisBank> getAllBanks() {
        return disBankMapper.getAllBanks();
    }

    @Override
    public List<ReceiptModeDto> getAllReceiptModes() {
        List<ReceiptMode> rms = receiptModeMapper.getAllReceiptModes();
        List<ReceiptModeDto> rmds = Lists.newArrayList();
        if (rms != null && rms.size() > 0) {
            for (ReceiptMode rm : rms) {
                ReceiptModeDto rmd = new ReceiptModeDto();
                rmd.setId(rm.getId());
                rmd.setAccount(rm.getReceiptAccount());
                rmd.setPayee(rm.getPayee());
                rmd.setBankName(disBankMapper.selectByPrimaryKey(rm.getBankId()).getBankName());
                rmd.setRemark(rm.getRemark());
                rmds.add(rmd);
            }
        }
        return rmds;
    }
}
