package dto.sales;

/**
 * @author longhuashen
 * @since 2017/3/13
 */
public class ShOrderStatus {

    public static final int CUSTOMMER_CONFIRM = 1; //客服确认

    public static final int FINANCE_CONFIRM = 2; //财务确认

    public static final int SEND_PRODUCT_BACK = 3;//寄回商品

    public static final int PLATFORM_RECEIPT = 4;//待平台收货

    public static final int SH_FINISH = 5;//售后完成

    public static final int SH_CLOSED = 6;//售后关闭

    public static final int CONFIRM_RECEIPT = 3;//确认收货

    public static final int SH_CONFIRM_YES = 1; //通过

    public static final int SH_CONFIRM_NO = 2;//关闭、不通过
}
