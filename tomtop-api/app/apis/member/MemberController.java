package apis.member;

import forms.Member.MemberBaseForm;
import forms.ReturnMessageForm;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.Constans;
import utils.HttpUtil;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * 会员Api
 *
 * @author ye_ziran on 2016/3/22.
 */
public class MemberController extends Controller{

    public static final String TT_LTC = "TT_LTC";
    public static final String TT_STC = "TT_STC";

    //cookie失效时间，2小时
    private static final int COOKIE_EXPIRE_TIME = 2 * 60 * 60;

    public Result login() throws IOException {
        Result result = noContent();

        Form<MemberBaseForm> f = Form.form(MemberBaseForm.class).bindFromRequest();
        Map<String,String> params = f.data();

        if(params.isEmpty()){
            return badRequest();
        }

        Http.Context context = Http.Context.current();
        Http.Session session =context.session();
        CookieStore cookieStore = HttpUtil.getCookieStore();

        CloseableHttpClient httpclient = null;

        try{
            httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();

            CloseableHttpResponse response = null;
            try{
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                nvps.add(new BasicNameValuePair("email", params.get("memberEmail")));
                nvps.add(new BasicNameValuePair("passWord", params.get("password")));
                HttpEntity reqEncodedEntity = new UrlEncodedFormEntity(nvps, "UTF-8");
                HttpUriRequest reqUri = RequestBuilder.post().setUri(new URI(Constans.MEMBER_LOGIN)).setEntity(reqEncodedEntity).build();

                response = httpclient.execute(reqUri);

                StatusLine statusLine = response.getStatusLine();
                Logger.info("Login form get: " + response.getStatusLine());

                ReturnMessageForm msgForm = new ReturnMessageForm();

                if(statusLine.getStatusCode() == 200){//登录成功
                    msgForm.setRes(true);
                    msgForm.setMsg("Sign in success!");
                    Logger.info("{} sign in api system", params.get("memberEmail"));

                    result = ok(Json.toJson(msgForm));
                }else{
                    msgForm.setRes(false);
                    msgForm.setMsg("Sign in faild!");
                    result = forbidden(Json.toJson(msgForm));
                }

                List<Cookie> cookies = cookieStore.getCookies();

                if (!cookies.isEmpty()) {
                    for (int i=0,len=cookies.size();i<len;i++){
                        Cookie cookieInServer = cookies.get(i);
                        if(cookieInServer.getName().equals(TT_STC)){
                            String cookieVal = cookieInServer.getValue();
                            //将cookie的值作为key，存进session
                            if(!session.containsKey(cookieVal)){
                                session.put(cookieVal, "1");
                            }
                            Http.Cookie cookie =Http.Cookie.builder(cookieInServer.getName(),cookieInServer.getValue())
                                    .withMaxAge(COOKIE_EXPIRE_TIME).build();
                            context.response().setCookie(cookie);

                            Logger.debug("TT_LTC cookie : {}" , cookieInServer.toString());
                        }
                    }
                }

            } catch (URISyntaxException e) {
                e.printStackTrace();
            } finally {
                response.close();
            }
        }finally {
            httpclient.close();
        }

        return  result;
    }

}
