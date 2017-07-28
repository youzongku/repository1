package dto.sales;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author longhuashen
 * @since 2017/5/16
 */
public class JdLogisticResult {

    private String resultMessage;

    private String success;

    @JsonProperty("resultMessage")
    public String getResultMessage() {
        return resultMessage;
    }

    @JsonProperty("resultMessage")
    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    @JsonProperty("success")
    public String getSuccess() {
        return success;
    }

    @JsonProperty("success")
    public void setSuccess(String success) {
        this.success = success;
    }
}
