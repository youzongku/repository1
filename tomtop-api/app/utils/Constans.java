package utils;

/**
 * Created by Administrator on 2016/3/22.
 */
public class Constans {
    //URL
    public static final String B2CBASEURL = "http://www.tomtop.hk";
    public static final String B2BBASEURL = "http://b2b.com.cn";

    //APIs
    public static final String CHECK_LOGIN = B2BBASEURL +"/member/isnulogin";//是否登录
    public static final String MEMBER_LOGIN = B2BBASEURL + "/member/login";//登录
    public static final String MEMBER_INFO = B2BBASEURL +"/member/infor";//会员信息

    public static final String PRODUCT_READ = B2BBASEURL +"/product/api/getProducts";//获得产品列表

    public static final String SALES_ORDER_READ = B2BBASEURL +"/sales/getsl";//查看销售单
    public static final String SALES_ORDER_CONFIRM = B2BBASEURL + "/sales/adslodr";//销售订单下单
    public static final String SALE_ORDER_RECEIVER = B2BBASEURL + "/sales/getRes";//获得接收人地址

    public static final String PURCHASE_ORDER_READ = B2BBASEURL +"/purchase/viewpurchase";//采购订单查询
    public static final String PURCHASE_ORDER_CONFIRM = B2BBASEURL +"/purchase/order";//采购订单下单

    public static final String MICRO_WAREHOUSE_READ = B2CBASEURL + "/warehousing/micro-warehouse";



}
