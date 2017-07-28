package dto.sales;

import com.jd.open.api.sdk.response.AbstractResponse;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author longhuashen
 * @since 2017/5/16
 */
public class JdLogisticSendResponse extends AbstractResponse {

    private JdLogisticResult jdLogisticResult;

    @JsonProperty("result")
    public JdLogisticResult getJdLogisticResult() {
        return jdLogisticResult;
    }

    @JsonProperty("result")
    public void setJdLogisticResult(JdLogisticResult jdLogisticResult) {
        this.jdLogisticResult = jdLogisticResult;
    }
}
