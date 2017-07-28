package services.dismember.impl;

import com.google.inject.Inject;
import entity.dismember.DisEmailVerify;
import mapper.dismember.DisEmailVerifyMapper;
import services.dismember.IEmailService;

import java.util.Calendar;

/**
 * Created by luwj on 2016/9/21.
 */
public class EmailService implements IEmailService{

    @Inject
    private DisEmailVerifyMapper disEmailVerifyMapper;

    @Override
    public boolean moreThanLimit(int limit, String email, int sendType) {
        DisEmailVerify disEmailVerify = new DisEmailVerify();
        disEmailVerify.setCemail(email);
        disEmailVerify.setSendType(sendType);
        int count = disEmailVerifyMapper.getVerifyCount(disEmailVerify);
        return count < limit;//查看是否超过发送次数限制
    }

    @Override
    public Integer saveSendRecord(String email,String sendParams, int sendType, int valid, String code, int ymdM){
        DisEmailVerify verify = new DisEmailVerify();
        verify.setCemail(email);
        verify.setBisending(true);
        verify.setCactivationcode(code);
        Calendar calendar = Calendar.getInstance();
        verify.setDsenddate(calendar.getTime());
        verify.setDcreatedate(calendar.getTime());
        verify.setSendType(sendType);
        //注册激活邮件有效时间为1天
        calendar.add(ymdM, valid);
        verify.setDvaliddate(calendar.getTime());
        //保存发送邮件的链接参数 by huchuyin 2016-9-24
        verify.setSendParams(sendParams);
        return disEmailVerifyMapper.insertSelective(verify);
    }
}
