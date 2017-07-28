package utils.payment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.tempuri.Kvp;
import org.tempuri.TransAPIExporterService;
import org.tempuri.TransSoap;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Charsets;

import entity.payment.shengpay.ShengConfig;
import play.Logger;
import play.libs.Json;

/**
 * 盛付通工具类
 * Created by LSL on 2016/5/3.
 */
public class SFTUtil {

    public static final String SIGN_ALGORITHMS = "MD5";

    public static TransSoap getTransSoap(String wsurl) {
        URL url = null;
        try {
            url = new URL(wsurl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Logger.error("Can not initialize the default wsdl from " + wsurl, e);
        }
        TransAPIExporterService service = new TransAPIExporterService(url);
        return service.getTransAPIExporterPort();
    }

    public static Kvp getKvp(String key, String value) {
        Kvp kvp = new Kvp();
        kvp.setKey(key);
        kvp.setValue(value);
        return kvp;
    }

    /**
     * 使用给定的 charset 将此 String 编码到 byte 序列，并将结果存储到新的 byte 数组。
     *
     * @param content 字符串对象
     *
     * @param charset 编码方式
     *
     * @return 所得 byte 数组
     */
    public static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }

        try {
            return content.getBytes(charset);
        }
        catch (UnsupportedEncodingException ex) {
            throw new IllegalArgumentException("Not support:" + charset, ex);
        }
    }

    /**
     * 构建键值对
     * @param key
     * @param value
     * @return
     * @throws UnsupportedEncodingException
     */
    public static NameValuePair createNameValuePair(String key, String value)
            throws UnsupportedEncodingException {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
            return null;
        }
        return new NameValuePair(key, URLEncoder.encode(value, "utf-8"));
    }

    /**
     * 把{@code Byte}数组转成十六进制格式的字符串
     *
     * @param value 需要转换的字节数组
     *
     * @return 返回转换后的{@code String}对象
     */
    public static String toHexString(byte[] value) {
        if (value == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer(value.length * 2);
        for (int i = 0; i < value.length; i++) {
            sb.append(toHexString(value[i]));
        }
        return sb.toString();
    }

    /**
     * 把{@code Byte}类型转成十六进制格式的字符串
     *
     * @param value 需要转换的值
     *
     * @return 返回转换后的{@code String}对象
     */
    public static String toHexString(byte value) {
        String hex = Integer.toHexString(value & 0xFF);

        return padZero(hex, 2);
    }

    /**
     * 使用"0"左补齐字符串
     *
     * @param hex 十六进制字符串
     *
     * @param length 字符串的固定长度
     *
     * @return 返回补齐后的十六进制字符串
     */
    private static String padZero(String hex, int length) {
        for (int i = hex.length(); i < length; i++) {
            hex = "0" + hex;
        }
        return hex.toUpperCase();
    }

    /**
     * 使用{@code MD5}方式对字符串进行签名
     *
     * @param text 需要加签名的数据
     * @param key 对需要签名的的数据进行加盐
     * @param charset 数据的编码方式
     * @return 返回签名信息
     */
    public static String sign(String text, String key, String charset) {
        String message = text + key;

        MessageDigest digest = getDigest(SIGN_ALGORITHMS);
        digest.update(getContentBytes(message, charset));

        byte[] signed = digest.digest();

        return toHexString(signed);
    }

    /**
     * 使用{@code MD5}方式对签名信息进行验证
     *
     * @param text 需要加签名的数据
     * @param sign 签名信息
     * @param key 对需要签名的的数据进行加盐
     * @param charset 数据的编码方式
     * @return 是否验证通过。{@code True}表示通过
     */
    public static boolean verify(String text, String sign, String key, String charset) {
        String mysign = sign(text, key, charset);

        if (mysign.equals(sign)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 返回实现指定摘要算法的 {@code MessageDigest} 对象。
     *
     * @param algorithm 信息摘要算法名称
     *
     * @return 返回摘要算法对象
     */
    private static MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (final NoSuchAlgorithmException ex) {
            throw new IllegalArgumentException("Not support:" + algorithm, ex);
        }
    }

    /**
     * GET方式HTTP请求
     * @param request_url 请求URL
     * @return 响应数据
     */
    public static String requestGET(String request_url) {
        Logger.debug("requestGET    request_url----->" + request_url);
        String response_data = "";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(request_url);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                response_data = EntityUtils.toString(response.getEntity(), Charsets.UTF_8.name());
            } else {
                Logger.debug("requestGET    GET请求执行失败，HTTP状态码：" + statusCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Logger.debug("requestGET    执行GET请求发生异常----->", e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
                Logger.debug("requestGET    关闭同步HTTP客户端发生异常----->", e);
            }
        }
        Logger.debug("requestGET    response_data----->" + response_data);
        return response_data;
    }

    /**
     * POST方式HTTP请求。
     * @param request_data 请求数据
     * @return 响应数据
     * Content-Type请求媒体类型，默认为application/x-www-form-urlencoded，即普通表单提交
     */
    public static String requestPOST(String request_data, ShengConfig config) {
        Logger.debug("requestPOST    request_data----->" + request_data);
        Logger.debug("requestPOST    request_url----->" + config.getVerifyUrl());
        String response_data = "", header_data = "";
        ObjectNode node = Json.newObject();
        node.put("response_data", response_data);
        node.put("header_data", header_data);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(config.getVerifyUrl());
        try {
            StringEntity entity = new StringEntity(request_data, Consts.UTF_8);
            entity.setContentType(ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
            entity.setContentEncoding(Charsets.UTF_8.name());
            httpPost.setEntity(entity);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            httpPost.getEntity().writeTo(baos);
            String preSign = baos.toString("UTF-8");
            Logger.debug("requestPOST    preSign----->" + preSign);
            String signed = sign(preSign, config.getCkey(), config.getSignCharset());
            Logger.debug("requestPOST    signed----->" + signed);
            httpPost.addHeader("signType", "MD5");
            httpPost.addHeader("signMsg", signed);

            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                response_data = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
                String signType = response.getFirstHeader("signType").getValue();
                String signMsg = response.getFirstHeader("signMsg").getValue();
                header_data = Json.newObject().put("signType", signType)
                        .put("signMsg", signMsg).toString();
                node.put("response_data", response_data);
                node.put("header_data", header_data);
            } else {
                Logger.debug("requestPOST    POST请求执行失败，HTTP状态码：" + statusCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Logger.debug("requestPOST    执行POST请求发生异常----->", e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
                Logger.debug("requestPOST    关闭同步HTTP客户端发生异常----->", e);
            }
        }
        Logger.debug("requestPOST    response_data----->" + node.toString());
        return node.toString();
    }

}
