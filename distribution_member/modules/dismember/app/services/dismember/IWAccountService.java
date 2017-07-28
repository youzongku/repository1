package services.dismember;

import com.alibaba.fastjson.JSONObject;

import entity.dismember.DisEmailVerify;

/**
 * Created by luwj on 2016/9/20.
 */
public interface IWAccountService {

    /**
     * 添加提现银行帐号
     * @param param : 参数
     * @return :
     */
    public Integer addWAccountNo(String param);

    /**
     * 校验银行帐号是否存在
     * @param params
     * @return
     */
    public JSONObject existWAccountNo(JSONObject params);

    /**
     * 提现绑定银行帐号确认邮件
     * @param param : 参数
     * @return :
     */
    public Integer activateWBEmail(String param);
    
    /**
     * 重新发送邮件
     * @param emailVerify
     * @return
     * @author huchuyin
     * @date 2016年9月24日 上午9:32:36
     */
    public String reSendEmail(DisEmailVerify emailVerify);

    /**
     * 发送提现绑定帐户短信验证码
     * @param withdrawAccount : 绑定帐号
     * @param types : 类型
     * @return :
     * @throws Exception
     */
    public int sendTelCode(String withdrawAccount, int types) throws Exception;

}
