package utils;

import play.mvc.Http;

/**
 * @author ye_ziran
 * @since 2016/3/25 11:08
 */
public class ContextUtil {

    public static final String TT_LTC = "TT_LTC";

    public static void refreshCookie(Http.Context context){
        Http.Cookie cookie = context.request().cookie(TT_LTC);
    }

}