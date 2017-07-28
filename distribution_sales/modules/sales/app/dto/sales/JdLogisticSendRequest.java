package dto.sales;

import com.jd.open.api.sdk.internal.util.JsonUtil;
import com.jd.open.api.sdk.request.AbstractRequest;
import com.jd.open.api.sdk.request.JdRequest;

import java.io.IOException;
import java.util.TreeMap;

/**
 * @author longhuashen
 * @since 2017/5/16
 */
public class JdLogisticSendRequest extends AbstractRequest implements JdRequest<JdLogisticSendResponse> {

    private String shipmentNumber;

    public String getShipmentNumber() {
        return shipmentNumber;
    }

    public void setShipmentNumber(String shipmentNumber) {
        this.shipmentNumber = shipmentNumber;
    }

    @Override
    public String getApiMethod() {
        return "jingdong.edi.sn.send";
    }

    @Override
    public String getAppJsonParams() throws IOException {
        TreeMap map = new TreeMap();
        map.put("shipmentNumber", this.shipmentNumber);
        return JsonUtil.toJson(map);
    }

    @Override
    public Class<JdLogisticSendResponse> getResponseClass() {
        return JdLogisticSendResponse.class;
    }
}
