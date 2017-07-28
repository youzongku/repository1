package vo.dismember;

import filters.common.CookieTrackingFilter;
import play.mvc.Http.Context;

/**
 * Created by LSL on 2015/12/24.
 */
public class LoginContextFactory {

    public static LoginContext initLC(String userID, String username, String email, String distributionmode,Integer distributionType) {
        LoginContext lc = new LoginContext();
        lc.setUserID(userID);
        lc.setUsername(username);
        lc.setEmail(email);
        lc.setDistributionmode(distributionmode);
        lc.setDistributionType(distributionType);
        lc.setLtc(CookieTrackingFilter.getLongTermCookie(Context.current()));
        lc.setStc(CookieTrackingFilter.getShortTermCookie(Context.current()));
        return lc;
    }

}
