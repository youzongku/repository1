package service.timer;

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

}
