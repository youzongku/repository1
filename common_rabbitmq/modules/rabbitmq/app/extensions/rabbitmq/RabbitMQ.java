package extensions.rabbitmq;


/**
 * @author wujirui
 */
public class RabbitMQ {

    private String host = null;

    private String vhost = null;

    private Integer port = null;

    private String username = null;

    private String password = null;

    private Boolean autoAck = false;

    private Boolean basicQos = false;

    private final Integer defaultRetries = 5;

    private Integer retries = defaultRetries;

    private Boolean durable = false;

    private String exchangeType = "direct";

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getVhost() {
        return vhost;
    }

    public void setVhost(String vhost) {
        this.vhost = vhost;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAutoAck() {
        return autoAck;
    }

    public void setAutoAck(Boolean autoAck) {
        this.autoAck = autoAck;
    }

    public Boolean getBasicQos() {
        return basicQos;
    }

    public void setBasicQos(Boolean basicQos) {
        this.basicQos = basicQos;
    }

    public Integer getDefaultRetries() {
        return defaultRetries;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public Boolean getDurable() {
        return durable;
    }

    public void setDurable(Boolean durable) {
        this.durable = durable;
    }

    public String getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(String exchangeType) {
        this.exchangeType = exchangeType;
    }
}
