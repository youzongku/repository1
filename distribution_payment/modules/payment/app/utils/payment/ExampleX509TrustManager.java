package utils.payment;

import javax.net.ssl.X509TrustManager;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * HTTPS证书检查管理器
 * X509TrustManager接口是用来判断服务器提供的证书是否可以被客户端信任
 * 需要校验证书请放开注释
 * Created by LSL on 2017/4/5.
 */
public class ExampleX509TrustManager implements X509TrustManager {

    private Certificate certificate = null;

    /*public ExampleX509TrustManager() {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            fis = new FileInputStream("证书地址");
            bis = new BufferedInputStream(fis);
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            while (bis.available() > 0) {
                certificate = factory.generateCertificate(bis);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Tool.closeStream(bis, fis);
        }
    }*/

    /**
     * 检查客户端的证书
     * @Author LSL on 2017-04-05 11:32:22
     */
    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    /**
     * 检查服务端的证书
     * @Author LSL on 2017-04-05 11:32:47
     */
    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        /*for (X509Certificate x509Certificate : x509Certificates) {
            if (x509Certificate.toString().equals(certificate.toString())) return;
        }
        throw new CertificateException("certificate is illegal");*/
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        //return new X509Certificate[] {(X509Certificate) certificate};
        return null;
    }

}
