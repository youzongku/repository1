package handlers.dismember;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import constant.dismember.Constant;
import entity.dismember.DisAccount;
import entity.dismember.DisApply;
import entity.dismember.DisBill;
import events.dismember.ActiveStateEvent;
import events.dismember.WithdrawApplyEvent;
import mapper.dismember.DisAccountMapper;
import mapper.dismember.DisApplyMapper;
import mapper.dismember.DisBillMapper;
import play.Logger;
import services.dismember.IActiveService;
import services.dismember.ISequenceService;
import services.dismember.impl.DisBillService;
import utils.dismember.HttpUtil;
import utils.dismember.IDUtils;

public class ActiveHandler {
	
	@Inject
	private IActiveService activeService;

    @Inject
    DisApplyMapper applyMapper;

    @Inject
    DisBillMapper billMapper;

    @Inject
    ISequenceService sequenceService;

    @Inject
    DisAccountMapper disAccountMapper;
    
    @Inject
    DisBillService billService;
    /**
     * 销售订单推送到b2c
     * @param event
     */
    @Subscribe
    public void execute(ActiveStateEvent event){
        Logger.debug("ActiveHandler 更新活动相关信息，定时执行，凌晨12点。");
        activeService.execute();
    }

    /**
     * 定时发送提现申请到M站
     * @Author LSL on 2016-09-28 09:52:32
     */
    @Subscribe
    public void sendWithdrawApplyToMsite(WithdrawApplyEvent event) {
        //Logger.debug("--------------------sendWithdrawApplyToMsite start--------------------");
        //查询申请类型为提现、申请状态为处理中、提现账户类型为M站的提现申请
        List<DisApply> das = applyMapper.findWithdrawToMsiteApply();
        if (!CollectionUtils.isEmpty(das)) {
            JSONObject temp = null;
            String res = null;
            DisApply apply = null;
            int line = 0;
            DisAccount account = null;
            BigDecimal frozenAmount = null;
            DisBill bill = null;
            for (DisApply da : das) {
                Logger.debug("sendWithdrawApplyToMsite    WithdrawApplyNo----->" + da.getOnlineApplyNo());
                //Logger.debug("sendWithdrawApplyToMsite    disemail----->" + da.getEmail());
                //发送提现申请信息到M站
                temp = new JSONObject();
                temp.put("disemail", String.valueOf(da.getEmail()));
                temp.put("orderNo", String.valueOf(da.getOnlineApplyNo()));
                temp.put("amount", da.getWithdrawAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                temp.put("timestamp", String.valueOf(new Date().getTime()));
                res = HttpUtil.sendWithdrawApply(temp);
                Logger.debug("sendWithdrawApplyToMsite    res----->" + res);
                if (JSON.parseObject(res).getBoolean("result")) {
                    //更新申请状态为已完成
                    apply = applyMapper.selectByPrimaryKey(da.getId());
                    apply.setAuditState(Constant.AUDIT_PASS);
                    apply.setAuditReasons("已确认");
                    apply.setUpdatedate(new Date());
                    line = applyMapper.updateByPrimaryKeySelective(apply);
                    //Logger.debug("sendWithdrawApplyToMsite    [update DisApply]line----->" + line);

                    if (line == 1) {
                        //扣减冻结余额
                        account = disAccountMapper.getDisAccountByEmail(da.getEmail());
                        frozenAmount = account.getFrozenAmount();
                        account.setFrozenAmount(frozenAmount.subtract(da.getWithdrawAmount()));
                        line = disAccountMapper.updateByPrimaryKeySelective(account);
                        //Logger.debug("sendWithdrawApplyToMsite    [update DisAccount]line----->" + line);

                        //新增交易记录
                        bill = new DisBill();
                        bill.setAmount(da.getWithdrawAmount());
                        bill.setPurpose("2");
                        String serialNumber = IDUtils.getOnlineTopUpCode("SN", sequenceService.selectNextValue("WITHDRAW_AMOUNT_NO"));
                        bill.setSerialNumber(serialNumber);
                        bill.setPaymentType("余额提现");
                        bill.setApplyId(apply.getId());
                        bill.setSourceCard(apply.getEmail());
                        bill.setBalance(account.getBalance());
                        bill.setAccountId(account.getId());
                        bill.setSources(0);//子交易记录
                        line =billService.save(bill);
                        //Logger.debug("sendWithdrawApplyToMsite    [insert child DisBill]line----->" + line);
                        bill.setId(null);
                        bill.setSources(3);//总交易记录
                        line =billService.save(bill);
                        //Logger.debug("sendWithdrawApplyToMsite    [insert main DisBill]line----->" + line);
                    }
                }
            }
        }
        //Logger.debug("--------------------sendWithdrawApplyToMsite end--------------------");
    }

}
