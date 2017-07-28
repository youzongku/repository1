package services.dismember.impl;

import com.google.inject.Inject;

import filters.common.CookieTrackingFilter;
//import play.Logger;
import play.mvc.Http.Context;
import services.dismember.ILoginService;
import session.ISessionService;
import vo.dismember.LoginContext;

/**
 * Created by LSL on 2015/12/23.
 */
public class LoginService implements ILoginService {

    private static final String NORMAL_LOGIN_SESSION_NAME = "NORMAL_USER_LOGIN_CONTEXT";
    private static final String ADMIN_LOGIN_SESSION_NAME = "ADMIN_USER_LOGIN_CONTEXT";

    @Inject
    private ISessionService sessionService;

    @Override
    public boolean isLogin(int mark) {
        LoginContext lc = this.getLoginContext(mark);
        if (lc == null) {
            return false;
        }
        return lc.isLogin();
    }

    @Override
    public void setLoginContext(int mark, LoginContext lc) {
        String sign = mark == 1 ? NORMAL_LOGIN_SESSION_NAME : ADMIN_LOGIN_SESSION_NAME;
        sessionService.set(sign, lc);
    }

    @Override
    public LoginContext getLoginContext(int mark) {
        String sign = mark == 1 ? NORMAL_LOGIN_SESSION_NAME : ADMIN_LOGIN_SESSION_NAME;
        return (LoginContext)sessionService.get(sign);
    }

    @Override
    public void clearLoginContext(int mark) {
        String sign = mark == 1 ? NORMAL_LOGIN_SESSION_NAME : ADMIN_LOGIN_SESSION_NAME;
        sessionService.set(sign, null);
        //清除登陆信息
        sessionService.remove(mark + CookieTrackingFilter.getLongTermCookie(Context.current()));
    }

    /**
     * 获取当前session的ID值
     * @return
     */
    public String getSessionID() {
        return sessionService.getSessionID();
    }

    /**
     * 获取当前客户端IP
     * @return
     */
    public String getClientIP() {
        return Context.current().request().remoteAddress();
    }

    /**
     * 获取当前客户端主机名称
     * @return
     */
    public String getHost() {
        return Context.current().request().host();
    }

}
