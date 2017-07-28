package services.sales;

/**
 * Created by luwj on 2016/1/19.
 */
public interface ISalesPushToB2CService {

    /**
     * b2b销售订单推送至b2c
     * @param exeType
     * @return
     */
    public void pushSales(String exeType);

    /**
     * httpPost
     * @param url
     * @param request_xml
     * @return
     */
    public  String payUnifiedorder( String url ,String request_xml);
}
