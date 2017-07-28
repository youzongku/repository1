package util.sales;

import java.util.Date;
import java.util.UUID;

/**
 * Created by luwj on 2015/11/26.
 */
public class IDUtils {
	
	/**
	 * 营销单单号前缀
	 */
	public static final String MARKETING_ORDER_NO_PREFIX = "YX";
	/**
	 * 销售单单号前缀
	 */
	public static final String SALE_ORDER_NO_PREFIX = "XS";
	/**
	 * 售后单单号前缀
	 */
	public static final String AFTER_SALE_ORDER_NO_PREFIX = "SH";
	/**
	 * 交易号前缀
	 */
	public static final String TRANSACTION_NO_PREFIX = "TT_BBC_";

    /**
     * 发货单退款售后单单号前缀
     */
    private static String XSSH_AFTER_SALE_ORDER_NO_PREFIX = "XSSH";
    
    /**
     * 
     */
    private static String COMBINE_SALE_ORDER_NO_PREFIX = "HBXS";
    
    /**
     * 生成随机窜
     * @return
     */
    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成时间加随机数字符窜
     * @return
     */
    public static String buildRefundNo(){
        String str = DateUtils.date2string(new Date(), DateUtils.FORMAT_DATETIME_BACKEND);
        int x=(int)(Math.random()*1000);
        return str + String.valueOf(x);
    }

    /**
     * eg.2015112700000025
     * @param nextval
     * @return
     */
    public static String getCode(String seqvalue){
        String cal = DateUtils.date2string(new Date() , DateUtils.FORMAT_DATETIME_BACKEND);
        return cal + paddingLeft(seqvalue,8);
    }
    
    /**
     * 左侧补全
     * @param seqvalue
     * @param paddingLength
     * @return
     */
    public static String paddingLeft(String seqvalue,int paddingLength){
    	if(seqvalue.length() < paddingLength){
    		StringBuilder sb = new StringBuilder();
    		for(int i = 0;i< paddingLength - seqvalue.length();i++){
    			sb.append("0");
    		}
    		return sb.append(seqvalue).toString();
    	}else{
    		return seqvalue;
    	}
    }
    
    /**
     * 销售单单号
     * @param seqvalue
     * @return
     */
    public static String getSalesCode(String seqvalue){
    	return SALE_ORDER_NO_PREFIX+getCode(seqvalue);
    }
    
    /**
     * 营销单单号
     * @param seqvalue
     * @return
     */
    public static String getMarketingOrderCode(String seqvalue){
    	return MARKETING_ORDER_NO_PREFIX+getCode(seqvalue);
    }
    
    /**
     * 获取售后单单号
     * @param seqvalue
     * @return
     */
    public static String getAfterSalesCode(String seqvalue){
    	return AFTER_SALE_ORDER_NO_PREFIX+getCode(seqvalue);
    }

    /**
     * 生成一个时间+4位随机数
     * 例：2016032916420001
     * @return
     */
    public static String buildArgNo(){
        String cal = DateUtils.date2string(new Date() , DateUtils.FORMAT_DATETIME_BACKEND);
        int x=(int)(Math.random()*10000);
        String end = "";
        switch (String.valueOf(x).length()){
            case 1:
                end = "000"+x;
                break;
            case 2:
                end = "00"+x;
                break;
            case 3:
                end = "0"+x;
                break;
            default:
                end=String.valueOf(x);
                break;
        }
        return cal + end;
    }

    /**
     * 销售模块--系统生成交易号规则
     * 例：TT_BBC_2016032916420001
     * @return
     */
    public static String getPayNo(){
        return TRANSACTION_NO_PREFIX+ buildArgNo();
    }

    public static void main(String[] args){
        for(int i=0;i<20;i++) {
            System.out.println(getPayNo());
        }
    }

    /**
     * 获取销售发货退款单号
     *
     * @param seqvalue
     * @return
     */
    public static String getXsshSaleOrderCode(String seqvalue) {
        return XSSH_AFTER_SALE_ORDER_NO_PREFIX + getCode(seqvalue);
    }
    
    /**
     * 获取 合并发货单号
     * @author zbc
     * @since 2017年5月19日 上午9:37:25
     */
    public static String getCombineSaleOrderCode(String seqvalue){
    	  return COMBINE_SALE_ORDER_NO_PREFIX + getCode(seqvalue);
    }
    
    /**	  
     * 获取随机uid
     * @author zbc
     * @since 2017年5月19日 下午2:32:01
     * @return
     */
    public static String getUid(){
    	return getUUID()+System.currentTimeMillis();
    }
}
