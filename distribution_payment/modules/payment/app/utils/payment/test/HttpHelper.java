/*
 * Copyright 2010 sdp.com, Inc. All rights reserved.
 * sdp.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * creator : lishaohua
 * create time : 2016-4-27 下午1:36:24
 */
package utils.payment.test;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * HTTP请求辅助类
 * @author lishaohua
 * time : 2016-4-27 下午1:36:24
 */
public class HttpHelper {

    /**
     * 
     * @param url
     * @param merchantprivatekey
     * @param sftRsaPublicKey
     * @param signType
     * @param params
     * @return
     * @throws IOException
     */
    public static String httpSend(String url, String merchantprivatekey, String sftRsaPublicKey, String signType,
            NameValuePair[] params) throws IOException {
        // 构建HttpClient
        HttpClient client = new HttpClient();
        HttpConnectionManagerParams httpParams = client.getHttpConnectionManager().getParams();
        httpParams.setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        PostMethod postMethod = new PostMethod(url);
        postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

        // 设置请求参数列表
        postMethod.addParameters(params);

        // 签名<与参数列表顺序等无关，request body作为签名的明文>, 详细请参见《盛付通快捷支付API》--RSA签名
        RequestEntity requestEntity = postMethod.getRequestEntity();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        requestEntity.writeRequest(baos);
        String plainText = baos.toString();
        System.out.println("请求签名明文(request body) : "+ plainText);
        String signMsg = RSA.sign(plainText, merchantprivatekey, "utf-8");

        // 设置签名类型与签名串到请求header里面
        postMethod.addRequestHeader("signType", signType);
        postMethod.addRequestHeader("signMsg", signMsg);
        System.out.println("请求签名串 : "+signMsg);

        // 发起请求
        int httpCode = client.executeMethod(postMethod);
        String responseBody = postMethod.getResponseBodyAsString();
        System.out.println("http请求响应状态码 : {}"+ httpCode);
        System.out.println("http请求响应body : {}"+ responseBody);

        // 验证签名，响应的签名类型与签名串同样也是从header里面去取
        Header responseSignMsgHeader = postMethod.getResponseHeader("signMsg");
        Header responseSignTypeHeader = postMethod.getResponseHeader("signType"); // 快捷API只会返回RSA
        if ((null != responseSignMsgHeader) && (null != responseSignTypeHeader)) {
            String responseSignType = responseSignTypeHeader.getValue();
            String responseSignMsg = responseSignMsgHeader.getValue();
            System.out.println("响应签名类型 : {}"+ responseSignType);
            System.out.println("响应签名串 : {}"+ responseSignMsg);

            boolean signResult = false;
            // 盛付通公钥
            if (StringUtils.equalsIgnoreCase("RSA", responseSignType)) {
                signResult = RSA.verify(responseBody, responseSignMsg, sftRsaPublicKey, "utf-8");
            } else {
                System.out.println("未知的签名类型  : "+ responseSignType);
            }

            if (signResult) {
                System.out.println("验证签名成功");
            } else {
                System.out.println("验证签名失败");
            }
        } else {
            System.out.println("找不到签名相关信息，验证签名失败");
        }
        return responseBody;
    }
}
