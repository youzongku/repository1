package services.dismember;

import vo.dismember.LoginContext;

/**
 * Created by LSL on 2015/12/23.
 */
public interface ILoginService {

    /**
     * 当前用户是否登录，
     * mark = 1 --> 前台用户，
     * mark = 2 --> 后台用户
     * @param mark
     * @return
     */
    boolean isLogin(int mark);

    /**
     * 保存登录信息，
     * mark = 1 --> 前台用户，
     * mark = 2 --> 后台用户
     * @param mark
     * @param lc
     */
    void setLoginContext(int mark, LoginContext lc);

    /**
     * 获取登录信息，
     * mark = 1 --> 前台用户，
     * mark = 2 --> 后台用户
     * @param mark
     * @return
     */
    LoginContext getLoginContext(int mark);

    /**
     * 清除登录信息，
     * mark = 1 --> 前台用户，
     * mark = 2 --> 后台用户
     * @param mark
     */
    void clearLoginContext(int mark);

}
