package util.product;

import java.util.Date;
import java.util.UUID;

/**
 * Created by luwj on 2015/11/26.
 */
public class IDUtils {
	
	/**
	 * 交易号前缀
	 */
	public static final String CONTRACT_SEQ = "HT";
	
	public static final String INVENTORY_LOCK_SEQ = "LOCK";

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
    	if(seqvalue.length() < 8){
    		StringBuilder sb = new StringBuilder();
    		for(int i = 0;i< 8 - seqvalue.length();i++){
    			sb.append("0");
    		}
    		return sb.append(seqvalue).toString();
    	}else{
    		return seqvalue;
    	}
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

	public static String getContractNoCode(String selectNextValue) {
		return CONTRACT_SEQ + getCode(selectNextValue);
	}
	
	public static String getKALockNo(String selectNextValue) {
		return INVENTORY_LOCK_SEQ + getCode(selectNextValue);
	}
}
