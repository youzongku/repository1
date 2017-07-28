package utils.payment;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.util.IdleConnectionTimeoutThread;
import play.Logger;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * 支付宝接口调用工具类
 * @author luwj
 *
 */
public class HttpProtocolHandler {

    private static String              DEFAULT_CHARSET                    = "UTF-8";

    private static String 				METHOD_GET						   = "GET";

//    private static String 				METHOD_POST						   = "POST";

    /** 连接超时时间，由bean factory设置，缺省为8秒钟 */
    private int                        defaultConnectionTimeout            = 8000;

    /** 回应超时时间, 由bean factory设置，缺省为30秒钟 */
    private int                        defaultSoTimeout                    = 30000;

    /** 闲置连接超时时间, 由bean factory设置，缺省为60秒钟 */
    private int                        defaultIdleConnTimeout              = 60000;

    private int                        defaultMaxConnPerHost               = 30;

    private int                        defaultMaxTotalConn                 = 80;

    /** 默认等待HttpConnectionManager返回连接超时（只有在达到最大连接数时起作用）：1秒*/
    private static final long          defaultHttpConnectionManagerTimeout = 3 * 1000;

    /**
     * HTTP连接管理器，该连接管理器必须是线程安全的.
     */
    private HttpConnectionManager      connectionManager;

    private static HttpProtocolHandler httpProtocolHandler                 = new HttpProtocolHandler();

    /**
     * 工厂方法
     *
     * @return
     */
    public static HttpProtocolHandler getInstance() {
        return httpProtocolHandler;
    }

    /**
     * 私有的构造方法
     */
    private HttpProtocolHandler() {
        // 创建一个线程安全的HTTP连接池
        connectionManager = new MultiThreadedHttpConnectionManager();
        connectionManager.getParams().setDefaultMaxConnectionsPerHost(defaultMaxConnPerHost);
        connectionManager.getParams().setMaxTotalConnections(defaultMaxTotalConn);

        IdleConnectionTimeoutThread ict = new IdleConnectionTimeoutThread();
        ict.addConnectionManager(connectionManager);
        ict.setConnectionTimeout(defaultIdleConnTimeout);
        ict.start();
    }

    /**
     * 执行Http请求
     * @param type 请求方式：POST GET
     * @param charset 字符集
     * @param url 请求地址
     * @param params 请求参数
     * @return
     */
    public String execute(String type,String charset,String url,NameValuePair[] params)throws Exception{
        String responseStr = "";
        HttpClient httpclient = new HttpClient(connectionManager);
        // 设置连接超时
        int connectionTimeout = defaultConnectionTimeout;
        httpclient.getHttpConnectionManager().getParams().setConnectionTimeout(connectionTimeout);
        // 设置回应超时
        int soTimeout = defaultSoTimeout;
        httpclient.getHttpConnectionManager().getParams().setSoTimeout(soTimeout);
        // 设置等待ConnectionManager释放connection的时间
        httpclient.getParams().setConnectionManagerTimeout(defaultHttpConnectionManagerTimeout);

        charset = charset == null ? DEFAULT_CHARSET : charset;
        Logger.info(">>>>charset>>>>>"+charset);
        HttpMethod method = null;

        //get模式且不带上传文件
        if (type.toUpperCase().equals(METHOD_GET)) {
            method = new GetMethod(url);
            method.getParams().setCredentialCharset(charset);
        } else {
            //post模式且不带上传文件
            Logger.info(">>>>>>url>>>>>>>"+url);
            method = new PostMethod(url);
            ((PostMethod) method).addParameters(params);
            method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; text/html; charset=" + charset);
        }
        try {
            httpclient.executeMethod(method);
            responseStr = method.getResponseBodyAsString();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            method.releaseConnection();
        }
        return responseStr;
    }
}
