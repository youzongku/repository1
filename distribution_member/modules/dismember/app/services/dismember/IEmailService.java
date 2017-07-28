package services.dismember;

/**
 * Created by luwj on 2016/9/21.
 */
public interface IEmailService {

    /**
     * 校验当天发送邮件次数是否已经超限
     * @param limit : 限制次数
     * @param email : 邮箱
     * @param sendType : 发送类型:0：代表注册激活，1：手机更改，
     *                 2：支付密码修改, 3：绑定提现帐号
     * @return :
     */
    public boolean moreThanLimit(int limit, String email, int sendType);


    /**
     * 保存发送邮件记录
     * @param email : 邮箱
     * @param sendType ：发送类型
     * @param valid ： 有效时长
     * @param code ： 验证码
     * @param ymdM ： Calendar.DATE
     * @return ：
     */
    public Integer saveSendRecord(String email, String sendParams,int sendType, int valid,
                                  String code, int ymdM);
}
