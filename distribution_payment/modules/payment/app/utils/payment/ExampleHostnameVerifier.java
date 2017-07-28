package utils.payment;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * HTTPS主机名称验证器
 * HostnameVerifier接口主要是在SSL握手时验证对方主机名称的，这样做的一个目的也是防止链接被重定向到其他的不安全的地址上去，并且若出现服务器证书上的Hostname和实际的URL不匹配时，也能做一些处理，否则会抛出这样的异常：javax.net.ssl.SSLPeerUnverifiedException: Host name '192.168.2.177' does not match the certificate subject provided by the peer，因此实现HostnameVerifier接口，我们能做一些hostname确认的工作，提高安全性。
 * Created by LSL on 2017/3/31.
 */
public class ExampleHostnameVerifier implements HostnameVerifier {
    @Override
    public boolean verify(String s, SSLSession sslSession) {
        if ("tomtop.com.cn".equals(s) ||
                "b2b.com.cn".equals(s) ||
                "middle.b2b.com.cn".equals(s) ||
                "localhost:9007".equals(s)) {
            return true;
        }
        return false;
    }
}
